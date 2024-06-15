package model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Appointment(
    @DocumentId
    var id: String? = null, // Use nullable String for the document ID
    var clientName: String,
    var time: Date,
    var doctorCertId: String // Add a field for the doctorCertId
)