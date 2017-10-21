package org.jetbrains.ktor.samples.json

import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import com.github.salomonbrys.kodein.instance
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.features.CallLogging
import org.jetbrains.ktor.features.Compression
import org.jetbrains.ktor.features.DefaultHeaders
import org.jetbrains.ktor.gson.GsonSupport
import org.jetbrains.ktor.host.commandLineEnvironment
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
import org.jetbrains.ktor.tomcat.Tomcat

data class Model(val name: String, val items: List<Item>)
data class Item(val key: String, val value: String)

fun main(args: Array<String>) {
   embeddedServer(Tomcat, commandLineEnvironment(args)).start(wait = true)
}

fun Application.main() {
    routing {}
    Controller(this).initialize()
}

private class Controller(val receiver : Application) : KodeinGlobalAware {

    fun initialize() {
        val model : Model = instance()
        this.receiver.routing {
            get("/v1") {
                call.respond(model)
            }
            get("/v1/item/{key}") {
                val item = model.items.firstOrNull { it.key == call.parameters["key"] }
                if (item == null)
                    call.respond(HttpStatusCode.NotFound)
                else
                    call.respond(item)
            }
        }
    }
}

fun Application.features() {
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(GsonSupport) {
        setPrettyPrinting()
    }
    install(PrometheusMonitor)
    install(RequestTiming)
}
