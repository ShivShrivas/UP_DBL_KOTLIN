package com.nagarnikay.up_dbl



import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide


class MainActivity : AppCompatActivity() {
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CAMERA
    )
    private lateinit var image_data:ImageView
    private lateinit var image_data1:ImageView

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allPermissionsGranted = permissions.values.all { it }
            if (allPermissionsGranted) {
                Handler().postDelayed({
                    startActivity(Intent(this@MainActivity, Login::class.java))
                    finish()
                }, 4000)
            } else {
                showPermissionAlertDialog()
            }
        }

    private fun checkPermissions(): Boolean {
        return PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun showPermissionAlertDialog() {
        AlertDialog.Builder(this)
            .setMessage("This app requires all permissions to function properly. Please grant all permissions to continue.")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                finish()
            }
            .create()
            .show()
    }

    private fun requestPermissions() {
        requestPermissionLauncher.launch(PERMISSIONS)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        image_data = findViewById(R.id.image_data)
        image_data1 = findViewById(R.id.image_data1)
        Glide.with(this).load(R.drawable.ofaf).into(image_data)
        Glide.with(this).load(R.drawable.gps).into(image_data1)

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            Handler().postDelayed({
                val intent = Intent(applicationContext, Login::class.java)
                startActivity(intent)
                finish()
            }, 4000)
        }
    }
}
