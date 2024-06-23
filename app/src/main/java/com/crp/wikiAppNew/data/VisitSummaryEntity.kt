package com.crp.wikiAppNew.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "visit_summary")
data class VisitSummaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val placeName: String,
    val description: String,
    val imageUrl: String,
    val visitCount: Int
)
