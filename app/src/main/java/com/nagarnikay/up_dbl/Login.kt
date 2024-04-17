package com.nagarnikay.up_dbl

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.JsonObject
import com.nagarnikay.up_dbl.Adapter.SchemesAdapter
import com.nagarnikay.up_dbl.Model.LoginResponse
import com.nagarnikay.up_dbl.Model.SchemeData
import com.nagarnikay.up_dbl.Model.SchemeItem
import com.nagarnikay.up_dbl.Retrofit.ApiClient
import com.nagarnikay.up_dbl.Retrofit.ApiInterface
import com.nagarnikay.up_dbl.Utils.CustomProgress
import com.nagarnikay.up_dbl.Utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {

    private lateinit var spinnerImage: Spinner
    private lateinit var schemeId: String
    private lateinit var schemeName: String
    private lateinit var btnLogin: Button
    private lateinit var etPassword: EditText
    private lateinit var etUserName: EditText
    private lateinit var customProgress: CustomProgress
    private lateinit var schemesAdapter: SchemesAdapter
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        customProgress = CustomProgress
        spinnerImage = findViewById(R.id.spinner_imagetype)
        etUserName = findViewById(R.id.etUserName)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        spinnerImage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                schemeId = (adapterView?.selectedItem as SchemeItem).schemeId.toString()
                schemeName = (adapterView?.selectedItem as SchemeItem).schemename.toString()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        btnLogin.setOnClickListener {
            if (dataValidate()) {
                getLoginResult()
            }
        }

        etPassword.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == android.view.MotionEvent.ACTION_UP) {
                val drawableEnd = etPassword.right - etPassword.compoundDrawables[2].bounds.width()
                if (motionEvent.rawX >= drawableEnd) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

        fetchSchemes()
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            val hideDrawable = ContextCompat.getDrawable(this, R.drawable.eye_close_ic)
            etPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, hideDrawable, null)
        } else {
            etPassword.transformationMethod = null
            val showDrawable = ContextCompat.getDrawable(this, R.drawable.eye_open_ic)
            etPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, showDrawable, null)
        }
        isPasswordVisible = !isPasswordVisible
    }

    private fun getLoginResult() {
        customProgress.showProgress(this, "Logging in...", false)
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val jsonObject = getLoginJsonObj()
        Log.d("TAG", "getLoginResult: $jsonObject")
        val call: Call<LoginResponse> = apiService.getLogin(jsonObject)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d("TAG", "onResponse: " + response.body()?.respMessage)
                if (response.isSuccessful && response.body()?.respCode == "101" && response.body()?.respMessage.equals("success")) {
                    MySharedPreferences.saveLoginObject(applicationContext, response.body()!!)
                    customProgress.hideProgress()
                    val intent = Intent(this@Login, HomeActivity::class.java)
                    intent.putExtra("SchName",schemeName)
                    intent.putExtra("SchId",schemeId)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@Login, response.body()?.respMessage, Toast.LENGTH_SHORT).show()
                    customProgress.hideProgress()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@Login, "Please enter correct username and password", Toast.LENGTH_SHORT).show()
                customProgress.hideProgress()
            }
        })
    }

    private fun getLoginJsonObj(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ApiUserName", "CMNSYUSER")
        jsonObject.addProperty("Token", "12345")
        jsonObject.addProperty("username", etUserName.text.toString().trim())
        jsonObject.addProperty("Password", etPassword.text.toString().trim())
        jsonObject.addProperty("Schemeid", schemeId)
        return jsonObject
    }

    private fun dataValidate(): Boolean {
        if (etUserName.text.toString().trim().length < 3) {
            etUserName.error = "Please enter username"
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show()
            return false
        } else if (etPassword.text.toString().trim().length < 3) {
            etPassword.error = "Please enter password"
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun fetchSchemes() {
        customProgress.showProgress(this, "Please wait..", false)
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call: Call<SchemeData> = apiService.getSchemes("CMNSYUSER", "12345", "1")
        call.enqueue(object : Callback<SchemeData> {
            override fun onResponse(call: Call<SchemeData>, response: Response<SchemeData>) {
                if (response.isSuccessful) {
                    val list: MutableList<SchemeItem> = ArrayList()
                    response.body()?.data?.let { list.addAll(it) }
                    schemesAdapter = SchemesAdapter(this@Login, list)
                    spinnerImage.adapter = schemesAdapter
                    customProgress.hideProgress()
                } else {
                    Toast.makeText(this@Login, "Something went wrong...", Toast.LENGTH_SHORT).show()
                    customProgress.hideProgress()
                }
            }

            override fun onFailure(call: Call<SchemeData>, t: Throwable) {
                Toast.makeText(this@Login, "Something went wrong...", Toast.LENGTH_SHORT).show()
                customProgress.hideProgress()
            }
        })
    }
}
