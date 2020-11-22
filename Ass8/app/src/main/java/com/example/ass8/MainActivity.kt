package com.example.ass8

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_dialog_layout.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    val employeeList = arrayListOf<Employee>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler_view.layoutManager = LinearLayoutManager(applicationContext) as RecyclerView.LayoutManager?
        recycler_view.itemAnimator = DefaultItemAnimator() as RecyclerView.ItemAnimator?

    }

    override fun onResume() {
        super.onResume()
        callEmployeeData()
    }

    fun addEmployee(v: View){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.add_dialog_layout, null)
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setView(mDialogView)
        val mAlertDialog = mBuilder.show()


        mDialogView.btnAdd.setOnClickListener {
            val api: EmployeeAPI = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EmployeeAPI::class.java)

            var radioGroup: RadioGroup = mDialogView.radioGroup
            var selectedId: Int = radioGroup.checkedRadioButtonId
            var radioButton: RadioButton = mDialogView.findViewById(selectedId)

            api.insertEmp(
                mDialogView.edit_name.text.toString(),
                radioButton.text.toString(),
                mDialogView.edit_email.text.toString(),
                mDialogView.edit_salary.text.toString().toInt()).enqueue(object : Callback<Employee> {

                override fun onResponse(
                    call: Call<Employee>, response: retrofit2.Response<Employee>) {
                    if (response.isSuccessful()) {
                        Toast.makeText(
                            applicationContext,
                            "Successfully Inserted",
                            Toast.LENGTH_SHORT
                        ).show()
                        mAlertDialog.dismiss()
                        onResume()


                    } else {
                        Toast.makeText(applicationContext, "Error ", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onFailure(call: Call<Employee>, t: Throwable) {
                    Toast.makeText(
                        applicationContext, "Error onFailure " + t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }

        mDialogView.btnCancel.setOnClickListener() {
            mAlertDialog.dismiss()
        }
    }

    fun callEmployeeData(){
        employeeList.clear();
        val serv : EmployeeAPI = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EmployeeAPI ::class.java)
        serv.retrieveEmployee()
            .enqueue(object : Callback<List<Employee>> {
                override fun onResponse(call: Call<List<Employee>>, response: Response<List<Employee>>) {
                    response.body()?.forEach {
                        employeeList.add(Employee(it.emp_name, it.emp_gender,it.emp_email,it.emp_salary)) }
//// Set Data to RecyclerRecyclerView
                    recycler_view.adapter = EmployeeAdapter(employeeList,applicationContext)

                }
                override fun onFailure(call: Call<List<Employee>>, t: Throwable) = t.printStackTrace()
            })
    }
}

