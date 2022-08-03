package co.uk.zohaib.nextholidaykotlin.api

import co.uk.zohaib.nextholidaykotlin.clock.Clock
import io.mockk.InternalPlatformDsl.toStr
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NextHolidayControllerTest {
    private val client: WebClient = mockk()
    private val clock: Clock = mockk()
    private val nextHolidayController: NextHolidayController = NextHolidayController(client, clock)

    @Test
    fun shouldReturnNextHoliday() {

        val response = mockk<WebClient.ResponseSpec>()
        val spec = mockk<WebClient.RequestHeadersUriSpec<*>>()
        val bodySpec = mockk<WebClient.RequestBodySpec>()

        every { clock.now() } returns LocalDate.EPOCH
        every { client.get() } returns spec
        every { spec.uri("https://date.nager.at/api/v3/PublicHolidays/2022/GB") } returns bodySpec
        every { bodySpec.retrieve() } returns response
        every {
            response.toEntity(object : ParameterizedTypeReference<List<HolidayResponseBody>?>() {})
        } returns Mono.just(ResponseEntity.ok(listOf(HolidayResponseBody(LocalDate.of(2022, 8, 2).toStr(), "lol"))))

        runBlocking {
            val nextHoliday: ResponseEntity<HolidayResponseBody> =
                nextHolidayController.nextHoliday(HolidayRequestBody("GB"))
            assertEquals(HttpStatus.OK, nextHoliday.statusCode)
        }
    }

    @Test
    fun shouldReturnError() {
        val spec = mockk<WebClient.RequestHeadersUriSpec<*>>()
        val bodySpec = mockk<WebClient.RequestBodySpec>()

        every { clock.now() } returns LocalDate.EPOCH
        every { client.get() } returns spec
        every { spec.uri("https://date.nager.at/api/v3/PublicHolidays/2022/GB") } returns bodySpec
        every { bodySpec.retrieve() } throws WebClientResponseException(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "some reason",
            null,
            null,
            null
        )

        runBlocking {
            assertThrows<WebClientResponseException> { nextHolidayController.nextHoliday(HolidayRequestBody("GB")) }
        }
    }
}
