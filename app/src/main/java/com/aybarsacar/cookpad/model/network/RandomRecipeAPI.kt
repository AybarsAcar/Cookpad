package com.aybarsacar.cookpad.model.network

import com.aybarsacar.cookpad.model.entities.RandomRecipe
import com.aybarsacar.cookpad.utils.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomRecipeAPI {

  @GET(Constants.API_ENDPOINT)
  fun getRandomRecipe(
    @Query(Constants.API_KEY) apiKey: String,
    @Query(Constants.LIMIT_LICENCE) limitLicence: Boolean,
    @Query(Constants.TAGS) tags: String,
    @Query(Constants.NUMBER) number: Int,
  ): Single<RandomRecipe.Recipes>

}