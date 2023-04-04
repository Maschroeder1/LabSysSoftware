package infra.endpoints

import application.UfrgsService
import model.CollegeClass
import model.KeyNotRegisteredException
import model.ScheduleTime
import model.Timeslot
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
            prettify(possibilities)
        } catch (e: KeyNotRegisteredException) {
            ResponseEntity.status(400).body(ApiResponse("Key was not previously created", null))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(ApiResponse("Internal server error", null))
        }
    }

    private fun prettify(possibilities: Map<String, CollegeClass?>): ResponseEntity<ApiResponse> {
        val pretty = possibilities.map { (key, value) -> toView(key, value) }

        return if (!possibilities.containsValue(null)) {
            ResponseEntity.ok(ApiResponse("Ok", pretty))
        } else {
            ResponseEntity.status(206).body(ApiResponse("Processing", pretty))
        }
    }

    private fun toView(key: String, value: CollegeClass?) : CollegeClassView {
        return if (value != null) {
            CollegeClassView(key, value.timeslots, value.credits, value.classPlan)
        } else {
            CollegeClassView(key, null, null, null)
        }
    }
}

data class CollegeClassView(val name: String, val timeslots: List<Timeslot>?, val credits: Int?, var classPlan: String?)