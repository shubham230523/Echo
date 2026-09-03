package com.shubhamthorat.echo.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)
