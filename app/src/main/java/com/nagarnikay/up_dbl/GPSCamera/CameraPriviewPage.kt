package com.nagarnikay.up_dbl.GPSCamera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.service.controls.ControlsProviderService
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    private fun startCamera() {

        val width = cameraView.width
        val height = cameraView.height


        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        //Get lat and long from viewModel and pass it to overlay
        viewModel.locationUpdates.observe(this, Observer { location ->
            // Remove any existing TextViews from cameraView
            for (i in 0 until cameraView.childCount) {
                val childView = cameraView.getChildAt(i)
                if (childView is TextView) {
                    cameraView.removeView(childView)
                }
            }
            cardView = CardView(this).apply {
                // Set layout parameters to wrap_content
                layoutParams = FrameLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.card_width),
                    resources.getDimensionPixelSize(R.dimen.card_height)
                ).apply {
                    // Align to bottom of parent
                    //gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL

                    // Set margin
                    //bottomMargin = resources.getDimensionPixelSize(R.dimen.card_margin_bottom)
                }
                //  radius = resources.getDimensionPixelSize(R.dimen.card_corner_radius).toFloat()
            }
            var addressUtil: AddressUtil =AddressUtil(this)
            var fullAddress:String="Latitude: ${location.latitude},\n" +
                    "Longitude: ${location.longitude}\n${addressUtil.getAddress(location.latitude,location.longitude)}"
            var textView= TextView(this).apply {
                text = fullAddress
                setTextColor(ContextCompat.getColor(context, android.R.color.black))
                setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
                gravity =  Gravity.CENTER
            }
            cardView.addView(textView)
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
        }
    }
    private fun drawViewToCanvas(view: CardView, canvas: Canvas) {
        // Measure and layout the view

//        view.measure( resources.getDimensionPixelSize(R.dimen.card_width),
//            resources.getDimensionPixelSize(R.dimen.card_height))
//        view.layout(0, 0, resources.getDimensionPixelSize(R.dimen.card_width),
//            resources.getDimensionPixelSize(R.dimen.card_height))
        val cardWidth = resources.getDimensionPixelSize(R.dimen.card_width)
        val cardHeight = resources.getDimensionPixelSize(R.dimen.card_height)
        val canvasWidth = canvas.width
        val canvasHeight = canvas.height
        val left = (canvasWidth - cardWidth) / 2 // Center horizontally
        val top = canvasHeight - cardHeight // At the bottom

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