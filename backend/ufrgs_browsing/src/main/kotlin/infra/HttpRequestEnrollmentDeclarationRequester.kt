package infra

import model.*
import java.net.http.HttpClient
import java.net.http.HttpResponse

class HttpRequestEnrollmentDeclarationRequester(
    private val client: HttpClient,
    private val creator: HttpRequestCreator,
    private val parser: UfrgsPageParser
) : EnrollmentDeclarationRequester {
    private val endpoint = "https://www1.ufrgs.br/especial/index.php?cods=1,1,2,11"

    override fun requestEnrollmentDeclaration(cookie: Cookie): String {
        try {
            val html = getResponse(cookie)
            return parser.parseEnrollment(html)
        } catch (e: Exception) {
            throw exceptionFrom(e)
        }
    }

    private fun getResponse(cookie: Cookie): String {
        val request = creator.createGetRequest(cookie, endpoint)

        var response = client.send(request, HttpResponse.BodyHandlers.ofString())

        if (!response.uri().toString().endsWith("1,1,2,11")) {
            // UFRGS sometimes force redirects us
            response = client.send(request, HttpResponse.BodyHandlers.ofString())
        }
        validate(response)
        return response.body()
    }

    private fun validate(response: HttpResponse<String>) {

    }

    private fun exceptionFrom(e: Exception): Exception {
        if (e is CouldNotGetUfrgsPageException ||
            e is InterruptedException ||
            e is OutdatedCookieException
        ) {
            return e
        }
        return CouldNotGetUfrgsPageException("Error contacting UFRGS server")
    }
}