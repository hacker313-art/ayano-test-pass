package com.example.ui.screens.admin

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.parser.NeetPdfParser
import com.example.data.repository.AyanoRepository
import com.example.data.sample.SampleTests
import com.example.model.QuestionModel
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
import com.example.ui.theme.CbtAnsweredGreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateTestScreen(
    user: User,
    onTestCreated: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { AyanoRepository.getInstance(context) }
    val scope = rememberCoroutineScope()

    var testName by remember { mutableStateOf("AYANO NEET FULL SYLLABUS MOCK - 02") }
    var durationMinutes by remember { mutableStateOf("180") }
    val currentDateStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    var testDate by remember { mutableStateOf(currentDateStr) }
    var isPublished by remember { mutableStateOf(true) }

    var importTabIndex by remember { mutableIntStateOf(0) } // 0: PDF File, 1: Raw Text Paste
    var rawTextPaper by remember { mutableStateOf("") }
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
    var pdfFileName by remember { mutableStateOf("") }

    var isParsing by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    // Extracted questions
    val extractedQuestions = remember { mutableStateListOf<QuestionModel>() }

    // PDF picker launcher
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPdfUri = uri
            pdfFileName = uri.lastPathSegment ?: "selected_exam_paper.pdf"
            isParsing = true
            scope.launch {
                val extractedText = withContext(Dispatchers.IO) {
                    NeetPdfParser.extractTextFromPdfUri(context, uri)
                }
                if (extractedText.isNotBlank()) {
                    val parsed = withContext(Dispatchers.Default) {
                        NeetPdfParser.parseNeetPaperText(extractedText)
                    }
                    extractedQuestions.clear()
                    extractedQuestions.addAll(parsed)
                    Toast.makeText(context, "Parsed ${parsed.size} questions from PDF!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Could not extract text. Make sure PDF is text-based.", Toast.LENGTH_LONG).show()
                }
                isParsing = false
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = AyanoBackgroundDark
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AyanoTopBar(
                title = "Create NEET Test",
                subtitle = "Text-based PDF Parser & Exam Creator",
                showBack = true,
                onBackClick = onBack
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Test Config Card
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = AyanoSurfaceCard,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AyanoBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "TEST PARAMETERS",
                                color = AyanoPrimaryCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = testName,
                                onValueChange = { testName = it },
                                label = { Text("Test Name / Title") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = AyanoTextPrimary,
                                    unfocusedTextColor = AyanoTextPrimary,
                                    focusedBorderColor = AyanoPrimaryCyan,
                                    unfocusedBorderColor = AyanoBorder
                                ),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_test_name")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = durationMinutes,
                                    onValueChange = { durationMinutes = it },
                                    label = { Text("Duration (mins)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = AyanoTextPrimary,
                                        unfocusedTextColor = AyanoTextPrimary,
                                        focusedBorderColor = AyanoPrimaryCyan,
                                        unfocusedBorderColor = AyanoBorder
                                    ),
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("input_test_duration")
                                )

                                OutlinedTextField(
                                    value = testDate,
                                    onValueChange = { testDate = it },
                                    label = { Text("Date (YYYY-MM-DD)") },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = AyanoTextPrimary,
                                        unfocusedTextColor = AyanoTextPrimary,
                                        focusedBorderColor = AyanoPrimaryCyan,
                                        unfocusedBorderColor = AyanoBorder
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { isPublished = !isPublished }
                            ) {
                                Checkbox(
                                    checked = isPublished,
                                    onCheckedChange = { isPublished = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = CbtAnsweredGreen,
                                        uncheckedColor = AyanoTextMuted
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Publish Immediately (Visible to students)",
                                    color = AyanoTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Import Method Tabs (PDF Upload or Paste Text)
                item {
                    Surface(
                        color = AyanoSurfaceCardElevated,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        TabRow(
                            selectedTabIndex = importTabIndex,
                            containerColor = AyanoSurfaceCardElevated,
                            contentColor = AyanoPrimaryCyan
                        ) {
                            Tab(
                                selected = importTabIndex == 0,
                                onClick = { importTabIndex = 0 },
                                text = { Text("PDF Upload (Text-Based)", fontSize = 13.sp) }
                            )
                            Tab(
                                selected = importTabIndex == 1,
                                onClick = { importTabIndex = 1 },
                                text = { Text("Paste Question Paper", fontSize = 13.sp) }
                            )
                        }
                    }
                }

                // Tab 0: PDF Upload
                if (importTabIndex == 0) {
                    item {
                        Surface(
                            color = AyanoSurfaceCard,
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AyanoBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = AyanoPrimaryCyan,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (pdfFileName.isNotBlank()) pdfFileName else "Select NEET Question Paper PDF",
                                    color = AyanoTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Supported: Text-based PDFs with questions 1-180 & answer key",
                                    color = AyanoTextSecondary,
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        pdfPickerLauncher.launch(arrayOf("application/pdf"))
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AyanoPrimaryBlue),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("btn_select_pdf")
                                ) {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Browse & Parse PDF", fontWeight = FontWeight.Bold)
                                }

                                if (isParsing) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            color = AyanoPrimaryCyan,
                                            modifier = Modifier.size(18.dp),
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Extracting questions with local PDFBox...", color = AyanoPrimaryCyan, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Tab 1: Paste Text Paper
                    item {
                        Surface(
                            color = AyanoSurfaceCard,
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AyanoBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "PASTE PAPER OR LOAD SAMPLE",
                                        color = AyanoTextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    OutlinedButton(
                                        onClick = {
                                            rawTextPaper = SampleTests.SAMPLE_PASTE_PDF_PAPER.trimIndent()
                                        },
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Load Sample Text", fontSize = 11.sp, color = AyanoPrimaryCyan)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = rawTextPaper,
                                    onValueChange = { rawTextPaper = it },
                                    placeholder = { Text("Paste questions, options (1-4), and answer key here...") },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = AyanoTextPrimary,
                                        unfocusedTextColor = AyanoTextPrimary,
                                        focusedBorderColor = AyanoPrimaryCyan,
                                        unfocusedBorderColor = AyanoBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .testTag("input_raw_paper")
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        if (rawTextPaper.isBlank()) {
                                            Toast.makeText(context, "Please paste paper text first", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        val parsed = NeetPdfParser.parseNeetPaperText(rawTextPaper)
                                        extractedQuestions.clear()
                                        extractedQuestions.addAll(parsed)
                                        Toast.makeText(context, "Extracted ${parsed.size} questions!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AyanoPrimaryCyan),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("btn_parse_text_paper")
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AyanoBackgroundDark, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Parse & Extract Questions", color = AyanoBackgroundDark, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Extracted Questions Header & List
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EXTRACTED QUESTIONS (${extractedQuestions.size})",
                            color = AyanoTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        // Add question manually
                        OutlinedButton(
                            onClick = {
                                val nextNum = extractedQuestions.size + 1
                                extractedQuestions.add(
                                    QuestionModel(
                                        id = UUID.randomUUID().toString(),
                                        number = nextNum,
                                        text = "New Question $nextNum",
                                        options = listOf("Option 1", "Option 2", "Option 3", "Option 4"),
                                        correctOption = 1,
                                        solution = "Solution for question $nextNum",
                                        subject = NeetPdfParser.autoDetectSubject(nextNum, "Physics")
                                    )
                                )
                            },
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Question", fontSize = 11.sp, color = AyanoPrimaryCyan)
                        }
                    }
                }

                if (extractedQuestions.isEmpty()) {
                    item {
                        Surface(
                            color = AyanoSurfaceCard,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier.padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No questions extracted yet. Upload a PDF or paste text above.",
                                    color = AyanoTextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                } else {
                    itemsIndexed(extractedQuestions) { index, question ->
                        AdminQuestionEditorCard(
                            question = question,
                            onUpdate = { updated ->
                                extractedQuestions[index] = updated
                            },
                            onDelete = {
                                extractedQuestions.removeAt(index)
                            }
                        )
                    }

                    // Save and Publish Button
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (testName.isBlank()) {
                                    Toast.makeText(context, "Please enter test name", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (extractedQuestions.isEmpty()) {
                                    Toast.makeText(context, "Please extract at least 1 question", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                isSaving = true
                                val durationInt = durationMinutes.toIntOrNull() ?: 180
                                val test = TestModel(
                                    testId = "test_${System.currentTimeMillis()}",
                                    name = testName.trim(),
                                    date = testDate.trim(),
                                    duration = durationInt,
                                    totalQuestions = extractedQuestions.size,
                                    subjects = listOf("Physics", "Chemistry", "Botany", "Zoology"),
                                    markingScheme = mapOf("correct" to 4, "wrong" to -1, "unattempted" to 0),
                                    createdBy = user.name.ifBlank { "Admin" },
                                    publishedAt = System.currentTimeMillis(),
                                    isPublished = isPublished
                                )

                                scope.launch {
                                    repository.saveTest(test, extractedQuestions.toList())
                                    isSaving = false
                                    Toast.makeText(context, "Test saved and published to Firestore!", Toast.LENGTH_LONG).show()
                                    onTestCreated()
                                }
                            },
                            enabled = !isSaving,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_save_test_firestore"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Save & Publish Test to Firestore",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminQuestionEditorCard(
    question: QuestionModel,
    onUpdate: (QuestionModel) -> Unit,
    onDelete: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        color = AyanoSurfaceCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AyanoBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
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
                            color = AyanoPrimaryCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = question.subject,
                        color = AyanoTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        color = CbtAnsweredGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Ans: (${question.correctOption})",
                            color = CbtAnsweredGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AyanoPrimaryCyan, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = question.text,
                color = AyanoTextPrimary,
                fontSize = 13.sp,
                maxLines = if (isExpanded) 10 else 2,
                lineHeight = 18.sp
            )

            // If expanded: edit form
            if (isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))

                // Subject picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Physics", "Chemistry", "Botany", "Zoology").forEach { sub ->
                        val isSel = question.subject.equals(sub, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) AyanoPrimaryCyan else AyanoSurfaceCardElevated)
                                .clickable { onUpdate(question.copy(subject = sub)) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = sub,
                                color = if (isSel) AyanoBackgroundDark else AyanoTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = question.text,
                    onValueChange = { onUpdate(question.copy(text = it)) },
                    label = { Text("Question Text") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AyanoTextPrimary,
                        unfocusedTextColor = AyanoTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Edit 4 Options (1..4)
                question.options.forEachIndexed { idx, opt ->
                    val optNum = idx + 1
                    OutlinedTextField(
                        value = opt,
                        onValueChange = { newText ->
                            val updatedOpts = question.options.toMutableList()
                            if (idx < updatedOpts.size) {
                                updatedOpts[idx] = newText
                                onUpdate(question.copy(options = updatedOpts))
                            }
                        },
                        label = { Text("Option ($optNum)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AyanoTextPrimary,
                            unfocusedTextColor = AyanoTextPrimary
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Select correct option 1, 2, 3, or 4
                Text(
                    text = "Correct Answer Option:",
                    color = AyanoTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1, 2, 3, 4).forEach { optNum ->
                        val isCorrect = question.correctOption == optNum
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCorrect) CbtAnsweredGreen else AyanoSurfaceCardElevated)
                                .clickable { onUpdate(question.copy(correctOption = optNum)) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "($optNum)",
                                color = if (isCorrect) Color.White else AyanoTextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = question.solution,
                    onValueChange = { onUpdate(question.copy(solution = it)) },
                    label = { Text("Solution / Hint") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AyanoTextPrimary,
                        unfocusedTextColor = AyanoTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private val AyanoSurfaceDark = Color(0xFF0F172A)
