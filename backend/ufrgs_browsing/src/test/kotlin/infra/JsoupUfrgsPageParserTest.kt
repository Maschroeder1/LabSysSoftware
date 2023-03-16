package infra

import com.google.gson.Gson
import model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.fail

class JsoupUfrgsPageParserTest {
    private val parser = JsoupUfrgsPageParser()

    @Test
    fun parsesPossibilitiesPage() {
        val possibilitiesPage = getHtml("/possibilities.html")
        val expected = listOf(
            ClassCode("ARQUITETURAS AVAN�ADAS DE COMPUTADORES - (INF01191)", "3671", "36", "95", "2022022"),
            ClassCode("BIOLOGIA COMPUTACIONAL - (INF05018)", "19994", "36", "95", "2022022")
        )

        val actual = parser.parsePossibilities(possibilitiesPage)

        assertEquals(expected, actual)
    }

    @Test
    fun parsesClassPageWithMultiplePossibilities() {
        val classPage = getHtml("/class_multiple.html")

        val actual = parser.parseClass(classPage)

        assertEquals(getMultiplePossibilitiesClassExpected(), actual)
    }

    @Test
    fun parsesClassPageWithRemoteClassOption() {
        val classPage = getHtml("/class_remote.html")

        val actual = parser.parseClass(classPage)

        assertEquals(getRemoteClassExpected(), actual)
    }

    @Test
    fun parsesRecentPreGeneratedEnrollmentPage() {
        val preGeneratedEnrollmentPage = getHtml("/enrollment_generated_new.html")

        val actual = parser.parseEnrollment(preGeneratedEnrollmentPage)

        assertEquals(
            "http://www1.ufrgs.br/PortalEnsino/GraduacaoAluno/public/exibeComprovantePDF.php?chave=123ABC", actual)
    }

    @Test
    fun parsesOlderPreGeneratedEnrollmentPage() {
        val preGeneratedEnrollmentPage = getHtml("/enrollment_generated_old.html")

        val actual = parser.parseEnrollment(preGeneratedEnrollmentPage)

        assertEquals(
            "http://www1.ufrgs.br/PortalEnsino/GraduacaoAluno/public/exibeComprovantePDF.php?chave=123ABC", actual)
    }

    @Test
    fun throwsIfNoEnrollmentWasGenerated() {
        val preGeneratedEnrollmentPage = getHtml("/enrollment_not_generated.html")

        assertThrows(JavascriptException::class.java) {
            parser.parseEnrollment(preGeneratedEnrollmentPage)
        }
    }

    private fun getHtml(source: String): String {
        val htmlFile = JsoupUfrgsPageParserTest::class.java.getResource(source)
        if (htmlFile != null) {
            return htmlFile.readText()
        }
        fail("Unable to read html")
    }

    private fun getMultiplePossibilitiesClassExpected(): CollegeClass {
        return getClassExpected("/class_multiple_expected.json")
    }

    private fun getRemoteClassExpected(): CollegeClass {
        return getClassExpected("/class_remote_expected.json")
    }

    private fun getClassExpected(filename: String): CollegeClass {
        val htmlFile = JsoupUfrgsPageParserTest::class.java.getResource(filename)
        if (htmlFile != null) {
            val gson = Gson()
            return gson.fromJson(htmlFile.readText(), CollegeClass::class.java)
        }
        fail("Unable to read expected json")
    }
}