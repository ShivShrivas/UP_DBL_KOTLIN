package com.nagarnikay.up_dbl.Utils



import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.nagarnikay.up_dbl.R
import java.io.File

class ImageDialogFragment : DialogFragment() {

    private var filePath: String? = null
    private lateinit var context: Context;

    companion object {
        fun newInstance(filePath: String, context: Context): ImageDialogFragment {
            val fragment = ImageDialogFragment()
            val args = Bundle()
            args.putString("filePath", filePath)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            filePath = it.getString("filePath")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_image, null)
        val imageView: ImageView = dialogView.findViewById(R.id.imageViewDialog)

        filePath?.let {
            val imgFile = File(it)
            if (imgFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                imageView.setImageBitmap(bitmap)
            }else{
                Glide.with(this) // Pass fragment reference
                    .load(filePath)
                    .placeholder(R.drawable.no_image_ic)

                    .into(imageView)
            }
        }

        builder.setView(dialogView)
            .setPositiveButton("Close") { dialog: DialogInterface, which: Int ->
                dialog.dismiss()
            }
        return builder.create()
    }
}
