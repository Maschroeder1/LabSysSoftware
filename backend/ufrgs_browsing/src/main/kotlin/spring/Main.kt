package spring

import application.UfrgsService
import infra.*
import model.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.http.HttpClient

@SpringBootApplication
open class Main

val httpClient: HttpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()
val httpRequestCreator = HttpRequestCreator()
val ufrgsPageParser = JsoupUfrgsPageParser()
val loginRequester: LoginRequester = HttpRequestLoginRequester(httpClient, httpRequestCreator)
val possibilitiesRequester: PossibilitiesRequester =
    HttpRequestPossibilitiesRequester(httpClient, httpRequestCreator, ufrgsPageParser)
val collegeClassRequester: CollegeClassRequester =
    HttpRequestCollegeClassRequester(httpClient, httpRequestCreator, ufrgsPageParser)
val ufrgsService = UfrgsService(loginRequester, possibilitiesRequester, collegeClassRequester)

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}

@RestController
@RequestMapping("/api", produces = [MediaType.APPLICATION_JSON_VALUE])
class Endpoints {

    @PostMapping("/login")
    fun loginEndpoint(@RequestBody credentials: Login): ResponseEntity<ApiResponse> {
        if (credentials.user.isEmpty() && credentials.password.isEmpty()) {
            return ResponseEntity.status(400).body(ApiResponse("Missing user and password", null))
        }
        if (credentials.user.isEmpty()) {
            return ResponseEntity.status(400).body(ApiResponse("Missing user", null))
        }
        if (credentials.password.isEmpty()) {
            return ResponseEntity.status(400).body(ApiResponse("Missing password", null))
        }

        val response = ufrgsService.requestLogin(credentials)

        return when (response.reason) {
            LoginRequestResult.LOGIN_ERROR -> ResponseEntity.status(401)
                .body(ApiResponse("Invalid user/password", response.cookie))
            LoginRequestResult.CONNECTION_ERROR -> ResponseEntity.status(504)
                .body(ApiResponse("Error contacting UFRGS server", response.cookie))
            LoginRequestResult.COOKIE_ERROR -> ResponseEntity.status(500)
                .body(ApiResponse("Error extracting login result", response.cookie))
            LoginRequestResult.CAPTCHA_ERROR -> ResponseEntity.status(400)
                .body(ApiResponse("Requires manual Captcha. Please login to actual website", response.cookie))
            LoginRequestResult.SUCCESS -> successResponseWithSetCookie(response)
        }
    }

    private fun successResponseWithSetCookie(loginResponse: LoginRequestResponse): ResponseEntity<ApiResponse> {
        val headers = HttpHeaders()
        if (loginResponse.cookie != null) {
            headers.add("set-cookie", loginResponse.cookie.value)
        }
        return ResponseEntity<ApiResponse>(ApiResponse("", loginResponse.cookie), headers, HttpStatus.OK)
    }

    @GetMapping("/classes")
    fun classesEndpoint(@RequestHeader Cookie: String?): ResponseEntity<ApiResponse> {
        if (Cookie.isNullOrEmpty()) {
            return ResponseEntity.status(401).body(ApiResponse("Missing cookie", null))
        }

        return try {
            val classes = ufrgsService.requestEnrollmentPossibilities(cookieFrom(Cookie))
            ResponseEntity.ok(ApiResponse("Ok", classes))
        } catch (e: OutdatedCookieException) {
            ResponseEntity.status(401).body(ApiResponse("Outdated cookie", null))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(ApiResponse("Internal server error", null))
        } catch (e: CouldNotParseException) {
            ResponseEntity.status(501).body(ApiResponse("Error parsing UFRGS response", e.message))
        } catch (e: CouldNotGetUfrgsPageException) {
            ResponseEntity.status(502).body(ApiResponse("Error contacting UFRGS", e.message))
        }
    }

    private fun cookieFrom(inputCookie: String): Cookie {
        var partialCookie = inputCookie.trim()
        if (!partialCookie.endsWith(";")) {
            partialCookie += ";"
        }
        if (!partialCookie.contains("Path")) {
            partialCookie += " Path=/;"
        }
        if (!partialCookie.contains("Domain")) {
            partialCookie += " Domain=www1.ufrgs.br;"
        }

        return Cookie(partialCookie)
    }
}

data class ApiResponse(val message: String, val content: Any?) {}