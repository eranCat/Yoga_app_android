package com.erank.yogappl.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.erank.yogappl.data.models.Event
import com.erank.yogappl.data.models.Lesson
import com.erank.yogappl.data.models.User
import com.erank.yogappl.data.room.dao.EventDao
import com.erank.yogappl.data.room.dao.LessonDao
import com.erank.yogappl.data.room.dao.UserDao

@Database(
    entities = [User::class, Lesson::class, Event::class],
    exportSchema = false, version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val lessonsDao: LessonDao
    abstract val eventsDao: EventDao
    abstract val usersDao: UserDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * use application context when using this
         */
        fun getDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "yoga_db"
            )
                .build()
        }


        private val LOCK = Any()
        operator fun invoke(context: Context) = INSTANCE ?: synchronized(LOCK) {
            INSTANCE ?: getDatabase(context).also {
                INSTANCE = it
            }
        }
    }
}