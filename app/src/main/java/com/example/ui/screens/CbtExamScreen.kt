package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AttemptModel
import com.example.model.QuestionModel
import com.example.model.TestModel
import com.example.model.User
import com.example.ui.components.CbtOptionItem
import com.example.ui.components.QuestionPaletteContent
import com.example.ui.components.TimerDisplay
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
import com.example.ui.theme.CbtAnsweredMarkedIndigo
import com.example.ui.theme.CbtMarkedPurple
import com.example.ui.theme.CbtNotAnsweredRed
import com.example.ui.theme.CbtNotVisitedGrey
import com.example.ui.theme.ChemistryAmber
import com.example.ui.theme.PhysicsCyan
import com.example.ui.theme.ZoologyRose
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CbtExamScreen(
    user: User,
    test: TestModel,
    questions: List<QuestionModel>,
    onSubmitTest: (answers: Map<String, Int>, marked: List<String>, visited: List<String>, timeTaken: Long) -> Unit,
    onCancelTest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var currentIndex by remember { mutableIntStateOf(0) }
    val totalTimeSeconds = remember(test) { (test.duration * 60).toLong() }
    var remainingSeconds by remember { mutableLongStateOf(totalTimeSeconds) }

    // State tracking
    val answers = remember { mutableStateMapOf<String, Int>() } // questionId -> chosen 1..4
    val markedForReview = remember { mutableStateOf(setOf<String>()) }
    val visitedQuestions = remember { mutableStateOf(setOf<String>()) }

    var showPaletteSheet by remember { mutableStateOf(false) }
    var showSubmitDialog by remember { mutableStateOf(false) }
    var showExitConfirmDialog by remember { mutableStateOf(false) }

    // Mark current question as visited
    LaunchedEffect(currentIndex, questions) {
        if (questions.isNotEmpty() && currentIndex in questions.indices) {
            val q = questions[currentIndex]
            visitedQuestions.value = visitedQuestions.value + q.id
        }
    }

    // Timer countdown effect
    LaunchedEffect(Unit) {
        while (remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        }
        // Auto-submit when time expires
        Toast.makeText(context, "Time's up! Submitting test automatically...", Toast.LENGTH_LONG).show()
        onSubmitTest(
            answers.toMap(),
            markedForReview.value.toList(),
            visitedQuestions.value.toList(),
            totalTimeSeconds
        )
    }

    // Back handler
    BackHandler {
        showExitConfirmDialog = true
    }

    if (questions.isEmpty()) {
        Surface(modifier = Modifier.fillMaxSize(), color = AyanoBackgroundDark) {
            Box(contentAlignment = Alignment.Center) {
                Text("No questions found for this test.", color = AyanoTextPrimary)
            }
        }
        return
    }

    val currentQuestion = questions[currentIndex]
    val currentSelectedOption = answers[currentQuestion.id]
    val isCurrentMarked = markedForReview.value.contains(currentQuestion.id)

    // Subject tabs
    val subjects = listOf("All", "Physics", "Chemistry", "Botany", "Zoology")
    val currentSubjectTab = currentQuestion.subject

    Surface(
        modifier = modifier.fillMaxSize(),
        color = AyanoBackgroundDark
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Exam Header Bar
            Surface(
                color = AyanoSurfaceDark,
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AYANO TEST PASS • CBT",
                            color = AyanoPrimaryCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = test.name,
                            color = AyanoTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Timer display
                        TimerDisplay(remainingSeconds = remainingSeconds)

                        Spacer(modifier = Modifier.width(8.dp))

                        // Palette Toggle Button
                        IconButton(
                            onClick = { showPaletteSheet = true },
                            modifier = Modifier
                                .testTag("btn_open_palette")
                                .clip(RoundedCornerShape(8.dp))
                                .background(AyanoSurfaceCardElevated)
                                .size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GridOn,
                                contentDescription = "Question Palette",
                                tint = AyanoPrimaryCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Submit Button
                        Button(
                            onClick = { showSubmitDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("btn_submit_exam")
                        ) {
                            Text(
                                text = "Submit",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Subject Jump Bar
            ScrollableTabRow(
                selectedTabIndex = subjects.indexOf(currentSubjectTab).coerceAtLeast(0),
                containerColor = AyanoSurfaceCardElevated,
                contentColor = AyanoPrimaryCyan,
                edgePadding = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                subjects.forEach { sub ->
                    val isSelected = sub.equals(currentQuestion.subject, ignoreCase = true) ||
                            (sub == "All" && currentSubjectTab.isBlank())
                    Tab(
                        selected = isSelected,
                        onClick = {
                            if (sub != "All") {
                                val firstInSub = questions.indexOfFirst { it.subject.equals(sub, ignoreCase = true) }
                                if (firstInSub >= 0) {
                                    currentIndex = firstInSub
                                }
                            }
                        },
                        text = {
                            Text(
                                text = sub,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) AyanoPrimaryCyan else AyanoTextSecondary
                            )
                        }
                    )
                }
            }

            // Question View Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Question Header Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = AyanoPrimaryBlue.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AyanoPrimaryCyan.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "Question ${currentQuestion.number} / ${questions.size}",
                                color = AyanoPrimaryCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Subject Chip
                        val subColor = when (currentQuestion.subject.lowercase()) {
                            "physics" -> PhysicsCyan
                            "chemistry" -> ChemistryAmber
                            "botany" -> BotanyGreen
                            "zoology" -> ZoologyRose
                            else -> AyanoPrimaryCyan
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(subColor.copy(alpha = 0.15f))
                                .border(1.dp, subColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = currentQuestion.subject,
                                color = subColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Marking scheme
                    Text(
                        text = "+4.0  -1.0",
                        color = Color(0xFF10B981),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Question Text
                Surface(
                    color = AyanoSurfaceCard,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AyanoBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = currentQuestion.text,
                        color = AyanoTextPrimary,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .padding(16.dp)
                            .testTag("question_text_${currentQuestion.number}")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Options (1), (2), (3), (4)
                currentQuestion.options.forEachIndexed { optIndex, optText ->
                    val optionNumber = optIndex + 1
                    val isSelected = currentSelectedOption == optionNumber

                    CbtOptionItem(
                        optionIndex = optionNumber,
                        optionText = optText,
                        isSelected = isSelected,
                        onSelect = {
                            if (isSelected) {
                                answers.remove(currentQuestion.id)
                            } else {
                                answers[currentQuestion.id] = optionNumber
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Bottom Navigation & Actions Bar
            Surface(
                color = AyanoSurfaceDark,
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    // Top row: Clear Response & Mark for Review
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Clear Response
                        OutlinedButton(
                            onClick = {
                                answers.remove(currentQuestion.id)
                            },
                            enabled = currentSelectedOption != null,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = AyanoTextSecondary,
                                disabledContentColor = AyanoTextMuted
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AyanoBorder),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_clear_response")
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear", fontSize = 12.sp)
                        }

                        // Mark for Review & Next
                        Button(
                            onClick = {
                                if (isCurrentMarked) {
                                    markedForReview.value = markedForReview.value - currentQuestion.id
                                } else {
                                    markedForReview.value = markedForReview.value + currentQuestion.id
                                }
                                // Auto move to next question if not at end
                                if (currentIndex < questions.size - 1) {
                                    currentIndex++
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCurrentMarked) CbtMarkedPurple else CbtMarkedPurple.copy(alpha = 0.25f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_mark_review")
                        ) {
                            Icon(
                                Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = if (isCurrentMarked) Color.White else CbtMarkedPurple,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isCurrentMarked) "Marked" else "Mark & Next",
                                color = if (isCurrentMarked) Color.White else CbtMarkedPurple,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Bottom row: Previous & Save and Next
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Previous Question
                        OutlinedButton(
                            onClick = {
                                if (currentIndex > 0) currentIndex--
                            },
                            enabled = currentIndex > 0,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_previous_question"),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AyanoBorder)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Previous", fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Save & Next
                        Button(
                            onClick = {
                                if (currentIndex < questions.size - 1) {
                                    currentIndex++
                                } else {
                                    // Reached end: show submit confirmation
                                    showSubmitDialog = true
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_next_question"),
                            colors = ButtonDefaults.buttonColors(containerColor = AyanoPrimaryBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (currentIndex < questions.size - 1) "Save & Next" else "Submit Paper",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }

    // Question Palette Bottom Sheet
    if (showPaletteSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPaletteSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = AyanoSurfaceDark
        ) {
            QuestionPaletteContent(
                questions = questions,
                currentQuestionIndex = currentIndex,
                answers = answers.toMap(),
                markedForReview = markedForReview.value.toList(),
                visitedQuestions = visitedQuestions.value.toList(),
                onSelectQuestion = { selectedIndex ->
                    currentIndex = selectedIndex
                    showPaletteSheet = false
                },
                onClose = { showPaletteSheet = false }
            )
        }
    }

    // Submit Confirmation Dialog
    if (showSubmitDialog) {
        val answeredCount = answers.size
        val markedCount = markedForReview.value.size
        val visitedCount = visitedQuestions.value.size
        val notAnsweredCount = (visitedCount - answeredCount).coerceAtLeast(0)
        val notVisitedCount = (questions.size - visitedCount).coerceAtLeast(0)

        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            containerColor = AyanoSurfaceDark,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Done, contentDescription = null, tint = AyanoPrimaryCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Submit NEET CBT Test",
                        color = AyanoTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Are you sure you want to finish and submit your test? You will not be able to change your answers once submitted.",
                        color = AyanoTextSecondary,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        color = AyanoSurfaceCard,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Questions:", color = AyanoTextSecondary, fontSize = 12.sp)
                                Text("${questions.size}", color = AyanoTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Answered:", color = CbtAnsweredGreen, fontSize = 12.sp)
                                Text("$answeredCount", color = CbtAnsweredGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Not Answered:", color = CbtNotAnsweredRed, fontSize = 12.sp)
                                Text("$notAnsweredCount", color = CbtNotAnsweredRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Marked for Review:", color = CbtMarkedPurple, fontSize = 12.sp)
                                Text("$markedCount", color = CbtMarkedPurple, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Not Visited:", color = CbtNotVisitedGrey, fontSize = 12.sp)
                                Text("$notVisitedCount", color = CbtNotVisitedGrey, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitDialog = false
                        val timeTaken = totalTimeSeconds - remainingSeconds
                        onSubmitTest(
                            answers.toMap(),
                            markedForReview.value.toList(),
                            visitedQuestions.value.toList(),
                            timeTaken
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_confirm_submit")
                ) {
                    Text("Yes, Final Submit", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showSubmitDialog = false },
                    border = androidx.compose.foundation.BorderStroke(1.dp, AyanoBorder),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Resume Test", color = AyanoTextSecondary)
                }
            }
        )
    }

    // Exit Confirmation Dialog
    if (showExitConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showExitConfirmDialog = false },
            containerColor = AyanoSurfaceDark,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Leave Exam?",
                        color = AyanoTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    text = "Your test is in progress. Leaving now will discard your attempt. Do you want to cancel the test?",
                    color = AyanoTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitConfirmDialog = false
                        onCancelTest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Leave Test", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showExitConfirmDialog = false },
                    border = androidx.compose.foundation.BorderStroke(1.dp, AyanoBorder),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Continue Test", color = AyanoTextSecondary)
                }
            }
        )
    }
}

private val AyanoSurfaceDark = Color(0xFF0F172A)
