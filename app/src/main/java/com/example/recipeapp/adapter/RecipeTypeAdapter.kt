package com.example.recipeapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.recipeapp.R
import com.example.recipeapp.dataBean.RecipeType

class RecipeTypeAdapter(context: Context, resource: Int, recipeTypes: List<RecipeType>) :
    ArrayAdapter<RecipeType>(context, resource, recipeTypes) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createRecipeTypeView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createRecipeTypeView(position, convertView, parent)
    }

    private fun createRecipeTypeView(position: Int, convertView: View?, parent: ViewGroup): View {
        val recipeType = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)

        val recipeTypeNameTextView = view.findViewById<TextView>(R.id.recipeTypeNameTextView)
        recipeTypeNameTextView.text = recipeType?.name

        return view
    }
}
