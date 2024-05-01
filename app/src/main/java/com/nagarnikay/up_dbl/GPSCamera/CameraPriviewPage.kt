package com.nagarnikay.up_dbl.GPSCamera

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Typeface
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.service.controls.ControlsProviderService
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.cardview.widget.CardView
import androidx.compose.ui.text.toUpperCase
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nagarnikay.up_dbl.R
import com.tooncoder.livelook.AddressUtil
import com.tooncoder.livelook.LocationViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class CameraPriviewPage  : AppCompatActivity(), OnMapReadyCallback {

    private val viewModel: LocationViewModel by viewModels()

    private lateinit var mapView: MapView

    private lateinit var cameraView: PreviewView
    private lateinit var googleMap: GoogleMap
    private lateinit var cardView: CardView;

    private var imageCapture: ImageCapture? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_priview_page)

        mapView = findViewById(R.id.mapView)
        cameraView = findViewById(R.id.cameraView)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync(this)


        //Switch Camera to MapView
//        val btnStartCamera: Button = findViewById(R.id.btnStartCamera)
//        btnStartCamera.setOnClickListener {
//            //if map is visible, hide it and show camera view and vice versa
//            if (mapView.visibility == View.VISIBLE) {
//                mapView.visibility = View.GONE
//                cameraView.visibility = View.VISIBLE
//                startCamera()
//                btnStartCamera.text = "Switch to Map"
//            } else {
//                mapView.visibility = View.VISIBLE
//                cameraView.visibility = View.GONE
//                btnStartCamera.text = "Switch to Camera"
//            }
//        }

