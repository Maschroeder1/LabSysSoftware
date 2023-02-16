package infra

import model.Cookie
import model.Login
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpRequest

open class HttpRequestCreator {

    open fun createLoginRequest(credentials: Login, endpoint: String): HttpRequest {
        val parameters = "usuario=${URLEncoder.encode(credentials.user, "UTF-8")}" +
                "&senha=${URLEncoder.encode(credentials.password, "UTF-8")}"

        return HttpRequest
            .newBuilder()
            .uri(URI.create(endpoint))
            .POST(HttpRequest.BodyPublishers.ofString(parameters))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("cache-control", "no-cache")
            .build()
    }

    open fun createGetRequest(cookie: Cookie, endpoint: String): HttpRequest {
        val cookieValue = cookie.value.replace("localhost", "www1.ufrgs.br")

        return HttpRequest
            .newBuilder()
            .uri(URI.create(endpoint))
            .GET()
            .header("Cookie", cookieValue)
            .build()
    }

    open fun createGetRequest(endpoint: String) : HttpRequest {
        return HttpRequest
            .newBuilder()
            .uri(URI.create(endpoint))
            .GET()
            .build()
    }
}