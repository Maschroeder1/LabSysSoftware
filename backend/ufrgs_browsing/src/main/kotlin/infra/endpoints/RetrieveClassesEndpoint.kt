package infra.endpoints

import application.UfrgsService
import model.KeyNotRegisteredException
import org.springframework.http.ResponseEntity
import spring.ApiResponse

class RetrieveClassesEndpoint(private val service: UfrgsService) : Endpoint {
    override fun process(input: Any?): ResponseEntity<ApiResponse> {
        if (input !is String? || input == null) {
            return ResponseEntity.status(400).body(ApiResponse("Badly formatted input", null))
        }
        val key =
            input.toIntOrNull() ?: return ResponseEntity.status(401).body(ApiResponse("Missing or invalid key", null))

        return doProcess(key)
    }

    private fun doProcess(key: Int): ResponseEntity<ApiResponse> {
        return try {
            val possibilities = service.retrieveCurrentPossibilities(key)
            ResponseEntity.ok(ApiResponse("Ok", possibilities))
        } catch (e: KeyNotRegisteredException) {
            ResponseEntity.status(400).body(ApiResponse("Key was not previously created", null))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(ApiResponse("Internal server error", null))
        }
    }
}