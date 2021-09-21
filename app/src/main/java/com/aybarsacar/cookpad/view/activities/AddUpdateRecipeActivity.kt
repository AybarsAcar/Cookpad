package com.aybarsacar.cookpad.view.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.BoringLayout.make
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.aybarsacar.cookpad.R
import com.aybarsacar.cookpad.application.CookPadApplication
import com.aybarsacar.cookpad.databinding.ActivityAddUpdateRecipeBinding
import com.aybarsacar.cookpad.databinding.DialogCustomImageSelectionBinding
import com.aybarsacar.cookpad.databinding.DialogCustomListBinding
import com.aybarsacar.cookpad.model.entities.Recipe
import com.aybarsacar.cookpad.utils.Constants
import com.aybarsacar.cookpad.view.adaptors.ListItemAdaptor
import com.aybarsacar.cookpad.viewmodel.RecipeViewModel
import com.aybarsacar.cookpad.viewmodel.RecipeViewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.*


/**
 * both adding and updating the new Recipe
 */
class AddUpdateRecipeActivity : AppCompatActivity(), View.OnClickListener {

  companion object {
    private const val IMAGE_DIRECTORY = "CookPod"
  }

  // inject the view models
  private val _recipeViewModel: RecipeViewModel by viewModels {
    RecipeViewModelFactory((application as CookPadApplication).repository)
  }


  // view binding variable
  private lateinit var _binding: ActivityAddUpdateRecipeBinding

  // private variables
  private var _imagePath: String = ""
  private lateinit var _listDialog: Dialog

  // register for activity result for both image and camera
  private val getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

    if (result.resultCode == Activity.RESULT_OK) {
      try {
        // get image from user gallery
        val selectedImageUri: Uri? = result.data?.data

        if (selectedImageUri != null) {

//          _binding.ivRecipeImage.setImageURI(selectedImage)
          // using glide instead
          Glide.with(this)
            .load(selectedImageUri)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Drawable> {
              override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
              ): Boolean {
                Log.e("TAG", "Error Loading image", e)
                return false
              }

              override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
              ): Boolean {
                resource?.let {
                  val bitmap: Bitmap = resource.toBitmap()
                  _imagePath = saveImageToInternalStorage(bitmap)
                }

                return false
              }

            })
            .into(_binding.ivRecipeImage)

