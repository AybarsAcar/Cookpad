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
import com.aybarsacar.cookpad.R
import com.aybarsacar.cookpad.application.CookPadApplication
import com.aybarsacar.cookpad.databinding.FragmentAllRecipesBinding
import com.aybarsacar.cookpad.view.activities.AddUpdateRecipeActivity
import com.aybarsacar.cookpad.viewmodel.HomeViewModel
import com.aybarsacar.cookpad.viewmodel.RecipeViewModel
import com.aybarsacar.cookpad.viewmodel.RecipeViewModelFactory


class AllRecipesFragment : Fragment() {

  private lateinit var homeViewModel: HomeViewModel
  private var _binding: FragmentAllRecipesBinding? = null

  // This property is only valid between onCreateView and
  // onDestroyView.
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

    // fetch all the recipes and observe it
    _recipeViewModel.recipesList.observe(viewLifecycleOwner) { recipes ->
      recipes?.let {
        for (recipe in it) {
          Log.i("Recipe Title", recipe.title)
        }
      }
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    homeViewModel =
      ViewModelProvider(this).get(HomeViewModel::class.java)

    _binding = FragmentAllRecipesBinding.inflate(inflater, container, false)
    val root: View = binding.root

    val textView: TextView = binding.textHome
    homeViewModel.text.observe(viewLifecycleOwner, Observer {
      textView.text = it
    })
    return root
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