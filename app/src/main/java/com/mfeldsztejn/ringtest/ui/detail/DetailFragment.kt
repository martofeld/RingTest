package com.mfeldsztejn.ringtest.ui.detail

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.util.Linkify
import android.transition.TransitionInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.mfeldsztejn.ringtest.GlideApp
import com.mfeldsztejn.ringtest.R
import com.mfeldsztejn.ringtest.util.doOnFinish
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

    private val postId by lazy { requireArguments()[KEY_POST_ID] as Int }
    private val viewModel by viewModel<DetailViewModel> { parametersOf(postId) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        postponeEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        thumbnail.transitionName = getString(R.string.thumbnail_transition_name, postId)
        title.transitionName = getString(R.string.title_transition_name, postId)
        author.transitionName = getString(R.string.author_transition_name, postId)
        viewModel.post.observe(viewLifecycleOwner) {
            title.text = it.title
            author.text = it.author
            if (it.thumbnail != null) {
                GlideApp.with(this)
                    .load(it.thumbnail)
                    .doOnFinish { startPostponedEnterTransition() }
                    .into(thumbnail)
            } else {
                startPostponedEnterTransition()
            }
            if (it.text != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    text.text = Html.fromHtml(it.text, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    @Suppress("DEPRECATION")
                    text.text = Html.fromHtml(it.text)
                }
            }
            url.text = it.url
            Linkify.addLinks(url, Linkify.WEB_URLS)
        }
    }
}