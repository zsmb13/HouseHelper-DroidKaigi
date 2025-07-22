package com.kotlinconf.workshop.househelper.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.kotlinconf.workshop.househelper.database.dao.AreaDao
import com.kotlinconf.workshop.househelper.database.dao.DeviceDao
import com.kotlinconf.workshop.househelper.database.entities.AreaEntity
import com.kotlinconf.workshop.househelper.database.entities.DeviceEntity

@Database(
    entities = [
        AreaEntity::class,
        DeviceEntity::class
    ],
    version = 3,
    exportSchema = false,
)
@ConstructedBy(value = AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roomDao(): AreaDao
    abstract fun deviceDao(): DeviceDao

    companion object {
        const val DATABASE_NAME = "house_helper_db"
    }
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
