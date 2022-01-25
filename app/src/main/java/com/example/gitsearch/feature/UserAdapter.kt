package com.example.gitsearch.feature

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.model.User
import com.example.gitsearch.R
import com.example.gitsearch.databinding.ItemLoadBinding
import com.example.gitsearch.databinding.ItemUserBinding

class UserAdapter(private val context: Context, private val dataset: MutableList<User>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    companion object {
        const val VIEW_TYPE_LOAD = 0
        const val  VIEW_TYPE_ITEM = 1
    }

    fun addItems(items: List<User>) {
        dataset.addAll(items)
        notifyDataSetChanged()
    }

    fun addLoad() {
        if(dataset.find { it.id == 0L } != null)
            return
        dataset.add(User(0, "", "", ""))
        notifyItemInserted(dataset.size)
    }

    fun removeLoad() {
        val loadItem = dataset.firstOrNull { it.id == 0L } ?: return
        dataset.remove(loadItem)
        notifyItemRemoved(dataset.indexOf(loadItem))
    }

    fun removeAll() {
        dataset.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType)  {
            VIEW_TYPE_ITEM -> {
                UserViewHolder(
                    context,
                    ItemUserBinding.inflate(inflater, parent, false)
                )
            }
            else -> {
                LoadViewHolder(
                    ItemLoadBinding.inflate(inflater, parent, false)
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataset[position]
        when(item.id){
            VIEW_TYPE_LOAD.toLong() -> {
                (holder as LoadViewHolder).bind()
            }
            else -> {
                (holder as UserViewHolder).bind(item)
            }
        }
    }

    override fun getItemCount(): Int {
       return dataset.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(dataset[position].id == 0L) {
            VIEW_TYPE_LOAD
        } else VIEW_TYPE_ITEM
    }


    internal class UserViewHolder(
        private val context: Context,
        private val binding: ItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: User) {
            binding.tvName.text = item.name

            if(item.avatarUrl.isNotEmpty()) {
                Glide.with(context)
                    .load(item.avatarUrl)
                    .placeholder(R.drawable.ic_place_holder)
                    .into(binding.imgProfile)
            }
        }
    }

    internal class LoadViewHolder(
         private val binding: ItemLoadBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.pbLoad.visibility = View.VISIBLE
        }
    }

}