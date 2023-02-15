package infra

import model.ClassCode
import model.CollegeClass
import model.CollegeClassRequester

class HttpRequestCollegeClassRequester() : CollegeClassRequester {

    override fun bulkRequest(codes: List<ClassCode>): List<CollegeClass> {
        return listOf()
    }
}