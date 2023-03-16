package infra

import model.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
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
        val elem = elements[0].getElementsByAttribute("href").first()!!
        val aux = elem.attr("href")
        val aux2 = aux.substring(aux.indexOf("(")+1, aux.indexOf(")")).split(",").map { a -> a.trim() }

        return ClassCode(elem.text(), aux2[0], aux2[1], aux2[2], aux2[3])
    }

    override fun parseClass(html: String): CollegeClass {
        val doc = Jsoup.parse(html)

        return CollegeClass(getTimeslots(doc), getCreditCount(doc))
    }

    private fun getTimeslots(doc: Document): List<Timeslot> {
        return doc.getElementsByClass("modelo1odd").map { elem -> toTimeslot(elem) } +
                doc.getElementsByClass("modelo1even").map { elem -> toTimeslot(elem) }
    }

    private fun toTimeslot(elem: Element): Timeslot {
        val centers = elem.getElementsByAttributeValue("align", "center")
        val lefts = elem.getElementsByAttributeValue("align", "left")
        return Timeslot(centers[1].text(), centers[2].text().toInt(), toProfessors(lefts[2]), toScheduleTimeList(lefts[1]))
    }

    private fun toProfessors(elem: Element?): List<String> {
        return elem?.getElementsByTag("li")?.map { e -> e.text() } ?: emptyList()
    }

    private fun toScheduleTimeList(elem: Element?): List<ScheduleTime> {
        val liElements = elem?.getElementsByTag("li") ?: throw CouldNotParseException()

        return if (hasRemoteClass(liElements)) {
            liElements
                .filter { e -> !isRemoteClass(e) }
                .filter { e -> e.getElementsByTag("b").isNullOrEmpty() }
                .map { e -> toScheduleTime(e, "EAD", null) }
        } else {
            liElements
                .filter { e -> e.getElementsByTag("b").isNullOrEmpty() }
                .map { e -> toScheduleTime(e) }
        }
    }

    private fun hasRemoteClass(elements: Elements): Boolean {
        return elements.any { node -> isRemoteClass(node) }
    }

    private fun isRemoteClass(element: Element): Boolean {
        return element.text().contains("Ensino a Dist")
    }

    private fun toScheduleTime(elem: Element, location: String, locationMap: String?): ScheduleTime {
        val text = elem.textNodes().first()?.text()
        val day = text?.substringBefore(" ") ?: "???"
        val times = (text?.substringAfter(" ")?.split(" - ") ?: listOf("?", "?")).toMutableList()
        if (times[1].contains(" ")) {
            times[1] = times[1].substringBefore(" ")
        }
        return ScheduleTime(day, times[0].trim(), times[1].trim(), location, locationMap)
    }

    private fun toScheduleTime(elem: Element): ScheduleTime {
        val location = elem.getElementsByTag("a").first()?.text() ?: ""
        val locationMap = elem.getElementsByClass("clicavel").first()?.attr("href")

        return toScheduleTime(elem, location, locationMap)
    }

    private fun getCreditCount(doc: Document): Int {
        return doc.getElementsByClass("modelo1odd").first()!!
            .getElementsByAttributeValue("align", "center").first()!!
            .text()
            .toInt()
    }

    override fun parseEnrollment(html: String): String {
        val doc = Jsoup.parse(html)

        return if (html.contains("Reimprimir o comprovante")) {
            getPreGeneratedPage(doc.getElementsByClass("moldura"))
        } else {
            throw JavascriptException()
        }
    }

    private fun getPreGeneratedPage(elements: Elements): String {
        val first = elements.first() ?: throw CouldNotParseException()

        var pageLink = first.getElementsByTag("a").attr("href")
        if (pageLink.isNullOrEmpty()) {
            throw CouldNotParseException()
        }

        if (!(pageLink.startsWith("www") || pageLink.startsWith("http"))) {
            pageLink = "www1.ufrgs.br$pageLink"
        }
        if (!pageLink.startsWith("http")) {
            pageLink = "http://$pageLink"
        }
        return pageLink
    }
}