package com.example.recipeapp.dataBean

import android.net.Uri

data class Recipe(
    val id: Int,
    val name: String,
    var description: String,
    val typeId: Int,
    var steps: String,
    var ingredients: String,
    val imageUri: Uri?
)