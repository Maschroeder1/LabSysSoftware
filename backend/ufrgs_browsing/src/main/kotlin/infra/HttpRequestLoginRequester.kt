package infra

import model.*
import java.net.http.HttpClient
import java.net.http.HttpResponse

class HttpRequestLoginRequester(private val client: HttpClient, private val creator: HttpRequestCreator) :
    LoginRequester {
    private val endpoint = "https://www1.ufrgs.br/sistemas/portal/login?Destino=portal-matricula"

    override fun requestLogin(credentials: Login): LoginRequestResponse {
        val request = creator.createLoginRequest(credentials, endpoint)

        val response: HttpResponse<String>
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString())
        } catch (e: Exception) {
            return errorResponse(LoginRequestResult.CONNECTION_ERROR)
        }

        val loginResponse = loginResponseFrom(response)
        return if (loginResponse.cookie != null && cookieIsInactive(response.body())) {
            loginResponseWithActivatedCookie(credentials, loginResponse.cookie)
        } else {
            loginResponseFrom(response)
        }
    }

    private fun errorResponse(cause: LoginRequestResult): LoginRequestResponse {
        return LoginRequestResponse(false, cause, null)
    }

    private fun loginResponseFrom(response: HttpResponse<String>) : LoginRequestResponse {
        if (response.statusCode() >= 400) {
            return errorResponse(LoginRequestResult.CONNECTION_ERROR)
        }
        if (response.body() != null) {
            if (response.body().contains("Usuário ou senha inválida")) {
                return errorResponse(LoginRequestResult.LOGIN_ERROR)
            }
            if (response.body().contains("falhas de logins excedido")) {
                return errorResponse(LoginRequestResult.CAPTCHA_ERROR)
            }
        }

        val cookie = cookieFrom(response)
        return if (cookie != null) successResponse(cookie) else errorResponse(LoginRequestResult.COOKIE_ERROR)
    }

    private fun cookieFrom(response: HttpResponse<String>): Cookie? {
        val cookieHeader = response.headers().map()["set-cookie"]

        return if (!cookieHeader.isNullOrEmpty()) Cookie(
            cookieHeader[0].replace(
                "www1.ufrgs.br",
                "localhost"
            )
        ) else null
    }

    private fun successResponse(cookie: Cookie): LoginRequestResponse {
        return LoginRequestResponse(true, LoginRequestResult.SUCCESS, cookie)
    }

    private fun cookieIsInactive(html: String): Boolean {
         return html.contains("o login novamente")
    }

    private fun loginResponseWithActivatedCookie(credentials: Login, cookie: Cookie): LoginRequestResponse {
        return try {
            val request = creator.createLoginRequest(credentials, cookie, endpoint)
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            if (response.body().isEmpty()) {
                successResponse(cookie)
            } else {
                errorResponse(LoginRequestResult.COOKIE_ERROR)
            }
        } catch (e: Exception) {
            errorResponse(LoginRequestResult.CONNECTION_ERROR)
        }
    }
}