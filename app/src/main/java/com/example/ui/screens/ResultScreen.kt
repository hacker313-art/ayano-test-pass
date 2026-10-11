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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import com.example.model.AttemptModel
import com.example.model.TestModel
import com.example.model.User
import com.example.ui.components.AyanoTopBar
import com.example.ui.theme.AyanoBackgroundDark
import com.example.ui.theme.AyanoBorder
import com.example.ui.theme.AyanoPrimaryBlue
import com.example.ui.theme.AyanoPrimaryCyan
import com.example.ui.theme.AyanoSurfaceCard
import com.example.ui.theme.AyanoSurfaceCardElevated
import com.example.ui.theme.AyanoTextMuted
import com.example.ui.theme.AyanoTextPrimary
import com.example.ui.theme.AyanoTextSecondary
import com.example.ui.theme.BotanyGreen
import com.example.ui.theme.CbtAnsweredGreen
import com.example.ui.theme.CbtNotAnsweredRed
import com.example.ui.theme.CbtNotVisitedGrey
import com.example.ui.theme.ChemistryAmber
import com.example.ui.theme.PhysicsCyan
import com.example.ui.theme.ZoologyRose
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResultScreen(
    user: User,
    test: TestModel,
    attempt: AttemptModel,
    onViewAnalysis: () -> Unit,
    onViewLeaderboard: () -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val maxScore = test.totalQuestions * 4
    val percentage = if (maxScore > 0) ((attempt.score.toFloat() / maxScore) * 100).coerceAtLeast(0f) else 0f

    val hours = attempt.timeTaken / 3600
    val minutes = (attempt.timeTaken % 3600) / 60
    val seconds = attempt.timeTaken % 60
    val formattedTime = if (hours > 0) {
        String.format(Locale.US, "%d hr %d min %d sec", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%d min %d sec", minutes, seconds)
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = AyanoBackgroundDark
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AyanoTopBar(
                title = "Exam Result & Performance",
                subtitle = test.name,
                showBack = true,
                onBackClick = onBackToHome
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main Score Card
                Surface(
                    color = AyanoSurfaceCardElevated,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AyanoPrimaryCyan.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_total_score")
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = AyanoPrimaryCyan.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "NTA NEET SCORECARD",
                                color = AyanoPrimaryCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${attempt.score}",
                                color = if (attempt.score >= 0) AyanoPrimaryCyan else CbtNotAnsweredRed,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = " / $maxScore",
                                color = AyanoTextSecondary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Accuracy: ${String.format(Locale.US, "%.1f", percentage)}%",
                            color = AyanoTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = AyanoTextSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Time Taken: $formattedTime",
                                color = AyanoTextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Question Attempt Stats (Correct, Wrong, Unattempted)
                Surface(
                    color = AyanoSurfaceCard,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AyanoBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        // Correct (+4)
                        StatColumn(
                            count = attempt.correctCount,
                            label = "Correct",
                            subtext = "+${attempt.correctCount * 4} Marks",
                            color = CbtAnsweredGreen
                        )

                        // Wrong (-1)
                        StatColumn(
                            count = attempt.wrongCount,
                            label = "Incorrect",
                            subtext = "-${attempt.wrongCount} Marks",
                            color = CbtNotAnsweredRed
                        )

                        // Unattempted (0)
                        StatColumn(
                            count = attempt.unattemptedCount,
                            label = "Unattempted",
                            subtext = "0 Marks",
                            color = CbtNotVisitedGrey
                        )
                    }
                }

                // Subject-wise Breakdown
                Text(
                    text = "SUBJECT-WISE SCORE BREAKDOWN",
                    color = AyanoTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                val subjectColors = mapOf(
                    "Physics" to PhysicsCyan,
                    "Chemistry" to ChemistryAmber,
                    "Botany" to BotanyGreen,
                    "Zoology" to ZoologyRose
                )

                listOf("Physics", "Chemistry", "Botany", "Zoology").forEach { sub ->
                    val score = attempt.subjectScores[sub] ?: 0
                    val col = subjectColors[sub] ?: AyanoPrimaryCyan

                    Surface(
                        color = AyanoSurfaceCard,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AyanoBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(col)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = sub,
                                        color = AyanoTextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = "$score Marks",
                                    color = if (score >= 0) col else CbtNotAnsweredRed,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons
                Button(
                    onClick = onViewAnalysis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_view_analysis"),
                    colors = ButtonDefaults.buttonColors(containerColor = AyanoPrimaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Assessment, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Detailed Analysis & Solutions",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onViewLeaderboard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_view_leaderboard"),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AyanoPrimaryCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Leaderboard, contentDescription = null, tint = AyanoPrimaryCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "View All India Leaderboard",
                        color = AyanoPrimaryCyan,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = onBackToHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("btn_back_home"),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AyanoBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Home, contentDescription = null, tint = AyanoTextSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Return to Dashboard",
                        color = AyanoTextSecondary,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun StatColumn(
    count: Int,
    label: String,
    subtext: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$count",
            color = color,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = label,
            color = AyanoTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = subtext,
            color = AyanoTextSecondary,
            fontSize = 11.sp
        )
    }
}
