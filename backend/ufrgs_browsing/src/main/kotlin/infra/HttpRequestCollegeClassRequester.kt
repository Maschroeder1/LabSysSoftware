package infra

import model.ClassCode
import model.CollegeClass
import model.CollegeClassRequester
import java.net.http.HttpClient
import java.net.http.HttpResponse

class HttpRequestCollegeClassRequester(
    private val client: HttpClient, private val creator: HttpRequestCreator, private val parser: UfrgsPageParser
) : CollegeClassRequester {

    override fun bulkRequest(codes: List<ClassCode>): List<CollegeClass> {
        return codes.map { code -> toClassUri(code) }
            .map { uri -> creator.createGetRequest(uri) }
            .map { req -> client.send(req, HttpResponse.BodyHandlers.ofString()) }
            .map { res -> parser.parseClasses(res.body()) }
    }

    private fun toClassUri(code: ClassCode): String {
        return "https://www1.ufrgs.br/PortalEnsino/GraduacaoAluno/view/HorarioAtividade.php?" +
                "CodAtiv=${code.activity}&" +
                "CodHab=${code.hab}&" +
                "CodCur=${code.course}&" +
                "Sem=${code.semester}"
    }
}