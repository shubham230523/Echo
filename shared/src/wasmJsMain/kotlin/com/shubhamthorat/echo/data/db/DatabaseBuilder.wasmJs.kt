package com.shubhamthorat.echo.data.db

import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(): RoomDatabase.Builder<EchoDatabase> {
    return Room.inMemoryDatabaseBuilder<EchoDatabase>()
}
