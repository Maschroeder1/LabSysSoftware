package infra

import com.google.gson.Gson
import model.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class JsoupUfrgsPageParserTest {
    private val parser = JsoupUfrgsPageParser()

    @Test
    fun parsesPossibilitiesPage() {
        val possibilitiesPage = getPossibilitiesHtml()
        val expected = listOf(ClassCode("3671", "36", "95", "2022022"), ClassCode("19994", "36", "95", "2022022"))

        val actual = parser.parsePossibilities(possibilitiesPage)

        assertEquals(expected, actual)
    }

    @Test
    fun parsesClassPage() {
        val classPage = getClassHtml()

        val actual = parser.parseClasses(classPage)

        assertEquals(getClassExpected(), actual)
    }

    private fun getPossibilitiesHtml(): String {
        return getHtml("/possibilities.html")
    }

    private fun getClassHtml(): String {
        return getHtml("/algprog.html")
    }

    private fun getHtml(source: String): String {
        val htmlFile = JsoupUfrgsPageParserTest::class.java.getResource(source)
        if (htmlFile != null) {
            return htmlFile.readText()
        }
        fail("Unable to read html")
    }

    private fun getClassExpected(): CollegeClass {
        val htmlFile = JsoupUfrgsPageParserTest::class.java.getResource("/algprog_expected.json")
        if (htmlFile != null) {
            val gson = Gson()
            return gson.fromJson(htmlFile.readText(), CollegeClass::class.java)
        }
        fail("Unable to read expected json")
    }
}