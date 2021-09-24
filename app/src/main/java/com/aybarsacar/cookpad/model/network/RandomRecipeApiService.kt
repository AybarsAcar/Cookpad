package com.aybarsacar.cookpad.model.network

import com.aybarsacar.cookpad.BuildConfig
import com.aybarsacar.cookpad.model.entities.RandomRecipe
import com.aybarsacar.cookpad.utils.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RandomRecipeApiService {

  private val api = Retrofit.Builder()
    .baseUrl(Constants.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
    .build()
    .create(RandomRecipeAPI::class.java)


  fun getRandomRecipe(): Single<RandomRecipe.Recipes> {
    return api.getRandomRecipe(
      BuildConfig.SPOONACULAR_API_KEY,
      Constants.LIMIT_LICENCE_DEFAULT_VALUE,
      Constants.TAGS_DEFAULT_VALUE,
      Constants.NUMBER_DEFAULT_VALUE
    )
  }
}