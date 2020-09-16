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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jinu.jjimagepicker.adapter.AlbumDropdownAdapter
import com.jinu.jjimagepicker.adapter.GalleryAdapter
import com.jinu.jjimagepicker.model.Album
import com.jinu.jjimagepicker.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.fragment_gallery.view.*

class GalleryFragment : Fragment() {
    private val viewModel: MainActivityViewModel by activityViewModels()
    private lateinit var galleryAdapter: GalleryAdapter

    private val requestPermission: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted)
            viewModel.loadImages()
        else
            requireActivity().finish()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)

        initToolbar(view)
        initGalleryAdapter(view)
        initAlbumsObserver(view)
        initSelectedImagesObserver(view)

        if (haveStoragePermission())
            viewModel.loadImages()
        else
            requestPermission()

        return view
    }

    private fun haveStoragePermission() =
        ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun initToolbar(view: View) {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }

        view.fragment_gallery_toolbar.setNavigationIcon(R.drawable.ic_close)
        view.fragment_gallery_toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        view.fragment_gallery_toolbar.inflateMenu(R.menu.menu_fragment_gallery)
        view.fragment_gallery_toolbar.setOnMenuItemClickListener { item ->
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

    private fun initGalleryAdapter(view: View) {
        galleryAdapter = GalleryAdapter(viewModel) { position, image ->
            val action = GalleryFragmentDirections.actionToPreviewFragment(viewModel.selectedAlbumIndex, position)
            findNavController().navigate(action)
        }

        view.fragment_gallery_recyclerview.also {
            it.layoutManager = GridLayoutManager(requireContext(), 3)
            it.adapter = galleryAdapter
            it.addItemDecoration(ItemDecoration())
        }
    }

    private fun initAlbumsObserver(view: View) {
        viewModel.albums.observe(viewLifecycleOwner, { albums ->
            initAlbumDropdownAdapter(view, albums)

            if (albums.isNotEmpty())
                albumSelected(0, albums[0])
        })
    }

    private fun initAlbumDropdownAdapter(view: View, albums: ArrayList<Album>) {
        if (albums.isEmpty()) {
            view.fragment_gallery_layout_album.visibility = View.GONE
            return
        }

        view.fragment_gallery_layout_album.visibility = View.VISIBLE

        val albumDropdownAdapter = AlbumDropdownAdapter(requireContext(), albums) { position, album ->
            view.fragment_gallery_actv_album.text = Editable.Factory.getInstance().newEditable(album.bucketDisplayName)
            view.fragment_gallery_actv_album.dismissDropDown()

            albumSelected(position, album)
        }

        view.fragment_gallery_actv_album.setAdapter(albumDropdownAdapter)
        view.fragment_gallery_actv_album.text = Editable.Factory.getInstance().newEditable(albums[0].bucketDisplayName)
    }

    private fun albumSelected(position: Int, album: Album) {
        viewModel.selectedAlbumIndex = position
        galleryAdapter.submitList(album.images)
    }

    private fun initSelectedImagesObserver(view: View) {
        viewModel.selectedImages.observe(viewLifecycleOwner, { selectedImages ->
            updateToolbar(view, selectedImages.size)
        })
    }

    private fun updateToolbar(view: View, selectedCount: Int) {
        when (selectedCount) {
            0 -> {
                // 미리보기 버튼
                view.fragment_gallery_toolbar.menu.findItem(R.id.menu_fragment_gallery_submit).isEnabled = false
                view.fragment_gallery_toolbar.menu.findItem(R.id.menu_fragment_gallery_submit).title = getString(R.string.gallery_submit_default)
            }
            else -> {
                // singleselectionmode 이면 menu title default로 !!
                // 미리보기 버튼
                view.fragment_gallery_toolbar.menu.findItem(R.id.menu_fragment_gallery_submit).isEnabled = true
                view.fragment_gallery_toolbar.menu.findItem(R.id.menu_fragment_gallery_submit).title = getString(R.string.gallery_submit, selectedCount)
            }
        }
    }

    inner class ItemDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            val lp = view.layoutParams as GridLayoutManager.LayoutParams
            val spanIndex = lp.spanIndex

            val oneSideSpace = dpToPx(2f)
            val twoSideSpace = dpToPx(1f)

            when (spanIndex) {
                0 -> outRect.right = oneSideSpace
                1 -> {
                    outRect.left = twoSideSpace
                    outRect.right = twoSideSpace
                }
                2 -> outRect.left = oneSideSpace
            }

            outRect.bottom = dpToPx(3f)
        }

        private fun dpToPx(dp: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.displayMetrics
            ).toInt()
        }
    }
}