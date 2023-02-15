package infra

import model.Cookie
import model.Login
import model.LoginRequestResponse
import model.LoginRequestResult
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import java.io.IOException
import java.net.http.HttpClient
import java.net.http.HttpHeaders
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.function.BiPredicate

class HttpRequestLoginRequesterTest {
    private val httpClient: HttpClient = mock(HttpClient::class.java)
    private val httpRequestCreator: HttpRequestCreator = mock(HttpRequestCreator::class.java)
    private val httpRequestLoginRequester = HttpRequestLoginRequester(httpClient, httpRequestCreator)
    private val signupEndpoint = "https://www1.ufrgs.br/sistemas/portal/login?Destino=portal-matricula";

    @Test
    fun returnsCookieFromSuccessfulResponse() {
        val login = Login("an user", "a password")
        val request = mock(HttpRequest::class.java)
        val httpResponse = aSuccessfulResponse()
        `when`(httpRequestCreator.createLoginRequest(login, signupEndpoint)).thenReturn(request)
        `when`(httpClient.send(ArgumentMatchers.eq(request), any(HttpResponse.BodyHandler::class.java)))
            .thenReturn(httpResponse)

        val actual = httpRequestLoginRequester.requestLogin(login)

        val expected = LoginRequestResponse(true, LoginRequestResult.SUCCESS, Cookie("a value"))
        assertEquals(expected, actual)
    }

    @Test
    fun returnsConnectionErrorResponse() {
        val login = Login("an user", "a password")
        val request = mock(HttpRequest::class.java)
        `when`(httpRequestCreator.createLoginRequest(login, signupEndpoint)).thenReturn(request)
        `when`(httpClient.send(ArgumentMatchers.eq(request), any(HttpResponse.BodyHandler::class.java)))
            .thenThrow(IOException())

        val actual = httpRequestLoginRequester.requestLogin(login)

        val expected = LoginRequestResponse(false, LoginRequestResult.CONNECTION_ERROR, null)
        assertEquals(expected, actual)
    }

    @Test
    fun returnsLoginErrorResponse() {
        val login = Login("an user", "a password")
        val request = mock(HttpRequest::class.java)
        val httpResponse = aWrongUsernameOrPasswordResponse()
        `when`(httpRequestCreator.createLoginRequest(login, signupEndpoint)).thenReturn(request)
        `when`(httpClient.send(ArgumentMatchers.eq(request), any(HttpResponse.BodyHandler::class.java)))
            .thenReturn(httpResponse)

        val actual = httpRequestLoginRequester.requestLogin(login)

        val expected = LoginRequestResponse(false, LoginRequestResult.LOGIN_ERROR, null)
        assertEquals(expected, actual)
    }

    @Test
    fun returnsCaptchaBlockedResponse() {
        val login = Login("an user", "a password")
        val request = mock(HttpRequest::class.java)
        val httpResponse = aCaptchaResponse()
        `when`(httpRequestCreator.createLoginRequest(login, signupEndpoint)).thenReturn(request)
        `when`(httpClient.send(ArgumentMatchers.eq(request), any(HttpResponse.BodyHandler::class.java)))
            .thenReturn(httpResponse)

        val actual = httpRequestLoginRequester.requestLogin(login)

        val expected = LoginRequestResponse(false, LoginRequestResult.CAPTCHA_ERROR, null)
        assertEquals(expected, actual)
    }

    @Test
    fun returnsMissingCookieResponse() {
        val login = Login("an user", "a password")
        val request = mock(HttpRequest::class.java)
        val httpResponse = aMissingCookieResponse()
        `when`(httpRequestCreator.createLoginRequest(login, signupEndpoint)).thenReturn(request)
        `when`(httpClient.send(ArgumentMatchers.eq(request), any(HttpResponse.BodyHandler::class.java)))
            .thenReturn(httpResponse)

        val actual = httpRequestLoginRequester.requestLogin(login)

        val expected = LoginRequestResponse(false, LoginRequestResult.COOKIE_ERROR, null)
        assertEquals(expected, actual)
    }

    private fun aSuccessfulResponse(): HttpResponse<String> {
        val httpResponse = mock(HttpResponse::class.java)

        `when`(httpResponse.body()).thenReturn("")
        `when`(httpResponse.headers()).thenReturn(HttpHeaders.of(mapOf("set-cookie" to listOf("a value")), TestFiler()))

        return httpResponse as HttpResponse<String>
    }

    private fun aWrongUsernameOrPasswordResponse(): HttpResponse<String> {
        val httpResponse = mock(HttpResponse::class.java)

        `when`(httpResponse.body()).thenReturn("<body>(...) Usuário ou senha inválida (...)</body>")
        `when`(httpResponse.headers()).thenReturn(HttpHeaders.of(mapOf(), TestFiler()))

        return httpResponse as HttpResponse<String>
    }

    private fun aCaptchaResponse(): HttpResponse<String> {
        val httpResponse = mock(HttpResponse::class.java)

        `when`(httpResponse.body()).thenReturn("<body>(...) falhas de logins excedido (...)</body>")
        `when`(httpResponse.headers()).thenReturn(HttpHeaders.of(mapOf(), TestFiler()))

        return httpResponse as HttpResponse<String>
    }

    private fun aMissingCookieResponse(): HttpResponse<String> {
        val httpResponse = mock(HttpResponse::class.java)

        `when`(httpResponse.body()).thenReturn("success body")
        `when`(httpResponse.headers()).thenReturn(HttpHeaders.of(mapOf(), TestFiler()))

        return httpResponse as HttpResponse<String>
    }
}

class TestFiler : BiPredicate<String, String> {
    override fun test(t: String, u: String): Boolean {
        return true
    }

}