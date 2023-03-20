package model

interface EnrollmentDeclarationRequester {

    fun requestEnrollmentDeclaration(cookie: Cookie): String
}