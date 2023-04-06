package spring

import application.UfrgsService
import infra.*
import infra.endpoints.EnrollmentDeclarationEndpoint
import infra.endpoints.LoginEndpoint
import infra.endpoints.RetrieveClassesEndpoint
import infra.endpoints.StartClassesEndpoint
import kotlinx.coroutines.*
import model.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
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
val collegeClassController = CollegeClassController()
val collegeClassRequester: AsyncHttpRequestCollegeClassRequester =
    AsyncHttpRequestCollegeClassRequester(httpClient, httpRequestCreator, ufrgsPageParser, collegeClassController)
val enrollmentDeclarationRequester: EnrollmentDeclarationRequester =
    HttpRequestEnrollmentDeclarationRequester(httpClient, httpRequestCreator, ufrgsPageParser)
val ufrgsService = UfrgsService(
    loginRequester, possibilitiesRequester, collegeClassRequester, enrollmentDeclarationRequester)
val loginEndpoint = LoginEndpoint(ufrgsService)
val cookieFactory = CookieFactory()
val postClassesEndpoint = StartClassesEndpoint(ufrgsService, cookieFactory)
val getClassesEndpoint = RetrieveClassesEndpoint(ufrgsService)
val enrollmentEndpoint = EnrollmentDeclarationEndpoint(ufrgsService, cookieFactory)
dos2unix -n /home/timm/.asdf/lib/utils.bash /home/timm/.asdf/lib/utils.bash

suspend fun main(args: Array<String>) {
    runApplication<Main>(*args)
    keepCachePopulated()
}

private suspend fun keepCachePopulated() {
    var updatedCache: Boolean
    while (true) {
        updatedCache = collegeClassRequester.updateController()

        if (!updatedCache) {
            delay(10_000)
        } else {
            delay(100)
        }
    }
}
export PATH="$./bin:$PATH"
@RestController
@RequestMapping("/api", produces = [MediaType.APPLICATION_JSON_VALUE])
class Endpoints {

    @PostMapping("/login")
    fun loginEndpoint(@RequestBody credentials: Login): ResponseEntity<ApiResponse> {
        return loginEndpoint.process(credentials)
    }

    @PostMapping("/classes")
    fun postClassesEndpoint(@RequestHeader Cookie: String?): ResponseEntity<ApiResponse> {
        return postClassesEndpoint.process(Cookie)
    }

    @GetMapping("/classes/{key}")
    fun getClassesEndpoint(@PathVariable key: String): ResponseEntity<ApiResponse> {
        return getClassesEndpoint.process(key)
    }

    @GetMapping("/enrollmentdeclaration")
    fun getEnrollmentDeclarationEndpoint(@RequestHeader Cookie: String?): ResponseEntity<ApiResponse> {
        return enrollmentEndpoint.process(Cookie)
    }
}

data class ApiResponse(val message: String, val content: Any?) {}