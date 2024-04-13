package com.nagarnikay.up_dbl.Adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.nagarnikay.up_dbl.R

class ImageTypeAdapter(private val activity: Context, private val data: ArrayList<String>, private val res: Resources) :
    ArrayAdapter<String>(activity, 0, data) {

    private val inflater: LayoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    // This function called for each row ( Called data.size() times )
    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row = convertView
        if (row == null) {
            row = inflater.inflate(R.layout.spinner_category_item, parent, false)
        }

        val tempValue = data[position]
        val textStreamName = row!!.findViewById<TextView>(R.id.text_distname)
        val textStreamID = row.findViewById<TextView>(R.id.text_distID)
        textStreamName.text = tempValue
        textStreamID.text = tempValue

        return row
    }
}
