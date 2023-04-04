package infra.endpoints

import application.UfrgsService
import model.Login
import model.LoginRequestResponse
import model.LoginRequestResult
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spring.ApiResponse

class LoginEndpoint(private val service: UfrgsService) : Endpoint {
    override fun process(input: Any?): ResponseEntity<ApiResponse> {
        if (input !is Login) {
            return ResponseEntity.status(400).body(ApiResponse("Badly formatted input", null))
        }
        if (input.user.isEmpty() && input.password.isEmpty()) {
            return ResponseEntity.status(400).body(ApiResponse("Missing user and password", null))
        }
        if (input.user.isEmpty()) {
            return ResponseEntity.status(400).body(ApiResponse("Missing user", null))
        }
        if (input.password.isEmpty()) {
            return ResponseEntity.status(400).body(ApiResponse("Missing password", null))
        }

        return doProcess(input)
    }

    private fun doProcess(credentials: Login): ResponseEntity<ApiResponse> {
        val response = service.requestLogin(credentials)

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
}