package com.mfeldsztejn.ringtest.ui.detail

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.util.Linkify
import android.transition.TransitionInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.palette.graphics.Palette
import com.mfeldsztejn.ringtest.GlideApp
import com.mfeldsztejn.ringtest.R
import com.mfeldsztejn.ringtest.util.CreateDocumentContract
import com.mfeldsztejn.ringtest.util.FileDownloadService
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

    private val createDocument =
        registerForActivityResult(CreateDocumentContract()) { uri ->
            if (uri != null) {
                // Image is know to not be null or the download button wouldn't be there
                FileDownloadService.startService(
                    requireContext(),
                    uri.toString(),
                    viewModel.post.value?.image!!
                )
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
        viewModel.post.observe(viewLifecycleOwner) { post ->
            title.text = post.title
            author.text = post.author
            if (post.image?.url.isNullOrEmpty()) {
                download.isVisible = false
                thumbnail.isVisible = false
                thumbnail_base.setGuidelinePercent(0f)
                startPostponedEnterTransition()
            } else {
                download.isVisible = true
                download.setOnClickListener {
                    createDocument.launch(post.title)
                }
                GlideApp.with(this)
                    .asBitmap()
                    .load(post.image!!.url)
                    .fitCenter()
                    .doOnFinish { bitmap ->
                        if (bitmap != null) {
                            Palette.from(bitmap).generate { palette ->
                                if (palette != null) {
                                    val color = ContextCompat.getColor(
                                        requireContext(),
                                        R.color.colorPrimary
                                    )
                                    activity?.actionBar?.setBackgroundDrawable(ColorDrawable(color))
                                    thumbnail.setBackgroundColor(palette.getDominantColor(color))
                                }
                            }
                        }
                        startPostponedEnterTransition()
                    }
                    .into(thumbnail)
            }
            if (post.text != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    text.text = Html.fromHtml(post.text, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    @Suppress("DEPRECATION")
                    text.text = Html.fromHtml(post.text)
                }
            } else {
                text.isVisible = false
            }
            url.text = post.url
            Linkify.addLinks(url, Linkify.WEB_URLS)
        }
    }
}