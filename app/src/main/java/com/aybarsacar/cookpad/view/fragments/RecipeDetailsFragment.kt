package com.aybarsacar.cookpad.view.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.aybarsacar.cookpad.R
import com.aybarsacar.cookpad.databinding.FragmentRecipeDetailsBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.IOException
import java.lang.Exception
import java.util.*


/**
 * fragment to display the Recipe details
 */
class RecipeDetailsFragment : Fragment() {

  private var _binding: FragmentRecipeDetailsBinding? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = FragmentRecipeDetailsBinding.inflate(inflater, container, false)

    return _binding!!.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)


    // get the navigation arguments
    val args: RecipeDetailsFragmentArgs by navArgs()
    val recipe = args.recipeDetails

    recipe.let {
      try {
        Glide.with(requireActivity())
          .load(it.image)
          .centerCrop()
          .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
              e: GlideException?,
              model: Any?,
              target: Target<Drawable>?,
              isFirstResource: Boolean
            ): Boolean {
              Log.e("Image", "Error loading image", e)
              return false
            }

            override fun onResourceReady(
              resource: Drawable?,
              model: Any?,
              target: Target<Drawable>?,
              dataSource: DataSource?,
              isFirstResource: Boolean
            ): Boolean {
              // set the background colour to the prominent colour of the recipe image
              Palette.from(resource!!.toBitmap()).generate { palette ->
                val colour = palette?.vibrantSwatch?.rgb ?: 0
                _binding!!.rlRecipeDetailMain.setBackgroundColor(colour)
              }

              return false
            }

          })
          .into(_binding!!.ivRecipeImage)

      } catch (e: IOException) {
        e.printStackTrace()
      }

      _binding!!.tvTitle.text = it.title
      _binding!!.tvType.text = it.type.capitalize(Locale.ROOT)
      _binding!!.tvCategory.text = it.category
      _binding!!.tvIngredients.text = it.ingredients
      _binding!!.tvCookingDirection.text = it.instructions
      _binding!!.tvCookingTime.text = "Cooking Time: ${it.cookingTime}"
    }
  }

  override fun onDestroy() {
    super.onDestroy()

    _binding = null
  }

}