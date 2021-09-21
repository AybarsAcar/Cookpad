package com.aybarsacar.cookpad.application

import android.app.Application
import com.aybarsacar.cookpad.model.database.CookPadRoomDatabase
import com.aybarsacar.cookpad.model.database.RecipeRepository


class CookPadApplication : Application() {

  // add our repositories and databases like a dependancy injection container
  // we only want the database and repository is when needed
  private val database by lazy {
    CookPadRoomDatabase.getDatabase(this@CookPadApplication)
  }

  val repository by lazy {
    RecipeRepository(database.recipeDao())
  }
}