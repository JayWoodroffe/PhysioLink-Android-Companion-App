package data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import model.Appointment

object AppointmentsDataAccess {
    private val db = FirebaseFirestore.getInstance()
    fun getNextAppointment(name: String,  callback: (Appointment?) -> Unit) {
        val currentTimeStamp: Timestamp = Timestamp.now()
        val appointmentsCollectionRef = db.collection("appointments")

        val query = appointmentsCollectionRef
            .whereEqualTo("clientName", name)
            .whereGreaterThanOrEqualTo("date", currentTimeStamp)
            .orderBy("date", Query.Direction.ASCENDING)
            .limit(1) // Limit the result to the first document

        query.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Parse and process the first appointment document
                    val id = document.id
                    val time = document.getTimestamp("date")?.toDate()
                    val clientName = document.getString("clientName")
                    val doctorCertId = document.getString("doctorCertId") // Retrieve doctorCertId

                    if (time != null && clientName != null && doctorCertId != null) {
                        val nextAppointment = Appointment(id, clientName, time, doctorCertId)
                        callback(nextAppointment)
                    } else {
                        callback(null)
                    }
                    break
                }
                if (documents.isEmpty()) {
                    Log.d("Appt", "documents empty")
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Appt", "exception" + exception)
                callback(null)
            }
    }


}