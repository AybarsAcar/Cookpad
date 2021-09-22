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
import com.aybarsacar.cookpad.application.CookPadApplication
import com.aybarsacar.cookpad.databinding.FragmentFavouriteRecipesBinding
import com.aybarsacar.cookpad.viewmodel.DashboardViewModel
import com.aybarsacar.cookpad.viewmodel.RecipeViewModel
import com.aybarsacar.cookpad.viewmodel.RecipeViewModelFactory

class FavouriteRecipesFragment : Fragment() {

  private lateinit var dashboardViewModel: DashboardViewModel
  private var _binding: FragmentFavouriteRecipesBinding? = null

  // inject the view models
  private val _recipeViewModel: RecipeViewModel by viewModels {
    RecipeViewModelFactory((requireActivity().application as CookPadApplication).repository)
  }

  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    dashboardViewModel =
      ViewModelProvider(this).get(DashboardViewModel::class.java)

    _binding = FragmentFavouriteRecipesBinding.inflate(inflater, container, false)
    val root: View = binding.root

    val textView: TextView = binding.textDashboard
    dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
      textView.text = it
    })
    return root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    _recipeViewModel.favouriteRecipesList.observe(viewLifecycleOwner, { favouriteRecipes ->

      favouriteRecipes?.let {
        if (it.isNotEmpty()) {
          for (recipe in it) {
            println("Favourite Recipe: ${recipe.title}")
          }
        } else {
          println("No Favourite Recipe")
        }
      }

    })

  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}