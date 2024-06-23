package com.crp.wikiAppNew.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val coordinates: String,
    val dateTime: String,
    val name: String,
    val Extract: String?,
    val Url: String?,
    val placeName: String
)
