package com.example.memeexplorer.search.presentation

import android.content.ContentResolver
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
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
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memeexplorer.R
import com.example.memeexplorer.common.presentation.Event
import com.example.memeexplorer.common.presentation.MemesAdapter
import com.example.memeexplorer.databinding.FragmentSearchBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
        return MemesAdapter()
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
        updateInitialStateViews(newState.noSearchQuery)
        searchAdapter.submitList(newState.memes)

        updateRemoteSearchViews(newState.loading)
        updateNoResultsViews(newState.noMemeResults)
        handleFailures(newState.failure)
    }

    private fun updateInitialStateViews(inInitialState: Boolean) {
        binding.initialSearchImageView.isVisible = inInitialState
        binding.initialSearchText.isVisible = inInitialState
    }

    private fun updateRemoteSearchViews(searchingRemotely: Boolean) {
        binding.searchRemotelyProgressBar.isVisible = searchingRemotely
        binding.searchRemotelyText.isVisible = searchingRemotely
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
//        viewModel.getAllImages(activity!!.contentResolver)
        runBlocking {
            val paths = fetchImages()
            viewModel.saveMemes(paths)
        }
        viewModel.onEvent(SearchEvent.RequestInitialMemesList)
    }

    fun loadImagesfromSDCard(contentResolver: ContentResolver): ArrayList<String> {
        val uris = arrayOf(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        var cursor: Cursor?
        var column_index_data: Int
        var column_index_folder_name: Int
        val listOfAllImages = ArrayList<String>()
        var absolutePathOfImage: String? = null

        val projection =
            arrayOf(MediaStore.MediaColumns.DATA)

        for (uri in uris){
            cursor = contentResolver.query(uri, projection, null, null, null)

//            column_index_data = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            column_index_data = cursor!!.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA)
            column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
//            .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data)
                listOfAllImages.add(absolutePathOfImage)
            }
        }

        return listOfAllImages
    }

    var imageList: ArrayList<String> = ArrayList()
    fun fetchImages(): ArrayList<String> {
        val columns = arrayOf(MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID)
        val imagecursor: Cursor = requireActivity().managedQuery(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
            null, ""
        )
        for (i in 0 until imagecursor.count) {
            imagecursor.moveToPosition(i)
            val dataColumnIndex =
                imagecursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            imageList.add(imagecursor.getString(dataColumnIndex))
        }
        return imageList
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}