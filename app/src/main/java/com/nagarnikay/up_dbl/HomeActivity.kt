package com.nagarnikay.up_dbl

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nagarnikay.up_dbl.Adapter.ProjectAdapterSpinner
import com.nagarnikay.up_dbl.Adapter.SchemesAdapter
import com.nagarnikay.up_dbl.Adapter.WorkStatusAdapter
import com.nagarnikay.up_dbl.Adapter.YearSpinnerAdapter
import com.nagarnikay.up_dbl.GPSCamera.CameraPriviewPage
import com.nagarnikay.up_dbl.Location.GpsTracker
import com.nagarnikay.up_dbl.Model.AllProjectsResponse
import com.nagarnikay.up_dbl.Model.DataSubmitResponse
import com.nagarnikay.up_dbl.Model.FIData
import com.nagarnikay.up_dbl.Model.FinancialYearResponse
import com.nagarnikay.up_dbl.Model.LoginResponse
import com.nagarnikay.up_dbl.Model.ProjectData
import com.nagarnikay.up_dbl.Model.SchemeData
import com.nagarnikay.up_dbl.Model.WorkStatusData
import com.nagarnikay.up_dbl.Model.WorkStatusResponse
import com.nagarnikay.up_dbl.Retrofit.ApiClient
import com.nagarnikay.up_dbl.Retrofit.ApiInterface
import com.nagarnikay.up_dbl.Utils.CustomProgress
import com.nagarnikay.up_dbl.Utils.DateUtils
import com.nagarnikay.up_dbl.Utils.ImageDialogFragment
import com.nagarnikay.up_dbl.Utils.ImageUtils
import com.nagarnikay.up_dbl.Utils.MySharedPreferences
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*


class HomeActivity : AppCompatActivity() {

    private lateinit var spinWorkStatus: AppCompatSpinner
    private lateinit var spinScheme: AppCompatSpinner
    private lateinit var spinFI: AppCompatSpinner
    private lateinit var spinProject: AppCompatSpinner
    private lateinit var txtLongitude: TextView
    private lateinit var txtLattitude: TextView
    private lateinit var llAfterWorkImage: LinearLayout
    private lateinit var llBeforeWorkImage: LinearLayout
    private lateinit var aftImageView: ImageView
    private lateinit var befImageView: ImageView
    private lateinit var btnSubmintData: Button
    private lateinit var etAftWorkRemark: EditText
    private lateinit var etBefWorkRemark: EditText
    private lateinit var etFinanceWorkPercent: EditText
    private lateinit var etPhysicalWorkPercent: EditText
    private lateinit var txtOfficeCode: TextView
    private lateinit var username: TextView
    private lateinit var btn_logout: AppCompatButton
    private lateinit var fab_reports: FloatingActionButton
    private lateinit var customProgress: CustomProgress
    private lateinit var loginResponse: LoginResponse
    private lateinit var gpsTracker: GpsTracker
    private lateinit var yearSpinnerAdapter: YearSpinnerAdapter
    private lateinit var workStatusAdapter: WorkStatusAdapter
    private lateinit var schemesAdapter: SchemesAdapter
    private lateinit var schemeData: SchemeData
    private lateinit var fiData: FIData

