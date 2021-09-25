package com.aybarsacar.cookpad.view.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.aybarsacar.cookpad.R
import com.aybarsacar.cookpad.application.CookPadApplication
import com.aybarsacar.cookpad.databinding.FragmentRecipeDetailsBinding
import com.aybarsacar.cookpad.model.entities.Recipe
import com.aybarsacar.cookpad.utils.Constants
import com.aybarsacar.cookpad.viewmodel.RecipeViewModel
import com.aybarsacar.cookpad.viewmodel.RecipeViewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.lang.Exception
import java.util.*


/**
 * fragment to display the Recipe details
 */
class RecipeDetailsFragment : Fragment() {

  // view binding
  private var _binding: FragmentRecipeDetailsBinding? = null

  // view models to update the data
  val _recipeViewModel: RecipeViewModel by viewModels {
    RecipeViewModelFactory((requireActivity().application as CookPadApplication).repository)
  }

  // caches
  private var _selectedRecipeDetails: Recipe? = null


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setHasOptionsMenu(true)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

    // inflate the menu layout
    inflater.inflate(R.menu.menu_share, menu)

    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {

    if (item.itemId == R.id.action_share_recipe) {
      // share recipe
      val type = "text/plain"
      val subject = "Check out the Recipe"
      var extraText = ""
      val shareWith = "Share with"

      _selectedRecipeDetails?.let {
        var image = ""
        if (it.imageSource == Constants.RECIPE_IMAGE_SOURCE_ONLINE) {
          image = it.image
        }

        var cookingInstructions = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
          cookingInstructions = Html.fromHtml(it.instructions, Html.FROM_HTML_MODE_COMPACT).toString()
        } else {
          @Suppress("DEPRECATION")
          cookingInstructions = Html.fromHtml(it.instructions).toString()
        }

        extraText = "$image \n" +
            "\n Title: ${it.title} \n\n Type: ${it.type} \n\n" +
            "Category: ${it.category}" +
            "\n\n Ingredients: \n ${it.ingredients} \n\n Instructions " +
            "to Cook: \n $cookingInstructions" +
            "\n\n Time required to cook the recipe is approx ${it.cookingTime} minutes"
      }

      val intent = Intent(Intent.ACTION_SEND)
      intent.type = type
      intent.putExtra(Intent.EXTRA_SUBJECT, subject)
      intent.putExtra(Intent.EXTRA_TEXT, extraText)
      startActivity(Intent.createChooser(intent, shareWith))

      return true
    }

    return super.onOptionsItemSelected(item)
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

    _selectedRecipeDetails = args.recipeDetails


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
      _binding!!.tvCookingTime.text = "Cooking Time: ${it.cookingTime}"

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        _binding!!.tvCookingDirection.text = Html.fromHtml(it.instructions, Html.FROM_HTML_MODE_COMPACT).toString()
      } else {
        @Suppress("DEPRECATION")
        _binding!!.tvCookingDirection.text = Html.fromHtml(it.instructions).toString()
      }

      // make sure to update the icon
      setFavouriteButtonIcon(args, view, false)
    }

    _binding!!.ivLikeRecipe.setOnClickListener {

      // set to the opposite of the previous favourite state
      args.recipeDetails.favouriteRecipe = !args.recipeDetails.favouriteRecipe

      // update in db
      _recipeViewModel.update(args.recipeDetails)

      // make sure to update the icon
      setFavouriteButtonIcon(args, view, true)
    }
  }

  override fun onDestroy() {
    super.onDestroy()

    _binding = null
  }


  private fun setFavouriteButtonIcon(args: RecipeDetailsFragmentArgs, view: View, isRecipeUpdated: Boolean) {
    if (args.recipeDetails.favouriteRecipe) {
      _binding!!.ivLikeRecipe.setImageDrawable(
        ContextCompat.getDrawable(
          requireActivity(), R.drawable.ic_favorite_selected
        )
      )

      if (isRecipeUpdated) {
        Snackbar.make(view, "Recipe added to your favourites!", Snackbar.LENGTH_LONG).show()
      }
    } else {
      _binding!!.ivLikeRecipe.setImageDrawable(
        ContextCompat.getDrawable(
          requireActivity(), R.drawable.ic_favorite_unselected
        )
      )

      if (isRecipeUpdated) {
        Snackbar.make(view, "Recipe removed from your favourites", Snackbar.LENGTH_LONG).show()
      }
    }
  }
}