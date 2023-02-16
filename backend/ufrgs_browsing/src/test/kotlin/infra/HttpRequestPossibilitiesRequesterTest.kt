package infra

import model.ClassCode
import model.Cookie
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class HttpRequestPossibilitiesRequesterTest {
    private val client = mock(HttpClient::class.java)
    private val creator = mock(HttpRequestCreator::class.java)
    private val parser = mock(UfrgsPageParser::class.java)
    private val requester = HttpRequestPossibilitiesRequester(client, creator, parser)

    @Test
    fun returnsResultsFromCookie() {
        val cookie = Cookie("any cookie")
        val request = mock(HttpRequest::class.java)
        val response = mock(HttpResponse::class.java)
        val expected = listOf(ClassCode("an activity", "a hab", "a course", "a semester"))
        `when`(creator.createGetRequest(cookie, "https://www1.ufrgs.br/especial/index.php?cods=1,1,2,5"))
            .thenReturn(request)
        `when`(client.send(ArgumentMatchers.eq(request), any(HttpResponse.BodyHandler::class.java)))
            .thenReturn(response)
        `when`(response.body()).thenReturn("any html")
        `when`(parser.parsePossibilities("any html")).thenReturn(expected)

        val actual = requester.requestPossibilities(cookie)

        assertEquals(expected, actual)
    }
}