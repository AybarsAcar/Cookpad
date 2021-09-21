package com.aybarsacar.cookpad.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aybarsacar.cookpad.model.entities.Recipe


@Database(entities = [Recipe::class], version = 1)
abstract class CookPadRoomDatabase : RoomDatabase() {

  // we need to set this up fore each repository we have
  abstract fun recipeDao(): RecipeDao

  companion object {

    @Volatile
    private var INSTANCE: CookPadRoomDatabase? = null

    fun getDatabase(context: Context): CookPadRoomDatabase {

      // if the INSTANCE is not null, then return it
      // if it is null, then create the database
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          CookPadRoomDatabase::class.java,
          "fav_dish_database"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        // return instance
        instance
      }
    }
  }
}