package com.nagarnikay.up_dbl.Adapter



import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.nagarnikay.up_dbl.Model.ProjectReportData
import com.nagarnikay.up_dbl.R
import com.nagarnikay.up_dbl.Utils.ImageDialogFragment

class ProjectsRecViewAdapter(
    private val context: Context,
    private val data: List<ProjectReportData>,
   private val supportFragmentManager: FragmentManager
) :
    RecyclerView.Adapter<ProjectsRecViewAdapter.ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.project_itemcard, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val projectData = data[position]
        with(holder) {
            financialProgressTextView.text = projectData.financialProgress.toString()
            physicalProgressTextView.text = projectData.physicalProgress.toString()
            projectNameTextView.text = projectData.projectName
            latitudeTextView.text = projectData.latitude.toString()
            longitudeTextView.text = projectData.longitude.toString()
            paymentAmountTextView.text = projectData.paymentAmount.toString()
            text_photo1_remark1.text = projectData.remarkPhoto1
            text_photo1_remark2.text = projectData.remarkPhoto2

            Glide.with(progressPhoto1ImageView.context)
                .load(projectData.progressPhoto1)
                .placeholder(R.drawable.no_image_ic)
                .centerCrop()
                .into(progressPhoto1ImageView)

            Glide.with(progressPhoto2ImageView.context)
                .load(projectData.progressPhoto2)
                .placeholder(R.drawable.no_image_ic)
                .centerCrop()
                .into(progressPhoto2ImageView)
            holder.progressPhoto1ImageView.setOnClickListener {
                val dialogFragment = ImageDialogFragment.newInstance(projectData.progressPhoto1.toString(), holder.itemView.context)
                dialogFragment.show(supportFragmentManager, "ImageDialogFragment")
            }

            holder.progressPhoto2ImageView.setOnClickListener {
                val dialogFragment = ImageDialogFragment.newInstance(projectData.progressPhoto2.toString(), holder.itemView.context)
                dialogFragment.show(supportFragmentManager, "ImageDialogFragment")
            }
        }
    }

    override fun getItemCount(): Int = data.size

    inner class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val projectNameTextView: TextView = itemView.findViewById(R.id.text_project_name)
        val longitudeTextView: TextView = itemView.findViewById(R.id.text_longitude)
        val latitudeTextView: TextView = itemView.findViewById(R.id.text_latitude)
        val paymentAmountTextView: TextView = itemView.findViewById(R.id.text_payment_amount)
        val progressPhoto1ImageView: ImageView = itemView.findViewById(R.id.image_progress_photo1)
        val progressPhoto2ImageView: ImageView = itemView.findViewById(R.id.image_progress_photo2)
        val physicalProgressTextView: TextView = itemView.findViewById(R.id.text_physical_progress)
        val financialProgressTextView: TextView = itemView.findViewById(R.id.text_financial_progress)
        val text_photo1_remark1: TextView = itemView.findViewById(R.id.text_photo1_remark)
        val text_photo1_remark2: TextView = itemView.findViewById(R.id.text_photo1_remark2)
    }
}
