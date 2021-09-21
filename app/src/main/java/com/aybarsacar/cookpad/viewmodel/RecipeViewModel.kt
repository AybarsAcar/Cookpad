package com.aybarsacar.cookpad.viewmodel

import androidx.lifecycle.*
import com.aybarsacar.cookpad.model.database.RecipeRepository
import com.aybarsacar.cookpad.model.entities.Recipe
import kotlinx.coroutines.launch


class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

  fun insert(recipe: Recipe) = viewModelScope.launch {
    repository.insertRecipeDetailsData(recipe)
  }

  // cache the recipes as live data
  val recipesList: LiveData<List<Recipe>> = repository.recipesList.asLiveData()
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