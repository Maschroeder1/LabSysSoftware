package infra.endpoints

import application.UfrgsService
import infra.CookieFactory
import model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import spring.ApiResponse

class EnrollmentDeclarationEndpointTest {
    private val service = mock(UfrgsService::class.java)
    private val factory = mock(CookieFactory::class.java)
    private val endpoint = EnrollmentDeclarationEndpoint(service, factory)
    private val aCookieString = "123"
    private val aCookie = Cookie("123")
    private val expectedLink = "http://a.link.com/somewhere"

    @Test
    fun returnsSuccessfulResponse() {
        val expected = ApiResponse("Ok", expectedLink)
        `when`(service.retrieveEnrollmentDeclaration(aCookie)).thenReturn(expectedLink)
        `when`(factory.createCookie(aCookieString)).thenReturn(aCookie)

        val actual = endpoint.process(aCookieString)

        assertEquals(expected, actual.body)
        assertEquals(200, actual.statusCode.value())
    }

    @Test
    fun returns400WhenBadlyFormattedInput() {
        val expected = ApiResponse("Badly formatted input", null)

        val actual = endpoint.process(123)

        kotlin.test.assertEquals(expected, actual.body)
        kotlin.test.assertEquals(400, actual.statusCode.value())
    }

    @Test
    fun returns401WhenEmptyInput() {
        val expected = ApiResponse("Missing cookie", null)

        val actual = endpoint.process("")

        kotlin.test.assertEquals(expected, actual.body)
        kotlin.test.assertEquals(401, actual.statusCode.value())
    }

    @Test
    fun returns400WhenNoDeclarationWasPreviouslyGenerated() {
        val expected = ApiResponse("No enrollment declaration previously generated", null)
        `when`(factory.createCookie(aCookieString)).thenReturn(aCookie)
        `when`(service.retrieveEnrollmentDeclaration(aCookie)).thenThrow(JavascriptException())

        val actual = endpoint.process(aCookieString)

        assertEquals(expected, actual.body)
        assertEquals(400, actual.statusCode.value())
    }

    @Test
    fun returns401WhenOutdateCookie() {
        val expected = ApiResponse("Outdated cookie", null)
        `when`(factory.createCookie(aCookieString)).thenReturn(aCookie)
        `when`(service.retrieveEnrollmentDeclaration(aCookie)).thenThrow(OutdatedCookieException())

        val actual = endpoint.process(aCookieString)

        assertEquals(expected, actual.body)
        assertEquals(401, actual.statusCode.value())
    }

    @Test
    fun returns501WhenCouldNotParseUfrgsResponse() {
        val expected = ApiResponse("Error parsing UFRGS response", null)
        `when`(factory.createCookie(aCookieString)).thenReturn(aCookie)
        `when`(service.retrieveEnrollmentDeclaration(aCookie)).thenThrow(CouldNotParseException())

        val actual = endpoint.process(aCookieString)

        assertEquals(expected, actual.body)
        assertEquals(501, actual.statusCode.value())
    }

    @Test
    fun returns502WhenConnectionProblems() {
        val expected = ApiResponse("Error contacting UFRGS", "a")
        `when`(factory.createCookie(aCookieString)).thenReturn(aCookie)
        `when`(service.retrieveEnrollmentDeclaration(aCookie)).thenThrow(CouldNotGetUfrgsPageException("a"))

        val actual = endpoint.process(aCookieString)

        assertEquals(expected, actual.body)
        assertEquals(502, actual.statusCode.value())
    }

    @Test
    fun returns500WhenOtherException() {
        val expected = ApiResponse("Internal server error", null)
        `when`(factory.createCookie(aCookieString)).thenReturn(aCookie)
        `when`(service.retrieveEnrollmentDeclaration(aCookie)).thenThrow(RuntimeException())

        val actual = endpoint.process(aCookieString)

        assertEquals(expected, actual.body)
        assertEquals(500, actual.statusCode.value())
    }
}