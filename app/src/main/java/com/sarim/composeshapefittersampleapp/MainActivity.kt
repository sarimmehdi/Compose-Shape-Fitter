package com.sarim.composeshapefittersampleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.sarim.compose_shape_fitter.DrawingScreen
import com.sarim.compose_shape_fitter.ShapeType
import com.sarim.composeshapefittersampleapp.ui.theme.ComposeShapeFitterSampleAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeShapeFitterSampleAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DrawingScreen(
                        shapeType = ShapeType.Ellipse(ShapeType.Ellipse.Companion.Type.SKEWED_ELLIPSE),
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