    private var currentPhotoPathBefWork: String = ""
    private var currentPhotoPathAftWork: String = ""
    private var ImageTye: Int = 0
    private val REQUEST_IMAGE_CAPTURE_BEFORE_WORK = 100
    private val REQUEST_IMAGE_CAPTURE_AFTER_WORK = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        if (supportActionBar != null) {
            supportActionBar?.hide()
        }
        customProgress = CustomProgress
        loginResponse = MySharedPreferences.getLoginObject(this, LoginResponse::class.java)!!
        gpsTracker = GpsTracker(this)
        initView()
        initListeners()

    }

    private fun initView() {
        txtLongitude = findViewById(R.id.txtLongitude)
        btn_logout = findViewById(R.id.btn_logout)
        fab_reports = findViewById(R.id.fab_reports)
        txtLattitude = findViewById(R.id.txtLattitude)
        befImageView = findViewById(R.id.befImageView)
        aftImageView = findViewById(R.id.aftImageView)
        etPhysicalWorkPercent = findViewById(R.id.etPhysicalWorkPercent)
        etFinanceWorkPercent = findViewById(R.id.etFinanceWorkPercent)
        txtLattitude.text = DecimalFormat("#.###", DecimalFormatSymbols(Locale.US)).format(gpsTracker.getLatitude())
        txtLongitude.text = DecimalFormat("#.###", DecimalFormatSymbols(Locale.US)).format(gpsTracker.getLongitude())
        spinFI = findViewById(R.id.spinFI)
        spinScheme = findViewById(R.id.spinScheme)
        spinWorkStatus = findViewById(R.id.spinWorkStatus)
        llBeforeWorkImage = findViewById(R.id.llBeforeWorkImage)
        llAfterWorkImage = findViewById(R.id.llAfterWorkImage)
        spinProject = findViewById(R.id.spinProject)
        etBefWorkRemark = findViewById(R.id.etBefWorkRemark)
        etAftWorkRemark = findViewById(R.id.etAftWorkRemark)
        txtOfficeCode = findViewById(R.id.txtOfficeCode)
        username = findViewById(R.id.username)
        username.text = loginResponse.officeName.toString()
        txtOfficeCode.text = loginResponse.officeId.toString()
        btnSubmintData = findViewById(R.id.btnSubmintData)
    }

    private fun initListeners() {
        btn_logout.setOnClickListener {
            startActivity(Intent(this@HomeActivity, Login::class.java))
            finish()
        }
        fab_reports.setOnClickListener {
            startActivity(Intent(this@HomeActivity, ProjectReports::class.java))
        }
        btnSubmintData.setOnClickListener {
            if (dataValidated()) {
                submitData()
            }
        }
        spinFI.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                fiData = (adapterView?.selectedItem as FIData)
                loginResponse.schemeid?.let { fetchWorkStatus(it) }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        spinScheme.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                schemeData = (adapterView?.selectedItem as SchemeData)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        spinWorkStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                fetchAllProject(loginResponse.schemeid!!, fiData.yearId!!)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        llBeforeWorkImage.setOnClickListener {
//            ImageTye = 1
//            dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE_BEFORE_WORK)
            val intent: Intent = Intent(
                this@HomeActivity,
                CameraPriviewPage::class.java
            )
            startActivityForResult(intent, 1100)
          //  startActivity(Intent(this@HomeActivity, CameraPriviewPage::class.java))
        }
        llAfterWorkImage.setOnClickListener {
//            ImageTye = 2
//            dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE_AFTER_WORK)

            val intent: Intent = Intent(
                this@HomeActivity,
                CameraPriviewPage::class.java
            )
            startActivityForResult(intent, 1200)
        }
        befImageView.setOnClickListener {
            val dialogFragment = ImageDialogFragment.newInstance(currentPhotoPathBefWork)
            dialogFragment.show(supportFragmentManager, "ImageDialogFragment")
        }
        aftImageView.setOnClickListener {
            val dialogFragment = ImageDialogFragment.newInstance(currentPhotoPathAftWork)
            dialogFragment.show(supportFragmentManager, "ImageDialogFragment")
        }
        fetchFI()
    }

    private fun submitData() {
        customProgress.showProgress(this@HomeActivity, "Data Submitting...", false)
        val projectId = (spinProject.selectedItem as ProjectData).projectId
        val fiId = (spinFI.selectedItem as FIData).yearId
        val workStatusId = (spinWorkStatus.selectedItem as WorkStatusData).photoTypeId
        val schemeId = loginResponse.schemeid

        val image_befWorkBody = RequestBody.create(MediaType.parse("image/*"), ImageUtils.compressImage(currentPhotoPathBefWork))
        val befWorkImagePart = MultipartBody.Part.createFormData("P2",
            ImageUtils.compressImage(currentPhotoPathBefWork)?.name, image_befWorkBody)

        val image_aftWorkBody = RequestBody.create(MediaType.parse("image/*"), ImageUtils.compressImage(currentPhotoPathAftWork))
        val aftWorkImagePart = MultipartBody.Part.createFormData("P3",
            ImageUtils.compressImage(currentPhotoPathAftWork)?.name, image_aftWorkBody)

        val apiService = ApiClient.getClient().create(ApiInterface::class.java)

        val call = apiService.submitData("CMNSYUSER", "12345", 0,loginResponse.officeId!!, projectId!!,"NA", "NA", "NA", gpsTracker.getLongitude(), gpsTracker.getLatitude(), etBefWorkRemark.text.toString(), etAftWorkRemark.text.toString(), fiId!!, 1, schemeId!!, etPhysicalWorkPercent.text.toString().trim().toFloat(), etFinanceWorkPercent.text.toString().trim().toFloat(), workStatusId!!, DateUtils.getCurrentDateAsString(), befWorkImagePart, befWorkImagePart, aftWorkImagePart)

        call.enqueue(object : Callback<DataSubmitResponse> {
                override fun onResponse(call: Call<DataSubmitResponse>, response: Response<DataSubmitResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@HomeActivity, response.body()?.respMessage, Toast.LENGTH_SHORT).show()
                        AlertDialog.Builder(this@HomeActivity)
                            .setTitle("Submit Response")
                            .setMessage("(${response.body()?.respCode}) ${response.body()?.respMessage}")
                            .setPositiveButton("OK") { dialog, which ->
                                clearAllField()
                                dialog.dismiss()
                            }
                            .setCancelable(false)
                            .show()
                        customProgress.hideProgress()
                    } else {
                        Toast.makeText(this@HomeActivity, "Something went wrong...", Toast.LENGTH_SHORT).show()
                        customProgress.hideProgress()
                    }
                }

                override fun onFailure(call: Call<DataSubmitResponse>, t: Throwable) {
                    Toast.makeText(this@HomeActivity, "" + t.message, Toast.LENGTH_SHORT).show()
                    customProgress.hideProgress()
                }
            })

    }

    private fun clearAllField() {
        befImageView.visibility = View.GONE
        currentPhotoPathBefWork = ""
        aftImageView.visibility = View.GONE
        currentPhotoPathAftWork = ""
        etBefWorkRemark.text.clear()
        etAftWorkRemark.text.clear()
        etPhysicalWorkPercent.text.clear()
        etFinanceWorkPercent.text.clear()
    }

    private fun dataValidated(): Boolean {
        return when {
            (spinWorkStatus.selectedItem as WorkStatusData).type == "--Select--" -> {
                Toast.makeText(this, "Please choose correct work status", Toast.LENGTH_SHORT).show()
                false
            }
            currentPhotoPathBefWork.isEmpty() -> {
                Toast.makeText(this, "Please Capture Before Work Image", Toast.LENGTH_SHORT).show()
                false
            }
            etBefWorkRemark.text.toString().trim().length < 4 -> {
                Toast.makeText(this, "Please Enter Valid Before work status", Toast.LENGTH_SHORT).show()
                false
            }
            currentPhotoPathAftWork.isEmpty() -> {
                Toast.makeText(this, "Please Capture After Work Image", Toast.LENGTH_SHORT).show()
                false
            }
            etAftWorkRemark.text.toString().trim().length < 4 -> {
                Toast.makeText(this, "Please Enter Valid After work status", Toast.LENGTH_SHORT).show()
                false
            }
            etPhysicalWorkPercent.text.toString().trim().isEmpty() -> {
                Toast.makeText(this, "Please Enter Valid Physical work percentage", Toast.LENGTH_SHORT).show()
                false
            }
            etFinanceWorkPercent.text.toString().trim().isEmpty() -> {
                Toast.makeText(this, "Please Enter Valid Financial work percentage", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun fetchAllProject(schemeId: Int, yearId: Int) {
        customProgress.showProgress(this@HomeActivity, "Just a second...", false)
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiService.getAllProjects("CMNSYUSER", "12345", "1", loginResponse.officeId.toString(), yearId.toString(), schemeId.toString())
        call.enqueue(object : Callback<AllProjectsResponse> {
            override fun onResponse(call: Call<AllProjectsResponse>, response: Response<AllProjectsResponse>) {
                if (response.isSuccessful) {
                    val list = response.body()?.data ?: emptyList()
                    val projectAdapterSpinner = ProjectAdapterSpinner(this@HomeActivity, list)
                    spinProject.adapter = projectAdapterSpinner
                    customProgress.hideProgress()
                } else {
                    Toast.makeText(this@HomeActivity, "Something went wrong!!\nProjects not fetched", Toast.LENGTH_SHORT).show()
                    customProgress.hideProgress()
                }
            }

            override fun onFailure(call: Call<AllProjectsResponse>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Something went wrong!!\nProjects not fetched", Toast.LENGTH_SHORT).show()
                customProgress.hideProgress()
            }
        })
    }

    private fun fetchWorkStatus(schemeId: Int) {
        customProgress.showProgress(this, "Please wait...", false)

        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiService.getWorkStatus("CMNSYUSER", "12345", "2", schemeId.toString())
        call.enqueue(object : Callback<WorkStatusResponse> {
            override fun onResponse(call: Call<WorkStatusResponse>, response: Response<WorkStatusResponse>) {
                Log.d("TAG", "onResponse: " + response.body()?.respMessage)
                if (response.isSuccessful && response.body()?.data != null) {
                    val workStatusResponse = response.body()
                    workStatusAdapter = WorkStatusAdapter(this@HomeActivity, workStatusResponse?.data ?: emptyList())
                    spinWorkStatus.adapter = workStatusAdapter
                    customProgress.hideProgress()
                } else {
                    Toast.makeText(this@HomeActivity, "Something went wrong!!\nWork Status not fetched", Toast.LENGTH_SHORT).show()
                    customProgress.hideProgress()
                }
            }

            override fun onFailure(call: Call<WorkStatusResponse>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Something went wrong!!\nWork Status not fetched", Toast.LENGTH_SHORT).show()
                customProgress.hideProgress()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE_BEFORE_WORK && resultCode == RESULT_OK) {
            val imgFile = File(currentPhotoPathBefWork)
            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                befImageView.setImageBitmap(myBitmap)
                befImageView.visibility = View.VISIBLE
            }
            Toast.makeText(this, "Image saved to: $currentPhotoPathBefWork", Toast.LENGTH_SHORT).show()
        } else if (requestCode == REQUEST_IMAGE_CAPTURE_AFTER_WORK && resultCode == RESULT_OK) {
            val imgFile = File(currentPhotoPathAftWork)
            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                aftImageView.setImageBitmap(myBitmap)
                aftImageView.visibility = View.VISIBLE
            }
            Toast.makeText(this, "Image saved to: $currentPhotoPathAftWork", Toast.LENGTH_SHORT).show()
        }else if (requestCode == 1100) {
            if (resultCode == RESULT_OK) {
                // Handle the result received from the second activity
                if (data != null) {
                    val result = data.getStringExtra("key_result");
                    currentPhotoPathBefWork=result!!;
                    Log.d("TAG", "onActivityResult: "+result)
                    befImageView.setImageBitmap(loadAndConvertImage(result!!.trim()))
                    befImageView.visibility = View.VISIBLE

                }
            } else if (resultCode == RESULT_CANCELED) {
                // Handle if the user canceled the operation
            }
        }else if (requestCode == 1200) {
            if (resultCode == RESULT_OK) {
                // Handle the result received from the second activity
                if (data != null) {
                    val result = data.getStringExtra("key_result");
                    currentPhotoPathAftWork=result!!;
                    Log.d("TAG", "onActivityResult: "+result)
                    aftImageView.setImageBitmap(loadAndConvertImage(result!!.trim()))
                    aftImageView.visibility = View.VISIBLE

                    // Do something with the result
                }
            } else if (resultCode == RESULT_CANCELED) {
                // Handle if the user canceled the operation
            }
        }
    }

//    private fun fetchSchemes(yearId: Int) {
//        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
//        val call = apiService.getSchemes("CMNSYUSER", "12345", yearId.toString())
//        call.enqueue(object : Callback<SchemeData> {
//            override fun onResponse(call: Call<SchemeData>, response: Response<SchemeData>) {
//                if (response.isSuccessful) {
//                    val list = ArrayList<SchemeData>()
//                    list.add(response.body()!!)
//                    schemesAdapter = SchemesAdapter(this@HomeActivity, list)
//                    spinScheme.adapter = schemesAdapter
//                }
//            }
//
//            override fun onFailure(call: Call<SchemeData>, t: Throwable) {
//                // Handle failure
//            }
//        })
//    }
    private fun loadAndConvertImage(filePath: String): Bitmap? {
        // Load the image file from the file path

        // Convert the image to another format (e.g., PNG)
        // Here, we are just returning the original bitmap without conversion
        return BitmapFactory.decodeFile(filePath)
    }
    private fun fetchFI() {
        customProgress.showProgress(this@HomeActivity, "Data Loading...", false)
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiService.getFinancial("CMNSYUSER", "12345", "1", loginResponse.schemeid.toString())
        call.enqueue(object : Callback<FinancialYearResponse> {
            override fun onResponse(call: Call<FinancialYearResponse>, response: Response<FinancialYearResponse>) {
                if (response.isSuccessful) {
                    val financialYearResponse = response.body()
                    yearSpinnerAdapter = YearSpinnerAdapter(this@HomeActivity, financialYearResponse?.data ?: emptyList())
                    spinFI.adapter = yearSpinnerAdapter
                    customProgress.hideProgress()
                } else {
                    customProgress.hideProgress()
                    fetchFI()
                }
            }

            override fun onFailure(call: Call<FinancialYearResponse>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Something went wrong we are not fetching financial years...", Toast.LENGTH_SHORT).show()
                customProgress.hideProgress()
            }
        })
    }

    private fun dispatchTakePictureIntent(reqCode: Int) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                try {
                    val photoFile: File? = createImageFile()
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(this, "com.nagarnikay.up_dbl.provider", it)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, reqCode)
                    }
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_$timeStamp"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            if (ImageTye == 1) {
                currentPhotoPathBefWork = absolutePath
            } else {
                currentPhotoPathAftWork = absolutePath
            }
        }
    }

}