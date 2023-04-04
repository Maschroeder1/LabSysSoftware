package infra.endpoints

import org.springframework.http.ResponseEntity
import spring.ApiResponse

interface Endpoint {

    fun process(input: Any?): ResponseEntity<ApiResponse>
}