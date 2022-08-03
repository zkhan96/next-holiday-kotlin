package co.uk.zohaib.nextholidaykotlin

import co.uk.zohaib.nextholidaykotlin.clock.Clock
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient


@SpringBootApplication
class NextHolidayKotlinApplication {
    @Bean
    fun webClient(): WebClient {
        return WebClient.create()
    }

    @Bean
    fun clock(): Clock {
        return Clock()
    }
}

fun main(args: Array<String>) {
    runApplication<NextHolidayKotlinApplication>(*args)
}

