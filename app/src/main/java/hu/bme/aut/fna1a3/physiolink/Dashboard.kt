package hu.bme.aut.fna1a3.physiolink

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import data.AppointmentsDataAccess
import data.AuthServices
import data.PatientDataHolder
import hu.bme.aut.fna1a3.physiolink.databinding.ActivityDashboardBinding
import model.Appointment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
        binding.tvGreeting.setText(getGreeting())
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
                setUpGreeting()
                Log.d("User", "" + patientModel.id)
            },
            onFailure = {errorMessage ->
                Log.e("Dashboard", "$errorMessage")
            }
        )
    }

    private fun setUpGreeting()
    {
        var currentUser =PatientDataHolder.getLoggedInPatient()
        if(currentUser!=null)
        {
            binding.tvName.setText( currentUser!!.name)
            //setting up the appt display
            currentUser!!.name?.let {

                AppointmentsDataAccess.getNextAppointment(currentUser!!.name){ appointment ->
                    if(appointment!= null){
                        updateUIWithAppointment(appointment)
                    } else{
                        showNoAppointmentsMessage()
                    }
                }
            }
        }
    }

    private fun updateUIWithAppointment(appointment: Appointment)
    {
        binding.layoutAppt.visibility = View.VISIBLE
        binding.tvDate.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(appointment.time)
        binding.tvTime.text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(appointment.time)
    }

    private fun showNoAppointmentsMessage()
    {
        binding.layoutAppt.visibility = View.GONE
    }

    fun getGreeting():String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        return when {
            hour in 0..11 -> "good morning"
            hour in 12..16 -> "good afternoon"
            hour in 17..23 -> "good evening"
            else -> ""
        }

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
//            R.id.nav_settings ->{
//                //TODO settings
//                true
//            }
            else -> false
        }
    }
}