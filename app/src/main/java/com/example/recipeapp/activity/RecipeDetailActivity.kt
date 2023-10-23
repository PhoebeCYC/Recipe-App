package com.example.recipeapp.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.dataBean.Recipe
import com.example.recipeapp.dataBean.RecipeType
import com.example.recipeapp.databinding.ActivityRecipeDetailBinding
import com.example.recipeapp.viewModel.RecipeViewModel
import com.example.recipeapp.viewModel.RecipeViewModelFactory

class RecipeDetailActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRecipeDetailBinding
    private lateinit var viewModel: RecipeViewModel
    private var recipeId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = RecipeViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[RecipeViewModel::class.java]

        recipeId = intent.getIntExtra("recipeId", -1)

        val recipe = getRecipeDetails(recipeId)

        if (recipe != null) {
            binding.layoutbar.tvTitle.text = recipe.name
            binding.recipeDescriptionEditText.setText(recipe.description)
            val recipeType = getRecipeType(recipe.typeId)
            binding.recipeTypeEditText.setText(recipeType?.name ?: "Unknown")
            binding.ingredientsEditText.setText(recipe.ingredients)
            binding.stepsEditText.setText(recipe.steps)

            if(recipe.imageUri != null) {
                Glide.with(this)
                    .load(recipe.imageUri)
                    .error(R.mipmap.default_photo)
                    .into(binding.recipeImageView)
            }
        }

        initListener()
    }
    private fun initListener() {
        binding.layoutbar.ivLeft.setOnClickListener(this)
        binding.ivEdit.setOnClickListener(this)
        binding.updateRecipeButton.setOnClickListener(this)
        binding.cancelbutton.setOnClickListener(this)
        binding.deleteButton.setOnClickListener(this)
    }

    private fun getRecipeDetails(recipeId: Int): Recipe? {
        return viewModel.getRecipeById(recipeId)
    }


    private fun getRecipeType(recipeTypeId: Int): RecipeType? {
        return viewModel.getRecipeTypeById(recipeTypeId)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.iv_left -> {
                finish()
            }
            R.id.ivEdit -> {
                binding.ivEdit.visibility = View.GONE
                binding.deleteButton.visibility = View.GONE
                binding.layoutEdit.visibility = View.VISIBLE

                binding.ingredientsEditText.isEnabled = true
                binding.stepsEditText.isEnabled = true
                binding.recipeDescriptionEditText.isEnabled = true
                binding.recipeDescriptionEditText.setBackgroundColor(Color.parseColor("#f4f2fa"))
                binding.ingredientsEditText.setBackgroundColor(Color.parseColor("#f4f2fa"))
                binding.stepsEditText.setBackgroundColor(Color.parseColor("#f4f2fa"))
            }
            R.id.cancelbutton -> {
                binding.layoutEdit.visibility = View.GONE
                binding.deleteButton.visibility = View.VISIBLE
                binding.ivEdit.visibility = View.VISIBLE

                binding.ingredientsEditText.isEnabled = false
                binding.recipeDescriptionEditText.isEnabled = false
                binding.ingredientsEditText.setTextColor(getColor(R.color.black))
                binding.recipeDescriptionEditText.setTextColor(getColor(R.color.black))
                binding.stepsEditText.isEnabled = false
                binding.stepsEditText.setTextColor(getColor(R.color.black))

                binding.ingredientsEditText.setBackgroundColor(getColor(android.R.color.transparent))
                binding.stepsEditText.setBackgroundColor(getColor(android.R.color.transparent))
                binding.recipeDescriptionEditText.setBackgroundColor(getColor(android.R.color.transparent))
            }
            R.id.updateRecipeButton -> {
                binding.layoutEdit.visibility = View.GONE
                binding.deleteButton.visibility = View.VISIBLE
                binding.ivEdit.visibility = View.VISIBLE

                binding.ingredientsEditText.isEnabled = false
                binding.ingredientsEditText.setTextColor(getColor(R.color.black))
                binding.stepsEditText.isEnabled = false
                binding.recipeDescriptionEditText.isEnabled = false
                binding.recipeDescriptionEditText.setTextColor(getColor(R.color.black))
                binding.stepsEditText.setTextColor(getColor(R.color.black))

                binding.ingredientsEditText.setBackgroundColor(getColor(android.R.color.transparent))
                binding.stepsEditText.setBackgroundColor(getColor(android.R.color.transparent))
                binding.recipeDescriptionEditText.setBackgroundColor(getColor(android.R.color.transparent))

                viewModel.updateRecipe(recipeId, binding.recipeDescriptionEditText.text.toString(),
                    binding.ingredientsEditText.text.toString(), binding.stepsEditText.text.toString())
            }
            R.id.deleteButton -> {
                viewModel.deleteRecipe(recipeId)
                finish()
            }
        }
    }
}