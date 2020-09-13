package com.jinu.jjimagepicker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
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
            findNavController().navigateUp()
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
            findNavController().navigateUp()
            // 종료시켜야함
        }

        view.fragment_gallery_toolbar.setNavigationIcon(R.drawable.ic_close)
        view.fragment_gallery_toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        view.fragment_gallery_toolbar.inflateMenu(R.menu.menu_fragment_gallery)
        view.fragment_gallery_toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_fragment_gallery_submit -> {
                    // submit
                    true
                }
                else -> false
            }
        }
    }

    private fun initGalleryAdapter(view: View) {
        galleryAdapter = GalleryAdapter { image ->
            println("onclick $image")
        }

        view.fragment_gallery_recyclerview.also {
            it.layoutManager = GridLayoutManager(requireContext(), 3)
            it.adapter = galleryAdapter
        }
    }

    private fun initAlbumsObserver(view: View) {
        viewModel.albums.observe(viewLifecycleOwner, { albums ->
            initAlbumDropdownAdapter(view, albums)

            if (albums.isNotEmpty())
                albumSelected(albums[0])
        })
    }

    private fun initAlbumDropdownAdapter(view: View, albums: ArrayList<Album>) {
        if (albums.isEmpty()) {
            view.fragment_gallery_layout_album.visibility = View.GONE
            return
        }

        view.fragment_gallery_layout_album.visibility = View.VISIBLE

        val albumDropdownAdapter = AlbumDropdownAdapter(requireContext(), albums) { album ->
            view.fragment_gallery_actv_album.text = Editable.Factory.getInstance().newEditable(album.bucketDisplayName)
            view.fragment_gallery_actv_album.dismissDropDown()

            albumSelected(album)
        }

        view.fragment_gallery_actv_album.setAdapter(albumDropdownAdapter)
        view.fragment_gallery_actv_album.text = Editable.Factory.getInstance().newEditable(albums[0].bucketDisplayName)
    }

    private fun albumSelected(album: Album) {
        galleryAdapter.submitList(album.images)
    }
}