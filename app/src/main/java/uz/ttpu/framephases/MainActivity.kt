package uz.ttpu.framephases

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Box(Modifier.fillMaxSize().safeDrawingPadding()) {

                    // Show ONE demo at a time: leave exactly one line uncommented.

                    LectureTree()             // Q4  (Logcat: tag:Phases)
                    // PaddingDemo()          // Q5  (tag:Phases)
                    // OffsetDemo()           // Q6  (tag:Phases)
                    // OffsetDemoExperiment() // Q6  experiment
                    // ColorDemo()            // Q7  (tag:Phases)
                    // ColorDemoBackground()  // Q7  background(color) version
                    // ParallaxSlow()         // Q9  (tag:Parallax)
                    // ParallaxFast()         // Q9
                    // ScrollToTopNaive()     // Q10 (tag:Derived)
                    // ScrollToTopDerived()   // Q10 derivedStateOf version
                    // Parent()               // Q11 step 1 (tag:Skip)
                    // ParentLambda()         // Q11 step 2
                    // CyclicDemo()           // Q13 (tag:Cycle)
                    // CyclicFixed()          // Q13
                    // StackBelowDemo()       // Q14 (tag:StackBelow)
                    // StackBelowThreeChildren() // Q14 third child
                }
            }
        }
    }
}