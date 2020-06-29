package com.mfeldsztejn.ringtest.ui.detail

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.util.Linkify
import android.transition.TransitionInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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

        fun newInstance(name: String) = DetailFragment().apply {
            arguments = bundleOf(KEY_POST_ID to name)
        }
    }

    private val createDocument =
        registerForActivityResult(CreateDocumentContract()) { uri ->
            if (uri != null) {
                // Image is know to not be null or the download button wouldn't be there
                FileDownloadService.startService(
                    requireContext(),
                    uri.toString(),
                    viewModel.postDetail.value!!.image!!
                )
            }
        }
    private val postName by lazy { requireArguments()[KEY_POST_ID] as String }
    private val viewModel by viewModel<DetailViewModel> { parametersOf(postName) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        viewModel.postDetail.value?.let {
            if (!it.image?.url.isNullOrEmpty())
                inflater.inflate(R.menu.detail_fragment_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.download) {
            createDocument.launch(viewModel.post.value!!.title)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title.transitionName = getString(R.string.title_transition_name, postName)
        author.transitionName = getString(R.string.author_transition_name, postName)
        viewModel.post.observe(viewLifecycleOwner) { post ->
            title.text = post.title
            author.text = post.author
        }

        viewModel.postDetail.observe(viewLifecycleOwner) { detail ->
            activity?.invalidateOptionsMenu()
            if (detail.image?.url.isNullOrEmpty()) {
                thumbnail.isVisible = false
                thumbnail_base.setGuidelinePercent(0f)
            } else {
                GlideApp.with(this)
                    .asBitmap()
                    .load(detail.image!!.url)
                    .fitCenter()
                    .doOnFinish { bitmap ->
                        if (bitmap != null) {
                            Palette.from(bitmap).generate { palette ->
                                if (palette != null) {
                                    val color = ContextCompat.getColor(
                                        requireContext(),
                                        R.color.colorPrimary
                                    )
                                    thumbnail.setBackgroundColor(palette.getDominantColor(color))
                                }
                            }
                        }
                    }
                    .into(thumbnail)
            }
            if (detail.text != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    text.text = Html.fromHtml(detail.text, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    @Suppress("DEPRECATION")
                    text.text = Html.fromHtml(detail.text)
                }
            } else {
                text.isVisible = false
            }
            url.text = detail.url
            Linkify.addLinks(url, Linkify.WEB_URLS)
        }
    }
}