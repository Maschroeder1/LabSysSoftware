package model

data class LoginRequestResponse(val success: Boolean, val reason: LoginRequestResult, val cookie: Cookie?) {}

enum class LoginRequestResult {
    LOGIN_ERROR, CONNECTION_ERROR, COOKIE_ERROR, SUCCESS
}