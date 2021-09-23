package com.aybarsacar.cookpad.utils

object Constants {
  const val RECIPE_TYPE: String = "RecipeType"
  const val RECIPE_CATEGORY: String = "RecipeCategory"
  const val RECIPE_COOKING_TIME: String = "RecipeCookingTime"

  // multiple sources we can get our images from
  const val RECIPE_IMAGE_SOURCE_LOCAL: String = "Local"
  const val RECIPE_IMAGE_SOURCE_ONLINE: String = "Online"

  const val ALL_ITEMS: String = "All"
  const val FILTER_SELECTION: String = "FilterSelection"

  const val EXTRA_RECIPE_DETAILS: String = "RecipeDetails"

  fun recipeTypes(): ArrayList<String> {
    return arrayListOf(
      "breakfast",
      "lunch",
      "snacks",
      "dinner",
      "salads",
      "side dish",
      "dessert",
      "other",
    )
  }

  fun recipeCategories(): ArrayList<String> {
    return arrayListOf(
      "Pizza",
      "BBQ",
      "Bakery",
      "Burger",
      "Cafe",
      "Desert",
      "Drinks",
      "Hot Dogs",
      "Juices",
      "Sandwich",
      "Tea & Coffee",
      "Wraps",
      "Other",
    )
  }

  fun recipeCookingTimes(): ArrayList<String> {
    return arrayListOf(
      "10",
      "15",
      "20",
      "30",
      "45",
      "50",
      "60",
      "90",
      "120",
      "150",
      "180",
    )
  }
}