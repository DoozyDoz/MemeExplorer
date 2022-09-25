package com.example.memeexplorer.search.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.CodeBoy.MediaFacer.MediaFacer.withPictureContex
import com.CodeBoy.MediaFacer.PictureGet
import com.example.memeexplorer.R
import com.example.memeexplorer.common.presentation.Event
import com.example.memeexplorer.common.presentation.MemesAdapter
import com.example.memeexplorer.common.presentation.model.UIMeme
import com.example.memeexplorer.databinding.FragmentSearchBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class SearchFragment : Fragment() {
    companion object {
        private const val ITEMS_PER_ROW = 2
    }

    private val binding get() = _binding!!
    private var _binding: FragmentSearchBinding? = null

    private val viewModel: SearchFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        prepareForSearch()
        requestInitialMemesList()
    }

    private fun setupUI() {
        val adapter = createAdapter()
        setupRecyclerView(adapter)
        subscribeToViewStateUpdates(adapter)
    }

    private fun createAdapter(): MemesAdapter {
        return MemesAdapter().apply {
            setOnMemeClickListener { memeId ->
                val action = SearchFragmentDirections.actionSearchToDetails(memeId)
                findNavController().navigate(action)
            }
        }
    }

    private fun setupRecyclerView(searchAdapter: MemesAdapter) {
        binding.searchRecyclerView.apply {
            adapter = searchAdapter
            layoutManager = GridLayoutManager(requireContext(), ITEMS_PER_ROW)
            setHasFixedSize(true)
        }
    }

    private fun subscribeToViewStateUpdates(searchAdapter: MemesAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    updateScreenState(it, searchAdapter)
                }
            }
        }
    }

    private fun updateScreenState(newState: SearchViewState, searchAdapter: MemesAdapter) {

        searchAdapter.submitList(newState.memes)
//        updateInitialStateViews(newState.noSearchQuery)
        updateNoMemesViews(newState.memes)

        updateRemoteSearchViews(newState.loading)
//        updateNoResultsViews(newState.noMemeResults)
        handleFailures(newState.failure)
    }

    private fun updateNoMemesViews(memes: List<UIMeme>) {
        binding.initialSearchImageView.isVisible = memes.isEmpty()
        binding.initialSearchText.isVisible = memes.isEmpty()
    }

    private fun updateInitialStateViews(inInitialState: Boolean) {
        binding.initialSearchImageView.isVisible = inInitialState
        binding.initialSearchText.isVisible = inInitialState
    }

    private fun updateRemoteSearchViews(searchingRemotely: Boolean) {
        binding.searchRemotelyProgressBar.isVisible = searchingRemotely
        binding.searchRemotelyText.isVisible = searchingRemotely

//        binding.initialSearchImageView.isVisible = searchingRemotely
//        binding.initialSearchText.isVisible = searchingRemotely
    }

    private fun updateNoResultsViews(noResultsState: Boolean) {
        binding.noSearchResultsImageView.isVisible = noResultsState
        binding.noSearchResultsText.isVisible = noResultsState
    }

    private fun handleFailures(failure: Event<Throwable>?) {
        val unhandledFailure = failure?.getContentIfNotHandled() ?: return

        val fallbackMessage = getString(R.string.an_error_occurred)
        val snackbarMessage = if (unhandledFailure.message.isNullOrEmpty()) {
            fallbackMessage
        } else {
            unhandledFailure.message!!
        }

        if (snackbarMessage.isNotEmpty()) {
            Snackbar.make(requireView(), snackbarMessage, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun prepareForSearch() {
        setupSearchViewListener()
        viewModel.onEvent(SearchEvent.PrepareForSearch)
    }

    private fun setupSearchViewListener() {
        val searchView = binding.searchWidget.search

        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.onEvent(SearchEvent.QueryInput(query.orEmpty()))
                    searchView.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.onEvent(SearchEvent.QueryInput(newText.orEmpty()))
                    return true
                }
            }
        )
    }

    private fun requestInitialMemesList() {
        viewModel.onEvent(SearchEvent.IsLoadingMemes(true))
        viewModel.onEvent(SearchEvent.FetchImages)
        viewModel.onEvent(SearchEvent.IsLoadingMemes(false))
//        viewModel.onEvent(SearchEvent.RequestInitialMemesList)
    }

    private fun fetchImages(): ArrayList<String> {
        val allPhotos = withPictureContex(requireActivity())
            .getAllPictureContents(PictureGet.externalContentUri)
        return allPhotos.map { it.picturePath } as ArrayList<String>
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}