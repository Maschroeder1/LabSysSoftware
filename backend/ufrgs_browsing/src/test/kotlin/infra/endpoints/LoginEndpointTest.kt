package infra.endpoints

import application.UfrgsService
import model.Cookie
import model.Login
import model.LoginRequestResponse
import model.LoginRequestResult
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import spring.ApiResponse

class LoginEndpointTest {
    private val service = mock(UfrgsService::class.java)
    private val loginEndpoint = LoginEndpoint(service)
    private val anyCredentials = Login("abc", "123")
    private val anyCookie = Cookie("any value")

    @Test
    fun returnsSuccessFromService() {
        val expected = ApiResponse("", anyCookie)
        `when`(service.requestLogin(anyCredentials)).thenReturn(aLoginRequestResponseWith(LoginRequestResult.SUCCESS))

        val actual = loginEndpoint.process(anyCredentials)

        assertEquals(expected, actual.body)
        assertEquals(200, actual.statusCode.value())
        assertEquals(1, actual.headers.size)
        assertEquals(listOf("any value"), actual.headers["set-cookie"])
    }

    @Test
    fun returns400WhenBadlyFormattedInput() {
        val expected = ApiResponse("Badly formatted input", null)

        val actual = loginEndpoint.process("not credentials")

        assertEquals(expected, actual.body)
        assertEquals(400, actual.statusCode.value())
        assertTrue(actual.headers.isEmpty())
    }

    @Test
    fun returns400WhenMissingUserAndPassword() {
        val expected = ApiResponse("Missing user and password", null)

        val actual = loginEndpoint.process(Login("", ""))

        assertEquals(expected, actual.body)
        assertEquals(400, actual.statusCode.value())
        assertTrue(actual.headers.isEmpty())
    }

    @Test
    fun returns400WhenMissingUser() {
        val expected = ApiResponse("Missing user", null)

        val actual = loginEndpoint.process(Login("", "123"))

        assertEquals(expected, actual.body)
        assertEquals(400, actual.statusCode.value())
        assertTrue(actual.headers.isEmpty())
    }

    @Test
    fun returns400WhenMissingPassword() {
        val expected = ApiResponse("Missing password", null)

        val actual = loginEndpoint.process(Login("abc", ""))

        assertEquals(expected, actual.body)
        assertEquals(400, actual.statusCode.value())
        assertTrue(actual.headers.isEmpty())
    }

    @Test
    fun returns400WhenRequiresCaptcha() {
        val expected = ApiResponse("Requires manual Captcha. Please login to actual website", null)
        `when`(service.requestLogin(anyCredentials))
            .thenReturn(aLoginRequestResponseWith(LoginRequestResult.CAPTCHA_ERROR))

        val actual = loginEndpoint.process(anyCredentials)

        assertEquals(expected, actual.body)
        assertEquals(400, actual.statusCode.value())
        assertTrue(actual.headers.isEmpty())
    }

    @Test
    fun returns401WhenBadCredentials() {
        val expected = ApiResponse("Invalid user/password", null)
        `when`(service.requestLogin(anyCredentials))
            .thenReturn(aLoginRequestResponseWith(LoginRequestResult.LOGIN_ERROR))

        val actual = loginEndpoint.process(anyCredentials)

        assertEquals(expected, actual.body)
        assertEquals(401, actual.statusCode.value())
        assertTrue(actual.headers.isEmpty())
    }

    @Test
    fun returns500WhenCouldNotParseCookie() {
        val expected = ApiResponse("Error extracting login result", null)
        `when`(service.requestLogin(anyCredentials))
            .thenReturn(aLoginRequestResponseWith(LoginRequestResult.COOKIE_ERROR))

        val actual = loginEndpoint.process(anyCredentials)

        assertEquals(expected, actual.body)
        assertEquals(500, actual.statusCode.value())
        assertTrue(actual.headers.isEmpty())
    }

    @Test
    fun returns504WhenConnectionError() {
        val expected = ApiResponse("Error contacting UFRGS server", null)
        `when`(service.requestLogin(anyCredentials))
            .thenReturn(aLoginRequestResponseWith(LoginRequestResult.CONNECTION_ERROR))

        val actual = loginEndpoint.process(anyCredentials)

        assertEquals(expected, actual.body)
        assertEquals(504, actual.statusCode.value())
        assertTrue(actual.headers.isEmpty())
    }

    private fun aLoginRequestResponseWith(result: LoginRequestResult): LoginRequestResponse {
        return if (result == LoginRequestResult.SUCCESS) {
            LoginRequestResponse(true, result, anyCookie)
        } else {
            LoginRequestResponse(false, result, null)
        }
    }
}