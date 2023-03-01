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
    fun orchestratesEnrollmentPossibilities() {
        val cookie = Cookie("123")
        val possibilities = listOf(ClassCode("Class1", "1", "2", "3", "4"), ClassCode("Class2", "5", "6", "7", "8"))
        val expected = listOf(
            CollegeClass(
                listOf(
                    Timeslot(
                        "CLASS1",
                        4,
                        listOf("Prof1"), listOf(ScheduleTime("MON", "8:30", "10:10", "EAD", null))
                    )
                ), 2
            )
        )
        `when`(possibilitiesRequester.requestPossibilities(cookie)).thenReturn(possibilities)
        `when`(collegeClassRequester.bulkRequest(possibilities)).thenReturn(expected)

        val actual = service.requestEnrollmentPossibilities(cookie)

        assertEquals(expected, actual)
    }
}