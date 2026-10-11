package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import com.example.model.AttemptModel
import com.example.model.QuestionModel
import com.example.model.TestModel
import com.example.ui.components.AyanoTopBar
import com.example.ui.components.CbtOptionItem
import com.example.ui.theme.AyanoBackgroundDark
import com.example.ui.theme.AyanoBorder
import com.example.ui.theme.AyanoPrimaryCyan
import com.example.ui.theme.AyanoSurfaceCard
import com.example.ui.theme.AyanoSurfaceCardElevated
import com.example.ui.theme.AyanoTextMuted
import com.example.ui.theme.AyanoTextPrimary
import com.example.ui.theme.AyanoTextSecondary
import com.example.ui.theme.BotanyGreen
import com.example.ui.theme.CbtAnsweredGreen
import com.example.ui.theme.CbtMarkedPurple
import com.example.ui.theme.CbtNotAnsweredRed
import com.example.ui.theme.CbtNotVisitedGrey
import com.example.ui.theme.ChemistryAmber
import com.example.ui.theme.PhysicsCyan
import com.example.ui.theme.ZoologyRose

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnalysisScreen(
    test: TestModel,
    questions: List<QuestionModel>,
    attempt: AttemptModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredQuestions = remember(selectedFilter, questions, attempt) {
        when (selectedFilter) {
            "Correct" -> questions.filter {
                val ans = attempt.answers[it.id]
                ans != null && ans == it.correctOption
            }
            "Wrong" -> questions.filter {
                val ans = attempt.answers[it.id]
                ans != null && ans != it.correctOption
            }
            "Unattempted" -> questions.filter {
                !attempt.answers.containsKey(it.id)
            }
            "Marked" -> questions.filter {
                attempt.markedForReview.contains(it.id)
            }
            else -> questions
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = AyanoBackgroundDark
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AyanoTopBar(
                title = "Question Paper Analysis",
                subtitle = "${test.name} • Score: ${attempt.score}/${test.totalQuestions * 4}",
                showBack = true,
                onBackClick = onBack
            )

            // Filter Tabs Row
            Surface(
                color = AyanoSurfaceDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val filterOptions = listOf(
                        "All" to questions.size,
                        "Correct" to attempt.correctCount,
                        "Wrong" to attempt.wrongCount,
                        "Unattempted" to attempt.unattemptedCount,
                        "Marked" to attempt.markedForReview.size
                    )

                    filterOptions.forEach { (filterName, count) ->
                        val isSelected = selectedFilter == filterName
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = filterName },
                            label = {
                                Text(
                                    text = "$filterName ($count)",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (filterName) {
                                    "Correct" -> CbtAnsweredGreen.copy(alpha = 0.2f)
                                    "Wrong" -> CbtNotAnsweredRed.copy(alpha = 0.2f)
                                    "Marked" -> CbtMarkedPurple.copy(alpha = 0.2f)
                                    else -> AyanoPrimaryCyan.copy(alpha = 0.2f)
                                },
                                selectedLabelColor = when (filterName) {
                                    "Correct" -> CbtAnsweredGreen
                                    "Wrong" -> CbtNotAnsweredRed
                                    "Marked" -> CbtMarkedPurple
                                    else -> AyanoPrimaryCyan
                                },
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
            }

            // Question List
            if (filteredQuestions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No questions found in '$selectedFilter' category.",
                        color = AyanoTextSecondary,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredQuestions) { q ->
                        val userChoice = attempt.answers[q.id]
                        val isCorrect = userChoice != null && userChoice == q.correctOption
                        val isWrong = userChoice != null && userChoice != q.correctOption
                        val isUnattempted = userChoice == null
                        val isMarked = attempt.markedForReview.contains(q.id)

                        QuestionAnalysisCard(
                            question = q,
                            userChoice = userChoice,
                            isCorrect = isCorrect,
                            isWrong = isWrong,
                            isUnattempted = isUnattempted,
                            isMarked = isMarked
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionAnalysisCard(
    question: QuestionModel,
    userChoice: Int?,
    isCorrect: Boolean,
    isWrong: Boolean,
    isUnattempted: Boolean,
    isMarked: Boolean
) {
    Surface(
        color = AyanoSurfaceCard,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            when {
                isCorrect -> CbtAnsweredGreen.copy(alpha = 0.4f)
                isWrong -> CbtNotAnsweredRed.copy(alpha = 0.4f)
                else -> AyanoBorder
            }
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Q Number, Subject, Result Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = AyanoSurfaceCardElevated,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Q. ${question.number}",
                            color = AyanoTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    val subColor = when (question.subject.lowercase()) {
                        "physics" -> PhysicsCyan
                        "chemistry" -> ChemistryAmber
                        "botany" -> BotanyGreen
                        "zoology" -> ZoologyRose
                        else -> AyanoPrimaryCyan
                    }
                    Text(
                        text = question.subject,
                        color = subColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (isMarked) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Marked",
                            tint = CbtMarkedPurple,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Outcome Badge
                Surface(
                    color = when {
                        isCorrect -> CbtAnsweredGreen.copy(alpha = 0.15f)
                        isWrong -> CbtNotAnsweredRed.copy(alpha = 0.15f)
                        else -> CbtNotVisitedGrey.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = when {
                            isCorrect -> "+4 Correct"
                            isWrong -> "-1 Incorrect"
                            else -> "0 Unattempted"
                        },
                        color = when {
                            isCorrect -> CbtAnsweredGreen
                            isWrong -> CbtNotAnsweredRed
                            else -> AyanoTextMuted
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Question Text
            Text(
                text = question.text,
                color = AyanoTextPrimary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Options (1..4)
            question.options.forEachIndexed { optIdx, optText ->
                val optNumber = optIdx + 1
                val isThisCorrect = optNumber == question.correctOption
                val isThisUserChoice = userChoice != null && optNumber == userChoice

                CbtOptionItem(
                    optionIndex = optNumber,
                    optionText = optText,
                    isSelected = isThisUserChoice,
                    onSelect = {},
                    isReviewMode = true,
                    isCorrectOption = isThisCorrect,
                    isUserSelection = isThisUserChoice
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Solution Section
            if (question.solution.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = AyanoSurfaceCardElevated,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AyanoPrimaryCyan.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = AyanoPrimaryCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "HINT & DETAILED SOLUTION",
                                color = AyanoPrimaryCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = question.solution,
                            color = AyanoTextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

private val AyanoSurfaceDark = Color(0xFF0F172A)
