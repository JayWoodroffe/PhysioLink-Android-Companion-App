package data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import model.ExerciseModel

object ExerciseDataAccess {
    private var patientId = PatientDataHolder.getLoggedInPatient()?.id.toString();
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("exercises")

    fun getExercises(retired: Boolean, callback: (List<ExerciseModel>) -> Unit) {
        val exercises = mutableListOf<ExerciseModel>()

        try {
            collection.whereEqualTo("clientId", patientId)
                .whereEqualTo("retired", retired)
                .get().addOnSuccessListener { results ->
                    for (document in results.documents) {
                        val id = document.getLong("id")?.toInt() ?: 0
                        val name = document.getString("name") ?: ""
                        val description = document.getString("description") ?: ""
                        val sets = document.getLong("sets")?.toInt() ?: 0
                        val reps = document.getLong("reps")?.toInt() ?: 0
                        val patientId = document.getString("clientId") ?: ""
                        val doctorId = document.getString("doctorId") ?: ""
                        val retired = document.getBoolean("retired") ?: false
                        val exercise = ExerciseModel(id, name, description, sets, reps, patientId, doctorId, retired)
                        exercises.add(exercise)
                    }
                    callback(exercises)
                }
                .addOnFailureListener { exception ->
                    exception.message?.let { Log.d("Exercises", it) }
                    callback(emptyList())
                }
        } catch (e: Exception) {
            e.message?.let {
                Log.d("Exercises", it)
                callback(emptyList())
            }
        }
    }


}