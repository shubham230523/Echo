package com.shubhamthorat.echo.data.db

import androidx.room.*
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.shubhamthorat.echo.data.local.audiobook.AudiobookDao
import com.shubhamthorat.echo.data.local.audiobook.AudiobookEntity
import com.shubhamthorat.echo.data.local.chapter.ChapterDao
import com.shubhamthorat.echo.data.local.chapter.ChapterEntity
import com.shubhamthorat.echo.data.local.document.DocumentDao
import com.shubhamthorat.echo.data.local.document.DocumentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [TestEntity::class, ChapterEntity::class, AudiobookEntity::class, DocumentEntity::class], version = 1)
@TypeConverters(Converters::class)
@ConstructedBy(EchoDatabaseConstructor::class)
abstract class EchoDatabase : RoomDatabase() {
    abstract fun testDao(): TestDao
    abstract fun chapterDao(): ChapterDao
    abstract fun audiobookDao(): AudiobookDao
    abstract fun documentDao(): DocumentDao
}

@Dao
interface TestDao {
    @Insert
    suspend fun insert(entity: TestEntity)

    @Query("SELECT * FROM TestEntity")
    suspend fun getAll(): List<TestEntity>
}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object EchoDatabaseConstructor : RoomDatabaseConstructor<EchoDatabase> {
    override fun initialize(): EchoDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<EchoDatabase>
): EchoDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
