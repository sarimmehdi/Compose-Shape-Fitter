package com.sarim.composeshapefittersampleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sarim.compose_shape_fitter.CircleShape
import com.sarim.compose_shape_fitter.DrawableShape
import com.sarim.compose_shape_fitter.DrawingScreen
import com.sarim.compose_shape_fitter.Event
import com.sarim.composeshapefittersampleapp.ui.theme.ComposeShapeFitterSampleAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeShapeFitterSampleAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val currentShapeToDraw: DrawableShape = remember { CircleShape(Color.Blue, 5f) }
                    DrawingScreen(
                        drawableShape = currentShapeToDraw,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        onEvent = { event ->
                            when (event) {
                                is Event.ApproximateShapeChangedEvent -> {
                                    println("obtained shape ${event.approximateShape}")
                                }
                                is Event.PointsChangedEvent -> {
                                    println("Shape drawn with ${event.points.size} points.")
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}
