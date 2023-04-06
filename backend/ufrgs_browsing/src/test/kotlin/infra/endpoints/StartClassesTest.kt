package infra.endpoints

import application.UfrgsService
import infra.CookieFactory
import model.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import spring.ApiResponse
import kotlin.test.assertEquals

class StartClassesTest {
    private val ufrgsService = mock(UfrgsService::class.java)
    private val cookieFactory = mock(CookieFactory::class.java)
    private val startClassesEndpoint = StartClassesEndpoint(ufrgsService, cookieFactory)
    private val aValidCookieString = "abc"
    private val aValidCookie = Cookie("def")

    @Test
    fun returnsSuccessfulCode() {
        val expected = ApiResponse("Ok", 12345)
        `when`(ufrgsService.startPossibilitiesProcessing(aValidCookie)).thenReturn(12345)
        `when`(cookieFactory.createCookie(aValidCookieString)).thenReturn(aValidCookie)

        val actual = startClassesEndpoint.process(aValidCookieString)

        assertEquals(expected, actual.body)
        assertEquals(200, actual.statusCode.value())
    }

    @Test
    fun returns400WhenBadlyFormattedInput() {
        val expected = ApiResponse("Badly formatted input", null)

        val actual = startClassesEndpoint.process(123)

        assertEquals(expected, actual.body)
        assertEquals(400, actual.statusCode.value())
    }

    @Test
    fun returns401WhenEmptyInput() {
        val expected = ApiResponse("Missing cookie", null)

        val actual = startClassesEndpoint.process("")

        assertEquals(expected, actual.body)
        assertEquals(401, actual.statusCode.value())
    }

    @Test
    fun returns400WhenNoAvailableClasses() {
        val expected = ApiResponse("No available classes", listOf<ClassCode>())
        `when`(cookieFactory.createCookie(aValidCookieString)).thenReturn(aValidCookie)
        `when`(ufrgsService.startPossibilitiesProcessing(aValidCookie)).thenThrow(NoPossibilitiesException())

        val actual = startClassesEndpoint.process(aValidCookieString)

        assertEquals(expected, actual.body)
        assertEquals(400, actual.statusCode.value())
    }

    @Test
    fun returns401WhenOutdatedCookie() {
        val expected = ApiResponse("Outdated cookie", null)
        `when`(cookieFactory.createCookie(aValidCookieString)).thenReturn(aValidCookie)
        `when`(ufrgsService.startPossibilitiesProcessing(aValidCookie)).thenThrow(OutdatedCookieException())

        val actual = startClassesEndpoint.process(aValidCookieString)

        assertEquals(expected, actual.body)
        assertEquals(401, actual.statusCode.value())
    }

    @Test
    fun returns501WhenCouldNotParse() {
        val expected = ApiResponse("Error parsing UFRGS response", null)
        `when`(cookieFactory.createCookie(aValidCookieString)).thenReturn(aValidCookie)
        `when`(ufrgsService.startPossibilitiesProcessing(aValidCookie)).thenThrow(CouldNotParseException())

        val actual = startClassesEndpoint.process(aValidCookieString)

        assertEquals(expected, actual.body)
        assertEquals(501, actual.statusCode.value())
    }

    @Test
    fun returns502WhenConnectionIssues() {
        val expected = ApiResponse("Error contacting UFRGS", "a")
        `when`(cookieFactory.createCookie(aValidCookieString)).thenReturn(aValidCookie)
        `when`(ufrgsService.startPossibilitiesProcessing(aValidCookie)).thenThrow(CouldNotGetUfrgsPageException("a"))

        val actual = startClassesEndpoint.process(aValidCookieString)

        assertEquals(expected, actual.body)
        assertEquals(502, actual.statusCode.value())
    }

    @Test
    fun returns500WhenOtherIssue() {
        val expected = ApiResponse("Internal server error", null)
        `when`(cookieFactory.createCookie(aValidCookieString)).thenReturn(aValidCookie)
        `when`(ufrgsService.startPossibilitiesProcessing(aValidCookie)).thenThrow(RuntimeException())

        val actual = startClassesEndpoint.process(aValidCookieString)

        assertEquals(expected, actual.body)
        assertEquals(500, actual.statusCode.value())
    }
}