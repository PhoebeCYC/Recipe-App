package com.example.recipeapp.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.recipeapp.R
import com.example.recipeapp.adapter.RecipeTypeAdapter
import com.example.recipeapp.dataBean.RecipeType
import com.example.recipeapp.databinding.ActivityNewRecipeBinding
import com.example.recipeapp.popUp.BottomPopUp
import com.example.recipeapp.viewModel.RecipeViewModel
import com.example.recipeapp.viewModel.RecipeViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class NewRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewRecipeBinding
    private lateinit var viewModel: RecipeViewModel
    var selectedImageUri : Uri? = null
    private var bottomPopUp: BottomPopUp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, RecipeViewModelFactory(this))[RecipeViewModel::class.java]

        val recipeTypeSpinner = binding.recipeTypeSpinner
        val recipeTypeAdapter = RecipeTypeAdapter(this, R.layout.spinner_item, viewModel.observeRecipeTypes().value.orEmpty())
        recipeTypeSpinner.adapter = recipeTypeAdapter
        recipeTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                recipeTypeSpinner.setSelection(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.addRecipeButton.setOnClickListener {

            val recipeName = binding.recipeNameEditText.text.toString()
            val description = binding.descriptionEditText.text.toString()
            val recipeType = binding.recipeTypeSpinner.selectedItem as RecipeType
            val ingredients = binding.ingredientsEditText.text.toString()
            val steps = binding.stepsEditText.text.toString()

            val resultIntent = Intent()
            resultIntent.putExtra("id", generateUniqueId())
            resultIntent.putExtra("name", recipeName)
            resultIntent.putExtra("description", description)
            resultIntent.putExtra("typeId", recipeType.id)
            resultIntent.putExtra("ingredients", ingredients)
            resultIntent.putExtra("steps", steps)
            resultIntent.putExtra("imageUri", selectedImageUri.toString())
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        binding.layoutbar.ivLeft.setOnClickListener {
            finish()
        }

        binding.recipeImageView.setOnClickListener {

            bottomPopUp = BottomPopUp(this, itemClickCamera)
            bottomPopUp!!.showAtLocation(binding.layoutNewRecipe, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)

        }
    }

    private val itemClickCamera = View.OnClickListener { v ->
        bottomPopUp!!.dismiss()
        when (v.id) {
            R.id.popup_Camera -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
                } else {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                }
            }
            R.id.popup_Album -> {
                selectImage()
            }
        }
    }

    private fun generateUniqueId(): Int {
        val timestamp = System.currentTimeMillis()
        val random = (Math.random() * 1000).toInt()
        return (timestamp + random).toInt()
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, Companion.REQUEST_IMAGE_CAPTURE)
                } else {
                    Toast.makeText(this,"Permission was denied." ,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            Glide.with(this)
                .load(imageBitmap)
                .apply(RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true))
                .into(binding.recipeImageView)

            selectedImageUri = saveImageToGallery(imageBitmap)


        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            selectedImageUri = data?.data

            Glide.with(this)
                .load(selectedImageUri)
                .into(binding.recipeImageView)
        }
    }

    private fun saveImageToGallery(image: Bitmap): Uri? {
        val file = File(externalCacheDir, "recipe_image.jpg")
        val uri = Uri.fromFile(file)

        try {
            val outputStream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return uri
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1001;
        private const val REQUEST_IMAGE_PICK = 1002;
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100;
        private const val READ_EXTERNAL_STORAGE_REQUEST = 101;
    }
}