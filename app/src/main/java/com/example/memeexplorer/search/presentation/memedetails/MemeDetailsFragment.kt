package com.example.memeexplorer.search.presentation.memedetails

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.memeexplorer.R
import com.example.memeexplorer.databinding.FragmentDetailsBinding
import com.example.memeexplorer.utilities.Tools.share
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MemeDetailsFragment : Fragment() {

    companion object {
        const val MEME_ID = "id"
    }

    private val binding get() = _binding!!
    private var _binding: FragmentDetailsBinding? = null

    private val viewModel: MemeDetailsFragmentViewModel by viewModels()

    private var memeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        memeId = requireArguments().getString(MEME_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.menu_share, menu)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return if (item.itemId == R.id.share) {
//            navigateToSharing()
//            true
//        }
//        else {
//            super.onOptionsItemSelected(item)
//        }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeState()
        val event = MemeDetailsFragmentViewModel.LoadAnimalDetails(animalId!!)
        viewModel.handleEvent(event)
    }
}