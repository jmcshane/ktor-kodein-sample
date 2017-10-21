package org.jetbrains.ktor.samples.json

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.provider

fun init() {
    Kodein.global.addConfig {
        bind<Model>() with provider {
            Model("root", listOf(Item("A", "Apache"), Item("B", "Bing")))
        }
    }
}