package com.nagarnikay.up_dbl.Adapter



import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.nagarnikay.up_dbl.Model.ProjectData
import com.nagarnikay.up_dbl.R

import java.util.*

class ProjectAdapterSpinner(context: Context, yearItems: List<ProjectData>) :
    ArrayAdapter<ProjectData>(context, 0, yearItems) {

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
            val spannableStringBuilder = SpannableStringBuilder()
            spannableStringBuilder.append("(")
            spannableStringBuilder.append(yearItem.projectId.toString())
            spannableStringBuilder.append(") ")
            val projectName = yearItem.projectName!!.trim()
            spannableStringBuilder.append(projectName)
            spannableStringBuilder.setSpan(
                StyleSpan(Typeface.BOLD),
                spannableStringBuilder.length - projectName.length,
                spannableStringBuilder.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            textView.text = spannableStringBuilder
        }
        return convertViewVar
    }
}
