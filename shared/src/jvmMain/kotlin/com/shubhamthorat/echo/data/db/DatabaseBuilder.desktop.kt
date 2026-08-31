package com.shubhamthorat.echo.data.db

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

fun getDatabaseBuilder(): RoomDatabase.Builder<EchoDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "echo.db")
    return Room.databaseBuilder<EchoDatabase>(
        name = dbFile.absolutePath,
    )
}
