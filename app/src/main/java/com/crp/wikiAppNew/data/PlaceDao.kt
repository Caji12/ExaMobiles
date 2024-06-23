package com.crp.wikiAppNew.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlaceDao {
    @Insert
    fun insertPlace(place: PlaceEntity)

    @Update
    fun updatePlace(place: PlaceEntity)

    @Insert
    fun insertVisitSummary(visitSummary: VisitSummaryEntity)

    @Update
    fun updateVisitSummary(visitSummary: VisitSummaryEntity)

    @Query("SELECT * FROM visit_summary WHERE placename = :placeName")
    fun getVisitSummaryByPlaceName(placeName: String): VisitSummaryEntity?

    @Query("SELECT * FROM visit_summary ORDER BY visitcount DESC LIMIT :limit")
    fun getTopVisitedPlaces(limit: Int): List<VisitSummaryEntity>

    @Query("SELECT * FROM places WHERE placeName = :placeName")
    fun getPlaceByName(placeName: String): PlaceEntity?

    @Query("SELECT * FROM places")
    fun getAllPlaces(): List<PlaceEntity>
}
