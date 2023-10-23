package com.example.recipeapp.viewModel

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipeapp.R
import com.example.recipeapp.dataBean.Recipe
import com.example.recipeapp.dataBean.RecipeContract
import com.example.recipeapp.dataBean.RecipeDatabaseHelper
import com.example.recipeapp.dataBean.RecipeType
import org.xmlpull.v1.XmlPullParser

class RecipeViewModel(var context: Context) : ViewModel() {

    private val recipeTypes: List<RecipeType> = loadRecipeTypesFromXml(context.resources)

    val recipes = arrayListOf<Recipe>()
    var positionCurrent = 0
    var allRecipes: ArrayList<Recipe> = createRecipes()

    private val _recipeTypesLiveData = MutableLiveData<List<RecipeType>>()
    val recipeTypesLiveData: LiveData<List<RecipeType>> = _recipeTypesLiveData

    private val _filteredRecipesLiveData = MutableLiveData<List<Recipe>>()
    var filteredRecipesLiveData: LiveData<List<Recipe>> = _filteredRecipesLiveData

    init {
        _recipeTypesLiveData.value = recipeTypes
    }

    fun filterRecipesByType(recipeTypeId: Int) {
        val filteredRecipes = if (recipeTypeId == 0) {
            allRecipes
        } else {
            allRecipes.filter { it.typeId == recipeTypeId }
        }
        _filteredRecipesLiveData.value = filteredRecipes
    }

     @SuppressLint("UseCompatLoadingForDrawables")
     private fun createRecipes(): ArrayList<Recipe> {
        recipes.add(Recipe(1, "Scrambled Eggs",
            "Cottage cheese eggs for breakfast are a nice change from regular scrambled eggs. This egg recipe comes out creamy and soft. Perfect with a slice of tomato and turkey bacon for a delicious and fast low-carb breakfast.",
            1,
            "1.Gather all ingredients. \n 2.Melt butter in a skillet over medium heat. Pour beaten eggs into the skillet; let cook undisturbed until the bottom of the eggs begins to firm, 1 to 2 minutes." ,
            "1 tablespoon butter\n" + "4 large eggs, beaten\n" + "¼ cup cottage cheese\n" + "1 teaspoon chopped fresh chives, or to taste (Optional)\n" + "ground black pepper to taste",
            context.resources.getDrawable(R.mipmap.egg).toString()))
        recipes.add(Recipe(2,"Big Breakfast", "1099 calories per serve", 1, "Step 1\n" +
                "To make baked beans Preheat oven to 200°C. Heat oil in a saucepan over medium–high heat. Add onion, garlic and oregano, and cook, stirring, for 5 minutes or until onion is soft. Add tomato paste, sugar and tomatoes, and cook, stirring, for 3 minutes or until warmed through. Stir in beans, then transfer to an ovenproof dish. Bake for 20 minutes or until sauce is thickened. Stir in basil. Season with salt and pepper. Cover to keep warm.\n" +
                "Step 2\n" + "To make hash brown Wring potatoes in a clean tea towel to remove excess moisture. Combine in a bowl with butter and thyme. Season.\n" + "Step 3\n" +
                "Heat 2 tablespoons oil in a 22cm non-stick, heavy-based frying pan over low heat. Add potato mixture, then, using the back of a metal spoon, spread evenly over base of pan, pressing firmly to compact. Cook, pressing occasionally, for 10 minutes or until underside is deep golden. Using a spatula, turn over and cook for a further 10 minutes or until golden and cooked through.", "2 large desiree potatoes, peeled, coarsely grated\n" +
                "- 50g butter, melted\n" +
                "- 1 tbsp lemon thyme leaves, plus 4 sprigs extra\n" +
                "- 125ml (1/2 cup) olive oil\n" +
                "- 4 portobello mushrooms, (see top tips) stalks trimmed\n" +
                "- 2 red onions, cut into wedges", context.resources.getDrawable(R.mipmap.big).toString()))
        recipes.add(Recipe(3, "Mee Goreng", "Description of Mee Goreng", 3, "Steps 123", "Ingredients 123", null))
        recipes.add(Recipe( 4, "Nasi Lemak Ayam", "Description of Nasi Lemak Ayam", 2, "Steps 123", "Ingredients 123", null))
        recipes.add(Recipe( 5, "Brownie", "Description of Brownie", 4, "Step 123", "Ingredients 123", null))

        return recipes
    }

