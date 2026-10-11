package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.QuestionModel
import com.example.model.QuestionState
import com.example.ui.theme.AyanoBackgroundDark
import com.example.ui.theme.AyanoBorder
import com.example.ui.theme.AyanoPrimaryCyan
import com.example.ui.theme.AyanoSurfaceCard
import com.example.ui.theme.AyanoSurfaceCardElevated
import com.example.ui.theme.AyanoTextMuted
import com.example.ui.theme.AyanoTextPrimary
import com.example.ui.theme.AyanoTextSecondary
import com.example.ui.theme.CbtAnsweredGreen
import com.example.ui.theme.CbtAnsweredMarkedIndigo
import com.example.ui.theme.CbtMarkedPurple
import com.example.ui.theme.CbtNotAnsweredRed
import com.example.ui.theme.CbtNotVisitedGrey

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuestionPaletteContent(
    questions: List<QuestionModel>,
    currentQuestionIndex: Int,
    answers: Map<String, Int>,
    markedForReview: List<String>,
    visitedQuestions: List<String>,
    onSelectQuestion: (Int) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubjectFilter by remember { mutableStateOf("All") }

    val filteredQuestions = if (selectedSubjectFilter == "All") {
        questions
    } else {
        questions.filter { it.subject.equals(selectedSubjectFilter, ignoreCase = true) }
    }

    // Calculate status counts
    var answeredCount = 0
    var notAnsweredCount = 0
    var markedCount = 0
    var answeredAndMarkedCount = 0
    var notVisitedCount = 0

    questions.forEachIndexed { index, q ->
        val hasAnswer = answers.containsKey(q.id)
        val isMarked = markedForReview.contains(q.id)
        val isVisited = visitedQuestions.contains(q.id) || index == currentQuestionIndex

        when {
            hasAnswer && isMarked -> answeredAndMarkedCount++
            hasAnswer -> answeredCount++
            isMarked -> markedCount++
            isVisited -> notAnsweredCount++
            else -> notVisitedCount++
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("question_palette_sheet"),
        color = AyanoSurfaceDark,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "QUESTION PALETTE",
                        color = AyanoPrimaryCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Total Questions: ${questions.size}",
                        color = AyanoTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("btn_close_palette")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = AyanoTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Palette Legend Cards
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PaletteLegendChip(count = answeredCount, label = "Answered", color = CbtAnsweredGreen)
                PaletteLegendChip(count = notAnsweredCount, label = "Not Answered", color = CbtNotAnsweredRed)
                PaletteLegendChip(count = markedCount, label = "Marked", color = CbtMarkedPurple)
                PaletteLegendChip(count = answeredAndMarkedCount, label = "Ans & Marked", color = CbtAnsweredMarkedIndigo, showDot = true)
                PaletteLegendChip(count = notVisitedCount, label = "Not Visited", color = CbtNotVisitedGrey)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subject Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("All", "Physics", "Chemistry", "Botany", "Zoology").forEach { sub ->
                    val isSelected = selectedSubjectFilter == sub
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSubjectFilter = sub },
                        label = { Text(sub, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AyanoPrimaryCyan.copy(alpha = 0.2f),
                            selectedLabelColor = AyanoPrimaryCyan,
                            containerColor = AyanoSurfaceCardElevated,
                            labelColor = AyanoTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) AyanoPrimaryCyan else AyanoBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Question Grid (1..180)
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 48.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .height(280.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredQuestions) { q ->
                    val realIndex = questions.indexOf(q)
                    val isCurrent = realIndex == currentQuestionIndex
                    val hasAnswer = answers.containsKey(q.id)
                    val isMarked = markedForReview.contains(q.id)
                    val isVisited = visitedQuestions.contains(q.id) || isCurrent

                    val (bgColor, hasDot) = when {
                        hasAnswer && isMarked -> Pair(CbtAnsweredMarkedIndigo, true)
                        hasAnswer -> Pair(CbtAnsweredGreen, false)
                        isMarked -> Pair(CbtMarkedPurple, false)
                        isVisited -> Pair(CbtNotAnsweredRed, false)
                        else -> Pair(CbtNotVisitedGrey, false)
                    }

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .testTag("palette_q_${q.number}")
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgColor)
                            .border(
                                width = if (isCurrent) 2.5.dp else 1.dp,
                                color = if (isCurrent) AyanoPrimaryCyan else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                onSelectQuestion(realIndex)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${q.number}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (hasDot) {
                            // Green indicator dot for Answered & Marked
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(3.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(CbtAnsweredGreen)
                                    .border(1.dp, Color.White, CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaletteLegendChip(
    count: Int,
    label: String,
    color: Color,
    showDot: Boolean = false
) {
    Surface(
        color = AyanoSurfaceCardElevated,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AyanoBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$count",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                if (showDot) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(CbtAnsweredGreen)
                    )
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = AyanoTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private val AyanoSurfaceDark = Color(0xFF0F172A)
