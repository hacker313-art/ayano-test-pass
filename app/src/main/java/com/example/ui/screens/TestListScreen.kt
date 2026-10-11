package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.repository.AyanoRepository
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
import com.example.ui.theme.ChemistryAmber
import com.example.ui.theme.PhysicsCyan
import com.example.ui.theme.ZoologyRose

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TestListScreen(
    user: User,
    tests: List<TestModel>,
    onStartTest: (TestModel) -> Unit,
    onViewResult: (TestModel, AttemptModel) -> Unit,
    onOpenLeaderboard: (TestModel) -> Unit,
    onOpenAdmin: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { AyanoRepository.getInstance(context) }
    val attemptsMap = remember { mutableStateMapOf<String, AttemptModel?>() }

    LaunchedEffect(tests, user.userId) {
        tests.forEach { test ->
            val attempt = repository.checkHasAttempted(user.userId, test.testId)
            attemptsMap[test.testId] = attempt
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = AyanoBackgroundDark
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AyanoTopBar(
                title = "Exam Portal",
                subtitle = "Hello, ${user.name} (${user.userClass})",
                role = if (user.isAdmin) "admin" else null,
                actions = {
                    if (user.isAdmin) {
                        Surface(
                            onClick = onOpenAdmin,
                            color = AyanoPrimaryCyan.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("btn_top_admin")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin",
                                    tint = AyanoPrimaryCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Admin Panel",
                                    color = AyanoPrimaryCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hero Banner
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = AyanoSurfaceCardElevated
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = painterResource(id = R.drawable.ayano_hero_banner_1790937233742),
                                contentDescription = "AYANO Banner",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                AyanoBackgroundDark.copy(alpha = 0.9f),
                                                AyanoBackgroundDark.copy(alpha = 0.4f)
                                            )
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "AYANO TEST PASS",
                                    color = AyanoPrimaryCyan,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "All India NEET Computer Based Tests (CBT)",
                                    color = AyanoTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Real NTA Exam Interface • 180 Minutes • Instant Analysis",
                                    color = AyanoTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Section Title
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AVAILABLE NEET MOCK TESTS",
                            color = AyanoTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "${tests.filter { it.isPublished }.size} Available",
                            color = AyanoPrimaryCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Tests List
                val publishedTests = tests.filter { it.isPublished }
                if (publishedTests.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = AyanoSurfaceCard,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No tests published yet.",
                                    color = AyanoTextSecondary,
                                    fontSize = 14.sp
                                )
                                if (user.role == "admin") {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = onOpenAdmin,
                                        colors = ButtonDefaults.buttonColors(containerColor = AyanoPrimaryCyan)
                                    ) {
                                        Text("Create Test as Admin", color = AyanoBackgroundDark)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    items(publishedTests) { test ->
                        val attempt = attemptsMap[test.testId]
                        val isAttempted = attempt != null

                        TestCard(
                            test = test,
                            attempt = attempt,
                            isAttempted = isAttempted,
                            onStart = { onStartTest(test) },
                            onViewResult = { attempt?.let { onViewResult(test, it) } },
                            onLeaderboard = { onOpenLeaderboard(test) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TestCard(
    test: TestModel,
    attempt: AttemptModel?,
    isAttempted: Boolean,
    onStart: () -> Unit,
    onViewResult: () -> Unit,
    onLeaderboard: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("test_card_${test.testId}"),
        color = AyanoSurfaceCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isAttempted) CbtAnsweredGreen.copy(alpha = 0.5f) else AyanoBorder
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (isAttempted) CbtAnsweredGreen.copy(alpha = 0.15f) else AyanoPrimaryBlue.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isAttempted) Icons.Default.CheckCircle else Icons.Default.AssignmentTurnedIn,
                            contentDescription = null,
                            tint = if (isAttempted) CbtAnsweredGreen else AyanoPrimaryCyan,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAttempted) "TEST COMPLETED" else "NTA NEET FULL MOCK",
                            color = if (isAttempted) CbtAnsweredGreen else AyanoPrimaryCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = test.date.ifBlank { "Live Test" },
                    color = AyanoTextMuted,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = test.name,
                color = AyanoTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Specs row: Questions, Duration, Marking
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = AyanoTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${test.totalQuestions} Questions",
                        color = AyanoTextSecondary,
                        fontSize = 12.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = AyanoTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${test.duration} Minutes",
                        color = AyanoTextSecondary,
                        fontSize = 12.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "+4 / -1 Mark",
                        color = AyanoPrimaryCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subjects list chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Pair("Physics", PhysicsCyan),
                    Pair("Chemistry", ChemistryAmber),
                    Pair("Botany", BotanyGreen),
                    Pair("Zoology", ZoologyRose)
                ).forEach { (sub, col) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(col.copy(alpha = 0.15f))
                            .border(1.dp, col.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = sub,
                            color = col,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Score highlight if attempted
            if (isAttempted && attempt != null) {
                Surface(
                    color = AyanoSurfaceCardElevated,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Your Score",
                                color = AyanoTextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${attempt.score} / ${test.totalQuestions * 4}",
                                color = CbtAnsweredGreen,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "✓ ${attempt.correctCount}",
                                color = CbtAnsweredGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "✗ ${attempt.wrongCount}",
                                color = Color(0xFFEF4444),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "○ ${attempt.unattemptedCount}",
                                color = AyanoTextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!isAttempted) {
                    Button(
                        onClick = onStart,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("btn_start_test_${test.testId}"),
                        colors = ButtonDefaults.buttonColors(containerColor = AyanoPrimaryBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "START CBT TEST",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Button(
                        onClick = onViewResult,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("btn_view_result_${test.testId}"),
                        colors = ButtonDefaults.buttonColors(containerColor = AyanoSurfaceCardElevated),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CbtAnsweredGreen)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = CbtAnsweredGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Result & Analysis",
                            color = CbtAnsweredGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Leaderboard button
                OutlinedButton(
                    onClick = onLeaderboard,
                    modifier = Modifier
                        .height(46.dp)
                        .testTag("btn_leaderboard_${test.testId}"),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AyanoBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.Leaderboard,
                        contentDescription = "Leaderboard",
                        tint = AyanoPrimaryCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
