///*
// * Copyright 2017 Zhihu Inc.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.jinu.jjimagepicker.widget
//
//import android.content.Context
//import android.graphics.drawable.Drawable
//import android.text.format.DateUtils
//import android.util.AttributeSet
//import android.view.LayoutInflater
//import android.view.View
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.recyclerview.widget.RecyclerView
//import com.jinu.jjimagepicker.R
//import com.qingmei2.rximagepicker_extension.R
//import com.qingmei2.rximagepicker_extension.entity.Item
//import com.qingmei2.rximagepicker_extension.entity.SelectionSpec
//import kotlinx.android.synthetic.main.item_gallery.view.*
//
//open class GalleryItem : ConstraintLayout, View.OnClickListener {
//
//    protected lateinit var mThumbnail: ImageView
//    protected lateinit var mCheckView: CheckView
//
//    lateinit var media: Item
//    protected lateinit var mPreBindInfo: PreBindInfo
//    protected var mListener: OnMediaGridClickListener? = null
//
//    constructor(context: Context) : super(context) {
//        init(context)
//    }
//
//    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
//        init(context)
//    }
//
//    fun init(context: Context) {
//        val view = LayoutInflater.from(context).inflate(R.layout.item_gallery, this, true)
//
//        mThumbnail = view.image
//        mCheckView = view.check
//
//        mThumbnail.setOnClickListener(this)
//        mCheckView.setOnClickListener(this)
//    }
//
//    override fun onClick(v: View) {
//        if (mListener != null) {
//            if (v === mThumbnail) {
//                mListener!!.onThumbnailClicked(mThumbnail, media, mPreBindInfo.mViewHolder)
//            } else if (v === mCheckView) {
//                mListener!!.onCheckViewClicked(mCheckView, media, mPreBindInfo.mViewHolder)
//            }
//        }
//    }
//
//    fun preBindMedia(info: PreBindInfo) {
//        mPreBindInfo = info
//    }
//
//    fun bindMedia(item: Item) {
//        media = item
//        initCheckView()
//        setImage()
//    }
//
//    private fun initCheckView() {
//        mCheckView.setCountable(mPreBindInfo.mCheckViewCountable)
//    }
//
//    fun setCheckEnabled(enabled: Boolean) {
//        mCheckView.isEnabled = enabled
//    }
//
//    fun setCheckedNum(checkedNum: Int) {
//        mCheckView.setCheckedNum(checkedNum)
//    }
//
//    fun setChecked(checked: Boolean) {
//        mCheckView.setChecked(checked)
//    }
//
//    private fun setImage() {
//        if (media.isGif) {
//            SelectionSpec.instance.imageEngine.loadGifThumbnail(context, mPreBindInfo.mResize,
//                    mPreBindInfo.mPlaceholder, mThumbnail, media.contentUri!!)
//        } else {
//            SelectionSpec.instance.imageEngine.loadThumbnail(context, mPreBindInfo.mResize,
//                    mPreBindInfo.mPlaceholder, mThumbnail, media.contentUri!!)
//        }
//    }
//
//    fun setOnMediaGridClickListener(listener: OnMediaGridClickListener) {
//        mListener = listener
//    }
//
//    fun removeOnMediaGridClickListener() {
//        mListener = null
//    }
//
//    interface OnMediaGridClickListener {
//        fun onThumbnailClicked(thumbnail: ImageView, item: Item, holder: RecyclerView.ViewHolder)
//        fun onCheckViewClicked(checkView: CheckView, item: Item, holder: RecyclerView.ViewHolder)
//    }
//
//    class PreBindInfo(internal var mResize: Int, internal var mPlaceholder: Drawable, internal var mCheckViewCountable: Boolean,
//                      internal var mViewHolder: RecyclerView.ViewHolder)
//}
