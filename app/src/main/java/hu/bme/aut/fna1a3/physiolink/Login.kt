package hu.bme.aut.fna1a3.physiolink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import data.AuthServices
import hu.bme.aut.fna1a3.physiolink.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, InvitationCode::class.java))
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            Log.d("LoginActivity", "Login here: $email  $password")
            if (email?.isNullOrEmpty() == true || password?.isNullOrEmpty() == true)
            {
                Toast.makeText(this@Login, "Invalid login details, please try again", Toast.LENGTH_SHORT).show()
            }
            else{
                try {

                    AuthServices.login(email, password,
                        onSuccess = {
                            //login successful
                            Log.d("LoginActivity", "Login here2")
                            startActivity(Intent(this, Dashboard::class.java))
                            finish()
                        },
                        onFailure = {
                            Toast.makeText(
                                this@Login,
                                "Invalid login details, please try again",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("LoginActivity", "Login failed")
                        })
                }catch (e: Exception) {
                    // Handle any unexpected exceptions
                    Log.d("LoginActivity", "Login here3")
                    Toast.makeText(
                        this@Login,
                        "An unexpected error occurred, please try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("LoginActivity", "Unexpected error during login", e)
                }
            }


        }
    }
}