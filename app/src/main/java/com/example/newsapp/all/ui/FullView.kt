package com.example.newsapp.all.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.newsapp.all.adapter.NewsAdapter
import com.example.newsapp.all.database.NewsDatabase
import com.example.newsapp.all.repository.Repository
import com.example.newsapp.all.viewmodel.NewsViewModel
import com.example.newsapp.all.viewmodel.ViewModelFactory
import com.example.newsapp.databinding.FragmentFullViewBinding
import com.google.android.material.snackbar.Snackbar
import java.net.MalformedURLException
import java.net.URI
import java.net.URL

class FullView : Fragment() {

    private val args: FullViewArgs by navArgs()

    lateinit var binding: FragmentFullViewBinding

    lateinit var viewModel: NewsViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding = FragmentFullViewBinding.inflate(layoutInflater)

        viewModel = (activity as MainActivity).viewModel

        val article = args.article
        val url = args.article.url.toString()

        binding.webView.apply {
            try {
                webViewClient = WebViewClient()
                loadUrl(url)
                settings.javaScriptEnabled
            } catch (e: MalformedURLException) {
                Toast.makeText(requireContext(), e.printStackTrace().toString(), Toast.LENGTH_SHORT)
            }
        }

        binding.fab.setOnClickListener {
            viewModel.upsert(article)
            Snackbar.make(binding.root, "Added Successfully", Snackbar.LENGTH_SHORT).show()
        }





        return binding.root
    }


}