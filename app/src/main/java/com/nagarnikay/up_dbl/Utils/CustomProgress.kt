package com.nagarnikay.up_dbl.Utils


import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import android.widget.TextView

import com.nagarnikay.up_dbl.R

object CustomProgress {

    private var customProgress: CustomProgress? = null
    private var mDialog: Dialog? = null
    private var mProgressBar: ProgressBar? = null

    @JvmStatic
    fun getInstance(): CustomProgress {
        if (customProgress == null) {
            customProgress = CustomProgress
        }
        return customProgress!!
    }

    @JvmOverloads
    fun showProgress(context: Context, message: String?, cancelable: Boolean = false) {
        mDialog = Dialog(context)
        // no tile for the dialog
        mDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog!!.setContentView(R.layout.prograss_bar_dialog)
        mProgressBar = mDialog!!.findViewById<View>(R.id.progress_bar) as ProgressBar
        val progressText = mDialog!!.findViewById<View>(R.id.progress_text) as TextView
        progressText.text = message
        progressText.visibility = View.VISIBLE
        mProgressBar!!.visibility = View.VISIBLE
        // you can change or add this line according to your need
        mProgressBar!!.isIndeterminate = true
        mDialog!!.setCancelable(cancelable)
        mDialog!!.setCanceledOnTouchOutside(cancelable)
        mDialog!!.show()
    }

    fun hideProgress() {
        if (mDialog != null) {
            mDialog!!.dismiss()
            mDialog = null
        }
    }
}