          _binding.ivAddRecipeImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_edit_24))

        } else {
          // handling taking images with camera
          result.data?.let {
            val bitmap: Bitmap = result.data?.extras?.get("data") as Bitmap
//            _binding.ivRecipeImage.setImageBitmap(bitmap)

            // using glide instead
            Glide.with(this).load(bitmap).centerCrop().into(_binding.ivRecipeImage)

            // save the image
            _imagePath = saveImageToInternalStorage(bitmap)

            Toast.makeText(this@AddUpdateRecipeActivity, _imagePath, Toast.LENGTH_LONG).show()


            _binding.ivAddRecipeImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_edit_24))
          }
        }
      } catch (e: Exception) {
        Log.e("Error", e.localizedMessage)
      }
    } else if (result.resultCode == Activity.RESULT_CANCELED) {
      Log.e("Cancelled", "User cancelled image celection")
    }

  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // bind the view
    _binding = ActivityAddUpdateRecipeBinding.inflate(layoutInflater)
    setContentView(_binding.root)

    setupActionBar()

    // bind onclick listeners to our clickables on our view
    _binding.ivAddRecipeImage.setOnClickListener(this)
    _binding.etType.setOnClickListener(this)
    _binding.etCategory.setOnClickListener(this)
    _binding.etCookingTime.setOnClickListener(this)
    _binding.btnAddDish.setOnClickListener(this)
  }


  private fun setupActionBar() {
    setSupportActionBar(_binding.toolbarAddRecipeActivity)
    supportActionBar?.setDisplayHomeAsUpEnabled(true) // allow the back button

    _binding.toolbarAddRecipeActivity.setNavigationOnClickListener {
      onBackPressed() // replicate the back button
    }
  }

  private fun displayImageSelectionDialog() {
    val dialog = Dialog(this)
    val dialogBinding = DialogCustomImageSelectionBinding.inflate(layoutInflater)

    dialog.setContentView(dialogBinding.root)

    // implement its onclick events
    dialogBinding.tvCamera.setOnClickListener {

      // request for permissions using Dexter
      Dexter.withContext(this@AddUpdateRecipeActivity)
        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        .withListener(object : MultiplePermissionsListener {
          override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
            report?.let {
              if (report.areAllPermissionsGranted()) {
                // we will open the camera
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                getImage.launch(intent)
              } else {
                displayRationalDialogForPermission()
              }
            }


          }

          override fun onPermissionRationaleShouldBeShown(
            permissions: MutableList<PermissionRequest>?,
            token: PermissionToken?
          ) {
            // show the dialog
            displayRationalDialogForPermission()
          }
        }).onSameThread().check()

      dialog.dismiss()
    }

    dialogBinding.tvGallery.setOnClickListener {
      // request for permissions using Dexter
      Dexter.withContext(this@AddUpdateRecipeActivity)
        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(object : PermissionListener {
          override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

            // open up user's gallery
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            getImage.launch(galleryIntent)

          }

          override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
            Toast.makeText(this@AddUpdateRecipeActivity, "Gallery Permissions Denied", Toast.LENGTH_SHORT).show()
          }

          override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {
            displayRationalDialogForPermission()
          }

        }).onSameThread().check()

      dialog.dismiss()
    }

    dialog.show()
  }

  private fun displayRationalDialogForPermission() {
    AlertDialog.Builder(this)
      .setMessage("It looks like you have turned off permissions required for this feature. It can be enabled from the Application Settings")
      .setPositiveButton("Go to Settings") { _, _ ->
        try {

          // go to user's settings
          val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)

          // application link in user's settings
          val uri = Uri.fromParts("package", packageName, null)
          intent.data = uri

          startActivity(intent)

        } catch (e: ActivityNotFoundException) {
          Toast.makeText(this, "Problem finding settings", Toast.LENGTH_SHORT).show()
          e.printStackTrace()
        }
      }
      .setNegativeButton("Cancel") { dialog, _ ->
        dialog.dismiss()
      }
      .show()
  }

  private fun saveImageToInternalStorage(bitmap: Bitmap): String {

    // give our application context to the image so the user can filter
    val wrapper = ContextWrapper(applicationContext)

    var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
    file = File(file, "CookPod_${UUID.randomUUID()}.jpg")

    val stream: FileOutputStream
    try {
      stream = FileOutputStream(file)
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
      stream.flush()
      stream.close()
    } catch (e: IOException) {
      e.printStackTrace()
    }


    return file.absolutePath
  }

  /**
   * displays our custom list which will populate our recycler view
   */
  private fun displayItemsDialogue(title: String, itemList: List<String>, selection: String) {

    _listDialog = Dialog(this)
    val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

    _listDialog.setContentView(binding.root)

    binding.tvTitle.text = title

    binding.rvList.layoutManager = LinearLayoutManager(this)

    // set up the adapter
    val adapter = ListItemAdaptor(this, itemList, selection)
    binding.rvList.adapter = adapter

    _listDialog.show()
  }

  /**
   * called from the onclick event from our ListItemAdaptor
   * handles when a list item is selected
   */
  fun handleListItemSelection(item: String, selection: String) {
    when (selection) {
      Constants.RECIPE_TYPE -> {
        _listDialog.dismiss()
        _binding.etType.setText(item)
      }

      Constants.RECIPE_CATEGORY -> {
        _listDialog.dismiss()
        _binding.etCategory.setText(item)
      }

      Constants.RECIPE_COOKING_TIME -> {
        _listDialog.dismiss()
        _binding.etCookingTime.setText(item)
      }
    }
  }


  /**
   * behaviour on what should happen when we click on any object on our view
   * class listens to onclicks
   */
  override fun onClick(v: View?) {
    if (v != null) {

      when (v.id) {
        R.id.iv_add_recipe_image -> {
          // when we click on the add recipe image
          displayImageSelectionDialog()
          return
        }

        R.id.et_type -> {
          displayItemsDialogue("Select Recipe Type", Constants.recipeTypes(), Constants.RECIPE_TYPE)
          return
        }

        R.id.et_category -> {
          displayItemsDialogue("Select Recipe Category", Constants.recipeCategories(), Constants.RECIPE_CATEGORY)
          return
        }

        R.id.et_cooking_time -> {
          displayItemsDialogue(
            "Select Recipe Cooking Time",
            Constants.recipeCookingTimes(),
            Constants.RECIPE_COOKING_TIME
          )
          return
        }

        R.id.btn_add_dish -> {
          val title =
            _binding.etTitle.text.toString().trim { it <= ' ' } // remove empty spaces in the beginning and at the end
          val type = _binding.etType.text.toString().trim { it <= ' ' }
          val category = _binding.etCategory.text.toString().trim { it <= ' ' }
          val ingredients = _binding.etIngredients.text.toString().trim { it <= ' ' }
          val cookingTimeInMinutes = _binding.etCookingTime.text.toString().trim { it <= ' ' }
          val cookingInstructions = _binding.etDirectionToCook.text.toString().trim { it <= ' ' }

          when {
            TextUtils.isEmpty(_imagePath) -> {
              // no image was selected
              Snackbar.make(v, "Image is required", Snackbar.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(title) -> {
              // no image was selected
              Snackbar.make(v, "Title is required", Snackbar.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(type) -> {
              // no image was selected
              Snackbar.make(v, "Type is required", Snackbar.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(category) -> {
              // no image was selected
              Snackbar.make(v, "Category is required", Snackbar.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(ingredients) -> {
              // no image was selected
              Snackbar.make(v, "Ingredients are required", Snackbar.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(cookingTimeInMinutes) -> {
              // no image was selected
              Snackbar.make(v, "Cooking Time is required", Snackbar.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(cookingInstructions) -> {
              // no image was selected
              Snackbar.make(v, "Instructions are required", Snackbar.LENGTH_LONG).show()
            }

            else -> {
              // data is clean we can post it
              // add the new recipe to the database
              val recipeToInsert = Recipe(
                _imagePath,
                Constants.RECIPE_IMAGE_SOURCE_LOCAL,
                title,
                type,
                category,
                ingredients,
                cookingTimeInMinutes,
                cookingInstructions,
                true
              )

              _recipeViewModel.insert(recipeToInsert)

              Toast.makeText(this@AddUpdateRecipeActivity, "Recipe is added successfully!", Toast.LENGTH_LONG).show()

              // close the current activity
              finish()
            }
          }
        }
      }
    }
  }
}