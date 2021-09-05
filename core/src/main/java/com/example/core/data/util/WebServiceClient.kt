package com.example.core.data.util

import com.example.core.BuildConfig
import com.example.core.model.HttpException
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okio.ByteString
import okio.ByteString.Companion.decodeHex
import java.io.IOException
import java.nio.charset.Charset
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import com.example.core.data.util.Response as RequestResponse

class WebServiceClient @Inject constructor(
    private val client: OkHttpClient,
    private val moshi: Moshi
) {

    internal fun call(urlSegment: String) : Builder {
        val reqBuilder = Request.Builder()
        reqBuilder
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/vnd.github.v3+json")

        val baseUrl = BuildConfig.BASE_URL
        val urlBuilder = HttpUrl.parse(baseUrl + urlSegment)!!.newBuilder()

        return Builder(reqBuilder, client, moshi, urlBuilder)
    }

    internal class Builder(
        internal val builder: Request.Builder,
        private val client: OkHttpClient,
        private val moshi: Moshi,
        private val urlBuilder : HttpUrl.Builder
    ) {
        private val jsonType = MediaType.get("application/vnd.github.v3+json; charset=utf-8")

        internal fun addQueryParam(key: String, value: String) : Builder {
            urlBuilder.addQueryParameter(key, value)
            return this
        }

        internal fun addHeader(key: String, value: String) : Builder {
            builder.addHeader(key, value)
            return this
        }

        internal fun requestGet() : Builder {
            builder.url(urlBuilder.build())
            builder.get()
            return this
        }

        private suspend inline  fun <reified T>  Call.await(adapter: JsonAdapter<T>) : T {
            return suspendCancellableCoroutine { continuation ->
                val callback = ContinuationCallback(this, continuation,adapter)
                enqueue(callback)
                continuation.invokeOnCancellation(callback)
            }
        }

        internal suspend inline fun <reified T> execute(adapter: JsonAdapter<T>) : T {
            val call = client.newCall(builder.build())
            return call.await(adapter)
        }

        internal suspend inline fun <reified T> execute() : RequestResponse<T> {
            val call = client.newCall(builder.build())
            val type = Types.newParameterizedType(RequestResponse::class.java, T::class.java)
            val adapter = moshi.adapter<RequestResponse<T>>(type)
            return call.await(adapter)
        }

    }

    internal class ContinuationCallback<in T>(
        private val call: Call,
        private val continuation: CancellableContinuation<T>,
        private val adapter: JsonAdapter<T>
    ) : Callback, CompletionHandler {
        // Byte order mark. See: https://stackoverflow.com/a/2223926
        private val utf8Bom: ByteString = "EFBBBF".decodeHex()

        override fun onResponse(call: Call, response: Response) {

            if (response.isSuccessful) {
                val source = response.body()?.source()
                source?.request(Long.MAX_VALUE)
                val buffer = source?.buffer;
                val body = buffer?.clone()?.readString(Charset.forName("UTF-8"))

                if(body != null && response.code() != 200) {
                    continuation.resumeWithException(HttpException(response.code(), response.message()))
                } else {
                    if (source!!.rangeEquals(0, utf8Bom)) {
                        source.skip(utf8Bom.size.toLong())
                    }

                    val reader = JsonReader.of(source)

                    try {
                        val result = adapter.fromJson(reader)
                        continuation.resume(result!!)
                    } catch (ex: Exception) {
                        continuation.resumeWithException(ex)
                    }
                }
            }
        }

        override fun onFailure(call: Call, ex: IOException) {
            if (!call.isCanceled)
                continuation.resumeWithException(ex)
        }

        override fun invoke(cause: Throwable?) {
            try {
                call.cancel()
            } catch (_: Throwable){}
        }

    }

}