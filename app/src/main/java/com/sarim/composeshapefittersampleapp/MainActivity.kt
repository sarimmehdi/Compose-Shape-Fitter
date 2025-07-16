package com.sarim.composeshapefittersampleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.sarim.compose_shape_fiiter.DrawingScreen
import com.sarim.compose_shape_fiiter.ShapeType
import com.sarim.composeshapefittersampleapp.ui.theme.ComposeShapeFitterSampleAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeShapeFitterSampleAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DrawingScreen(
                        shapeType = ShapeType.PENTAGON,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
