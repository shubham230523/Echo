package com.shubhamthorat.echo.data.local.vector

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface VectorDao {
    @Insert
    suspend fun insertAll(vectors: List<VectorEntity>)

    @Query("SELECT * FROM document_vectors WHERE documentId = :documentId")
    suspend fun getVectorsByDocument(documentId: String): List<VectorEntity>

    @Query("SELECT * FROM document_vectors")
    suspend fun getAllVectors(): List<VectorEntity>

    @Query("DELETE FROM document_vectors WHERE documentId = :documentId")
    suspend fun deleteByDocument(documentId: String)

    @Query("DELETE FROM document_vectors")
    suspend fun clearAll()
}
