package com.aybarsacar.cookpad.view.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aybarsacar.cookpad.R
import com.aybarsacar.cookpad.application.CookPadApplication
import com.aybarsacar.cookpad.databinding.DialogCustomListBinding
import com.aybarsacar.cookpad.databinding.FragmentAllRecipesBinding
import com.aybarsacar.cookpad.model.entities.Recipe
import com.aybarsacar.cookpad.utils.Constants
import com.aybarsacar.cookpad.view.activities.AddUpdateRecipeActivity
import com.aybarsacar.cookpad.view.activities.MainActivity
import com.aybarsacar.cookpad.view.adaptors.ListItemAdaptor
import com.aybarsacar.cookpad.view.adaptors.RecipeCardAdaptor
import com.aybarsacar.cookpad.viewmodel.HomeViewModel
import com.aybarsacar.cookpad.viewmodel.RecipeViewModel
import com.aybarsacar.cookpad.viewmodel.RecipeViewModelFactory


class AllRecipesFragment : Fragment() {

  private var _binding: FragmentAllRecipesBinding? = null

  private lateinit var _recipeCardAdapter: RecipeCardAdaptor
  private lateinit var _customListDialog: Dialog


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
    _binding!!.rvRecipesList.layoutManager = GridLayoutManager(requireActivity(), 2)

    _recipeCardAdapter = RecipeCardAdaptor(this@AllRecipesFragment)
    _binding!!.rvRecipesList.adapter = _recipeCardAdapter


    // fetch all the recipes and observe it
    _recipeViewModel.recipesList.observe(viewLifecycleOwner) { recipes ->
      recipes?.let {

        // display the cards
        if (it.isEmpty()) {
          // we don't have any recipes
          _binding!!.rvRecipesList.visibility = View.GONE
          _binding!!.tvNoRecipesAddedYet.visibility = View.VISIBLE
        } else {
          // we have recipes to display
          // make sure set the visibility
          _binding!!.rvRecipesList.visibility = View.VISIBLE
          _binding!!.tvNoRecipesAddedYet.visibility = View.GONE

          _recipeCardAdapter.recipesList(it)

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

    return _binding!!.root
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

      R.id.action_filter_recipes -> {
        displayFilterRecipesListDialog()
        return true
      }
    }

    return super.onOptionsItemSelected(item)
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

  fun handleFilterSelection(filterItemSelection: String) {
    _customListDialog.dismiss()

    Log.i("Filter Selection", filterItemSelection)

    if (filterItemSelection == Constants.ALL_ITEMS) {
      _recipeViewModel.recipesList.observe(viewLifecycleOwner) { recipes ->
        recipes?.let {

          // display the cards
          if (it.isEmpty()) {
            // we don't have any recipes
            _binding!!.rvRecipesList.visibility = View.GONE
            _binding!!.tvNoRecipesAddedYet.visibility = View.VISIBLE
          } else {
            // we have recipes to display
            // make sure set the visibility
            _binding!!.rvRecipesList.visibility = View.VISIBLE
            _binding!!.tvNoRecipesAddedYet.visibility = View.GONE

            _recipeCardAdapter.recipesList(it)
          }
        }
      }
    } else {
      // not all items
      Log.i("Filter List", "Get filter list")
    }
  }

  private fun displayFilterRecipesListDialog() {
    _customListDialog = Dialog(requireActivity())
    val binding = DialogCustomListBinding.inflate(layoutInflater)

    _customListDialog.setContentView(binding.root)
    binding.tvTitle.text = "Select Item to filter"

    // setup the recycler view based on recipe types
    val recipeTypes = Constants.recipeTypes()
    recipeTypes.add(0, Constants.ALL_ITEMS)

    binding.rvList.layoutManager = LinearLayoutManager(requireActivity())

    val adapter = ListItemAdaptor(requireActivity(), this@AllRecipesFragment, recipeTypes, Constants.FILTER_SELECTION)

    binding.rvList.adapter = adapter

    _customListDialog.show()
  }

  /**
   * handles removing the recipe
   */
  fun handleRemoveRecipe(recipe: Recipe) {

    // build an aler dialog
    val builder = AlertDialog.Builder(requireActivity())
      .setTitle("Delete Recipe")
      .setMessage("Are you sure you want to delete ${recipe.title}?")
      .setIcon(android.R.drawable.ic_dialog_alert)
      .setPositiveButton("Yes") { dialogInterface, _ ->
        // delete recipe is the user clicks yes
        _recipeViewModel.remove(recipe)
        dialogInterface.dismiss()
      }
      .setNegativeButton("No") { dialogInterface, _ ->
        dialogInterface.dismiss()
      }

    val dialog = builder.create()
    dialog.setCancelable(false)
    dialog.show()
  }

  /**
   * handles the navigating to the details page
   * called from the Recipe Card Adaptor
   */
  fun handleRecipeDetailsNavigation(recipe: Recipe) {
    findNavController().navigate(AllRecipesFragmentDirections.actionNavigationAllRecipesToNavigationRecipeDetails(recipe))

    // hide the navigation
    if (requireActivity() is MainActivity) {
      (activity as MainActivity).hideBottomNavigationView()
    }
  }
}