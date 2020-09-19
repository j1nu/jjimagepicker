package com.jinu.jjimagepicker.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jinu.jjimagepicker.JJImagePickerActivity
import com.jinu.jjimagepicker.model.Album
import com.jinu.jjimagepicker.model.MediaStoreImage
import com.jinu.jjimagepicker.widget.CheckView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    var countable: Boolean = false
    var maxSelectable: Int = 0

    private val _albums = MutableLiveData<ArrayList<Album>>()
    val albums: LiveData<ArrayList<Album>> = _albums

    private val _selectedImages = MutableLiveData<ArrayList<MediaStoreImage>>(arrayListOf())
    val selectedImages: LiveData<ArrayList<MediaStoreImage>> = _selectedImages

    var selectedAlbumIndex: Int = 0

    private var contentObserver: ContentObserver? = null

    fun setExtraSettings(countable: Boolean, maxSelectable: Int) {
        this.countable = countable
        this.maxSelectable = maxSelectable
    }

    private fun notifySelectedImagesChanged() {
        _selectedImages.value = _selectedImages.value!!
    }

    fun select(image: MediaStoreImage) {
        _selectedImages.value!!.add(image)
        notifySelectedImagesChanged()
    }

    fun deselect(image: MediaStoreImage): Int {
        val index = _selectedImages.value!!.indexOf(image)
        if (index == -1)
            return -1

        _selectedImages.value!!.removeAt(index)
        notifySelectedImagesChanged()

        return index
    }

    fun isSelected(image: MediaStoreImage): Boolean {
        return _selectedImages.value!!.contains(image)
    }

    fun isSelectable(): Boolean {
        return _selectedImages.value!!.size < maxSelectable
    }

    fun getSelectedCount(): Int {
        return _selectedImages.value!!.size
    }

    fun selectedNumOf(image: MediaStoreImage): Int {
        val index = _selectedImages.value!!.indexOf(image)
        return if (index == -1) CheckView.UNCHECKED else index + 1
    }

    fun getAlbum(index: Int): Album {
        return _albums.value!![index]
    }

    fun loadImages() {
        viewModelScope.launch {
            val albums = queryImages()
            _albums.postValue(albums)

            if (contentObserver == null) {
                contentObserver = getApplication<Application>().contentResolver.registerObserver(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                ) {
                    loadImages()
                }
            }
        }
    }

    private suspend fun queryImages(): ArrayList<Album> {
        val albumIds = arrayListOf<Long>()
        val albums = arrayListOf<Album>()
        val images = arrayListOf<MediaStoreImage>()

        withContext(Dispatchers.IO) {
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )

//            val selection = "${MediaStore.Images.Media.DATE_ADDED} >= ?"
//
//            val selectionArgs = arrayOf(
//                dateToTimestamp(day = 22, month = 10, year = 2008).toString()
//            )

            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            getApplication<Application>().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateModifiedColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val bucketIdColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
                val bucketDisplayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val dateModified =
                        Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateModifiedColumn)))
                    val displayName = cursor.getString(displayNameColumn)
                    val bucketId = cursor.getLong(bucketIdColumn)
                    val bucketDisplayName = cursor.getString(bucketDisplayNameColumn)

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val image = MediaStoreImage(id, displayName, dateModified, contentUri)
                    images += image

                    val albumIndex = albumIds.indexOf(bucketId)
                    if (albumIndex == -1) {
                        albumIds.add(bucketId)
                        albums.add(Album(bucketId, bucketDisplayName, arrayListOf(image)))
                    } else
                        albums[albumIndex].images.add(image)
                }
            }
        }

        if (albums.isNotEmpty())
            albums.add(0, Album(-1, "최근 항목", images))

        return albums
    }

    @Suppress("SameParameterValue")
    @SuppressLint("SimpleDateFormat")
    private fun dateToTimestamp(day: Int, month: Int, year: Int): Long =
        SimpleDateFormat("dd.MM.yyyy").let { formatter ->
            TimeUnit.MICROSECONDS.toSeconds(formatter.parse("$day.$month.$year")?.time ?: 0)
        }

    override fun onCleared() {
        contentObserver?.let {
            getApplication<Application>().contentResolver.unregisterContentObserver(it)
        }
    }
}

private fun ContentResolver.registerObserver(
    uri: Uri,
    observer: (selfChange: Boolean) -> Unit
): ContentObserver {
    val contentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean) {
            observer(selfChange)
        }
    }
    registerContentObserver(uri, true, contentObserver)
    return contentObserver
}