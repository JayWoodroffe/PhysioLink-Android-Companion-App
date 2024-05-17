package model
import java.io.Serializable
data class ExerciseModel (
    val id: Int,
    var name: String,
    var description: String,
    var sets: Int,
    var reps: Int,
    val clientId: String,
    val doctorId: String,
    val retired: Boolean
) : Serializable