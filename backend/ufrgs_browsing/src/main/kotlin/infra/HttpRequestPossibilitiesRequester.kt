package infra

import model.ClassCode
import model.Cookie
import model.PossibilitiesRequester
import java.net.http.HttpClient
import java.net.http.HttpResponse

class HttpRequestPossibilitiesRequester(
    private val client: HttpClient,
    private val creator: HttpRequestCreator,
    private val parser: UfrgsPageParser
) : PossibilitiesRequester {
    private val endpoint = "https://www1.ufrgs.br/especial/index.php?cods=1,1,2,5"

    override fun requestPossibilities(cookie: Cookie): List<ClassCode> {
        val request = creator.createPossibilitiesRequest(cookie, endpoint)

        try {
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.body() != null) {
                return parser.parsePossibilities(response.body())
            }
        } catch (e: Exception) {
            return emptyList()
        }
        return emptyList()
    }
}