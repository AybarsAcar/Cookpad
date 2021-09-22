package com.aybarsacar.cookpad.model.database

import androidx.annotation.WorkerThread
import com.aybarsacar.cookpad.model.entities.Recipe
import kotlinx.coroutines.flow.Flow

/**
 * this is our repository
 */
class RecipeRepository(private val recipeDao: RecipeDao) {

  @WorkerThread
  suspend fun insertRecipeDetailsData(recipe: Recipe) {
    recipeDao.insertRecipeDetails(recipe)
  }


  val recipesList: Flow<List<Recipe>> = recipeDao.getAllRecipesList()


  @WorkerThread
  suspend fun updateRecipeData(recipe: Recipe) {
    recipeDao.updateRecipeDetails(recipe)
  }

  val favouriteRecipes: Flow<List<Recipe>> = recipeDao.getFavouriteRecipesList()
}