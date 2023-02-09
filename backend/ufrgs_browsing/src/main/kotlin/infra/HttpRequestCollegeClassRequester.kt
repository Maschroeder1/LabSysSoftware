package infra

import model.ClassCode
import model.CollegeClass
import model.CollegeClassRequester

class HttpRequestCollegeClassRequester : CollegeClassRequester {
    private val wat  = 1

    override fun bulkRequest(codes: List<ClassCode>): List<CollegeClass> {
        return listOf()
    }
}