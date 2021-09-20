package com.aybarsacar.cookpad.view.adaptors

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aybarsacar.cookpad.databinding.ItemCustomListBinding
import com.aybarsacar.cookpad.view.activities.AddUpdateRecipeActivity

/**
 * used to dynamically render the views in the list
 * dynamically render the list elements
 */
class ListItemAdaptor(
  private val activity: Activity,
  private val listItems: List<String>,
  private val selection: String
) : RecyclerView.Adapter<ListItemAdaptor.ViewHolder>() {

  class ViewHolder(view: ItemCustomListBinding) : RecyclerView.ViewHolder(view.root) {
    val tvText = view.tvText
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

    // pass the binding object
    val binding: ItemCustomListBinding = ItemCustomListBinding.inflate(LayoutInflater.from(activity), parent, false)

    return ViewHolder(binding)

  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    val item = listItems[position]

    holder.tvText.text = item

    // make it the items clickable
    // add the onclick listener
    holder.itemView.setOnClickListener {
      if (activity is AddUpdateRecipeActivity) {
        activity.handleListItemSelection(item, selection)
      }
    }
  }

  override fun getItemCount(): Int {
    return listItems.size
  }
}