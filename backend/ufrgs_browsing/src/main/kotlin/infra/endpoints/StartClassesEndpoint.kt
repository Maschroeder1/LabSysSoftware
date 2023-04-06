package infra.endpoints

import application.UfrgsService
import infra.CookieFactory
import model.*
import org.springframework.http.ResponseEntity
import spring.ApiResponse

class StartClassesEndpoint(private val service: UfrgsService, private val cookieFactory: CookieFactory) : Endpoint {
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
            val code = service.startPossibilitiesProcessing(cookie)
            ResponseEntity.ok(ApiResponse("Ok", code))
        } catch (e: NoPossibilitiesException) {
            ResponseEntity.status(400).body(ApiResponse("No available classes", emptyList<ClassCode>()))
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