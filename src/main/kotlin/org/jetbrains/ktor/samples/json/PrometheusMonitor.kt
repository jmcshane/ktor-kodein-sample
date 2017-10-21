package org.jetbrains.ktor.samples.json

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.ApplicationFeature
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
import org.jetbrains.ktor.util.AttributeKey

class PrometheusMonitor(val meterRegistry: PrometheusMeterRegistry) {
    class Configuration

    companion object Feature : ApplicationFeature<Application, Configuration, PrometheusMonitor> {
        override val key: AttributeKey<PrometheusMonitor> = AttributeKey("Prometheus Monitor")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): PrometheusMonitor {
            val feature = PrometheusMonitor(PrometheusMeterRegistry(DefaultPrometheusConfig()))
            feature.configureMeters()
            pipeline.routing {
                get ("/prometheus") {
                    call.respond(feature.meterRegistry.scrape())
                }
            }
            Kodein.global.addConfig {
                bind<PrometheusMeterRegistry>() with instance(feature.meterRegistry)
            }

            return feature
        }
    }

    fun configureMeters() {
        JvmMemoryMetrics().bindTo(meterRegistry)
        JvmGcMetrics().bindTo(meterRegistry)
        ProcessorMetrics().bindTo(meterRegistry)
        JvmThreadMetrics().bindTo(meterRegistry)
        ClassLoaderMetrics().bindTo(meterRegistry)
    }
}

private class DefaultPrometheusConfig : PrometheusConfig {
    override fun get(p0: String?): String = ""
}
