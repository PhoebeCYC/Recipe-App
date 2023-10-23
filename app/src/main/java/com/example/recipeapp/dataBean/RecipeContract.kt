package com.example.recipeapp.dataBean

object RecipeContract {
    // Define table and column names
    object RecipeEntry {
        const val TABLE_NAME = "recipes"
        const val _ID = "_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_TYPE_ID = "typeId"
        const val COLUMN_STEPS = "steps"
        const val COLUMN_INGREDIENTS = "ingredients"
        const val COLUMN_IMAGE_URI = "image_uri"
    }
}
