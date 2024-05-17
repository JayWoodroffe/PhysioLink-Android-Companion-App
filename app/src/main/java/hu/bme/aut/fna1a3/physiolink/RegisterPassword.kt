package hu.bme.aut.fna1a3.physiolink

import PatientModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import data.AuthServices
import hu.bme.aut.fna1a3.physiolink.databinding.ActivityRegisterPasswordBinding

class RegisterPassword : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        binding.btnFinish.setOnClickListener{
            var p1 = binding.etPassword1.text.toString()
            var p2 = binding.etPassword2.text.toString()

            //check if the password matches the requirements
            if(validPassword(p1) && passwordMatch(p1, p2))
            {
                val patientDocumentId = intent.getStringExtra("patientDocumentId")
                Log.e("Invite", "patient id received $patientDocumentId")
                val patient = PatientModel()
                val email = intent.getStringExtra("email")
                val pw = p1
                Log.d("Invite", "email addr $email")
                if (patientDocumentId != null && email !=null) {
                    Log.d("Invite", "here")
                    AuthServices.createUserWithEmailAndPassword(email, pw, patientDocumentId) { success ->
                        if (success) {
                            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                            Log.e("Invite", "Registration successful!")

                            val code = intent.getStringExtra("code")
                            if (code != null) {
                                AuthServices.deleteInvitationCode(code){success->
                                    if(success){
                                        Log.d("Invite", "Invitation Deleted")
                                    }
                                    else{
                                        Log.d("Invite", "failed to delete Invitation ")
                                    }
                                }
                            }

                            startActivity(Intent(this, Login::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show()
                            Log.e("Invite", "Registration failed. Please try again.")
                        }
                    }
                }
            }
        }
    }

    private fun validPassword(pw: String): Boolean
    {
        val specialCharRegex = Regex("[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]")
        val digitRegex = Regex("\\d")

        //checking if the password matches the requirements for a safe password
        var valid: Boolean = true
        if( pw.length < 8 )
        {
            binding.etPassword1.error = "Password too short. Must be 8 characters or more."
            valid = false
        }
        if (!specialCharRegex.containsMatchIn(pw))
        {
            binding.etPassword1.error = "Password should contain a special character"
            valid = false
        }
        if(!digitRegex.containsMatchIn(pw)) {
            binding.etPassword1.error = "Password should contain a digit"
            valid = false
        }
        if(valid)
        {
            binding.etPassword1.error= null
        }
        return valid
    }

    private fun passwordMatch(pw1: String, pw2: String): Boolean
    {
        return if (pw1!= pw2) {
            binding.etPassword2.error = "Passwords do not match"
            false
        } else{
            binding.etPassword2.error = null
            true
        }
    }
}