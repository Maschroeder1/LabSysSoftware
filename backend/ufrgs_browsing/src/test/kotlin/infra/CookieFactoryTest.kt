package infra

import model.Cookie
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CookieFactoryTest {
    private val factory = CookieFactory()

    @Test
    fun doesNothingIfAlreadyHasDomainAndPath() {
        val expected = Cookie("PHPSESSID=123; Path=/somewhere/; Domain=www.somewhere.com;")
        val actual = factory.createCookie("PHPSESSID=123; Path=/somewhere/; Domain=www.somewhere.com;")

        assertEquals(expected, actual)
    }

    @Test
    fun trimsLeadingAndTrailingSpaces() {
        val expected = Cookie("PHPSESSID=123; Path=/somewhere/; Domain=www.somewhere.com;")
        val actual = factory.createCookie("  PHPSESSID=123; Path=/somewhere/; Domain=www.somewhere.com;  ")

        assertEquals(expected, actual)
    }

    @Test
    fun addsTrailingSemiColon() {
        val expected = Cookie("PHPSESSID=123; Path=/somewhere/; Domain=www.somewhere.com;")
        val actual = factory.createCookie("PHPSESSID=123; Path=/somewhere/; Domain=www.somewhere.com")

        assertEquals(expected, actual)
    }

    @Test
    fun addsDefaultPath() {
        val expected = Cookie("PHPSESSID=123; Domain=www.somewhere.com; Path=/;")
        val actual = factory.createCookie("PHPSESSID=123; Domain=www.somewhere.com")

        assertEquals(expected, actual)
    }

    @Test
    fun addsDefaultDomain() {
        val expected = Cookie("PHPSESSID=123; Path=/somewhere/; Domain=www1.ufrgs.br;")
        val actual = factory.createCookie("PHPSESSID=123; Path=/somewhere/;")

        assertEquals(expected, actual)
    }

    @Test
    fun addsDefaultPathAndDomain() {
        val expected = Cookie("PHPSESSID=123; Path=/; Domain=www1.ufrgs.br;")
        val actual = factory.createCookie("PHPSESSID=123")

        assertEquals(expected, actual)
    }
}