package uz.ttpu.framephases

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

// ───────────── Question 4 — the first frame (tag:Phases) ─────────────

@Composable
fun LectureTree() {
    SideEffect { Log.d("Phases", "LectureTree: composition") }

    Row(Modifier.logPhases("Row")) {
        Box(Modifier.size(40.dp).background(Color.Gray).logPhases("Image"))
        Column(Modifier.logPhases("Column")) {
            Text("Title text", Modifier.logPhases("Text A"))
            Text("Subtitle", Modifier.logPhases("Text B"))
        }
    }
}

// ───────────── Question 5 — read in COMPOSITION (tag:Phases) ─────────────

@Composable
fun PaddingDemo() {
    var padding by remember { mutableStateOf(8.dp) }
    SideEffect { Log.d("Phases", "PaddingDemo: composition") }

    Column {
        Button(onClick = { padding = if (padding == 8.dp) 32.dp else 8.dp }) {
            Text("Toggle padding")
        }
        Text(
            text = "Padding demo",
            modifier = Modifier
                .padding(padding)               // read while the modifier is built -> composition
                .logPhases("padding demo"),
        )
    }
}

// ───────────── Question 6 — read in LAYOUT (placement) (tag:Phases) ─────────────

@Composable
fun OffsetDemo() {
    var offsetX by remember { mutableStateOf(0.dp) }
    SideEffect { Log.d("Phases", "OffsetDemo: composition") }

    Column {
        Button(onClick = { offsetX = if (offsetX == 0.dp) 80.dp else 0.dp }) {
            Text("Move")
        }
        Text(
            text = "Offset demo",
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToPx(), 0) }   // read inside the lambda -> placement
                .logPhases("offset demo"),
        )
    }
}

/** Question 6 experiment: the read is moved ABOVE the Text, i.e. into composition. */
@Composable
fun OffsetDemoExperiment() {
    var offsetX by remember { mutableStateOf(0.dp) }
    SideEffect { Log.d("Phases", "OffsetDemoExperiment: composition") }

    Column {
        Button(onClick = { offsetX = if (offsetX == 0.dp) 80.dp else 0.dp }) {
            Text("Move")
        }
        // new line, above the Text: offsetX is now read in composition
        val offsetPx = with(LocalDensity.current) { offsetX.roundToPx() }

        Text(
            text = "Offset demo",
            modifier = Modifier
                .offset { IntOffset(offsetPx, 0) }               // the lambda now uses the Int
                .logPhases("offset demo"),
        )
    }
}

// ───────────── Question 7 — read in DRAWING (tag:Phases) ─────────────

@Composable
fun ColorDemo() {
    var color by remember { mutableStateOf(Color.Red) }
    SideEffect { Log.d("Phases", "ColorDemo: composition") }

    Column {
        Button(onClick = { color = if (color == Color.Red) Color.Blue else Color.Red }) {
            Text("Change color")
        }
        Canvas(Modifier.size(120.dp).logPhases("color demo")) {
            drawRect(color)                    // read inside onDraw -> drawing phase only
        }
    }
}

/** Question 7, second half: Canvas replaced by background(color). */
@Composable
fun ColorDemoBackground() {
    var color by remember { mutableStateOf(Color.Red) }
    SideEffect { Log.d("Phases", "ColorDemoBackground: composition") }

    Column {
        Button(onClick = { color = if (color == Color.Red) Color.Blue else Color.Red }) {
            Text("Change color")
        }
        // background(Color) receives the value when the modifier is built -> composition read
        Box(Modifier.size(120.dp).background(color).logPhases("color demo"))
    }
}
