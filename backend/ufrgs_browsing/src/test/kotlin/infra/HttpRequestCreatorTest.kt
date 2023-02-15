package infra

import model.Cookie
import model.Login
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.net.URI

class HttpRequestCreatorTest {
    private val httpRequestCreator = HttpRequestCreator()

    @Test
    fun createsLoginRequest() {
        val login = Login("anUser", "aPassword")
        val endpoint = "http://www.somewhere.com"

        val result = httpRequestCreator.createLoginRequest(login, endpoint)

        assertEquals(URI.create("http://www.somewhere.com"), result.uri())
        val headers = result.headers()
        assertEquals(2, headers.map().size)
        assertEquals("application/x-www-form-urlencoded", headers.firstValue("Content-Type").get())
        assertEquals("no-cache", headers.firstValue("cache-control").get())
        assertEquals("POST", result.method())
        assertEquals("usuario=anUser&senha=aPassword".length.toLong(), result.bodyPublisher().get().contentLength())
        // cant figure out how to get body itself, comparing sizes is the best I can do
    }

    @Test
    fun urlEncodesLoginRequest() {
        val login = Login("an User", "my p@\$\$w0rd\"\\")
        val endpoint = "http://www.somewhere.com"

        val result = httpRequestCreator.createLoginRequest(login, endpoint)

        assertEquals(URI.create("http://www.somewhere.com"), result.uri())
        val headers = result.headers()
        assertEquals(2, headers.map().size)
        assertEquals("application/x-www-form-urlencoded", headers.firstValue("Content-Type").get())
        assertEquals("no-cache", headers.firstValue("cache-control").get())
        assertEquals("POST", result.method())
        assertEquals("usuario=an+User&senha=my+p%40%24%24w0rd%22%5C".length.toLong(),
            result.bodyPublisher().get().contentLength())
        // cant figure out how to get body itself, comparing sizes is the best I can do
    }

    @Test
    fun addsCookieToPossibilitiesRequest() {
        val cookie = Cookie("some localhost cookie")
        val endpoint = "http://www.somewhere.com"

        val result = httpRequestCreator.createPossibilitiesRequest(cookie, endpoint)

        assertEquals(URI.create("http://www.somewhere.com"), result.uri())
        val headers = result.headers()
        assertEquals(1, headers.map().size)
        assertEquals("some www1.ufrgs.br cookie", headers.firstValue("Cookie").get())
    }
}