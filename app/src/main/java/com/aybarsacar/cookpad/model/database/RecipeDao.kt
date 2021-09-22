package com.aybarsacar.cookpad.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
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
}