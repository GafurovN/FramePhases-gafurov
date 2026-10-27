package uz.ttpu.framephases

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// ───────────── Question 9 — parallax (tag:Parallax) ─────────────

@Composable
fun ParallaxSlow() {
    val listState = rememberLazyListState()
    val compositions = remember { intArrayOf(0) }
    SideEffect { Log.d("Parallax", "slow: composition #${++compositions[0]}") }

    Box {
        Box(
            Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color(0xFF6650A4))
                .offset(
                    // scroll offset read in COMPOSITION -> every scroll event recomposes
                    y = with(LocalDensity.current) {
                        (listState.firstVisibleItemScrollOffset / 2).toDp()
                    }
                )
        )
        LazyColumn(state = listState) {
            items(100) { index ->
                Text("Item $index", Modifier.fillMaxWidth().padding(16.dp))
            }
        }
    }
}

@Composable
fun ParallaxFast() {
    val listState = rememberLazyListState()
    val compositions = remember { intArrayOf(0) }
    SideEffect { Log.d("Parallax", "fast: composition #${++compositions[0]}") }

    Box {
        Box(
            Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color(0xFF6650A4))
                .offset {
                    // scroll offset read in the PLACEMENT step -> no recomposition
                    IntOffset(x = 0, y = listState.firstVisibleItemScrollOffset / 2)
                }
        )
        LazyColumn(state = listState) {
            items(100) { index ->
                Text("Item $index", Modifier.fillMaxWidth().padding(16.dp))
            }
        }
    }
}

// ───────────── Question 10 — derivedStateOf (tag:Derived) ─────────────

@Composable
fun ScrollToTopNaive() {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val compositions = remember { intArrayOf(0) }
    SideEffect { Log.d("Derived", "naive: composition #${++compositions[0]}") }

    // firstVisibleItemIndex read directly in composition: recomposes on every index change
    val showButton = listState.firstVisibleItemIndex > 0

    Box(Modifier.fillMaxSize()) {
        LazyColumn(state = listState) {
            items(200) { index ->
                Text("Item $index", Modifier.fillMaxWidth().padding(16.dp))
            }
        }
        if (showButton) {
            Button(
                onClick = { scope.launch { listState.animateScrollToItem(0) } },  // TODO 3 done
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            ) {
                Text("Top")
            }
        }
    }
}

@Composable
fun ScrollToTopDerived() {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val compositions = remember { intArrayOf(0) }
    SideEffect { Log.d("Derived", "derived: composition #${++compositions[0]}") }

    // Only notifies composition when the Boolean actually flips (false -> true -> false)
    val showButton by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(state = listState) {
            items(200) { index ->
                Text("Item $index", Modifier.fillMaxWidth().padding(16.dp))
            }
        }
        if (showButton) {
            Button(
                onClick = { scope.launch { listState.animateScrollToItem(0) } },  // TODO 3 done
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            ) {
                Text("Top")
            }
        }
    }
}

// ───────────── Question 11 — skipping and deferred reads (tag:Skip) ─────────────

/** Step 1: Child receives the VALUE, so `count` is read in Parent's body. */
@Composable
fun Parent() {
    var count by remember { mutableIntStateOf(0) }
    SideEffect { Log.d("Skip", "Parent: composition") }

    Column {
        Button(onClick = { count++ }) { Text("Add") }
        Child(count)
        Sibling()
    }
}

@Composable
fun Child(count: Int) {
    SideEffect { Log.d("Skip", "Child: composition") }
    Text("Count: $count")
}

@Composable
fun Sibling() {
    SideEffect { Log.d("Skip", "Sibling: composition") }
    Text("I never change")
}

/** Step 2: Child receives a LAMBDA, so `count` is read only inside Child. */
@Composable
fun ParentLambda() {
    var count by remember { mutableIntStateOf(0) }
    SideEffect { Log.d("Skip", "Parent: composition") }

    Column {
        Button(onClick = { count++ }) { Text("Add") }
        ChildLambda(count = { count })        // pass a lambda that returns the value
        Sibling()
    }
}

@Composable
fun ChildLambda(count: () -> Int) {
    SideEffect { Log.d("Skip", "Child: composition") }
    Text("Count: ${count()}")                 // the state is read here, not in Parent
}
