package com.shubhamthorat.echo.data.repository

import com.shubhamthorat.echo.data.local.audiobook.AudiobookDao
import com.shubhamthorat.echo.data.local.audiobook.toDomain
import com.shubhamthorat.echo.data.local.audiobook.toEntity
import com.shubhamthorat.echo.domain.model.Audiobook
import com.shubhamthorat.echo.domain.repository.AudiobookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomAudiobookRepository(
    private val audiobookDao: AudiobookDao
) : AudiobookRepository {

    override suspend fun insertAudiobook(audiobook: Audiobook) {
        audiobookDao.insertAudiobook(audiobook.toEntity())
    }

    override suspend fun updateAudiobook(audiobook: Audiobook) {
        audiobookDao.updateAudiobook(audiobook.toEntity())
    }

    override suspend fun getAudiobookById(id: String): Audiobook? {
        return audiobookDao.getAudiobookById(id)?.toDomain()
    }

    override fun observeAllAudiobooks(): Flow<List<Audiobook>> {
        return audiobookDao.observeAllAudiobooks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deleteAudiobook(audiobook: Audiobook) {
        audiobookDao.deleteAudiobook(audiobook.toEntity())
    }
}
