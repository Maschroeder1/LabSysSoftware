package infra

import model.ClassCode

interface UfrgsPageParser {

    fun parsePossibilities(html: String) : List<ClassCode>
}