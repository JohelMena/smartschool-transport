package com.johel.smartschoolapp.Db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [StudentEntity::class],   // luego agregamos más entidades aquí
    version = 1,
    exportSchema = false
)
abstract class SmartSchoolDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    companion object {
        @Volatile
        private var INSTANCE: SmartSchoolDatabase? = null

        fun getInstance(context: Context): SmartSchoolDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmartSchoolDatabase::class.java,
                    "smartschool_db"
                )
                    .fallbackToDestructiveMigration() // simple para desarrollo
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
