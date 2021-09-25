package com.aybarsacar.cookpad.view.fragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.aybarsacar.cookpad.R
import com.aybarsacar.cookpad.application.CookPadApplication
import com.aybarsacar.cookpad.databinding.FragmentRandomRecipeBinding
import com.aybarsacar.cookpad.model.entities.RandomRecipe
import com.aybarsacar.cookpad.model.entities.Recipe
import com.aybarsacar.cookpad.utils.Constants
import com.aybarsacar.cookpad.viewmodel.RandomRecipeViewModel
import com.aybarsacar.cookpad.viewmodel.RecipeViewModel
import com.aybarsacar.cookpad.viewmodel.RecipeViewModelFactory
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar


class RandomRecipeFragment : Fragment() {

  private var _binding: FragmentRandomRecipeBinding? = null

  // view models
  lateinit var _randomRecipeViewModel: RandomRecipeViewModel

  // view caches
  private var _loadingDialog: Dialog? = null


  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

    _binding = FragmentRandomRecipeBinding.inflate(inflater, container, false)

    return _binding!!.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // inject viw model
    _randomRecipeViewModel = ViewModelProvider(this).get(RandomRecipeViewModel::class.java)

    // load a random dish on a view created
    _randomRecipeViewModel.getRandomRecipeFromAPI()
    randomRecipeViewModelObserver()

    _binding!!.rlRecipeRandomMain.setOnRefreshListener {
      _randomRecipeViewModel.getRandomRecipeFromAPI()
    }
  }

  private fun randomRecipeViewModelObserver() {
    _randomRecipeViewModel.randomRecipeResponse.observe(viewLifecycleOwner, { randomRecipeResponse ->

      randomRecipeResponse?.let {

        if (_binding!!.rlRecipeRandomMain.isRefreshing) {
          _binding!!.rlRecipeRandomMain.isRefreshing = false
        }

        handleRandomRecipeResponseUI(randomRecipeResponse.recipes[0])
      }
    })

    _randomRecipeViewModel.randomRecipeLoadingError.observe(viewLifecycleOwner, { dataError ->

      dataError?.let {

        if (_binding!!.rlRecipeRandomMain.isRefreshing) {
          _binding!!.rlRecipeRandomMain.isRefreshing = false
        }

        println("Error $dataError")
      }
    })

    _randomRecipeViewModel.loadRandomRecipe.observe(viewLifecycleOwner, { loadRandomRecipe ->

      loadRandomRecipe?.let {
        // loading state
        if (loadRandomRecipe && !_binding!!.rlRecipeRandomMain.isRefreshing) {
          displayLoadingDialog()
        } else {
          hideLoadingDialog()
        }
      }
    })
  }


  private fun handleRandomRecipeResponseUI(recipe: RandomRecipe.Recipe) {
    Glide.with(requireActivity())
      .load(recipe.image)
      .centerCrop()
      .into(_binding!!.ivRecipeImage)

    _binding!!.tvTitle.text = recipe.title

    var recipeType = "other"

    if (recipe.dishTypes.isEmpty()) {
      recipeType = recipe.dishTypes[0]
    }

    _binding!!.tvType.text = recipeType

    var ingredients = ""
    for (value in recipe.extendedIngredients) {

      if (ingredients.isEmpty()) {
        ingredients = value.original
      } else {
        ingredients = ingredients + "\n" + value.original
      }
    }

    _binding!!.ivLikeRecipe.setImageDrawable(
      ContextCompat.getDrawable(requireActivity(), R.drawable.ic_favorite_unselected)
    )

    var addedToFavourites = false

    _binding!!.tvIngredients.text = ingredients

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      _binding!!.tvCookingDirection.text = Html.fromHtml(recipe.instructions, Html.FROM_HTML_MODE_COMPACT)
    } else {
      @Suppress("DEPRECATION")
      _binding!!.tvCookingDirection.text = Html.fromHtml(recipe.instructions)
    }

    _binding!!.tvCookingTime.text = "Cooking Time: ${recipe.readyInMinutes}"

    // we will save the recipe into our on machine sqlite database
    _binding!!.ivLikeRecipe.setOnClickListener {

      if (addedToFavourites) {

        Snackbar.make(requireView(), "Recipe already added to favourites", Snackbar.LENGTH_SHORT).show()

      } else {

        val recipeToInsert = Recipe(
          recipe.image,
          Constants.RECIPE_IMAGE_SOURCE_ONLINE,
          recipe.title,
          recipeType,
          "Other",
          ingredients,
          recipe.readyInMinutes.toString(),
          recipe.instructions,
          true,
        )

        val recipeViewModel: RecipeViewModel by viewModels {
          RecipeViewModelFactory((requireActivity().application as CookPadApplication).repository)
        }

        recipeViewModel.insert(recipeToInsert)

        addedToFavourites = true

        _binding!!.ivLikeRecipe.setImageDrawable(
          ContextCompat.getDrawable(
            requireActivity(),
            R.drawable.ic_favorite_selected
          )
        )

        Snackbar.make(requireView(), "Recipe added to favourites", Snackbar.LENGTH_SHORT).show()
      }
    }
  }

  private fun displayLoadingDialog() {
    _loadingDialog = Dialog(requireActivity())

    _loadingDialog?.let {
      it.setContentView(R.layout.dialog_custom_loading)
      it.show()
    }
  }

  private fun hideLoadingDialog() {
    _loadingDialog?.dismiss()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}