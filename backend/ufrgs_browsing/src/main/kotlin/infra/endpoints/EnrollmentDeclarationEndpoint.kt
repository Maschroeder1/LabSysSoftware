package infra.endpoints

import application.UfrgsService
import infra.CookieFactory
import model.*
import org.springframework.http.ResponseEntity
import spring.ApiResponse

class EnrollmentDeclarationEndpoint(private val service: UfrgsService, private val cookieFactory: CookieFactory) : Endpoint {
    override fun process(input: Any?): ResponseEntity<ApiResponse> {
        if (input !is String?) {
            return ResponseEntity.status(400).body(ApiResponse("Badly formatted input", null))
        }
        if (input.isNullOrEmpty()) {
            return ResponseEntity.status(401).body(ApiResponse("Missing cookie", null))
        }

        return doProcess(cookieFactory.createCookie(input!!))
    }

    private fun doProcess(cookie: Cookie) : ResponseEntity<ApiResponse> {
        return try {
            val declarationLink = service.retrieveEnrollmentDeclaration(cookie)
            ResponseEntity.ok(ApiResponse("Ok", declarationLink))
        } catch (e: JavascriptException) {
            ResponseEntity.status(400).body(ApiResponse("No enrollment declaration previously generated", null))
        } catch (e: OutdatedCookieException) {
            ResponseEntity.status(401).body(ApiResponse("Outdated cookie", null))
        } catch (e: CouldNotParseException) {
            ResponseEntity.status(501).body(ApiResponse("Error parsing UFRGS response", e.message))
        } catch (e: CouldNotGetUfrgsPageException) {
            ResponseEntity.status(502).body(ApiResponse("Error contacting UFRGS", e.message))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(ApiResponse("Internal server error", null))
        }
    }
}