package com.nagarnikay.up_dbl.Adapter

import com.nagarnikay.up_dbl.Model.FIData
import com.nagarnikay.up_dbl.R


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class YearSpinnerAdapter(context: Context, yearItems: List<FIData>) :
    ArrayAdapter<FIData>(context, 0, yearItems) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertViewVar = convertView
        if (convertViewVar == null) {
            convertViewVar = LayoutInflater.from(context).inflate(R.layout.spinner_category_item, parent, false)
        }
        val textView = convertViewVar!!.findViewById<TextView>(R.id.text_distname)
        val yearItem = getItem(position)
        if (yearItem != null) {
            textView.text = yearItem.yearText
        }
        return convertViewVar
    }
}
