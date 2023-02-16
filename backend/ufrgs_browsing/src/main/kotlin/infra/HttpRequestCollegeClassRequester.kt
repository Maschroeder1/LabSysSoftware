package infra

import model.ClassCode
import model.CollegeClass
import model.CollegeClassRequester
import java.net.http.HttpClient

class HttpRequestCollegeClassRequester(
    private val client: HttpClient,
    private val creator: HttpRequestCreator,
    private val parser: UfrgsPageParser) : CollegeClassRequester {

    override fun bulkRequest(codes: List<ClassCode>): List<CollegeClass> {
        return listOf()
    }
}