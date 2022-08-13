package com.example.newsapp.all.ui

import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
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

class SavedNews : Fragment() {


    lateinit var newsAdapter: NewsAdapter
    lateinit var viewModel: NewsViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentSavedNewsBinding.inflate(layoutInflater)

        viewModel = (activity as MainActivity).viewModel

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Saved"
        (requireActivity() as AppCompatActivity).supportActionBar?.setBackgroundDrawable(
            ColorDrawable(getResources().getColor(R.color.background)));

        newsAdapter = NewsAdapter()

        binding.recViewSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            itemAnimator = SlideInDownAnimator().apply {
                addDuration = 600L
            }
        }

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
                Snackbar.make(binding.root, "Deleted Successfully", Snackbar.LENGTH_LONG).apply {
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
        inflater.inflate(R.menu.menu_saved_news,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.deleteAllNews -> {
                deleteAllTasks()
            }
        }
        return super.onOptionsItemSelected(item)

    }

    fun deleteAllTasks() {
        android.app.AlertDialog.Builder(requireContext())
            .setIcon(android.R.drawable.ic_delete)
            .setTitle("Clear All?")
            .setMessage("Are you sure you want to delete all news article?")
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, which -> viewModel.deleteAll() }
            )
            .setNegativeButton("No", null)
            .show()
        Toast.makeText(requireContext(),"Deleted Successfully",Toast.LENGTH_SHORT).show()
    }

}