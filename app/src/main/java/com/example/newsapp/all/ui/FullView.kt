package com.example.newsapp.all.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.newsapp.all.viewmodel.NewsViewModel
import com.example.newsapp.databinding.FragmentFullViewBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.net.MalformedURLException
@AndroidEntryPoint
class FullView : Fragment() {

    private val args: FullViewArgs by navArgs()

    lateinit var binding: FragmentFullViewBinding

    private val viewModel by viewModels<NewsViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding = FragmentFullViewBinding.inflate(layoutInflater)

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

        binding.share.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, article.url.toString())
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share To:"))
        }

        return binding.root
    }


}