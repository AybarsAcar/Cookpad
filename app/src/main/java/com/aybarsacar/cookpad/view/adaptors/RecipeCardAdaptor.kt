package com.aybarsacar.cookpad.view.adaptors

import android.content.Intent
import android.provider.SyncStateContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.aybarsacar.cookpad.R
import com.aybarsacar.cookpad.databinding.ItemRecipeLayoutBinding
import com.aybarsacar.cookpad.model.entities.Recipe
import com.aybarsacar.cookpad.utils.Constants
import com.aybarsacar.cookpad.view.activities.AddUpdateRecipeActivity
import com.aybarsacar.cookpad.view.fragments.AllRecipesFragment
import com.aybarsacar.cookpad.view.fragments.FavouriteRecipesFragment
import com.bumptech.glide.Glide

class RecipeCardAdaptor(private val fragment: Fragment) : RecyclerView.Adapter<RecipeCardAdaptor.ViewHolder>() {

  class ViewHolder(view: ItemRecipeLayoutBinding) : RecyclerView.ViewHolder(view.root) {

    val ivRecipeImage = view.ivRecipeImage
    val tvTitle = view.tvTitle
    val buttonMore = view.btnMore

  }


  // we will use this to cache the recipes
  private var _recipes: List<Recipe> = listOf()


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

    val binding = ItemRecipeLayoutBinding.inflate(LayoutInflater.from(fragment.context), parent, false)

    return ViewHolder(binding)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val recipe = _recipes[position]

    Glide.with(fragment)
      .load(recipe.image)
      .into(holder.ivRecipeImage)

    holder.tvTitle.text = recipe.title


    holder.itemView.setOnClickListener {
      if (fragment is AllRecipesFragment) {
        // handle navigating to the details page
        fragment.handleRecipeDetailsNavigation(recipe)
      }

      if (fragment is FavouriteRecipesFragment) {
        fragment.handleRecipeDetailsNavigation(recipe)
      }
    }

    holder.buttonMore.setOnClickListener {
      // we will display the popup
      val popup = PopupMenu(fragment.context, holder.buttonMore)

      // inflate the popup menu
      popup.menuInflater.inflate(R.menu.menu_adapter, popup.menu)

      // now set click listeners to the items
      popup.setOnMenuItemClickListener {
        if (it.itemId == R.id.action_edit_recipe) {

          // go to add update recipe activity
          val intent = Intent(fragment.requireActivity(), AddUpdateRecipeActivity::class.java)
          intent.putExtra(Constants.EXTRA_RECIPE_DETAILS, recipe) // pass in the recipe
          fragment.requireActivity().startActivity(intent)

        } else if (it.itemId == R.id.action_delete_recipe) {

          // delete the recipe if the user is on all recipes fragment
          if (fragment is AllRecipesFragment) {
            fragment.handleRemoveRecipe(recipe)
          }
        }

        true
      }

      popup.show()
    }

    if (fragment is AllRecipesFragment) {
      holder.buttonMore.visibility = View.VISIBLE
    } else if (fragment is FavouriteRecipesFragment) {
      holder.buttonMore.visibility = View.GONE

    }
  }

  override fun getItemCount(): Int {
    return _recipes.size
  }

  fun recipesList(recipes: List<Recipe>) {
    _recipes = recipes

    // notify the registered observers that are observing this data
    // so the UI will update accordingly
    notifyDataSetChanged()
  }
}