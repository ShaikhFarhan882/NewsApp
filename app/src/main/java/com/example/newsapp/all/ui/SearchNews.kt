package com.example.newsapp.all.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.all.adapter.NewsAdapter
import com.example.newsapp.all.constants.Resource
import com.example.newsapp.all.database.NewsDatabase
import com.example.newsapp.all.repository.Repository
import com.example.newsapp.all.viewmodel.NewsViewModel
import com.example.newsapp.all.viewmodel.ViewModelFactory
import com.example.newsapp.databinding.FragmentSearchNewsBinding
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNews : Fragment() {

    lateinit var viewModel: NewsViewModel

    /*private val repository = Repository()*/
/*
    private val viewModel: NewsViewModel by activityViewModels { ViewModelFactory(repository) }*/

    private lateinit var searchAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentSearchNewsBinding.inflate(inflater)

        viewModel = (activity as MainActivity).viewModel

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Search"
        (requireActivity() as AppCompatActivity).supportActionBar?.setBackgroundDrawable(
            ColorDrawable(getResources().getColor(R.color.background)));



        searchAdapter = NewsAdapter()
        binding.recViewSearch.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(activity)
            itemAnimator = SlideInUpAnimator().apply {
                addDuration = 650L
            }
        }


        //calling the api function to get the data
        var job : Job? = null
        binding.etSearchNews.addTextChangedListener {
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                it?.let {
                    if(it.toString().isNotEmpty()){
                        viewModel.searchNews(it.toString())
                    }
                }
            }
        }


        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data.let {
                        binding.progessBar.visibility = View.INVISIBLE
                        searchAdapter.submitList(it!!.articles)
                    }
                }
                is Resource.Error -> {
                    response.message.let {
                        binding.progessBar.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "Failed to get Data", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
                is Resource.Loading -> {
                    binding.progessBar.visibility = View.VISIBLE
                }
            }
        })

        searchAdapter.setOnItemClickListener {
            val action = SearchNewsDirections.actionSearchNewsToFullView(article = it)
            findNavController().navigate(action)
        }


        return binding.root
    }
}