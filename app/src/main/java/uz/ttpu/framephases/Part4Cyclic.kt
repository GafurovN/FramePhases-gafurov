package uz.ttpu.framephases

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ───────────── Question 13 — the loop and the fix (tag:Cycle) ─────────────

@Composable
fun CyclicDemo() {
    var imageHeightPx by remember { mutableIntStateOf(0) }
    val heightInThisComposition = imageHeightPx
    val compositions = remember { intArrayOf(0) }
    SideEffect {
        Log.d("Cycle", "composition #${++compositions[0]}: " +
            "imageHeightPx=$heightInThisComposition")
    }

    Box {
        Box(
            Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color(0xFF6650A4))
                .onSizeChanged { size ->
                    Log.d("Cycle", "onSizeChanged: height=${size.height}")
                    imageHeightPx = size.height          // Don't do this
                }
        )
        Text(
            text = "I'm below the image",
            modifier = Modifier.padding(
                top = with(LocalDensity.current) { heightInThisComposition.toDp() }
            ),
        )
    }
}

@Composable
fun CyclicFixed() {
    val compositions = remember { intArrayOf(0) }
    SideEffect { Log.d("Cycle", "fixed: composition #${++compositions[0]}") }

    Column {
        Box(Modifier.fillMaxWidth().height(120.dp).background(Color(0xFF6650A4)))
        Text("I'm below the image")
    }
}

// ───────────── Question 14 — custom layout (tag:StackBelow) ─────────────

@Composable
fun StackBelow(
    modifier: Modifier = Modifier,
    spacing: Dp = 8.dp,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->

        // Step 1: measure the children, each exactly once,
        // with loose minimum constraints.
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { measurable ->
            // TODO 1
            Log.d("StackBelow", "measure child")
            measurable.measure(loose)
        }

        // Step 2: decide our own size: the widest child, and the heights
        // plus the gaps between them.
        val spacingPx = spacing.roundToPx()
        // TODO 2
        val width = placeables.maxOfOrNull { it.width } ?: 0
        val height = placeables.sumOf { it.height } +
            spacingPx * (placeables.size - 1).coerceAtLeast(0)

        // Step 3: place the children, one below the other.
        // (coerceIn = the same as constraints.constrainWidth / constrainHeight)
        layout(
            width.coerceIn(constraints.minWidth, constraints.maxWidth),
            height.coerceIn(constraints.minHeight, constraints.maxHeight),
        ) {
            // TODO 3
            Log.d("StackBelow", "place children")
            var y = 0
            placeables.forEach { placeable ->
                placeable.place(x = 0, y = y)
                y += placeable.height + spacingPx
            }
        }
    }
}

/** Replaces CyclicDemo: same look as CyclicFixed, 8.dp gap, no state at all. */
@Composable
fun StackBelowDemo() {
    StackBelow(Modifier.fillMaxWidth(), spacing = 8.dp) {
        Box(Modifier.fillMaxWidth().height(120.dp).background(Color(0xFF6650A4)))
        Text("I'm below the image")
    }
}

/** Q14 last step: a third child — expect three "measure child" lines per "place children". */
@Composable
fun StackBelowThreeChildren() {
    StackBelow(Modifier.fillMaxWidth(), spacing = 8.dp) {
        Box(Modifier.fillMaxWidth().height(120.dp).background(Color(0xFF6650A4)))
        Text("I'm below the image")
        Text("And I'm the third child")
    }
}
