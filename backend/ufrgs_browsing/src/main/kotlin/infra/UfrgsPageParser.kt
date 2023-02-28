package infra

import model.ClassCode
import model.CollegeClass

interface UfrgsPageParser {

    fun parsePossibilities(html: String) : List<ClassCode>

    fun parseClasses(html: String) : CollegeClass
}