package com.aybarsacar.cookpad.view.activities

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.aybarsacar.cookpad.R
import com.aybarsacar.cookpad.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

  private lateinit var _binding: ActivityMainBinding
  private lateinit var _navController: NavController

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    _binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(_binding.root)


    _navController = findNavController(R.id.nav_host_fragment_activity_main)

    // Passing each menu ID as a set of Ids because each
    // menu should be considered as top level destinations.
    val appBarConfiguration = AppBarConfiguration(
      setOf(
        R.id.navigation_all_recipes, R.id.navigation_favourite_dishes, R.id.navigation_random_recipe
      )
    )
    setupActionBarWithNavController(_navController, appBarConfiguration)

    // render the back button
    _binding.navView.setupWithNavController(_navController)
  }

  override fun onSupportNavigateUp(): Boolean {
    // implement the back button functionality on the history stack
    return NavigationUI.navigateUp(_navController, null)
  }

  fun hideBottomNavigationView() {
    _binding.navView.clearAnimation()
    _binding.navView.animate().translationY(_binding.navView.height.toFloat()).duration = 300
    _binding.navView.visibility = View.GONE
  }

  fun showBottomNavigationView() {
    _binding.navView.clearAnimation()
    _binding.navView.animate().translationY(0f).duration = 300
    _binding.navView.visibility = View.VISIBLE
  }
}