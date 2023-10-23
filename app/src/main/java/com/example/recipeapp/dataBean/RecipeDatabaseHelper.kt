package com.example.recipeapp.dataBean

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class RecipeDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "RecipeDatabase"
        private const val DATABASE_VERSION = 1
    }

    // Define the table creation SQL statement
    private val CREATE_RECIPE_TABLE = """
        CREATE TABLE ${RecipeContract.RecipeEntry.TABLE_NAME} (
            ${RecipeContract.RecipeEntry._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${RecipeContract.RecipeEntry.COLUMN_NAME} TEXT NOT NULL,
            ${RecipeContract.RecipeEntry.COLUMN_DESCRIPTION} TEXT,
            ${RecipeContract.RecipeEntry.COLUMN_TYPE_ID} INTEGER,
            ${RecipeContract.RecipeEntry.COLUMN_STEPS} TEXT,
            ${RecipeContract.RecipeEntry.COLUMN_INGREDIENTS} TEXT,
            ${RecipeContract.RecipeEntry.COLUMN_IMAGE_URI} TEXT
        )
    """.trimIndent()

    override fun onCreate(db: SQLiteDatabase) {
        // Create the recipe table
        db.execSQL(CREATE_RECIPE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}
