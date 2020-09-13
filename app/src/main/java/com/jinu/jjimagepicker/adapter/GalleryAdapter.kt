package com.jinu.jjimagepicker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jinu.jjimagepicker.R
import com.jinu.jjimagepicker.model.MediaStoreImage
import kotlinx.android.synthetic.main.item_gallery.view.*

class GalleryAdapter(private val onClick: (MediaStoreImage) -> Unit) :
    ListAdapter<MediaStoreImage, ImageViewHolder>(MediaStoreImage.DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_gallery, parent, false)
        return ImageViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val mediaStoreImage = getItem(position)
        holder.rootView.tag = mediaStoreImage

        Glide.with(holder.imageView)
            .load(mediaStoreImage.contentUri)
            .thumbnail(0.33f)
            .centerCrop()
            .into(holder.imageView)
    }
}

class ImageViewHolder(view: View, onClick: (MediaStoreImage) -> Unit) :
    RecyclerView.ViewHolder(view) {
    val rootView = view
    val imageView: ImageView = view.findViewById(R.id.image)
    val checkView = view.check

    init {
        imageView.setOnClickListener {
            val image = rootView.tag as? MediaStoreImage ?: return@setOnClickListener
            onClick(image)
        }

//        checkView.setOnClickListener {
//            if (mSelectedCollection.isSelected(item)) {
//                mSelectedCollection.remove(item)
//                notifyCheckStateChanged()
//            } else {
//                if (assertAddSelection(holder.itemView.context, item)) {
//                    mSelectedCollection.add(item)
//                    notifyCheckStateChanged()
//                }
//            }
//        }
    }
}

//private fun setCheckStatus(item: Item, mediaGrid: MediaGrid) {
//    if (mSelectionSpec.countable) {
//        val checkedNum = mSelectedCollection.checkedNumOf(item)
//        if (checkedNum > 0) {
//            mediaGrid.setCheckEnabled(true)
//            mediaGrid.setCheckedNum(checkedNum)
//        } else {
//            if (mSelectedCollection.maxSelectableReached()) {
//                mediaGrid.setCheckEnabled(false)
//                mediaGrid.setCheckedNum(CheckView.UNCHECKED)
//            } else {
//                mediaGrid.setCheckEnabled(true)
//                mediaGrid.setCheckedNum(checkedNum)
//            }
//        }
//    } else {
//        val selected = mSelectedCollection.isSelected(item)
//        if (selected) {
//            mediaGrid.setCheckEnabled(true)
//            mediaGrid.setChecked(true)
//        } else {
//            if (mSelectedCollection.maxSelectableReached()) {
//                mediaGrid.setCheckEnabled(false)
//                mediaGrid.setChecked(false)
//            } else {
//                mediaGrid.setCheckEnabled(true)
//                mediaGrid.setChecked(false)
//            }
//        }
//    }
//}