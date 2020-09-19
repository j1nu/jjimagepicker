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
import com.jinu.jjimagepicker.viewmodel.MainActivityViewModel
import com.jinu.jjimagepicker.widget.CheckView
import kotlinx.android.synthetic.main.item_gallery.view.*
import kotlinx.coroutines.selects.select

class PreviewAdapter(private val albumIndex: Int, private val viewModel: MainActivityViewModel, private val onClick: (MediaStoreImage) -> Unit) :
    ListAdapter<MediaStoreImage, PreviewAdapter.ImageViewHolder>(MediaStoreImage.DiffCallback) {

    override fun getItemCount(): Int {
        return viewModel.getAlbum(albumIndex).images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_preview, parent, false)
        return ImageViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val mediaStoreImage = getItem(position)
        holder.rootView.tag = mediaStoreImage

        Glide.with(holder.imageView)
            .load(mediaStoreImage.contentUri)
//            .thumbnail(0.33f)
//            .centerCrop()
            .into(holder.imageView)

        holder.setCheckStatus(mediaStoreImage)
    }

    inner class ImageViewHolder(view: View, onClick: (MediaStoreImage) -> Unit) :
        RecyclerView.ViewHolder(view) {
        val rootView = view
        val imageView: ImageView = view.findViewById(R.id.image)
        private val checkView: CheckView = view.check

        init {
            imageView.setOnClickListener {
                val image = rootView.tag as? MediaStoreImage ?: return@setOnClickListener
                onClick(image)
            }

            checkView.setOnClickListener {
                val image = rootView.tag as? MediaStoreImage ?: return@setOnClickListener

                if (viewModel.isSelected(image)) {
                    val index = viewModel.deselect(image)

                    if (viewModel.getSelectedCount() == index)
                        notifyItemChanged(layoutPosition)
                    else
                        notifyDataSetChanged()
                    // 이거 선택된 애들 layoutPosition을 받아올 수 있는 방법이 있으려나..?
                }
                else {
                    if (viewModel.isSelectable()) {
                        viewModel.select(image)
                        notifyItemChanged(layoutPosition)
                    }
                    else {
                        println("maxSelectable !!!")
                    }
                }
            }

            setCountable(viewModel.countable)
        }

        private fun setCountable(countable: Boolean) {
            checkView.setCountable(countable)
        }

        private fun setCheckedNum(checkedNum: Int) {
            checkView.setCheckedNum(checkedNum)
        }

        private fun setChecked(checked: Boolean) {
            checkView.setChecked(checked)
        }

        fun setCheckStatus(image: MediaStoreImage) {
            if (viewModel.countable) {
                val selectedNum = viewModel.selectedNumOf(image)
                if (selectedNum > 0)
                    setCheckedNum(selectedNum)
                else
                    setCheckedNum(CheckView.UNCHECKED)
            }
            else {
                val selected = viewModel.isSelected(image)
                setChecked(selected)
            }
        }
    }
}