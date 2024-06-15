package hu.bme.aut.fna1a3.physiolink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import data.AuthServices
import hu.bme.aut.fna1a3.physiolink.databinding.ActivityInvitationCodeBinding

class InvitationCode : AppCompatActivity() {
    private lateinit var binding: ActivityInvitationCodeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvitationCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        binding.imBack.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }

//        binding.ivHint.setOnClickListener {
//
//        }

        binding.btnProceed.setOnClickListener {
            var code = binding.etInvitationCode.text.toString().trim()
            AuthServices.checkInvitation(code){isValid, doctorID ->
                if (isValid)
                {
                    val intent = Intent(this, RegisterContact::class.java)
                    intent.putExtra("doctorID", doctorID)
                    intent.putExtra("Code",code )
                    startActivity(intent)
                }
                else
                {
                    binding.tvInvalidCode.text = "Invitation code is not valid. Please ask your physiotherapist to send you a new one."
                }
            }
        }
    }
}