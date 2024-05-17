package data

import PatientModel

object PatientDataHolder {
    private var loggedInPatient: PatientModel? = null

    fun setLoggedInPatient(patient: PatientModel) {
        loggedInPatient = patient
    }

    fun getLoggedInPatient(): PatientModel? {
        return loggedInPatient
    }
}