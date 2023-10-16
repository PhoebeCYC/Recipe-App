package com.example.recipeapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.dataBean.Recipe
import com.example.recipeapp.databinding.ItemRecipeBinding

class RecipeListAdapter(var context: Context, private var data: List<Recipe>) : RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size ?: 0
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = data[position]
        holder.bind(recipe)
    }

    interface OnItemClickListener {
        fun onItemClick(recipe: Recipe)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            binding.recipeNameTextView.text = recipe.name

            binding.root.setOnClickListener {
                onItemClickListener?.onItemClick(recipe)
            }
        }
    }

    fun updateRecipe(filteredRecipes: List<Recipe>) {
        data = filteredRecipes
        notifyDataSetChanged()
    }
}