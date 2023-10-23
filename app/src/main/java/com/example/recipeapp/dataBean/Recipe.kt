package com.example.recipeapp.dataBean

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    var description: String,
    val typeId: Int,
    var steps: String,
    var ingredients: String,
    val imageUri: String? = ""
)
