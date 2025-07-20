package com.sarim.composeshapefittersampleapp

import android.content.Context
import androidx.startup.Initializer
import org.koin.androix.startup.KoinInitializer
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.KoinComponent

class MainInitializer :
    Initializer<Unit>,
    KoinComponent {
    @Suppress("EmptyFunctionBlock")
    override fun create(context: Context) {
    }

    @OptIn(KoinExperimentalAPI::class)
    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(KoinInitializer::class.java)
}
