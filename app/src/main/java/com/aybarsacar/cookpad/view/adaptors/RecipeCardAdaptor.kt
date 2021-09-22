package com.aybarsacar.cookpad.view.adaptors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.aybarsacar.cookpad.databinding.ItemRecipeLayoutBinding
import com.aybarsacar.cookpad.model.entities.Recipe
import com.aybarsacar.cookpad.view.fragments.AllRecipesFragment
import com.bumptech.glide.Glide

class RecipeCardAdaptor(private val fragment: Fragment) : RecyclerView.Adapter<RecipeCardAdaptor.ViewHolder>() {

  class ViewHolder(view: ItemRecipeLayoutBinding) : RecyclerView.ViewHolder(view.root) {

    val ivRecipeImage = view.ivRecipeImage
    val tvTitle = view.tvTitle

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