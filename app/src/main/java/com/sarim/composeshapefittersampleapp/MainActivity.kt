package com.sarim.composeshapefittersampleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarim.compose_shape_fitter.CircleShape
import com.sarim.compose_shape_fitter.DrawableShape
import com.sarim.compose_shape_fitter.DrawingScreen
import com.sarim.compose_shape_fitter.EllipseShape
import com.sarim.compose_shape_fitter.Event
import com.sarim.compose_shape_fitter.HexagonShape
import com.sarim.compose_shape_fitter.ObbShape
import com.sarim.compose_shape_fitter.PentagonShape
import com.sarim.compose_shape_fitter.RectangleShape
import com.sarim.compose_shape_fitter.SkewedEllipseShape
import com.sarim.compose_shape_fitter.SquareShape
import com.sarim.compose_shape_fitter.TriangleShape
import com.sarim.composeshapefittersampleapp.ui.theme.ComposeShapeFitterSampleAppTheme
import kotlinx.coroutines.launch

// Define a sealed class or enum for cleaner shape type management
sealed class ShapeType(
    val displayName: String,
) {
    object Circle : ShapeType("Circle")

    object Ellipse : ShapeType("Ellipse")

    object Hexagon : ShapeType("Hexagon")

    object OrientedRectangle : ShapeType("Oriented Rectangle")

    object OrientedSquare : ShapeType("Oriented Square")

    object Pentagon : ShapeType("Pentagon")

    object Rectangle : ShapeType("Rectangle")

    object OrientedEllipse : ShapeType("Oriented Ellipse")

    object Square : ShapeType("Square")

    object Triangle : ShapeType("Triangle")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeShapeFitterSampleAppTheme {
                MainScreenWithNavigationDrawer()
            }
        }
    }
}

const val DEFAULT_STROKE_WIDTH = 5f

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod")
@Composable
fun MainScreenWithNavigationDrawer() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Default color and stroke width for new shapes
    val defaultShapeColor = Color.Blue

    var currentShapeToDraw by remember {
        mutableStateOf<DrawableShape>(CircleShape(defaultShapeColor, DEFAULT_STROKE_WIDTH))
    }

    val shapeOptions =
        listOf(
            ShapeType.Circle,
            ShapeType.Ellipse,
            ShapeType.Hexagon,
            ShapeType.OrientedRectangle,
            ShapeType.OrientedSquare,
            ShapeType.Pentagon,
            ShapeType.Rectangle,
            ShapeType.OrientedEllipse,
            ShapeType.Square,
            ShapeType.Triangle,
        )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.systemBarsPadding()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        IconButton(onClick = { scope.launch { drawerState.close() } }) {
                            Icon(Icons.Filled.Close, contentDescription = "Close Drawer")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Select Shape", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        thickness = DividerDefaults.Thickness,
                        color = DividerDefaults.color,
                    )
                    LazyColumn {
                        items(shapeOptions) { shapeType ->
                            NavigationDrawerItem(
                                label = { Text(shapeType.displayName) },
                                selected = getShapeType(currentShapeToDraw) == shapeType, // Highlight current
                                onClick = {
                                    currentShapeToDraw =
                                        when (shapeType) {
                                            ShapeType.Circle ->
                                                CircleShape(
                                                    defaultShapeColor,
                                                    DEFAULT_STROKE_WIDTH,
                                                )

                                            ShapeType.Ellipse ->
                                                EllipseShape(
                                                    defaultShapeColor,
                                                    DEFAULT_STROKE_WIDTH,
                                                )

                                            ShapeType.Hexagon ->
                                                HexagonShape(
                                                    defaultShapeColor,
                                                    DEFAULT_STROKE_WIDTH,
                                                )

                                            ShapeType.OrientedRectangle ->
                                                ObbShape(
                                                    defaultShapeColor,
                                                    DEFAULT_STROKE_WIDTH,
                                                    false,
                                                )

                                            ShapeType.OrientedSquare ->
                                                ObbShape(
                                                    defaultShapeColor,
                                                    DEFAULT_STROKE_WIDTH,
                                                    true,
                                                )

                                            ShapeType.Pentagon ->
                                                PentagonShape(
                                                    defaultShapeColor,
                                                    DEFAULT_STROKE_WIDTH,
                                                )

                                            ShapeType.Rectangle ->
                                                RectangleShape(
                                                    defaultShapeColor,
                                                    DEFAULT_STROKE_WIDTH,
                                                )

                                            ShapeType.OrientedEllipse ->
                                                SkewedEllipseShape(
                                                    defaultShapeColor,
                                                    DEFAULT_STROKE_WIDTH,
                                                )

                                            ShapeType.Square ->
                                                SquareShape(
                                                    defaultShapeColor,
                                                    DEFAULT_STROKE_WIDTH,
                                                )

                                            ShapeType.Triangle ->
                                                TriangleShape(
                                                    defaultShapeColor,
                                                    DEFAULT_STROKE_WIDTH,
                                                )
                                        }
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                            )
                        }
                    }
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Compose Shape Fitter") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Open Drawer")
                        }
                    },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                )
            },
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            DrawingScreen(
                drawableShape = currentShapeToDraw,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                // Apply padding from Scaffold
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

// Helper function to get the ShapeType from a DrawableShape instance for selection highlighting
fun getShapeType(drawableShape: DrawableShape): ShapeType? =
    when (drawableShape) {
        is CircleShape -> ShapeType.Circle
        is EllipseShape -> ShapeType.Ellipse
        is HexagonShape -> ShapeType.Hexagon
        is ObbShape -> if (drawableShape.allSidesEqual) ShapeType.OrientedSquare else ShapeType.OrientedRectangle
        is PentagonShape -> ShapeType.Pentagon
        is RectangleShape -> ShapeType.Rectangle
        is SkewedEllipseShape -> ShapeType.OrientedEllipse
        is SquareShape -> ShapeType.Square
        is TriangleShape -> ShapeType.Triangle
    }
