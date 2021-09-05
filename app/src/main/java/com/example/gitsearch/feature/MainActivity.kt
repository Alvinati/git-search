package com.example.gitsearch.feature

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gitsearch.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel : MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appToolbar)


        val adapter = UserAdapter(this, mutableListOf())
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvUser.layoutManager = layoutManager
        binding.rvUser.adapter = adapter

        binding.rvUser.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if(dy > 0) {
                    layoutManager.let {
                        val totalItem = it.itemCount
                        val completeVisible = it.findLastCompletelyVisibleItemPosition()

                        if (completeVisible == totalItem - 1) {
                            mainViewModel.onEventReceived(MainViewModel.Event.LoadMore(binding.etSearch.text.toString()))
                        }
                    }
                }
            }
        })
        binding.etSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {

                if(s.toString().length >= 3)
                    binding.imgClear.visibility = View.VISIBLE
                else  {
                    adapter.removeAll()
                    binding.imgClear.visibility = View.INVISIBLE
                }

                mainViewModel.onEventReceived(MainViewModel.Event.SearchUser(s.toString()))
            }
        })


        binding.imgClear.setOnClickListener {
            adapter.removeAll()
            binding.etSearch.setText("")
            binding.imgClear.visibility = View.INVISIBLE
        }
        bindViewModels(adapter)
    }

    private fun bindViewModels(adapter: UserAdapter) {
        mainViewModel.state.observe(this, {
            it.getContentIfNotHandled().also { state ->
                when(state) {
                    is MainViewModel.State.ShowLoading ->{
                        adapter.addLoad()
                    }
                    is MainViewModel.State.HideLoading -> {
                        adapter.removeLoad()
                    }
                    is MainViewModel.State.ShowToast -> {
                        Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    }
                    is MainViewModel.State.ShowSearchResult -> {
                        adapter.removeAll()
                        adapter.addItems(state.items)
                    }
                    is MainViewModel.State.LoadMoreResult -> {
                        adapter.addItems(state.items)
                    }
                }
            }
        })
    }

}