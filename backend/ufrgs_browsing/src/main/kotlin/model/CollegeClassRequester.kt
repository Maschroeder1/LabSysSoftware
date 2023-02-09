package model

interface CollegeClassRequester {

    fun bulkRequest(codes: List<ClassCode>): List<CollegeClass>
}