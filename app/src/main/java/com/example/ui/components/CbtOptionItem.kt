package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AyanoBackgroundDark
import com.example.ui.theme.AyanoBorder
import com.example.ui.theme.AyanoPrimaryCyan
import com.example.ui.theme.AyanoSurfaceCard
import com.example.ui.theme.AyanoSurfaceCardElevated
import com.example.ui.theme.AyanoTextMuted
import com.example.ui.theme.AyanoTextPrimary
import com.example.ui.theme.AyanoTextSecondary

@Composable
fun CbtOptionItem(
    optionIndex: Int, // 1, 2, 3, or 4
    optionText: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    isReviewMode: Boolean = false,
    isCorrectOption: Boolean = false,
    isUserSelection: Boolean = false
) {
    // Review mode colors
    val (backgroundColor, borderColor, badgeColor, textColor) = when {
        isReviewMode && isCorrectOption -> {
            Quad(
                Color(0xFF064E3B).copy(alpha = 0.5f),
                Color(0xFF10B981),
                Color(0xFF10B981),
                AyanoTextPrimary
            )
        }
        isReviewMode && isUserSelection && !isCorrectOption -> {
            Quad(
                Color(0xFF7F1D1D).copy(alpha = 0.5f),
                Color(0xFFEF4444),
                Color(0xFFEF4444),
                AyanoTextPrimary
            )
        }
        isSelected -> {
            Quad(
                AyanoPrimaryCyan.copy(alpha = 0.15f),
                AyanoPrimaryCyan,
                AyanoPrimaryCyan,
                AyanoTextPrimary
            )
        }
        else -> {
            Quad(
                AyanoSurfaceCard,
                AyanoBorder,
                AyanoTextMuted,
                AyanoTextSecondary
            )
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 52.dp)
            .testTag("option_$optionIndex")
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onSelect),
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(if (isSelected || (isReviewMode && (isCorrectOption || isUserSelection))) 2.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Option Badge (1), (2), (3), (4)
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) AyanoPrimaryCyan
                        else if (isReviewMode && isCorrectOption) Color(0xFF10B981)
                        else if (isReviewMode && isUserSelection) Color(0xFFEF4444)
                        else AyanoSurfaceCardElevated
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "($optionIndex)",
                    color = if (isSelected || (isReviewMode && (isCorrectOption || isUserSelection))) AyanoBackgroundDark else AyanoTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = optionText.ifBlank { "Option ($optionIndex)" },
                color = textColor,
                fontSize = 15.sp,
                fontWeight = if (isSelected || (isReviewMode && isCorrectOption)) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )

            if (isReviewMode) {
                Spacer(modifier = Modifier.width(8.dp))
                if (isCorrectOption) {
                    Text(
                        text = "✓ Correct",
                        color = Color(0xFF10B981),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else if (isUserSelection) {
                    Text(
                        text = "✗ Your Answer",
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
