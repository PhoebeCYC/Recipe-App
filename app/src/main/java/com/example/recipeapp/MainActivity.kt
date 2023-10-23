package com.example.recipeapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipeapp.activity.NewRecipeActivity
import com.example.recipeapp.activity.RecipeDetailActivity
import com.example.recipeapp.adapter.RecipeListAdapter
import com.example.recipeapp.dataBean.Recipe
import com.example.recipeapp.dataBean.RecipeContract
import com.example.recipeapp.dataBean.RecipeDatabaseHelper
import com.example.recipeapp.databinding.ActivityMainBinding
import com.example.recipeapp.viewModel.RecipeViewModel
import com.example.recipeapp.viewModel.RecipeViewModelFactory

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: RecipeViewModel
    private lateinit var recipeListAdapter: RecipeListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = RecipeViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[RecipeViewModel::class.java]

        binding.layoutbar.ivLeft.visibility = View.GONE
        binding.layoutbar.tvTitle.text = "Recipe App"

        binding.recyclerViewRecipes.layoutManager = LinearLayoutManager(this)
        recipeListAdapter = RecipeListAdapter(this, viewModel.allRecipes)
        binding.recyclerViewRecipes.adapter = recipeListAdapter

        observe()
        initListener()
    }

    private fun observe(){
        viewModel.recipeTypesLiveData.observe(this) { recipeTypes ->
            val recipeTypeNames = recipeTypes.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, recipeTypeNames)
            binding.spinnerRecipeTypes.adapter = adapter
        }

        viewModel.filteredRecipesLiveData.observe(this) { filteredRecipes ->
            recipeListAdapter.updateRecipe(filteredRecipes)
        }

        retrieveData()
    }

    private fun retrieveData(){
        val dbHelper = RecipeDatabaseHelper(this)
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            RecipeContract.RecipeEntry._ID,
            RecipeContract.RecipeEntry.COLUMN_NAME,
            RecipeContract.RecipeEntry.COLUMN_DESCRIPTION,
            RecipeContract.RecipeEntry.COLUMN_TYPE_ID,
            RecipeContract.RecipeEntry.COLUMN_STEPS,
            RecipeContract.RecipeEntry.COLUMN_INGREDIENTS,
            RecipeContract.RecipeEntry.COLUMN_IMAGE_URI
        )

        val sortOrder = "${RecipeContract.RecipeEntry.COLUMN_NAME} ASC"

        val cursor = db.query(
            RecipeContract.RecipeEntry.TABLE_NAME,   // The table to query
            projection,                            // The columns to return
            null,                                  // The columns for the WHERE clause (null indicates no WHERE clause)
            null,                                  // The values for the WHERE clause (null indicates no WHERE clause)
            null,                                  // Don't group the rows
            null,                                  // Don't filter by row groups
            sortOrder                              // The sort order
        )
        while (cursor.moveToNext()) {
            val recipeId = cursor.getInt(cursor.getColumnIndexOrThrow(RecipeContract.RecipeEntry._ID))

            // Check if the recipe ID already exists in the list
            val isDuplicate = viewModel.recipes.any { it.id == recipeId }

            if (!isDuplicate) {
                val recipeId =
                    cursor.getInt(cursor.getColumnIndexOrThrow(RecipeContract.RecipeEntry._ID))
                val name =
                    cursor.getString(cursor.getColumnIndexOrThrow(RecipeContract.RecipeEntry.COLUMN_NAME))
                val description =
                    cursor.getString(cursor.getColumnIndexOrThrow(RecipeContract.RecipeEntry.COLUMN_DESCRIPTION))
                val steps =
                    cursor.getString(cursor.getColumnIndexOrThrow(RecipeContract.RecipeEntry.COLUMN_STEPS))
                val typeId =
                    cursor.getInt(cursor.getColumnIndexOrThrow(RecipeContract.RecipeEntry.COLUMN_TYPE_ID))
                val ingredients =
                    cursor.getString(cursor.getColumnIndexOrThrow(RecipeContract.RecipeEntry.COLUMN_INGREDIENTS))
                val imageUri =
                    cursor.getString(cursor.getColumnIndexOrThrow(RecipeContract.RecipeEntry.COLUMN_IMAGE_URI))

                var recipe = Recipe(recipeId, name, description, typeId, steps, ingredients, imageUri)
                viewModel.recipes.add(recipe)
            }
        }
        viewModel.allRecipes = viewModel.recipes

        cursor.close()
        db.close()

        recipeListAdapter.updateRecipe(viewModel.allRecipes)
        viewModel.filterRecipesByType(viewModel.positionCurrent + 1)
    }

    private fun initListener() {
        binding.addNewRecipeButton.setOnClickListener(this)

        recipeListAdapter.setOnItemClickListener(object : RecipeListAdapter.OnItemClickListener {
            override fun onItemClick(recipe: Recipe) {
                val intent = Intent(this@MainActivity, RecipeDetailActivity::class.java)
                intent.putExtra("recipeId", recipe.id)
                intent.putExtra("recipeName", recipe.name)
                startActivity(intent)
            }
        })

        binding.spinnerRecipeTypes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.positionCurrent = position
                val selectedRecipeType = viewModel.recipeTypesLiveData.value?.get(position)
                selectedRecipeType?.let {
                    viewModel.filterRecipesByType(selectedRecipeType.id)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun refreshRecipeList() {
        retrieveData()
        recipeListAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.addNewRecipeButton ->{
                val intent = Intent(this@MainActivity, NewRecipeActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val id = data?.getIntExtra("id", 0) as Int
            val recipeName = data.getStringExtra("name") as String
            val description = data.getStringExtra("description") as String
            val typeId = data.getIntExtra("typeId", 0) as Int
            val ingredients = data.getStringExtra("ingredients") as String
            val steps = data.getStringExtra("steps") as String
            val imageUri = data.getStringExtra("imageUri") as String

            val newRecipe = Recipe(
                id = id,
                name = recipeName,
                description = description,
                typeId = typeId,
                ingredients = ingredients,
                steps = steps,
                imageUri = imageUri
            )
            viewModel.addRecipe(newRecipe)
            refreshRecipeList()
        }
    }

    companion object {
        const val REQUEST_CODE = 123
    }
}
