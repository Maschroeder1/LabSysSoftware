package infra

import model.ClassCode
import model.Cookie
import model.CouldNotGetUfrgsPageException
import model.NoPossibilitiesException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import java.io.IOException
import java.net.URI
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
        val expected = listOf(ClassCode("a class", "an activity", "a hab", "a course", "a semester", null))
        `when`(creator.createGetRequest(cookie, "https://www1.ufrgs.br/especial/index.php?cods=1,1,2,5"))
            .thenReturn(request)
        `when`(client.send(ArgumentMatchers.eq(request), any(HttpResponse.BodyHandler::class.java)))
            .thenReturn(response)
        `when`(response.statusCode()).thenReturn(200)
        `when`(response.body()).thenReturn("any html")
        `when`(response.uri()).thenReturn(URI.create("http://example.com?cods=1,1,2,5"))
        `when`(parser.parsePossibilities("any html")).thenReturn(expected)

        val actual = requester.requestPossibilities(cookie)

        assertEquals(expected, actual)
        verify(client, times(1)).send(ArgumentMatchers.eq(request), any(HttpResponse.BodyHandler::class.java))
    }

    @Test
    fun returnsResultsFromRetrialWhenWasRedirected() {
        val cookie = Cookie("any cookie")
        val request = mock(HttpRequest::class.java)
        val response = mock(HttpResponse::class.java)
        val expected = listOf(ClassCode("a class", "an activity", "a hab", "a course", "a semester", null))
        `when`(creator.createGetRequest(cookie, "https://www1.ufrgs.br/especial/index.php?cods=1,1,2,5"))
            .thenReturn(request)
        `when`(client.send(ArgumentMatchers.eq(request), any(HttpResponse.BodyHandler::class.java)))
            .thenReturn(response)
        `when`(response.statusCode()).thenReturn(200)
        `when`(response.body()).thenReturn("any html")
        `when`(response.uri()).thenReturn(URI.create("http://example.com"))
        `when`(parser.parsePossibilities("any html")).thenReturn(expected)

        val actual = requester.requestPossibilities(cookie)

        assertEquals(expected, actual)
        verify(client, times(2)).send(ArgumentMatchers.eq(request), any(HttpResponse.BodyHandler::class.java))
    }

    @Test
    fun throwsWhenGetsNonSuccessResponse() {
        val cookie = Cookie("any cookie")
        val request = mock(HttpRequest::class.java)
        val response = mock(HttpResponse::class.java)
        `when`(creator.createGetRequest(cookie, "https://www1.ufrgs.br/especial/index.php?cods=1,1,2,5"))
            .thenReturn(request)
        `when`(client.send(ArgumentMatchers.eq(request), any(HttpResponse.BodyHandler::class.java)))
            .thenReturn(response)
        `when`(response.statusCode()).thenReturn(400)
        `when`(response.uri()).thenReturn(URI.create("http://example.com?cods=1,1,2,5"))

        val exception = assertThrows(CouldNotGetUfrgsPageException::class.java) {
            requester.requestPossibilities(cookie)
        }
        assertEquals("Bad status code 400", exception.message)
    }

    @Test
    fun throwsWhenGetsEmptyResponse() {
        val cookie = Cookie("any cookie")
        val request = mock(HttpRequest::class.java)
        val response = mock(HttpResponse::class.java)
        `when`(creator.createGetRequest(cookie, "https://www1.ufrgs.br/especial/index.php?cods=1,1,2,5"))
            .thenReturn(request)
        `when`(client.send(ArgumentMatchers.eq(request), any(HttpResponse.BodyHandler::class.java)))
            .thenReturn(response)
        `when`(response.uri()).thenReturn(URI.create("http://example.com?cods=1,1,2,5"))
        `when`(response.statusCode()).thenReturn(200)

        val exception = assertThrows(CouldNotGetUfrgsPageException::class.java) {
            requester.requestPossibilities(cookie)
        }
        assertEquals("Cannot parse empty response", exception.message)
    }

    @Test
    fun rethrowsInterruptedException() {
        val cookie = Cookie("any cookie")
        val request = mock(HttpRequest::class.java)
        `when`(creator.createGetRequest(cookie, "https://www1.ufrgs.br/especial/index.php?cods=1,1,2,5"))
            .thenReturn(request)
        `when`(client.send(ArgumentMatchers.eq(request), any(HttpResponse.BodyHandler::class.java)))
            .thenThrow(InterruptedException("InterruptedException"))

        val exception = assertThrows(InterruptedException::class.java) {
            requester.requestPossibilities(cookie)
        }
        assertEquals("InterruptedException", exception.message)
    }

    @Test
    fun throwsOtherExceptions() {
        val cookie = Cookie("any cookie")
        val request = mock(HttpRequest::class.java)
        `when`(creator.createGetRequest(cookie, "https://www1.ufrgs.br/especial/index.php?cods=1,1,2,5"))
            .thenReturn(request)
        `when`(client.send(ArgumentMatchers.eq(request), any(HttpResponse.BodyHandler::class.java)))
            .thenThrow(IOException("IOException"))

        val exception = assertThrows(CouldNotGetUfrgsPageException::class.java) {
            requester.requestPossibilities(cookie)
        }
        assertEquals("Error contacting UFRGS server", exception.message)
    }

    @Test
    fun throwsWhenParserReturnsNoClasses() {
        val cookie = Cookie("any cookie")
        val request = mock(HttpRequest::class.java)
        val response = mock(HttpResponse::class.java)
        `when`(creator.createGetRequest(cookie, "https://www1.ufrgs.br/especial/index.php?cods=1,1,2,5"))
            .thenReturn(request)
        `when`(client.send(ArgumentMatchers.eq(request), any(HttpResponse.BodyHandler::class.java)))
            .thenReturn(response)
        `when`(response.statusCode()).thenReturn(200)
        `when`(response.uri()).thenReturn(URI.create("http://example.com?cods=1,1,2,5"))
        `when`(response.body()).thenReturn("any html")
        `when`(parser.parsePossibilities("any html")).thenReturn(listOf())

        assertThrows(NoPossibilitiesException::class.java) {
            requester.requestPossibilities(cookie)
        }
    }
}