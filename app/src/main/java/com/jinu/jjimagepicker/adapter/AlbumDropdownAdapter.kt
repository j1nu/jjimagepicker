package com.jinu.jjimagepicker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jinu.jjimagepicker.R
import com.jinu.jjimagepicker.model.Album
import kotlinx.android.synthetic.main.item_album_dropdown.view.*

class AlbumDropdownAdapter(context: Context, private val albums: ArrayList<Album>, private val onClick: (Album) -> Unit) : ArrayAdapter<Album>(context, R.layout.item_album_dropdown) {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return albums.size
    }

    override fun getItem(p0: Int): Album {
        return albums[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, parent: ViewGroup): View {
        val view = p1 ?: inflater.inflate(R.layout.item_album_dropdown, parent, false)

        view.setOnClickListener {
            onClick(albums[p0])
        }

        Glide.with(view.album_cover)
            .load(albums[p0].images[0].contentUri)
            .thumbnail(0.33f)
            .centerCrop()
            .into(view.album_cover)

        view.album_name.text = albums[p0].bucketDisplayName
        view.album_media_count.text = albums[p0].images.size.toString()

        return view
    }
}