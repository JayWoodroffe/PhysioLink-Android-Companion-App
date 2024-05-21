package hu.bme.aut.fna1a3.physiolink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import data.AuthServices
import data.PatientDataHolder
import hu.bme.aut.fna1a3.physiolink.databinding.ActivityDashboardBinding

class Dashboard : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        setUpUserData()

        //changing the activity when menu item is selected
        val navigationView = binding.btmNavMenu
        navigationView.selectedItemId = R.id.nav_home
        navigationView.setOnItemSelectedListener { item: MenuItem -> handleNavigationItemSelected(item)}
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

    private fun handleNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.nav_home ->{
                true
            }
            R.id.nav_chat ->{
                startActivity(Intent(this, Chat::class.java))
                true
            }
            R.id.nav_exercises ->{
                startActivity(Intent(this, Exercises::class.java))
                true
            }
            R.id.nav_settings ->{
                //TODO settings
                true
            }
            else -> false
        }
    }
}