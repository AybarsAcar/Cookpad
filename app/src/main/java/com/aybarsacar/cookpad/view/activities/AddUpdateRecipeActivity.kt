package com.aybarsacar.cookpad.view.activities

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import com.aybarsacar.cookpad.R
import com.aybarsacar.cookpad.databinding.ActivityAddUpdateRecipeBinding
import com.aybarsacar.cookpad.databinding.DialogCustomImageSelectionBinding
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

/**
 * both adding and updating the new Recipe
 */
class AddUpdateRecipeActivity : AppCompatActivity(), View.OnClickListener {

  // view binding variable
  private lateinit var _binding: ActivityAddUpdateRecipeBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // bind the view
    _binding = ActivityAddUpdateRecipeBinding.inflate(layoutInflater)
    setContentView(_binding.root)

    setupActionBar()

    _binding.ivAddRecipeImage.setOnClickListener(this)

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
            if (report!!.areAllPermissionsGranted()) {
              Toast.makeText(this@AddUpdateRecipeActivity, "Camera Permissions Granted", Toast.LENGTH_SHORT).show()
            } else {
              displayRationalDialogForPermission()
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
        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(object : MultiplePermissionsListener {
          override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
            if (report!!.areAllPermissionsGranted()) {
              Toast.makeText(this@AddUpdateRecipeActivity, "Gallery Permissions Granted", Toast.LENGTH_SHORT).show()
            } else {
              displayRationalDialogForPermission()
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
      }

    }
  }

}