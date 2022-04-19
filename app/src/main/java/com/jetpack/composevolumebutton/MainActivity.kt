package com.jetpack.composevolumebutton

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jetpack.composevolumebutton.ui.theme.ComposeVolumeButtonTheme
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeVolumeButtonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "Compose Volume Button",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            )
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ComposeVolumeButton()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ComposeVolumeButton() {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(2.dp, Color.Red, RoundedCornerShape(10.dp))
                .padding(25.dp)
        ) {
            var volume by remember { mutableStateOf(0f) }
            val barCount = 20

            MusicVolume(
                modifier = Modifier.size(100.dp)
            ) {
                volume = it
            }

            Spacer(modifier = Modifier.width(20.dp))

            VolumeBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                activeBars = (barCount * volume).roundToInt(),
                barCount = barCount
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MusicVolume(
    modifier: Modifier = Modifier,
    limitInAngle: Float = 25f,
    onValueChange: (Float) -> Unit
) {
    var rotation by remember { mutableStateOf(limitInAngle) }
    var touchX by remember { mutableStateOf(0f) }
    var touchY by remember { mutableStateOf(0f) }
    var centerX by remember { mutableStateOf(0f) }
    var centerY by remember { mutableStateOf(0f) }

    Image(
        painter = painterResource(id = R.drawable.music),
        contentDescription = "Volume Image",
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned {
                val windowBounds = it.boundsInWindow()
                centerX = windowBounds.size.width / 2f
                centerY = windowBounds.size.height / 2f
            }
            .pointerInteropFilter { event ->
                touchX = event.x
                touchY = event.y
                val angle = -atan2(centerX - touchX, centerY - touchY) * (180f / PI).toFloat()

                when (event.action) {
                    MotionEvent.ACTION_DOWN,
                    MotionEvent.ACTION_MOVE -> {
                        if (angle !in -limitInAngle..limitInAngle) {
                            val fixedAngle = if (angle in -180f..-limitInAngle) {
                                360f + angle
                            } else {
                                angle
                            }
                            rotation = fixedAngle

                            val percent = (fixedAngle - limitInAngle) / (360f - 2 * limitInAngle)
                            onValueChange(percent)
                            true
                        } else false
                    }
                    else -> false
                }
            }
            .rotate(rotation)
    )
}

@Composable
fun VolumeBar(
    modifier: Modifier = Modifier,
    activeBars: Int = 0,
    barCount: Int = 10
) {
    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        val barWidth = remember { constraints.maxWidth / (2f * barCount) }

        Canvas(
            modifier = modifier
        ) {
            for (i in 0 until barCount) {
                drawRoundRect(
                    color = if (i in 0..activeBars) Color.Red else Color.DarkGray,
                    topLeft = Offset(i * barWidth * 2f + barWidth / 2f, 0f),
                    size = Size(barWidth,  constraints.maxHeight.toFloat()),
                    cornerRadius = CornerRadius(0f)
                )
            }
        }
    }
}























