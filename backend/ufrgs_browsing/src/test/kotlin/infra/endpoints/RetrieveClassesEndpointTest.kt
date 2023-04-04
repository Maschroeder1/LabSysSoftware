package infra.endpoints

import application.UfrgsService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import model.CollegeClass
import model.KeyNotRegisteredException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import spring.ApiResponse

class RetrieveClassesEndpointTest {
    private val service = mock(UfrgsService::class.java)
    private val endpoint = RetrieveClassesEndpoint(service)

    @Test
    fun returnsFullyProcessedPossibilities() {
        val classes = getClassInput("/fully_processed_input.json")
        val expected = ApiResponse("Ok", getClassExpected("/fully_processed_expected.json"))
        `when`(service.retrieveCurrentPossibilities(123)).thenReturn(classes)

        val actual = endpoint.process("123")

        assertEquals(expected, actual.body)
        assertEquals(200, actual.statusCode.value())
    }

    @Test
    fun returnsPartiallyProcessedPossibilities() {
        val classes = getClassInput("/partial_processed_input.json")
        val expected = ApiResponse("Processing", getClassExpected("/partial_processed_expected.json"))
        `when`(service.retrieveCurrentPossibilities(123)).thenReturn(classes)

        val actual = endpoint.process("123")

        assertEquals(expected, actual.body)
        assertEquals(206, actual.statusCode.value())
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

    private fun getClassInput(filename: String): Map<String, CollegeClass?> {
        val htmlFile = RetrieveClassesEndpointTest::class.java.getResource(filename)
        if (htmlFile != null) {
            val gson = Gson()
            return gson.fromJson(htmlFile.readText(), object : TypeToken<Map<String, CollegeClass?>>() {}.type)
        }
        fail("Unable to read expected json")
    }

    private fun getClassExpected(filename: String): List<CollegeClassView> {
        val htmlFile = RetrieveClassesEndpointTest::class.java.getResource(filename)
        if (htmlFile != null) {
            val gson = Gson()
            return gson.fromJson(htmlFile.readText(), object : TypeToken<List<CollegeClassView>>() {}.type)
        }
        fail("Unable to read expected json")
    }
}