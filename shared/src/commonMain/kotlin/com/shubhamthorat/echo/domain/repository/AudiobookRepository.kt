package com.shubhamthorat.echo.domain.repository

import com.shubhamthorat.echo.domain.model.Audiobook
import kotlinx.coroutines.flow.Flow

interface AudiobookRepository {
    suspend fun insertAudiobook(audiobook: Audiobook)
    suspend fun updateAudiobook(audiobook: Audiobook)
    suspend fun getAudiobookById(id: String): Audiobook?
    fun observeAllAudiobooks(): Flow<List<Audiobook>>
    suspend fun deleteAudiobook(audiobook: Audiobook)
}
