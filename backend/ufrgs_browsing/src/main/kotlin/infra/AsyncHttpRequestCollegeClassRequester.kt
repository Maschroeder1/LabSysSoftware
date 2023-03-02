package infra

import model.ClassCode
import model.CollegeClass
import model.CollegeClassRequester
import java.net.http.HttpClient
import java.net.http.HttpResponse

class AsyncHttpRequestCollegeClassRequester(
    private val client: HttpClient, private val creator: HttpRequestCreator, private val parser: UfrgsPageParser, private val controller: CollegeClassController
) : CollegeClassRequester {

    override fun bulkRequest(codes: List<ClassCode>): Int {
        return controller.queueClassesToProcess(codes)
    }

    override fun bulkQuery(key: Int): Map<String, CollegeClass?> {
        return controller.getCollegeClasses(key)
    }

    fun updateController(): Boolean {
        val code = controller.getClassToProcess() ?: return false

        try {
            val request = creator.createGetRequest(toClassUri(code))
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            controller.cache(code.className, parser.parseClass(response.body()))
        } catch (e: Exception) {
            controller.queueClassesToProcess(listOf(code))
        }

        return true
    }

    private fun toClassUri(code: ClassCode): String {
        return "https://www1.ufrgs.br/PortalEnsino/GraduacaoAluno/view/HorarioAtividade.php?" +
                "CodAtiv=${code.activity}&" +
                "CodHab=${code.hab}&" +
                "CodCur=${code.course}&" +
                "Sem=${code.semester}"
    }
}