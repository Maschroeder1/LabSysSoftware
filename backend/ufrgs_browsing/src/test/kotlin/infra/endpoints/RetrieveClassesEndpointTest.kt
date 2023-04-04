package infra.endpoints

import application.UfrgsService
import model.CollegeClass
import model.KeyNotRegisteredException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import spring.ApiResponse

class RetrieveClassesEndpointTest {
    private val service = mock(UfrgsService::class.java)
    private val endpoint = RetrieveClassesEndpoint(service)

    @Test
    fun returnsSuccessfulPossibilities() {
        val classes = mapOf("c1" to CollegeClass(emptyList(), 1, null), "c2" to null)
        val expected = ApiResponse("Ok", classes)
        `when`(service.retrieveCurrentPossibilities(123)).thenReturn(classes)

        val actual = endpoint.process("123")

        assertEquals(expected, actual.body)
        assertEquals(200, actual.statusCode.value())
    }

    @Test
    fun returns400WhenMissingInput() {
        val expected = ApiResponse("Badly formatted input", null)

        val actual = endpoint.process(123)

        assertEquals(expected, actual.body)
        assertEquals(400, actual.statusCode.value())
    }

    @Test
    fun returns400WhenKeyWasNotInitialized() {
        val expected = ApiResponse("Key was not previously created", null)
        `when`(service.retrieveCurrentPossibilities(123)).thenThrow(KeyNotRegisteredException())

        val actual = endpoint.process("123")

        assertEquals(expected, actual.body)
        assertEquals(400, actual.statusCode.value())
    }

    @Test
    fun returns401WhenInputIsNotStringNumber() {
        val expected = ApiResponse("Missing or invalid key", null)

        val actual = endpoint.process("text")

        assertEquals(expected, actual.body)
        assertEquals(401, actual.statusCode.value())
    }

    @Test
    fun returns500WhenOtherException() {
        val expected = ApiResponse("Internal server error", null)
        `when`(service.retrieveCurrentPossibilities(123)).thenThrow(RuntimeException())

        val actual = endpoint.process("123")

        assertEquals(expected, actual.body)
        assertEquals(500, actual.statusCode.value())
    }
}