package com.erank.yogappl.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.erank.yogappl.models.Event
import com.erank.yogappl.models.Lesson
import com.erank.yogappl.models.User
import com.erank.yogappl.room.dao.EventDao
import com.erank.yogappl.room.dao.LessonDao
import com.erank.yogappl.room.dao.UserDao

@Database(
    entities = [User::class, Lesson::class, Event::class],
    exportSchema = false, version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun lessonsDao(): LessonDao
    abstract fun eventsDao(): EventDao
    abstract fun usersDao(): UserDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * use application context when using this
         */
        fun getDatabase(context: Context) = INSTANCE ?: run {
            val instance = Room.databaseBuilder(
                context,
                AppDatabase::class.java, "yoga_db"
            ).build()

            INSTANCE = instance
            return instance
        }
    }
}