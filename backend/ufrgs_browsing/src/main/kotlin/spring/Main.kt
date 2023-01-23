package spring

import infra.HttpRequestCreator
import infra.HttpRequestLoginRequester
import model.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.http.HttpClient

@SpringBootApplication
open class Main

val httpClient: HttpClient = HttpClient.newBuilder().build()
val httpRequestCreator = HttpRequestCreator()
val loginRequester: LoginRequester = HttpRequestLoginRequester(httpClient, httpRequestCreator)

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}

@RestController
@RequestMapping("/api", produces = [MediaType.APPLICATION_JSON_VALUE])
class Endpoints {

    @PostMapping("/login")
    fun loginEndpoint(@RequestBody credentials: Login): ResponseEntity<ApiResponse> {
        val response = loginRequester.requestLogin(credentials)

        return when (response.reason) {
            LoginRequestResult.LOGIN_ERROR -> ResponseEntity.status(401)
                .body(ApiResponse("Invalid user/password", response.cookie))
            LoginRequestResult.CONNECTION_ERROR -> ResponseEntity.status(504)
                .body(ApiResponse("Error contacting UFRGS server", response.cookie))
            LoginRequestResult.COOKIE_ERROR -> ResponseEntity.status(500)
                .body(ApiResponse("Error extracting login result", response.cookie))
            LoginRequestResult.CAPTCHA_ERROR -> ResponseEntity.status(400)
                .body(ApiResponse("Requires manual Captcha. Please login to actual website", response.cookie))
            LoginRequestResult.SUCCESS -> ResponseEntity.ok(ApiResponse("Success", response.cookie))
        }
    }
}

data class ApiResponse(val message: String, val content: Any?) {}