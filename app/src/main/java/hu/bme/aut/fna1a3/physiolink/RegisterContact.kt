package hu.bme.aut.fna1a3.physiolink

import PatientModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import data.AuthServices
import hu.bme.aut.fna1a3.physiolink.databinding.ActivityRegisterContactBinding
import java.util.Calendar
import java.util.Date

class RegisterContact : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterContactBinding

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var phoneNumber: String
    private lateinit var dateOfBirth: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        setUpNumbers()

        binding.btnNext.setOnClickListener {
            checkValues()
        }

        binding.imBack.setOnClickListener {
            startActivity(Intent(this, InvitationCode::class.java))
        }

    }

    private fun checkValues()
    {
        val isNameValid = validName()
        val isEmailValid = validEmail()
        val isNumberValid = validNumber()
        val isDOBValid = validDOB()

        if( isNameValid && isEmailValid && isNumberValid && isDOBValid)
        {
            addPatientToDb()
        }
    }

    private fun addPatientToDb()
    {
        val doctorID = intent.getStringExtra("doctorID")
        val code = intent.getStringExtra("Code")

        val patient = PatientModel()
        if (doctorID != null) {
            patient.setValues(name, email, phoneNumber, dateOfBirth,  doctorID)
        }

        AuthServices.addPatient(patient){ patientDocumentId ->
            // Callback function invoked after the client is added successfully

            patientDocumentId?.let {
                Log.d("Invite", "Client added with ID: $patientDocumentId")
                val intent = Intent(this, RegisterPassword::class.java)
                intent.putExtra ("patientDocumentId", patientDocumentId)
                intent.putExtra("code", code)
                intent.putExtra("email", email)
                startActivity(intent)

                startActivity(intent)
            } ?: run {
                Log.e("Invite", "Failed to add client to Firestore")
                // Handle failure to add client
            }
        }

    }


    private fun setUpNumbers()
    {
        binding.numberPickerDay.minValue= 1
        binding.numberPickerDay.maxValue= 31

        binding.numberPickerMonth.minValue=1
        binding.numberPickerMonth.maxValue = 12

        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        binding.numberPickerYear.minValue= 1900
        binding.numberPickerYear.maxValue= currentYear
        binding.numberPickerYear.value= 2000
    }

    //input validation

    private fun validName(): Boolean{
        name = binding.etName.text.toString().trim()
        val regex = Regex("^[a-zA-Z ]+\$")
        return if (name == "") {
            binding.etName.error = "Name cannot be empty"
            false
        } else if (!regex.matches(name)) {
            binding.etName.error = "Name can only contain letters and spaces"
            false
        } else {
            binding.etName.error = null
            true
        }
    }

    private fun validNumber(): Boolean
    {
        phoneNumber = binding.etNumber.text.toString()
        phoneNumber = phoneNumber.replace("\\s+".toRegex(), "")//removing all the spaces in the string
        var length = phoneNumber.length
        var alldig = phoneNumber.all{it.isDigit()}
        return if (alldig&& length == 10) {
            binding.etNumber.error =null
            true
        } else {
            binding.etNumber.error = "Invalid number"
            false
        }
    }

    private fun validEmail(): Boolean{
        email = binding.etEmail.text.toString().trim()
        val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,})+\$")
        if(!emailRegex.matches(email))
        {
            binding.etEmail.error = "Incorrect email format"
        }
        else
        {
            binding.etEmail.error = null
        }
        return emailRegex.matches(email)
    }

    private fun validDOB(): Boolean{
        val day = binding.numberPickerDay.value
        val month = binding.numberPickerMonth.value
        val year = binding.numberPickerYear.value

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        dateOfBirth = calendar.time
        return true
    }
}