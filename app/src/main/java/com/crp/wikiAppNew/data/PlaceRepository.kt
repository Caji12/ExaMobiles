package com.crp.wikiAppNew.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaceRepository(private val placeDao: PlaceDao) {

    suspend fun insertPlace(place: PlaceEntity) {
        withContext(Dispatchers.IO) {
            placeDao.insertPlace(place)
        }
    }

    suspend fun getAllPlaces(): List<PlaceEntity> {
        return withContext(Dispatchers.IO) {
            placeDao.getAllPlaces()
        }
    }
}

