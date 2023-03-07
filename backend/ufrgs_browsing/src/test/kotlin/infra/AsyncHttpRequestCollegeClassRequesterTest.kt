package infra

import model.ClassCode
import model.CollegeClass
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class AsyncHttpRequestCollegeClassRequesterTest {

    @Test
    fun bulkSavesCodesToController() {
        val classCodes = listOf(ClassCode("c1", "a1", "h1", "c1", "s1"), ClassCode("c2", "a2", "h2", "c2", "s2"))

        `when`(controller.queueClassesToProcess(classCodes)).thenReturn(123)

        assertEquals(123, requester.bulkRequest(classCodes))
    }

    @Test
    fun bulkQueries() {
        val expected = mapOf("c1" to CollegeClass(emptyList(), 456), "c2" to null)
        `when`(controller.getCollegeClasses(123)).thenReturn(expected)

        val actual = requester.bulkQuery(123)

        assertEquals(expected, actual)
    }

    @Test
    fun sendsRequestToUpdateController() {
        val request = mock(HttpRequest::class.java)
        val response = mock(HttpResponse::class.java)
        val expected = CollegeClass(listOf(), 5)
        `when`(creator.createGetRequest(endpoint1)).thenReturn(request)
        `when`(client.send(ArgumentMatchers.eq(request), Mockito.any(HttpResponse.BodyHandler::class.java)))
            .thenReturn(response)
        `when`(response.body()).thenReturn("any html")
        `when`(parser.parseClass("any html")).thenReturn(expected)
        `when`(controller.getClassToProcess()).thenReturn(ClassCode("c1", "a1", "h1", "c1", "s1"))

        requester.updateController()

        verify(controller).cache("c1", expected)
    }

    @Test
    fun doesNothingIfControllerIsEmpty() {
        `when`(controller.getClassToProcess()).thenReturn(null)
        `when`(creator.createGetRequest(endpoint1)).then { fail("Should not have executed") }

        requester.updateController()
    }

    private val client = mock(HttpClient::class.java)
    private val creator = mock(HttpRequestCreator::class.java)
    private val parser = mock(UfrgsPageParser::class.java)
    private val controller = mock(CollegeClassController::class.java)
    private val requester = AsyncHttpRequestCollegeClassRequester(client, creator, parser, controller)

    private val endpoint1 =
        "https://www1.ufrgs.br/PortalEnsino/GraduacaoAluno/view/HorarioAtividade.php?CodAtiv=a1&CodHab=h1&CodCur=c1&Sem=s1"
}