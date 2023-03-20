package infra

import model.ClassCode
import model.CollegeClass

interface UfrgsPageParser {

    fun parsePossibilities(html: String) : List<ClassCode>

    fun parseClass(html: String) : CollegeClass

    fun parseEnrollment(html: String): String
}