package com.aybarsacar.cookpad.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.aybarsacar.cookpad.application.CookPadApplication
import com.aybarsacar.cookpad.databinding.FragmentFavouriteRecipesBinding
import com.aybarsacar.cookpad.model.entities.Recipe
import com.aybarsacar.cookpad.view.activities.MainActivity
import com.aybarsacar.cookpad.view.adaptors.RecipeCardAdaptor
import com.aybarsacar.cookpad.viewmodel.DashboardViewModel
import com.aybarsacar.cookpad.viewmodel.RecipeViewModel
import com.aybarsacar.cookpad.viewmodel.RecipeViewModelFactory

class FavouriteRecipesFragment : Fragment() {

  private var _binding: FragmentFavouriteRecipesBinding? = null

  // inject the view models
  private val _recipeViewModel: RecipeViewModel by viewModels {
    RecipeViewModelFactory((requireActivity().application as CookPadApplication).repository)
  }


  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    _binding = FragmentFavouriteRecipesBinding.inflate(inflater, container, false)
    return _binding!!.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    _recipeViewModel.favouriteRecipesList.observe(viewLifecycleOwner, { favouriteRecipes ->

      favouriteRecipes?.let {

        // set the layout manager
        _binding!!.rvFavouriteRecipes.layoutManager = GridLayoutManager(requireActivity(), 2)

        // assign the adaptor
        val adapter = RecipeCardAdaptor(this)
        _binding!!.rvFavouriteRecipes.adapter = adapter

        if (it.isNotEmpty()) {
          _binding!!.rvFavouriteRecipes.visibility = View.VISIBLE
          _binding!!.tvFavouriteRecipe.visibility = View.GONE

          // send the list of recipes
          adapter.recipesList(it)

        } else {
          _binding!!.rvFavouriteRecipes.visibility = View.GONE
          _binding!!.tvFavouriteRecipe.visibility = View.VISIBLE
        }
      }
    })
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onResume() {
    super.onResume()

    // show the navigation when we come back
    if (requireActivity() is MainActivity) {
      (activity as MainActivity).showBottomNavigationView()
    }
  }

  /**
   * handles the navigating to the details page
   * called from the Recipe Card Adaptor
   */
  fun handleRecipeDetailsNavigation(recipe: Recipe) {
    findNavController().navigate(
      FavouriteRecipesFragmentDirections.actionNavigationFavouriteDishesToNavigationRecipeDetails(
        recipe
      )
    )

    // hide the navigation
    if (requireActivity() is MainActivity) {
      (activity as MainActivity).hideBottomNavigationView()
    }
  }
}