    fun observeRecipeTypes(): LiveData<List<RecipeType>> {
        return recipeTypesLiveData
    }

    private fun loadRecipeTypesFromXml(resources: Resources): List<RecipeType> {
        val recipeTypes = mutableListOf<RecipeType>()
        val xmlResourceParser = resources.getXml(R.xml.recipetypes)

        var eventType = xmlResourceParser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && xmlResourceParser.name == "recipeType") {
                val id = xmlResourceParser.getAttributeValue(null, "id").toInt()
                val name = xmlResourceParser.getAttributeValue(null, "name")
                recipeTypes.add(RecipeType(id, name))
            }
            eventType = xmlResourceParser.next()
        }
        xmlResourceParser.close()
        return recipeTypes
    }

    fun addRecipe(recipe: Recipe) {
        val db =  RecipeDatabaseHelper(context).writableDatabase

        val recipeValues = ContentValues().apply {
            put(RecipeContract.RecipeEntry._ID, recipe.id)
            put(RecipeContract.RecipeEntry.COLUMN_NAME, recipe.name)
            put(RecipeContract.RecipeEntry.COLUMN_DESCRIPTION, recipe.description)
            put(RecipeContract.RecipeEntry.COLUMN_INGREDIENTS, recipe.ingredients)
            put(RecipeContract.RecipeEntry.COLUMN_TYPE_ID, recipe.typeId)
            put(RecipeContract.RecipeEntry.COLUMN_STEPS, recipe.steps)
            put(RecipeContract.RecipeEntry.COLUMN_IMAGE_URI, recipe.imageUri)
        }
        db.insert(RecipeContract.RecipeEntry.TABLE_NAME, null, recipeValues)

        db.close()
    }

    fun getRecipeById(recipeId: Int): Recipe? {
        return allRecipes.find { it.id == recipeId }
    }

    fun getRecipeTypeById(typeId: Int): RecipeType? {
        return recipeTypes.find { it.id == typeId }
    }

    private fun getResourceUri(context: Context, resourceId: Int): Uri {
        val uriString = "android.resource://${context.packageName}/$resourceId"
        return Uri.parse(uriString)
    }

    fun updateRecipe(recipeId: Int, recipeDescription: String, updatedIngredients: String, updatedSteps: String) {

        val db = RecipeDatabaseHelper(context).writableDatabase

        val values = ContentValues()
        values.put(RecipeContract.RecipeEntry.COLUMN_DESCRIPTION, recipeDescription)
        values.put(RecipeContract.RecipeEntry.COLUMN_INGREDIENTS, updatedIngredients)
        values.put(RecipeContract.RecipeEntry.COLUMN_STEPS, updatedSteps)

        val whereClause = "${RecipeContract.RecipeEntry._ID} = ?"
        val whereArgs = arrayOf(recipeId.toString())

        val update = db.update(RecipeContract.RecipeEntry.TABLE_NAME, values, whereClause, whereArgs)
        db.close()

        if(update > 0) {
            recipes.forEachIndexed { _, it ->
                if (it.id == recipeId) {
                    it.description = recipeDescription
                    it.ingredients = updatedIngredients
                    it.steps = updatedSteps
                }
            }
        }
        allRecipes = recipes
        filterRecipesByType(positionCurrent + 1)
    }

    fun deleteRecipe(recipeId: Int) {
        val db =  RecipeDatabaseHelper(context).writableDatabase

        val whereClause = "${RecipeContract.RecipeEntry._ID} = ?"
        val whereArgs = arrayOf(recipeId.toString())

        val rowsDeleted = db.delete(RecipeContract.RecipeEntry.TABLE_NAME, whereClause, whereArgs)

        db.close()

        if (rowsDeleted > 0) {
            recipes.forEachIndexed { _, it ->
                if (it.id == recipeId) {
                    allRecipes.remove(it)
                }
            }
            allRecipes = recipes
            filterRecipesByType(positionCurrent + 1)
        }
    }
}
