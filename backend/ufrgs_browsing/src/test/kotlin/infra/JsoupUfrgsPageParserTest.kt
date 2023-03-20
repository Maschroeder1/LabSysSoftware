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
        val expected = listOf(
            ClassCode("ARQUITETURAS AVAN�ADAS DE COMPUTADORES - (INF01191)", "3671", "36", "95", "2022022", "http://www1.ufrgs.br/Ensino/PlanoDeEnsino/PlanoDeEnsinoPDF/Paginas/Visao/PDFPlanoDeEnsino.php?AtividadeEnsino=3671_2019012"),
            ClassCode("BIOLOGIA COMPUTACIONAL - (INF05018)", "19994", "36", "95", "2022022", "http://www1.ufrgs.br/Ensino/PlanoDeEnsino/PlanoDeEnsinoPDF/Paginas/Visao/PDFPlanoDeEnsino.php?AtividadeEnsino=19994_2019022")
        )

        val actual = parser.parsePossibilities(possibilitiesPage)

        assertEquals(expected, actual)
    }

    @Test
    fun parsesClassPageWithMultiplePossibilities() {
        val classPage = getMultiplePossibilitiesClassHtml()

        val actual = parser.parseClass(classPage)

        assertEquals(getMultiplePossibilitiesClassExpected(), actual)
    }

    @Test
    fun parsesClassPageWithRemoteClassOption() {
        val classPage = getClassHtmlRemoteClass()

        val actual = parser.parseClass(classPage)

        assertEquals(getRemoteClassExpected(), actual)
    }

    private fun getPossibilitiesHtml(): String {
        return getHtml("/possibilities.html")
    }

    private fun getMultiplePossibilitiesClassHtml(): String {
        return getHtml("/class_multiple.html")
    }

    private fun getClassHtmlRemoteClass(): String {
        return getHtml("/class_remote.html")
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