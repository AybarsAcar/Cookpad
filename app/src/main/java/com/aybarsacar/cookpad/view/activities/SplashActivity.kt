package com.aybarsacar.cookpad.view.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.aybarsacar.cookpad.R
import com.aybarsacar.cookpad.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // get the splash screen view
    val splashBinding = ActivitySplashBinding.inflate(layoutInflater)

    setContentView(splashBinding.root)

    // hide the status bar
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      // make it full screen without the action bar at the top - hides it
      // for newer versions of Android
      window.insetsController?.hide(WindowInsets.Type.statusBars())
    } else {
      @Suppress("DEPRECATION")
      window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
      )
    }

    // set the animation of hte name
    val splashAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_splash_screen)
    splashBinding.tvAppName.animation = splashAnimation

    splashAnimation.setAnimationListener(object : Animation.AnimationListener {

      override fun onAnimationStart(p0: Animation?) {
      }

      override fun onAnimationEnd(p0: Animation?) {
        // move onto the next activity
        Handler(Looper.getMainLooper()).postDelayed({
          startActivity(Intent(this@SplashActivity, MainActivity::class.java))
          finish()
        }, 1000)
      }

      override fun onAnimationRepeat(p0: Animation?) {
      }

    })

  }
}