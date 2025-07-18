package com.sarim.composeshapefittersampleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.sarim.compose_shape_fitter.CircleShape
import com.sarim.compose_shape_fitter.DrawableShape
import com.sarim.compose_shape_fitter.DrawingScreen
import com.sarim.composeshapefittersampleapp.ui.theme.ComposeShapeFitterSampleAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeShapeFitterSampleAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var drawnPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
                    val currentShapeToDraw: DrawableShape = remember { CircleShape(Color.Blue, 5f) }
                    DrawingScreen(
                        drawableShape = currentShapeToDraw,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        onPointsChange = { finalPoints ->
                            drawnPoints = finalPoints
                            // You can now do something with the finalPoints, like saving or analyzing them
                            println("Shape drawn with ${drawnPoints.size} points.")
                        },
                    )
                }
            }
        }
    }
}
