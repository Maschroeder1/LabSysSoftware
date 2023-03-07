package model

interface CollegeClassRequester {

    fun bulkRequest(codes: List<ClassCode>): Int

    fun bulkQuery(key: Int): Map<String, CollegeClass?>
}