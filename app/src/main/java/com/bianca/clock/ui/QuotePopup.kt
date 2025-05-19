package com.bianca.clock.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bianca.clock.R

@Composable
fun FlowerQuoteBubble(
    quote: String,
    imageRes: Int,
    onDismiss: () -> Unit
) {
    val transition = rememberInfiniteTransition()
    val offsetAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFFCCE5FF), Color(0xFFEDE7F6), Color(0xFFFFF3E0)),
        start = Offset(offsetAnim, 0f),
        end = Offset(0f, offsetAnim)
    )

    AnimatedVisibility(
        visible = quote.isNotBlank(),
        enter = fadeIn(tween(300)) + scaleIn(initialScale = 0.9f),
        exit = fadeOut(tween(300)) + scaleOut(targetScale = 0.9f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .widthIn(min = 220.dp, max = 320.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.6f), shape = RoundedCornerShape(24.dp))
                        .graphicsLayer {
                            shadowElevation = 20f // 加高
                            clip = true
                            shape = RoundedCornerShape(24.dp)
                        }
                        .background(brush = gradient)
                        .padding(24.dp)
                ) {
                    Text(
                        text = quote,
                        fontSize = 18.sp,
                        color = Color(0xFF333333),
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.White.copy(alpha = 0.4f),
                                offset = Offset(1f, 1f),
                                blurRadius = 2f
                            )
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    }
}




