package infra

import model.Cookie

open class CookieFactory {
    open fun createCookie(inputCookie: String): Cookie {
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