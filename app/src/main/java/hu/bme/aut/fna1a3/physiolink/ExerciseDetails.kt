package hu.bme.aut.fna1a3.physiolink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.bme.aut.fna1a3.physiolink.databinding.ActivityExerciseDetailsBinding
import model.ExerciseModel

class ExerciseDetails : AppCompatActivity() {

    private lateinit var binding: ActivityExerciseDetailsBinding
    private lateinit var exerciseModel: ExerciseModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        exerciseModel = intent.getSerializableExtra("ExerciseModel") as ExerciseModel
        populateFields(exerciseModel)

        binding.imBack.setOnClickListener {
            startActivity(Intent(this, Exercises::class.java))
        }
    }

    private fun populateFields(exerciseModel: ExerciseModel)
    {
        binding.tvName.text = exerciseModel.name
        binding.tvDescription.text = exerciseModel.description
        binding.tvSetsReps.text = "" +exerciseModel.sets + " x " + exerciseModel.reps

    }
}