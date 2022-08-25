package com.example.newsapp.all.ui

import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.all.adapter.NewsAdapter
import com.example.newsapp.all.database.NewsDatabase
import com.example.newsapp.all.repository.Repository
import com.example.newsapp.all.viewmodel.NewsViewModel
import com.example.newsapp.all.viewmodel.ViewModelFactory
import com.example.newsapp.databinding.FragmentBreakingNewsBinding
import com.example.newsapp.databinding.FragmentSavedNewsBinding
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.LandingAnimator
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class SavedNews : Fragment(),SearchView.OnQueryTextListener {


    lateinit var newsAdapter: NewsAdapter
    lateinit var viewModel: NewsViewModel

    private var _binding: FragmentSavedNewsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSavedNewsBinding.inflate(layoutInflater)

        viewModel = (activity as MainActivity).viewModel

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Saved"
        (requireActivity() as AppCompatActivity).supportActionBar?.setBackgroundDrawable(
            ColorDrawable(getResources().getColor(R.color.background)));

        newsAdapter = NewsAdapter()
        setRecyclerView(newsAdapter)

        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer {
            newsAdapter.submitList(it)
        })


        newsAdapter.setOnItemClickListener {
            val action = SavedNewsDirections.actionSavedNewsToFullView(article = it)
            findNavController().navigate(action)
        }

        //swipe to delete
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.currentList[position]
                viewModel.delete(article)
                val view = binding.root
                Snackbar.make(view, "Deleted Successfully", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.upsert(article)
                    }
                    show()
                }

            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.recViewSavedNews)
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_saved_news, menu)

        val search = menu.findItem(R.id.search_bar)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteAllNews -> {
                deleteAllTasksDialog()
            }
        }
        return super.onOptionsItemSelected(item)

    }

    private fun deleteAllTasksDialog() {
        android.app.AlertDialog.Builder(requireContext())
            .setIcon(android.R.drawable.ic_delete)
            .setTitle("Clear All?")
            .setMessage("Are you sure you want to delete all news article?")
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, which -> deleteAll() }
            )
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteAll() {
        viewModel.deleteAll()
        Toast.makeText(requireContext(), "Successfully Deleted", Toast.LENGTH_SHORT).show()
    }

    private fun setRecyclerView(newsAdapter: NewsAdapter) {
        binding.recViewSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            itemAnimator = SlideInDownAnimator().apply {
                addDuration = 600L
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
       return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if(query!!.isEmpty()) {
            viewModel.getSavedNews().observe(viewLifecycleOwner, Observer {
                newsAdapter.submitList(it)
            })
        }
        else{
            searchThroughDatabase(query)
        }
        return true
    }

    private fun searchThroughDatabase(query: String) {
        var searchQuery: String = query
        searchQuery = "%$searchQuery%"

        viewModel.searchDatabase(searchQuery).observe(viewLifecycleOwner, { Article ->
            newsAdapter.submitList(Article)
        })

    }

}