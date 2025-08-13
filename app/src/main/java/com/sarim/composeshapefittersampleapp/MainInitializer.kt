package com.sarim.composeshapefittersampleapp

import android.content.Context
import androidx.startup.Initializer
import org.koin.androix.startup.KoinInitializer
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.KoinComponent

internal class MainInitializer :
    Initializer<Unit>,
    KoinComponent {
    @Suppress("EmptyFunctionBlock")
    override fun create(context: Context) {
        // This function is empty because the primary purpose of this Initializer
        // is to ensure Koin is initialized via its dependency on KoinInitializer.
        // Koin itself handles its setup within KoinInitializer.
        // If there were any application-specific initialization tasks
        // that needed to run after Koin is ready, they would be placed here.
    }

    @OptIn(KoinExperimentalAPI::class)
    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(KoinInitializer::class.java)
}
