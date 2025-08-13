package com.sarim.test_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sarim.nav.MainActivityScreen
import com.sarim.nav.theme.ComposeShapeFitterSampleAppTheme

internal class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeShapeFitterSampleAppTheme {
                MainActivityScreen()
            }
        }
    }
}