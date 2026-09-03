package com.shubhamthorat.echo.data.local.vector

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "document_vectors")
data class VectorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val documentId: String,
    val chunkText: String,
    val embedding: List<Float>
)
