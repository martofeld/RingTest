package com.mfeldsztejn.ringtest.ui.detail

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.mfeldsztejn.ringtest.R
import kotlinx.android.synthetic.main.detail_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetailFragment : Fragment(R.layout.detail_fragment) {

    companion object {
        private const val KEY_POST_ID = "post_id"

        fun newInstance(postId: Int) = DetailFragment().apply {
            arguments = bundleOf(KEY_POST_ID to postId)
        }
    }

    private val postId by lazy { requireArguments()[KEY_POST_ID] }
    private val viewModel by viewModel<DetailViewModel> { parametersOf(postId) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title.transitionName = "title_$postId"
        viewModel.post.observe(viewLifecycleOwner) {
            title.text = it.title
        }
    }
}