package com.example.newsapp.all.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.example.newsapp.databinding.FragmentBreakingNewsBinding
import jp.wasabeef.recyclerview.animators.LandingAnimator
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import java.net.MalformedURLException


class BreakingNews : Fragment() {


    lateinit var newsAdapter: NewsAdapter
    lateinit var viewModel : NewsViewModel
    lateinit var binding: FragmentBreakingNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentBreakingNewsBinding.inflate(inflater)

        viewModel = (activity as MainActivity).viewModel

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Home"
        (requireActivity() as AppCompatActivity).supportActionBar?.setBackgroundDrawable(
            ColorDrawable(getResources().getColor(R.color.background)));

        binding.lifecycleOwner = this



        newsAdapter = NewsAdapter()
        binding.recView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            itemAnimator = LandingAnimator().apply {
                addDuration = 550L
            }
        }

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data.let {
                        binding.progressBar.visibility = View.INVISIBLE
                        newsAdapter.submitList(it!!.articles)
                    }
                }
                is Resource.Error -> {
                    response.message.let {
                        binding.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "Failed to get Data", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }

        })


        newsAdapter.setOnItemClickListener {
                val action = BreakingNewsDirections.actionBreakingNewsToFullView(article = it)
                findNavController().navigate(action)

        }

        return binding.root

    }


}