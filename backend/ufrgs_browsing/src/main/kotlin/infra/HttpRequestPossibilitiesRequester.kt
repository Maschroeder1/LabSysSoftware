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
            return toClassCode(getResponse(cookie))
        } catch (e: Exception) {
            throw exceptionFrom(e)
        }
    }

    private fun getResponse(cookie: Cookie): HttpResponse<String> {
        val request = creator.createGetRequest(cookie, endpoint)

        var response = client.send(request, HttpResponse.BodyHandlers.ofString())

        if (!response.uri().toString().endsWith("1,1,2,5")) {
            // UFRGS sometimes force redirects us
            response = client.send(request, HttpResponse.BodyHandlers.ofString())
        }
        validate(response)
        return response
    }

    private fun toClassCode(response: HttpResponse<String>): List<ClassCode> {
        val possibilities = parser.parsePossibilities(response.body())
        if (possibilities.isEmpty()) {
            throw NoPossibilitiesException()
        }
        return possibilities
    }

    private fun validate(response: HttpResponse<String>) {
        if (response.statusCode() >= 400) {
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
        if (e is CouldNotGetUfrgsPageException ||
            e is NoPossibilitiesException ||
            e is InterruptedException ||
            e is OutdatedCookieException) {
            return e
        }
        return CouldNotGetUfrgsPageException("Error contacting UFRGS server")
    }
}