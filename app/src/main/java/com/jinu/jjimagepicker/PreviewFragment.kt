package com.jinu.jjimagepicker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jinu.jjimagepicker.adapter.AlbumDropdownAdapter
import com.jinu.jjimagepicker.adapter.GalleryAdapter
import com.jinu.jjimagepicker.adapter.PreviewAdapter
import com.jinu.jjimagepicker.model.Album
import com.jinu.jjimagepicker.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.fragment_gallery.view.*
import kotlinx.android.synthetic.main.fragment_preview.view.*

class PreviewFragment : Fragment() {
    private val args: PreviewFragmentArgs by navArgs()

    private val viewModel: MainActivityViewModel by activityViewModels()
    private lateinit var previewAdapter: PreviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_preview, container, false)

        initToolbar(view)
        initPreviewAdapter(view)
        initSelectedImagesObserver(view)

        return view
    }

    private fun initToolbar(view: View) {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }

        view.fragment_preview_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        view.fragment_preview_toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        view.fragment_preview_toolbar.inflateMenu(R.menu.menu_fragment_gallery)
        view.fragment_preview_toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_fragment_gallery_submit -> {
                    val selectedUris = viewModel.selectedImages.value!!.map { mediaStoreImage -> mediaStoreImage.contentUri.toString() }
                    val intent = Intent().putStringArrayListExtra("selectedUris", ArrayList(selectedUris))

                    requireActivity().setResult(Activity.RESULT_OK, intent)
                    requireActivity().finish()

                    true
                }
                else -> false
            }
        }
    }

    private fun initPreviewAdapter(view: View) {
        previewAdapter = PreviewAdapter(args.albumIndex, viewModel) { image ->
            println("onclick $image")
        }

        view.fragment_preview_pager.adapter = previewAdapter
        previewAdapter.submitList(viewModel.albums.value!![args.albumIndex].images)
        view.fragment_preview_pager.currentItem = args.imagePosition
    }

    private fun initSelectedImagesObserver(view: View) {
        viewModel.selectedImages.observe(viewLifecycleOwner) { selectedImages ->
            updateToolbar(view, selectedImages.size)
        }
    }

    private fun updateToolbar(view: View, selectedCount: Int) {
        when (selectedCount) {
            0 -> {
                view.fragment_preview_toolbar.menu.findItem(R.id.menu_fragment_gallery_submit).isEnabled = false
                view.fragment_preview_toolbar.menu.findItem(R.id.menu_fragment_gallery_submit).title = getString(R.string.gallery_submit_default)
            }
            else -> {
                // singleselectionmode 이면 menu title default로 !!
                view.fragment_preview_toolbar.menu.findItem(R.id.menu_fragment_gallery_submit).isEnabled = true
                view.fragment_preview_toolbar.menu.findItem(R.id.menu_fragment_gallery_submit).title = getString(R.string.gallery_submit, selectedCount)
            }
        }
    }
}