//        val btnStartLocationUpdates: Button = findViewById(R.id.btnStartLocationUpdates)
//        btnStartLocationUpdates.setOnClickListener {
//
//            viewModel.startLocationUpdates()
//        }

        viewModel.locationUpdates.observe(this, Observer { location ->
            updateMap(location)
        })
        viewModel.startLocationUpdates()

        if (mapView.visibility == View.VISIBLE) {
            mapView.visibility = View.GONE
            cameraView.visibility = View.VISIBLE
            startCamera()
//            btnStartCamera.text = "Switch to Map"
        } else {
            mapView.visibility = View.VISIBLE
            cameraView.visibility = View.GONE
//            btnStartCamera.text = "Switch to Camera"
        }
    }

    fun getCurrentFullDateTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yy hh:mm a 'GMT' Z")
        dateFormat.timeZone = TimeZone.getDefault()
        return dateFormat.format(calendar.time)
    }

    private fun startCamera() {

        val width = cameraView.width
        val height = cameraView.height


        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        //Get lat and long from viewModel and pass it to overlay
        viewModel.locationUpdates.observe(this, Observer { location ->
            // Remove any existing TextViews from cameraView
            // Remove all CardViews from cameraView
            for (i in 0 until cameraView.childCount) {
                val childView = cameraView.getChildAt(i)
                if (childView is CardView) {
                    cameraView.removeView(childView)
                }
            }
            cardView = CardView(this).apply {
                // Set layout parameters to wrap_content
                layoutParams = FrameLayout.LayoutParams(
                   ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT // Set height to wrap_content
                ).apply {
                    // Align to bottom of parent
                    gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL

                    // Set margin
                     bottomMargin = resources.getDimensionPixelSize(R.dimen.card_margin_bottom)
                     topMargin = resources.getDimensionPixelSize(R.dimen.card_margin_bottom)
                }
                setCardBackgroundColor(ContextCompat.getColor(context, R.color.transparent_black_60))

                // Set other attributes as needed
                // radius = resources.getDimensionPixelSize(R.dimen.card_corner_radius).toFloat()
            }
            var addressUtil: AddressUtil =AddressUtil(this)
            var fullAddress:String="Lat:(${location.latitude}) \n" +
                    "Long:(${location.longitude}) (${location.accuracy}mtr)"
            var textView= TextView(this).apply {
                text = fullAddress
                setTextColor(ContextCompat.getColor(context, android.R.color.white))
                setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
                gravity =  Gravity.CENTER or Gravity.TOP
                textSize=14.0f
                setTypeface(null, Typeface.BOLD)

            }
            val marginInDp = 5 // Margin in dp
            val marginInPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                marginInDp.toFloat(),
                resources.displayMetrics
            ).toInt()

            val layoutParamsBottom = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                leftMargin = marginInPx*3 // Set bottom margin
                rightMargin = marginInPx*3 // Set bottom margin
                // Set bottom margin
            }
            var textView1= TextView(this).apply {
                text = "\n${addressUtil.getAddress(location.latitude,location.longitude )}${getCurrentFullDateTime().toUpperCase()}"
                setTextColor(ContextCompat.getColor(context, android.R.color.white))
                setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
                layoutParams=layoutParamsBottom
                textSize= 14.0f
                gravity = Gravity.CENTER
                setTypeface(null, Typeface.BOLD) // Make text bold

            }
            val linearLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity =Gravity.CENTER
                LinearLayout.LayoutParams.MATCH_PARENT
                ViewGroup.LayoutParams.WRAP_CONTENT



            }
            val mainLinearLayout =LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity =Gravity.CENTER
                LinearLayout.LayoutParams.MATCH_PARENT
                ViewGroup.LayoutParams.WRAP_CONTENT
            }
            val imageView = ImageView(this@CameraPriviewPage)
            // Set the image resource or other properties of the ImageView as needed
            imageView.setImageResource(R.drawable.updbllogo)

            // Set the height and width of the ImageView
            val params = LinearLayout.LayoutParams(
                dpToPx(60), // 30dp converted to pixels
                dpToPx(60),
                
            )
            imageView.layoutParams = params

            linearLayout.addView(textView)
            linearLayout.addView(textView1)
            mainLinearLayout.addView(imageView)
            mainLinearLayout.addView(linearLayout)
            cardView.addView(mainLinearLayout)
            cameraView.addView(cardView)

        })

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras 1to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    val viewFinder = cameraView
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(ControlsProviderService.TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
        val btnCapture: Button = findViewById(R.id.csptureImage)
        btnCapture.setOnClickListener {
            // Capture image
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(getOutputFile()).build()
            imageCapture?.takePicture(
                outputFileOptions, ContextCompat.getMainExecutor(this),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        compositeOverlayAndImage(cardView)
                        Log.d("TAG", "Image saved successfully: ${outputFileResults.savedUri}")

                    }

                    override fun onError(exception: ImageCaptureException) {
                        // Image capture failed
                        Log.e("TAG", "Error capturing image: ${exception.message}", exception)
                    }
                })
        }

    }
    fun Context.dpToPx(dp: Int): Int {
        val density: Float = resources.displayMetrics.density
        return (dp * density).toInt()
    }
    private fun compositeOverlayAndImage(cardView:CardView) {

        val cameraBitmap = cameraView.bitmap

        // Create a new bitmap with the same dimensions as the camera view bitmap
        val resultBitmap = cameraBitmap?.let {
            Bitmap.createBitmap(
                it.width, cameraBitmap.height, Bitmap.Config.ARGB_8888
            )
        }

        // Draw the camera view bitmap onto the result bitmap's canvas
        val canvas = resultBitmap?.let { Canvas(it) }
        if (cameraBitmap != null) {
            if (canvas != null) {
                canvas.drawBitmap(cameraBitmap, 0f, 0f, null)
            }
        }

        // Iterate through the child views of cameraView to find the CardView


        // Draw the CardView onto the canvas
        if (canvas != null) {
            drawViewToCanvas(cardView, canvas)
        }



        // Save the composite image
        if (resultBitmap != null) {
            saveCompositeImage(resultBitmap)
            saveImageToCameraFolder(this@CameraPriviewPage,resultBitmap)
        }
    }
    private fun drawViewToCanvas(view: CardView, canvas: Canvas) {
        // Measure and layout the view

//        view.measure( resources.getDimensionPixelSize(R.dimen.card_width),
//            resources.getDimensionPixelSize(R.dimen.card_height))
//        view.layout(0, 0, resources.getDimensionPixelSize(R.dimen.card_width),
//            resources.getDimensionPixelSize(R.dimen.card_height))
        val cardWidth =  canvas.width
        val cardHeight = view.height
        val canvasWidth = canvas.width
        val canvasHeight = canvas.height

// Calculate left position to center the view horizontally at the bottom of the canvas
        val left = (canvasWidth - cardWidth) / 2

// Calculate top position to place the view at the bottom of the canvas
        val top = canvasHeight - cardHeight


        // Draw the view onto the canvas
        canvas.translate(left.toFloat(), top.toFloat())
// Set the position for the view
        view.layout(left, top, left + cardWidth, top + cardHeight)

// Draw the view onto the canvas
        view.draw(canvas)
    }
    private fun saveCompositeImage(bitmap: Bitmap) {
        val outputFile = getOutputFile()
        try {
            FileOutputStream(outputFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                Log.d("TAG", "Composite image saved successfully: ${outputFile.absolutePath}")
                val resultIntent = Intent()
                resultIntent.putExtra("key_result", "${outputFile.absolutePath}")
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        } catch (e: IOException) {
            Log.e("TAG", "Error saving composite image: ${e.message}", e)
        }
    }
    fun saveImageToCameraFolder(context: Context, bitmap: Bitmap): Boolean {
        // Check if external storage is available
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            return false
        }

        // Get the directory for saving images
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val folder = File(directory, "Camera")
        if (!folder.exists()) {
            folder.mkdirs()
        }

        // Create a file name for the image
        val fileName = "IMG_${System.currentTimeMillis()}.jpg"
        val file = File(folder, fileName)

        // Save the bitmap to the file
        var outputStream: OutputStream? = null
        try {
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            // Insert image into the MediaStore
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Camera")
            }
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            outputStream?.close()
        }
    }
    private fun getOutputFile(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return File(mediaDir, "${System.currentTimeMillis()}.jpg")
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap
    }

    private fun updateMap(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        if (checkLocationPermission()) {
            googleMap.isMyLocationEnabled = true
            googleMap.addMarker(MarkerOptions().position(latLng).title("User Location"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        } else {
            showPermissionDeniedDialog()
        }
    }

    private fun showPermissionDeniedDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permission Denied")
            .setMessage("Permissions are necessary for the app to function. Please enable them in the app settings.")
            .setPositiveButton("Go to Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel") { _, _ ->

            }
            .show()
    }


    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


}