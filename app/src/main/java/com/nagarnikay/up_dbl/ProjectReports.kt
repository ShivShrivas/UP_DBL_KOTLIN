package com.nagarnikay.up_dbl

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.gson.JsonObject
import com.nagarnikay.up_dbl.Adapter.ProjectsRecViewAdapter
import com.nagarnikay.up_dbl.Adapter.SchemesAdapter
import com.nagarnikay.up_dbl.Adapter.YearSpinnerAdapter
import com.nagarnikay.up_dbl.Model.FIData
import com.nagarnikay.up_dbl.Model.FinancialYearResponse
import com.nagarnikay.up_dbl.Model.LoginResponse
import com.nagarnikay.up_dbl.Model.ProjectReportsResponse
import com.nagarnikay.up_dbl.Model.SchemeData
import com.nagarnikay.up_dbl.Retrofit.ApiClient
import com.nagarnikay.up_dbl.Retrofit.ApiInterface
import com.nagarnikay.up_dbl.Utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProjectReports : AppCompatActivity() {
    private lateinit var spinScheme: AppCompatSpinner
    private lateinit var spinFI: AppCompatSpinner
    private lateinit var txtFromDate: TextView
    private lateinit var txtToDate: TextView
    private lateinit var btnGetReport: Button
    private lateinit var recViewReports: RecyclerView
    private lateinit var schemesAdapter: SchemesAdapter
    private lateinit var yearSpinnerAdapter: YearSpinnerAdapter
    private lateinit var loginResponse: LoginResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_reports)

        supportActionBar?.apply {
            title = "UP-DLB Project Reports"
            setDisplayHomeAsUpEnabled(true)
        }

        loginResponse = MySharedPreferences.getLoginObject(this, LoginResponse::class.java)!!

        spinFI = findViewById(R.id.spinFI)
        spinScheme = findViewById(R.id.spinScheme)
        txtToDate = findViewById(R.id.txtToDate)
        txtFromDate = findViewById(R.id.txtFromDate)
        btnGetReport = findViewById(R.id.btnGetReport)
        recViewReports = findViewById(R.id.recViewReports)
        recViewReports.layoutManager = LinearLayoutManager(this)

        txtFromDate.setOnClickListener { showDatePickerDialog(txtFromDate) }
        txtToDate.setOnClickListener { showDatePickerDialog(txtToDate) }

        fetchFI()
        fetchSchemes()

        btnGetReport.setOnClickListener {
            if (dataValidated()) {
                getProjectReport()
            }
        }
    }

    private fun getProjectReport() {
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiService.getProjectReports(getJsonObj())
        call.enqueue(object : Callback<ProjectReportsResponse> {
            override fun onResponse(
                call: Call<ProjectReportsResponse>,
                response: Response<ProjectReportsResponse>
            ) {
                if (response.isSuccessful) {
                    val projectReportsResponse = response.body()
                    Log.d("TAG", "onResponse: ${projectReportsResponse?.respMessage}")
                    val projectsRecViewAdapter =
                        ProjectsRecViewAdapter(this@ProjectReports, projectReportsResponse?.data ?: ArrayList())
                    recViewReports.adapter = projectsRecViewAdapter
                    projectsRecViewAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<ProjectReportsResponse>, t: Throwable) {
                Log.d("TAG", "onFailure: ${t.message}")
            }
        })
    }

    private fun getJsonObj(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("ApiUserName", "CMNSYUSER")
        jsonObject.addProperty("Token", "12345")
        jsonObject.addProperty("OfficeId", 0)
        jsonObject.addProperty("FinYearId", (spinFI.selectedItem as FIData).yearId)
        jsonObject.addProperty("ProcId", 1)
        jsonObject.addProperty("SchemeId", (spinScheme.selectedItem as SchemeData).schemeId)
        jsonObject.addProperty("FromDate", txtFromDate.text.toString().trim())
        jsonObject.addProperty("ToDate", txtToDate.text.toString().trim())
        return jsonObject
    }

    private fun dataValidated(): Boolean {
        if (txtFromDate.text.toString().trim().length != 10) {
            Toast.makeText(this, "Please select fromdate", Toast.LENGTH_SHORT).show()
            return false
        } else if (txtToDate.text.toString().trim().length != 10) {
            Toast.makeText(this, "Please select todate", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun fetchSchemes() {
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiService.getSchemes("CMNSYUSER", "12345", loginResponse.schemeid.toString())
        call.enqueue(object : Callback<SchemeData> {
            override fun onResponse(call: Call<SchemeData>, response: Response<SchemeData>) {
                if (response.isSuccessful) {
                    val list = ArrayList<SchemeData>()
                    list.add(response.body() ?: SchemeData())
                    schemesAdapter = SchemesAdapter(this@ProjectReports, list)
                    spinScheme.adapter = schemesAdapter
                }
            }

            override fun onFailure(call: Call<SchemeData>, t: Throwable) {
            }
        })
    }

    private fun fetchFI() {
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiService.getFinancial("CMNSYUSER", "12345", "1", loginResponse.schemeid.toString())
        call.enqueue(object : Callback<FinancialYearResponse> {
            override fun onResponse(
                call: Call<FinancialYearResponse>,
                response: Response<FinancialYearResponse>
            ) {
                if (response.isSuccessful) {
                    val financialYearResponse = response.body()
                    yearSpinnerAdapter = YearSpinnerAdapter(this@ProjectReports, financialYearResponse?.data ?: ArrayList())
                    spinFI.adapter = yearSpinnerAdapter
                }
            }

            override fun onFailure(call: Call<FinancialYearResponse>, t: Throwable) {
            }
        })
    }

    private fun showDatePickerDialog(textView: TextView) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                textView.text = dateFormat.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }
}
