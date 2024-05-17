package hu.bme.aut.fna1a3.physiolink

import adapter.ExerciseAdapter
import adapter.ExerciseAdapterListener
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        //TODO set up navigation via btm nav
    }

    private fun setupGridView() {
        exerciseAdapter = ExerciseAdapter(this, exercises, this)
        binding.gridView.adapter = exerciseAdapter
    }

    private fun displayActiveExercises()
    {
        ExerciseDataAccess.getExercises (false){ exerciseList ->
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
    }
}