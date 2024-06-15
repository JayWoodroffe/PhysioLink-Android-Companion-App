package hu.bme.aut.fna1a3.physiolink

import adapter.ExerciseAdapter
import adapter.ExerciseAdapterListener
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.google.android.material.tabs.TabLayout
import data.ExerciseDataAccess
import hu.bme.aut.fna1a3.physiolink.databinding.ActivityExercisesBinding
import model.ExerciseModel

class Exercises : AppCompatActivity(), ExerciseAdapterListener {
    private lateinit var binding: ActivityExercisesBinding
    private lateinit var exerciseAdapter: ExerciseAdapter

    private val exercises = mutableListOf<ExerciseModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExercisesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGridView()
        displayActiveExercises()
        setUpToggleBar()

        //changing the activity when menu item is selected
        val navigationView = binding.btmNavMenu
        navigationView.selectedItemId = R.id.nav_exercises
        navigationView.setOnItemSelectedListener { item: MenuItem -> handleNavigationItemSelected(item)}

        //hiding the system bottom navigation
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
    }

    private fun setupGridView() {
        exerciseAdapter = ExerciseAdapter(this, exercises, this)
        binding.gridView.adapter = exerciseAdapter
    }

    private fun setUpToggleBar() {
        binding.toggleBar.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.text) {
                        "Active" -> displayActiveExercises()
                        "Retired" -> displayRetiredExercises()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // can handle tab unselected event here if needed
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                //  can handle tab reselected event here if needed
            }
        })
    }
    private fun displayActiveExercises()
    {
        showLoadingIndicator()
        ExerciseDataAccess.getExercises (false){ exerciseList ->
            hideLoadingIndicator()
            updateExerciseList(exerciseList)
        }
    }

    private fun displayRetiredExercises()
    {
        ExerciseDataAccess.getExercises (true) { exerciseList ->
            updateExerciseList(exerciseList)
        }
    }

    private fun updateExerciseList(exerciseList: List<ExerciseModel>) {
        exercises.clear()
        exercises.addAll(exerciseList)
        animateGridView()
        exerciseAdapter.notifyDataSetChanged()
    }

    private fun animateGridView() {
        val initialY = binding.gridView.translationY + 400
        val newY = initialY - 400
        val flyUp = ObjectAnimator.ofFloat(binding.gridView, "translationY", initialY, newY)
        flyUp.duration = 300
        val animSet = AnimatorSet()
        animSet.play(flyUp)
        animSet.start()
    }

    override fun onItemClick(position: Int) {
        val exercise = exercises[position]
        val intent = Intent(this, ExerciseDetails::class.java).apply {
            putExtra("ExerciseModel", exercise)
        }
        startActivity(intent)
    }

    private fun handleNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.nav_home ->{
                startActivity(Intent(this, Dashboard::class.java))
                true
            }
            R.id.nav_chat ->{
                startActivity(Intent(this, Chat::class.java))
                true
            }
            R.id.nav_exercises ->{
                true
            }
//            R.id.nav_settings ->{
//                //TODO settings
//                true
//            }
            else -> false
        }
    }

    private fun showLoadingIndicator() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingIndicator() {
        binding.progressBar.visibility = View.GONE
    }
}



