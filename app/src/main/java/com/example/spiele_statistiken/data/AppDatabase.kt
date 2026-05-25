package com.example.spiele_statistiken.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Spieler::class, SpielEvent::class, SpielEventTeilnehmer::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun spielerDao(): SpielerDao
    abstract fun spielEventDao(): SpielEventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "spiele_statistiken_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

