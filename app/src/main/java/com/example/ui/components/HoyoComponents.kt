package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DownloadProgressState
import com.example.ui.theme.HoyoCardBorder
import com.example.ui.theme.HoyoCardDark
import com.example.ui.theme.HoyoCyan
import com.example.ui.theme.HoyoCyanBright
import com.example.ui.theme.HoyoDeepDark
import com.example.ui.theme.HoyoGold
import com.example.ui.theme.HoyoGoldBright
import com.example.ui.theme.HoyoGreen
import com.example.ui.theme.HoyoRed
import com.example.ui.theme.HoyoSurfaceDark
import com.example.ui.theme.HoyoTextMuted
import com.example.ui.theme.HoyoTextPrimary
import com.example.ui.theme.HoyoTextSecondary

@Composable
fun HoyoGlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = HoyoCardBorder,
    backgroundColor: Color = HoyoCardDark.copy(alpha = 0.85f),
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .border(
                BorderStroke(1.dp, borderColor),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp)),
        color = backgroundColor
    ) {
        content()
    }
}

@Composable
fun HoyoStatusBadge(
    text: String,
    statusColor: Color,
    modifier: Modifier = Modifier,
    showGlow: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                color = statusColor.copy(alpha = if (showGlow) 0.2f * alphaAnim else 0.15f),
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                BorderStroke(1.dp, statusColor.copy(alpha = if (showGlow) alphaAnim else 0.6f)),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(statusColor, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = statusColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun HoyoPrimaryButton(
    text: String,
    subtext: String? = null,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = true,
    testTag: String = "primary_action_btn"
) {
    val brush = if (isHighlighted) {
        Brush.horizontalGradient(
            colors = listOf(
                HoyoGold,
                HoyoGoldBright
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                HoyoCyan,
                HoyoCyanBright
            )
        )
    }

    val contentColor = Color.Black

    Surface(
        modifier = modifier
            .height(56.dp)
            .clip(CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(brush)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = text,
                        color = contentColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    if (!subtext.isNullOrEmpty()) {
                        Text(
                            text = subtext,
                            color = contentColor.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HoyoSecondaryButton(
    text: String,
    subtext: String? = null,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "secondary_action_btn"
) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .border(
                BorderStroke(1.5.dp, HoyoCyan),
                shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp)
            )
            .clip(CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = HoyoCardDark.copy(alpha = 0.7f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = HoyoCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    color = HoyoCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                if (!subtext.isNullOrEmpty()) {
                    Text(
                        text = subtext,
                        color = HoyoTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun HoyoDownloadProgressCard(
    downloadProgress: DownloadProgressState,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = downloadProgress.isProcessing,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(1.5.dp, if (downloadProgress.isUpdate) HoyoGold else HoyoCyan),
                    shape = RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp)),
            color = HoyoSurfaceDark.copy(alpha = 0.95f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = if (downloadProgress.isUpdate) HoyoGold else HoyoCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (downloadProgress.isUpdate) "ATUALIZANDO PACOTE v2.0" else "BAIXANDO NOVO APLICATIVO",
                            color = if (downloadProgress.isUpdate) HoyoGold else HoyoCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "${(downloadProgress.progress * 100).toInt()}%",
                        color = HoyoTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { downloadProgress.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (downloadProgress.isUpdate) HoyoGold else HoyoCyan,
                    trackColor = HoyoCardBorder,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = downloadProgress.statusText,
                        color = HoyoTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = downloadProgress.speedText,
                        color = HoyoTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
