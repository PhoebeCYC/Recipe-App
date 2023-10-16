package com.example.recipeapp.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RecipeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            if (viewModel == null) {
                viewModel = RecipeViewModel(context)
            }
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        private var viewModel: RecipeViewModel? = null
    }
}

