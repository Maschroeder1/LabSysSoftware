package infra

import model.ClassCode
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class JsoupUfrgsPageParserTest {
    val parser = JsoupUfrgsPageParser()

    @Test
    fun parsesPossibilitiesPage() {
        val possibilitiesPage = getHtml()
        val expected = listOf(ClassCode("3671", "36", "95", "2022022"), ClassCode("19994", "36", "95", "2022022"))

        val actual = parser.parsePossibilities(possibilitiesPage)

        assertEquals(expected, actual)
    }

    private fun temp(doc: Element) {
        val aux = doc.getElementsByClass("modelo1odd")
            .filter{ key -> isOfferedThisSemester(key.getElementsByAttributeValue("align", "left").first())}
            .map { key -> toClassCode(key.getElementsByAttributeValue("align", "left")) }

        val i = 1+1
    }

    private fun isOfferedThisSemester(element: Element?): Boolean {
        return element != null &&
                element.getElementsByAttributeValue("title", "Esta atividade possui turmas oferecidas neste semestre.")
                    .isNotEmpty()
    }

    private fun toClassCode(elements: Elements): ClassCode {
        val aux = elements[0].getElementsByAttribute("href").first()!!.attr("href")
        val aux2 = aux.substring(aux.indexOf("(")+1, aux.indexOf(")")).split(",").map { a -> a.trim() }

        return ClassCode(aux2[0], aux2[1], aux2[2], aux2[3])
    }
    private fun getHtml() : String {
        val htmlFile = JsoupUfrgsPageParserTest::class.java.getResource("/possibilities.html")
        if (htmlFile != null) {
            return htmlFile.readText()
        }
        fail("Unable to read html")
    }
}