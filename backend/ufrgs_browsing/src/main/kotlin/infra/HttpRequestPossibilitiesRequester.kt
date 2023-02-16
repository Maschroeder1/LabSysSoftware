package infra

import model.*
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class HttpRequestPossibilitiesRequester(
    private val client: HttpClient,
    private val creator: HttpRequestCreator,
    private val parser: UfrgsPageParser
) : PossibilitiesRequester {
    private val endpoint = "https://www1.ufrgs.br/especial/index.php?cods=1,1,2,5"

    override fun requestPossibilities(cookie: Cookie): List<ClassCode> {
        try {
            val request = creator.createGetRequest(cookie, endpoint)
            return requestPossibilities(request)
        } catch (e: Exception) {
            throw exceptionFrom(e)
        }
    }

    private fun requestPossibilities(request: HttpRequest): List<ClassCode> {
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        validate(response)

        val possibilities = parser.parsePossibilities(response.body())
        if (possibilities.isEmpty()) {
            throw CouldNotGetUfrgsPageException("Could not find any classes")
        }
        return possibilities
    }

    private fun validate(response: HttpResponse<String>) {
        if (response.statusCode() != 200) {
            throw CouldNotGetUfrgsPageException("Bad status code ${response.statusCode()}")
        }
        if (response.body().isNullOrEmpty()) {
            throw CouldNotGetUfrgsPageException("Cannot parse empty response")
        }
        if (response.body().contains("o login novamente")) {
            throw OutdatedCookieException()
        }
    }

    private fun exceptionFrom(e: Exception): Exception {
        if (e is CouldNotGetUfrgsPageException) {
            return e
        }
        if (e is InterruptedException) {
            return e
        }
        return CouldNotGetUfrgsPageException("Error contacting UFRGS server")
    }
}