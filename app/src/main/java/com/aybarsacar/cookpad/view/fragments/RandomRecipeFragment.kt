package com.aybarsacar.cookpad.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aybarsacar.cookpad.databinding.FragmentRandomRecipeBinding

class RandomRecipeFragment : Fragment() {

  private var _binding: FragmentRandomRecipeBinding? = null


  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

    _binding = FragmentRandomRecipeBinding.inflate(inflater, container, false)

    return _binding!!.root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}