package infra

import model.ClassCode
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class JsoupUfrgsPageParser : UfrgsPageParser {
    override fun parsePossibilities(html: String): List<ClassCode> {
        val doc = Jsoup.parse(html)

        return getClassCodesFromClass(doc, "modelo1odd") + getClassCodesFromClass(doc, "modelo1even")
    }

    private fun getClassCodesFromClass(doc: Element, className: String): List<ClassCode> {
        return doc.getElementsByClass(className)
            .filter{ key -> isOfferedThisSemester(key.getElementsByAttributeValue("align", "left").first())}
            .map { key -> toClassCode(key.getElementsByAttributeValue("align", "left")) }
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
}