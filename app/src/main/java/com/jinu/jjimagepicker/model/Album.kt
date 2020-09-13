package com.jinu.jjimagepicker.model

data class Album(
    val bucketId: Long,
    val bucketDisplayName: String,
    val images: ArrayList<MediaStoreImage>
)