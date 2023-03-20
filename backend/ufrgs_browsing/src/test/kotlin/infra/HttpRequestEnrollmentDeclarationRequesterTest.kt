package infra

import model.Cookie
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class HttpRequestEnrollmentDeclarationRequesterTest {
    private val client = mock(HttpClient::class.java)
    private val creator = mock(HttpRequestCreator::class.java)
    private val parser = mock(UfrgsPageParser::class.java)
    private val requester = HttpRequestEnrollmentDeclarationRequester(client, creator, parser)

    @Test
    fun returnsLinkToPreGeneratedDeclaration() {
        val cookie = Cookie("any cookie")
        val request = mock(HttpRequest::class.java)
        val response = mock(HttpResponse::class.java)
        val expected = "Some link to a declaration"
        `when`(creator.createGetRequest(cookie, "https://www1.ufrgs.br/especial/index.php?cods=1,1,2,11"))
            .thenReturn(request)
        `when`(client.send(ArgumentMatchers.eq(request), any(HttpResponse.BodyHandler::class.java)))
            .thenReturn(response)
        `when`(response.body()).thenReturn("any html")
        `when`(response.uri()).thenReturn(URI.create("https://www1.ufrgs.br/especial/index.php?cods=1,1,2,11"))
        `when`(parser.parseEnrollment("any html")).thenReturn(expected)

        val actual = requester.requestEnrollmentDeclaration(cookie)

        assertEquals(expected, actual)
        verify(client, times(1)).send(ArgumentMatchers.eq(request), any(HttpResponse.BodyHandler::class.java))
    }

    @Test
    fun returnsLinkToPreGeneratedDeclarationAfterRedirectRetrial() {
        val cookie = Cookie("any cookie")
        val request = mock(HttpRequest::class.java)
        val response = mock(HttpResponse::class.java)
        val expected = "Some link to a declaration"
        `when`(creator.createGetRequest(cookie, "https://www1.ufrgs.br/especial/index.php?cods=1,1,2,11"))
            .thenReturn(request)
        `when`(client.send(ArgumentMatchers.eq(request), any(HttpResponse.BodyHandler::class.java)))
            .thenReturn(response)
        `when`(response.body()).thenReturn("any html")
        `when`(response.uri()).thenReturn(URI.create("https://www1.ufrgs.br/especial/index.php"))
        `when`(parser.parseEnrollment("any html")).thenReturn(expected)

        val actual = requester.requestEnrollmentDeclaration(cookie)

        assertEquals(expected, actual)
        verify(client, times(2)).send(ArgumentMatchers.eq(request), any(HttpResponse.BodyHandler::class.java))
    }
}