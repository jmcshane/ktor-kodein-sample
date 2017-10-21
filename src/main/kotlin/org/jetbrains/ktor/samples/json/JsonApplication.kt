package org.jetbrains.ktor.samples.json

import com.github.salomonbrys.kodein.*
import org.jetbrains.ktor.application.*
import org.jetbrains.ktor.features.*
import org.jetbrains.ktor.gson.*
import org.jetbrains.ktor.http.*
import org.jetbrains.ktor.response.*
import org.jetbrains.ktor.routing.*
import org.jetbrains.ktor.tomcat.*
import org.jetbrains.ktor.host.*
import com.github.salomonbrys.kodein.conf.*

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

}
