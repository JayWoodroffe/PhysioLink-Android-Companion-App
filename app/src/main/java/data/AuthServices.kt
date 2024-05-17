package data

import PatientModel
import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import kotlin.math.exp

object AuthServices {

    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("SuspiciousIndentation")
    fun checkInvitation(code:String, callback:(Boolean, String?)->Unit)
    {
        Log.e("Invite", "code $code")
        val currentTime = Calendar.getInstance().time
        db.collection("invitations")
            .whereEqualTo("code", code)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                //check if the invitation code is still valid
                if (!documents.isEmpty)
                {
                    val document = documents.first()
                    val expirationTime = document.getTimestamp("expirationTime")?.toDate()
                    val invitationValid = expirationTime != null && expirationTime.after(currentTime)
                    val doctorID = document.getString("doctorID")
                    callback(invitationValid, doctorID)
                }
                else
                    Log.e("Invite", "documents empty")
                    callback(false, null)
            }
            .addOnFailureListener{exception ->
                Log.e("Invite", "Error querying invitations: $exception")
            }
    }

    fun deleteInvitationCode(code: String, callback: (Boolean) -> Unit) {
        db.collection("invitations")
            .whereEqualTo("code", code) // Find the invitation with the specified code
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Delete the first document found with the specified code
                    val document = documents.documents[0]
                    document.reference.delete()
                        .addOnSuccessListener {
                            // Invitation deleted successfully
                            callback(true)
                        }
                        .addOnFailureListener { e ->
                            // Error occurred while deleting invitation
                            callback(false)
                        }
                } else {
                    // No invitation found with the specified code
                    callback(false)
                }
            }
            .addOnFailureListener { e ->
                // Error occurred while querying invitations
                callback(false)
            }
    }

    fun addPatient(patient: PatientModel, callback: (String?) -> Unit)
    {
        FirebaseFirestore.getInstance().collection("users")
            .add(patient)
            .addOnSuccessListener { documentReference->
                callback(documentReference.id)
            }
            .addOnFailureListener { e ->
                Log.e("ClientDataAccess", "Error adding client", e)
                // Return null to indicate failure via callback
                callback(null)
            }
    }

    fun createUserWithEmailAndPassword(email: String, password: String, patientDocumentId: String, callback: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                Log.e("Invite", "User added to auth")
                callback(true)

            }
            .addOnFailureListener { e ->
                Log.e("Invite", e.toString())
                // Failed to create Firebase Authentication user
                callback(false)
            }
    }

//    private fun associatePatientDocumentIdWithUser(user: FirebaseUser?, patientDocumentId: String, callback: (Boolean) -> Unit) {
//        if (user != null) {
//            val userUid = user.uid
//            val userRef = FirebaseFirestore.getInstance().collection("users").document(userUid)
//            userRef.update("patientDocumentId", patientDocumentId)
//                .addOnSuccessListener {
//                    // Successfully associated patientDocumentId with user
//                    callback(true)
//                }
//                .addOnFailureListener { e ->
//                    // Failed to associate patientDocumentId with user
//                    callback(false)
//                }
//        } else {
//            // User is null
//            callback(false)
//        }
//    }

    fun login(email:String, password: String, onSuccess:() -> Unit, onFailure:(String) -> Unit)
    {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            //successful login -> retrieve all doctors details and add them to the DoctorDataHolder
            .addOnSuccessListener { authResult->

                //if log in is successful, return users's email
                val userEmail = authResult.user?.email

                if(userEmail != null)
                {
                    onSuccess()
                }
                else{
                    Log.d("LoginActivity", "user email not available")
                    onFailure("User email not available")
                }
            }
            .addOnFailureListener{e->
                //handle log in failure
                Log.d("LoginActivity", "unknown error")
                onFailure(e.message?:"Unknown error")
            }
    }

    fun setUserData(onSuccess: (PatientModel) -> Unit, onFailure: (String) -> Unit) {
        // Get the currently logged-in user's email
        val userEmail = FirebaseAuth.getInstance().currentUser?.email

        if (userEmail != null) {
            // Query Firestore collection "users" based on the user's email
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        // Retrieve the first document from the query result
                        val documentSnapshot = querySnapshot.documents[0]
                        // Get the document ID
                        val userId = documentSnapshot.id
                        // Retrieve the user data from the query result
                        val userData = documentSnapshot.data
                        // Extract required fields from userData and create a PatientModel instance
                        val name = userData?.get("name") as String
                        val phoneNumber = userData["number"] as String
                        val dobTimestamp = userData["dob"] as? Timestamp
                        val dob = dobTimestamp?.toDate()
                        val doctorID = userData["doctorID"] as String

                        // Create a PatientModel instance with the retrieved data
                        val patientModel = PatientModel().apply {
                            setValues(userId, name, userEmail, phoneNumber, dob, doctorID)
                        }
                        onSuccess(patientModel)
                    } else {
                        onFailure("User data not found")
                    }
                }
                .addOnFailureListener { e ->
                    onFailure("Error fetching user data: ${e.message}")
                }
        } else {
            onFailure("User email not available")
        }
    }
}