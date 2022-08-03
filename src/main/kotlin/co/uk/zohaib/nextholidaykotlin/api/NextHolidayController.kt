package co.uk.zohaib.nextholidaykotlin.api

import co.uk.zohaib.nextholidaykotlin.clock.Clock
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate

data class HolidayRequestBody(val country: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class HolidayResponseBody(val date: String = "", val name: String = "")

private const val NEXT_HOLIDAY_API_URI = "https://date.nager.at/api/v3/PublicHolidays/%s/%s"

@Controller
class NextHolidayController(val client: WebClient, val clock: Clock) {

    @GetMapping("/next-holiday")
    suspend fun nextHoliday(@RequestBody body: HolidayRequestBody): ResponseEntity<HolidayResponseBody> {
        val responseEntity = client
            .get()
            .uri(String.format(NEXT_HOLIDAY_API_URI, LocalDate.now().year, body.country))
            .retrieve()
            .toEntity(object : ParameterizedTypeReference<List<HolidayResponseBody>>() {})
            .awaitSingle()

        return ResponseEntity.ok(responseEntity.body?.first { LocalDate.parse(it.date).isAfter(clock.now()) })
    }
}
