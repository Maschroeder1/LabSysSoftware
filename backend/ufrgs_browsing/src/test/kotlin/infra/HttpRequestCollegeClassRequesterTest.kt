package infra

import model.ClassCode
import model.CollegeClass
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class HttpRequestCollegeClassRequesterTest {

    @Test
    fun requestsClass() {
        val classes = listOf(ClassCode("Class1", "a1", "h1", "c1", "s1"))
        val request = mock(HttpRequest::class.java)
        val response = mock(HttpResponse::class.java)
        val expected = CollegeClass(listOf(), 5)
        `when`(creator.createGetRequest(endpoint1)).thenReturn(request)
        `when`(client.send(ArgumentMatchers.eq(request), Mockito.any(HttpResponse.BodyHandler::class.java)))
            .thenReturn(response)
        `when`(response.body()).thenReturn("any html")
        `when`(parser.parseClasses("any html")).thenReturn(expected)

        val actual = requester.bulkRequest(classes)

        assertEquals(listOf(expected), actual)
    }

    private val client = mock(HttpClient::class.java)
    private val creator = mock(HttpRequestCreator::class.java)
    private val parser = mock(UfrgsPageParser::class.java)
    private val requester = HttpRequestCollegeClassRequester(client, creator, parser)

    private val endpoint1 =
        "https://www1.ufrgs.br/PortalEnsino/GraduacaoAluno/view/HorarioAtividade.php?CodAtiv=a1&CodHab=h1&CodCur=c1&Sem=s1"
}