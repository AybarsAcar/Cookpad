package com.aybarsacar.cookpad.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aybarsacar.cookpad.model.entities.RandomRecipe
import com.aybarsacar.cookpad.model.network.RandomRecipeApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class RandomRecipeViewModel : ViewModel() {

  private val randomRecipeApiService = RandomRecipeApiService()

  private val compositeDisposable = CompositeDisposable()

  val loadRandomRecipe = MutableLiveData<Boolean>()
  val randomRecipeResponse = MutableLiveData<RandomRecipe.Recipes>()
  val randomRecipeLoadingError = MutableLiveData<Boolean>()

  fun getRandomRecipeFromAPI() {
    loadRandomRecipe.value = true

    compositeDisposable.add(
      randomRecipeApiService.getRandomRecipe()
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(object : DisposableSingleObserver<RandomRecipe.Recipes>() {
          override fun onSuccess(t: RandomRecipe.Recipes) {
            loadRandomRecipe.value = false
            randomRecipeResponse.value = t
            randomRecipeLoadingError.value = false
          }

          override fun onError(e: Throwable) {
            loadRandomRecipe.value = false
            randomRecipeLoadingError.value = true
            e.printStackTrace()
          }

        })
    )
  }

}

