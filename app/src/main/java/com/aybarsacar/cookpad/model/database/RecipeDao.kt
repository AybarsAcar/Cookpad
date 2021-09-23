package com.aybarsacar.cookpad.model.database

import androidx.room.*
import com.aybarsacar.cookpad.model.entities.Recipe
import kotlinx.coroutines.flow.Flow


/**
 * this is our repository interface
 */
@Dao
interface RecipeDao {

  @Insert
  suspend fun insertRecipeDetails(recipe: Recipe)


  @Query("select * from recipe_table order by id")
  fun getAllRecipesList(): Flow<List<Recipe>>


  @Update
  suspend fun updateRecipeDetails(recipe: Recipe)


  @Query("select * from recipe_table where favourite_recipe = 1 order by id")
  fun getFavouriteRecipesList(): Flow<List<Recipe>>


  @Delete
  suspend fun deleteRecipe(recipe: Recipe)


  @Query("select * from recipe_table where type = :filter")
  fun getFilteredRecipesList(filter: String): Flow<List<Recipe>>
}