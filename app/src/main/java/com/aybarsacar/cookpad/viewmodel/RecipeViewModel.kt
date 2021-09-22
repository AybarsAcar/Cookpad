package com.aybarsacar.cookpad.viewmodel

import androidx.lifecycle.*
import com.aybarsacar.cookpad.model.database.RecipeRepository
import com.aybarsacar.cookpad.model.entities.Recipe
import kotlinx.coroutines.launch


class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {
  // cache the recipes as live data
  val recipesList: LiveData<List<Recipe>> = repository.recipesList.asLiveData()
  val favouriteRecipesList: LiveData<List<Recipe>> = repository.favouriteRecipes.asLiveData()

  fun insert(recipe: Recipe) = viewModelScope.launch {
    repository.insertRecipeDetailsData(recipe)
  }

  fun update(recipe: Recipe) = viewModelScope.launch {
    repository.updateRecipeData(recipe)
  }
}


class RecipeViewModelFactory(private val repository: RecipeRepository) : ViewModelProvider.Factory {

  override fun <T : ViewModel?> create(modelClass: Class<T>): T {

    if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {

      @Suppress("UNCHECKED_CAST")
      return RecipeViewModel(repository) as T
    }


    throw IllegalArgumentException("Unknown View Model Class")
  }

}