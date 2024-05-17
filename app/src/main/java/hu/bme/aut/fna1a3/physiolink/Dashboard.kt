package hu.bme.aut.fna1a3.physiolink

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import data.AuthServices
import data.PatientDataHolder

class Dashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        setUpUserData()
    }

    private fun setUpUserData()
    {
        AuthServices.setUserData(
            onSuccess = {patientModel ->
                PatientDataHolder.setLoggedInPatient(patientModel)
                Log.d("User", "" + patientModel.id)
            },
            onFailure = {errorMessage ->
                Log.e("Dashboard", "$errorMessage")
            }
        )
    }
}