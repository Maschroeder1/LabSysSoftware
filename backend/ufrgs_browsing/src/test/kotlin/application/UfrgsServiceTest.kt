package application

import model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class UfrgsServiceTest {
    private val loginRequester = mock(LoginRequester::class.java)
    private var possibilitiesRequester = mock(PossibilitiesRequester::class.java)
    private var collegeClassRequester = mock(CollegeClassRequester::class.java)
    private val service = UfrgsService(loginRequester, possibilitiesRequester, collegeClassRequester)

    @Test
    fun delegatesLoginToLoginRequester() {
        val credentials = Login("user", "password")
        val expected = LoginRequestResponse(true, LoginRequestResult.SUCCESS, Cookie("123"))
        `when`(loginRequester.requestLogin(credentials)).thenReturn(expected)

        val actual = service.requestLogin(credentials)
        assertEquals(expected, actual)
    }

    @Test
    fun returnsKeyForUserRequest() {
        val cookie = Cookie("123")
        val possibilities = listOf(ClassCode("Class1", "1", "2", "3", "4"), ClassCode("Class2", "5", "6", "7", "8"))
        `when`(possibilitiesRequester.requestPossibilities(cookie)).thenReturn(possibilities)
        `when`(collegeClassRequester.bulkRequest(possibilities)).thenReturn(123)

        val actual = service.startPossibilitiesProcessing(cookie)

        assertEquals(123, actual)
    }

    @Test
    fun returnsPossibilitiesForKey() {
        val expected = mapOf("c1" to CollegeClass(emptyList(), 1), "c2" to null)
        `when`(collegeClassRequester.bulkQuery(123)).thenReturn(expected)

        val actual = service.retrieveCurrentPossibilities(123)

        assertEquals(expected, actual)
    }
}