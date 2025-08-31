# 🖌️ Compose Shape Fitter

**Compose Shape Fitter** is a lightweight Kotlin library for Jetpack Compose that approximates geometric shapes from a sequence of points and can render those points on a `DrawScope`. It’s ideal for gesture input, sketch recognition, and turning user strokes into meaningful primitives.

---

<div style="text-align: center;">
  <img src="demo.gif?raw=true" alt="Compose Shape Fitter Demo" width="300"/>
</div>

## ✨ Features

- Approximate a best-fit shape from a list of `Offset` points
- Draw the original stroke (points) into a Compose `DrawScope`
- Simple, composable-first API

### Currently Supported Shapes

- **Circle**
- **Ellipse**
- **Hexagon**
- **Oriented Bounding Box (OBB)**
- **Pentagon**
- **Triangle**
- **Square**

---

## Installation

Compose Shape Fitter is available on Maven Central.

**Using Version Catalogs:**

`libs.versions.toml`:
```toml
[versions]
composeShapeFitterVersion = "1.0.0"

[libraries]
composeShapeFitterLibrary = { group = "io.github.sarimmehdi", name = "compose-shape-fitter", version.ref = "composeShapeFitterVersion" }
```
`build.gradle.kts` (module-level):
```kts
implementation(libs.composeShapeFitterLibrary)
```

**Gradle (Kotlin DSL - `build.gradle.kts`):**:
```kts
implementation("io.github.sarimmehdi:compose-shape-fitter:1.0.0")
```
**`build.gradle` (Groovy)**:
```groovy
implementation 'io.github.sarimmehdi:compose-shape-fitter:1.0.0'
```

---

## 🚀 Quick Start

### Draw a user stroke (sequence of points)

```kotlin
val points: List<Offset> = /* collected from gesture/touch input */

draw(
    drawScope = drawScope,
    points = points
)
```

### Get an approximated shape

```kotlin
val approximatedShape = getApproximatedShape(points)

when (approximatedShape) {
    is ApproximatedShape.CircleShape -> { /* handle circle */ }
    is ApproximatedShape.EllipseShape -> { /* handle ellipse */ }
    is ApproximatedShape.HexagonShape -> { /* handle hexagon */ }
    is ApproximatedShape.PentagonShape -> { /* handle pentagon */ }
    is ApproximatedShape.SquareShape -> { /* handle square */ }
    is ApproximatedShape.TriangleShape -> { /* handle triangle */ }
    is ApproximatedShape.ObbShape -> { /* handle OBB */ }
    is ApproximatedShape.SkewedEllipseShape -> { /* handle oriented ellipse */ }
    null -> { /* no recognizable shape */ }
}
```

---

## 🧩 API Overview

```kotlin
fun draw(
    drawScope: DrawScope,
    points: List<Offset>,
)

fun getApproximatedShape(
    points: List<Offset>
): ApproximatedShape?
```

`ApproximatedShape` is a sealed type representing the supported shapes listed above.

---

## 🧠 Typical Use Cases

- Freehand shape recognition in drawing or note apps
- Gesture-based UI controls (e.g., draw a triangle to trigger an action)
- Educational tools for geometry and sketch classification

---

## 🛣️ Roadmap

- Support for **triangle variants** (isosceles, equilateral, scalene)
- **Line fitting**: linear, quadratic, and spline matching for polylines
- Overlap percentage between different shapes

---

## 🤝 Contributing

Contributions are welcome!  
Please open an issue to discuss your idea or submit a PR with tests and a brief description.
