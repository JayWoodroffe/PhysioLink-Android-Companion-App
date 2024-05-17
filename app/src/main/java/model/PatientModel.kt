import com.google.firebase.Timestamp
import java.util.Date

data class PatientModel(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var number: String = "",
    var dob: Date?,
    var doctorID: String = "",
    var searchField:String ="",
    val userType: String = "client"
) {

    constructor() : this("", "", "", "", null, "") {}

    fun setValues(id: String, name: String, email: String, phoneNumber: String, dateOfBirth: Date?, doctorID: String) {
        this.id = id
        this.name = name
        this.email = email
        this.number = phoneNumber
        this.dob = dateOfBirth
        this.doctorID = doctorID
        this.searchField = name.lowercase()
    }

    fun setValues( name: String, email: String, phoneNumber: String, dateOfBirth: Date?, doctorID: String) {
        this.name = name
        this.email = email
        this.number = phoneNumber
        this.dob = dateOfBirth
        this.doctorID = doctorID
        this.searchField = name.lowercase()
    }

}



