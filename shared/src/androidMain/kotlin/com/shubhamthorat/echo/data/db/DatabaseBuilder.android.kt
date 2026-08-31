package com.shubhamthorat.echo.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<EchoDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("echo.db")
    return Room.databaseBuilder<EchoDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
