package com.nagarnikay.up_dbl.Utils



import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtils {
    fun convertImageToBase64(imagePath: String): String? {
        val imgFile = File(imagePath)
        if (!imgFile.exists()) {
            return null // File does not exist
        }

        val bitmap = BitmapFactory.decodeFile(imagePath)
        if (bitmap == null) {
            return null // Unable to decode bitmap
        }

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun compressImage(inputPath: String): File? {
        val inputFile = File(inputPath)
        if (!inputFile.exists()) {
            return null // File does not exist
        }

        val bitmap = BitmapFactory.decodeFile(inputPath)

        // Calculate the target file size (120 KB)
        val targetSize = 120 * 1024 // 120 KB in bytes

        try {
            val outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val outputFile = File.createTempFile("compressed_image", ".jpg", outputDir)

            var quality = 90 // Initial quality

            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)

            while (byteArrayOutputStream.toByteArray().size > targetSize && quality > 10) {
                byteArrayOutputStream.reset() // Clear the previous output

                // Decrease quality by 10%
                quality -= 10

                // Compress the bitmap with the new quality
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            }

            // Write the compressed bitmap to the file
            val fileOutputStream = FileOutputStream(outputFile)
            fileOutputStream.write(byteArrayOutputStream.toByteArray())
            fileOutputStream.flush()
            fileOutputStream.close()

            return outputFile
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    fun getFileSizeInKB(filePath: String): Long {
        val file = File(filePath)
        return if (file.exists()) {
            file.length() / 1024 // Size in KB
        } else {
            0
        }
    }
}
