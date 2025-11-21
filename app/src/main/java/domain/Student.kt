package com.johel.smartschoolapp.domain

class Student(
    id: String,
    fullName: String,
    val birthDate: String,
    val enrollmentCode: String,
    val guardianId: String,
    val routeId: String? = null   // <-- new field
) : User(id, fullName) {

    override fun toString(): String {
        return "Student(id='$id', fullName='$fullName', birthDate='$birthDate', " +
                "enrollmentCode='$enrollmentCode', guardianId='$guardianId', routeId='$routeId')"
    }
}