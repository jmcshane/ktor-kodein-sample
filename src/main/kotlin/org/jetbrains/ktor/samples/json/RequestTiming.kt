package org.jetbrains.ktor.samples.json

import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.ApplicationCallPipeline
import org.jetbrains.ktor.application.ApplicationFeature
import org.jetbrains.ktor.util.AttributeKey

class RequestTiming {
    class Configuration

    companion object Feature : ApplicationFeature<Application, Configuration, RequestTiming> {
        override val key: AttributeKey<RequestTiming> = AttributeKey("Request Timing")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): RequestTiming {
            pipeline.intercept(ApplicationCallPipeline.Infrastructure) {
                val time = System.currentTimeMillis()
                proceed()
                System.out.println(System.currentTimeMillis() - time)
            }
            return RequestTiming()
        }
    }
}