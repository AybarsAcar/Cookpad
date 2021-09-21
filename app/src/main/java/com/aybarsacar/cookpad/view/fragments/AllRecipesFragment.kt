package com.aybarsacar.cookpad.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.aybarsacar.cookpad.R
import com.aybarsacar.cookpad.application.CookPadApplication
import com.aybarsacar.cookpad.databinding.FragmentAllRecipesBinding
import com.aybarsacar.cookpad.view.activities.AddUpdateRecipeActivity
import com.aybarsacar.cookpad.view.adaptors.RecipeCardAdaptor
import com.aybarsacar.cookpad.viewmodel.HomeViewModel
import com.aybarsacar.cookpad.viewmodel.RecipeViewModel
import com.aybarsacar.cookpad.viewmodel.RecipeViewModelFactory


class AllRecipesFragment : Fragment() {

  private var _binding: FragmentAllRecipesBinding? = null
  private val binding get() = _binding!!


  // setup the view model to query
  private val _recipeViewModel: RecipeViewModel by viewModels {
    RecipeViewModelFactory((requireActivity().application as CookPadApplication).repository)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // options menu at the top will appear
    setHasOptionsMenu(true)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // now _binding is available
    binding.rvRecipesList.layoutManager = GridLayoutManager(requireActivity(), 2)

    val recipeCardAdapter = RecipeCardAdaptor(this@AllRecipesFragment)
    binding.rvRecipesList.adapter = recipeCardAdapter


    // fetch all the recipes and observe it
    _recipeViewModel.recipesList.observe(viewLifecycleOwner) { recipes ->
      recipes?.let {

        // display the cards
        if (it.isEmpty()) {
          // we don't have any recipes
          binding.rvRecipesList.visibility = View.GONE
          binding.tvNoRecipesAddedYet.visibility = View.VISIBLE
        } else {
          // we have recipes to display
          // make sure set the visibility
          binding.rvRecipesList.visibility = View.VISIBLE
          binding.tvNoRecipesAddedYet.visibility = View.GONE

          recipeCardAdapter.recipesList(it)

        }
      }
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    // set up the binding inside of a fragment
    _binding = FragmentAllRecipesBinding.inflate(inflater, container, false)

    return binding.root
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

    inflater.inflate(R.menu.menu_all_recipes, menu)

    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {

    when (item.itemId) {

      R.id.action_add_recipe -> {
        startActivity(Intent(requireActivity(), AddUpdateRecipeActivity::class.java))
        return true
      }
    }

    return super.onOptionsItemSelected(item)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}