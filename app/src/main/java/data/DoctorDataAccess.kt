package data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object DoctorDataAccess {

    private var doctorId = PatientDataHolder.getLoggedInPatient()?.doctorID.toString();
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("doctors")

    fun getDoctorNameByCertId(doctorId: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {

        Log.d("Doctor", "id: " + doctorId)
        collection
            .whereEqualTo("certId", doctorId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Assuming there is only one document with the matching certId
                    val doctorName = documents.documents[0].getString("name")
                    doctorName?.let {
                        onSuccess(it)
                    } ?: run {
                        onFailure(Exception("Name field is missing"))
                    }
                } else {
                    onFailure(Exception("No doctor found with certId: $doctorId"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}