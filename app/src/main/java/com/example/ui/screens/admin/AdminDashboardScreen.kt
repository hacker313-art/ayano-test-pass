package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AyanoRepository
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
import kotlinx.coroutines.launch

@Composable
fun AdminDashboardScreen(
    user: User,
    tests: List<TestModel>,
    onCreateTest: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { AyanoRepository.getInstance(context) }
    val scope = rememberCoroutineScope()

    var testToDelete by remember { mutableStateOf<TestModel?>(null) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = AyanoBackgroundDark
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AyanoTopBar(
                title = "Admin Examination Control",
                subtitle = "AYANO TEST PASS • Test Creator Portal",
                showBack = true,
                onBackClick = onBack
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Create New Test CTA Card
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = AyanoSurfaceCardElevated,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, AyanoPrimaryCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "UPLOAD & GENERATE NEET CBT TEST",
                                color = AyanoPrimaryCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Upload text-based PDF test paper. Questions (1-180), 4 options, answer key, and solutions will be auto-parsed into Firestore!",
                                color = AyanoTextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = onCreateTest,
                                colors = ButtonDefaults.buttonColors(containerColor = AyanoPrimaryCyan),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("btn_create_test_pdf")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = AyanoBackgroundDark,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Create New Test (PDF Upload)",
                                    color = AyanoBackgroundDark,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Header for Tests
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MANAGE CREATED TESTS (${tests.size})",
                            color = AyanoTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Test list
                if (tests.isEmpty()) {
                    item {
                        Surface(
                            color = AyanoSurfaceCard,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier.padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No tests found. Click above to create one.",
                                    color = AyanoTextSecondary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                } else {
                    items(tests) { test ->
                        AdminTestItem(
                            test = test,
                            onTogglePublish = { newPublishState ->
                                scope.launch {
                                    repository.togglePublishTest(test.testId, newPublishState)
                                    Toast.makeText(
                                        context,
                                        if (newPublishState) "Test Published for Students!" else "Test Hidden (Unpublished)",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            onDelete = {
                                testToDelete = test
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }

    // Delete Confirmation Dialog
    testToDelete?.let { test ->
        AlertDialog(
            onDismissRequest = { testToDelete = null },
            containerColor = AyanoSurfaceDark,
            title = {
                Text(
                    text = "Delete Test?",
                    color = AyanoTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete '${test.name}'? This cannot be undone.",
                    color = AyanoTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            repository.deleteTest(test.testId)
                            Toast.makeText(context, "Test deleted", Toast.LENGTH_SHORT).show()
                            testToDelete = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { testToDelete = null },
                    border = androidx.compose.foundation.BorderStroke(1.dp, AyanoBorder),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel", color = AyanoTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun AdminTestItem(
    test: TestModel,
    onTogglePublish: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = AyanoSurfaceCard,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AyanoBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_test_${test.testId}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (test.isPublished) CbtAnsweredGreen.copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (test.isPublished) "PUBLISHED (VISIBLE)" else "DRAFT (HIDDEN)",
                        color = if (test.isPublished) CbtAnsweredGreen else Color(0xFFEF4444),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFEF4444).copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = test.name,
                color = AyanoTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Total Questions: ${test.totalQuestions} • Duration: ${test.duration} mins • ${test.date.ifBlank { "Active" }}",
                color = AyanoTextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Publish Toggle Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (test.isPublished) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = if (test.isPublished) CbtAnsweredGreen else AyanoTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (test.isPublished) "Visible to Students" else "Hidden from Students",
                        color = AyanoTextSecondary,
                        fontSize = 12.sp
                    )
                }

                Switch(
                    checked = test.isPublished,
                    onCheckedChange = onTogglePublish,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CbtAnsweredGreen,
                        checkedTrackColor = CbtAnsweredGreen.copy(alpha = 0.3f),
                        uncheckedThumbColor = AyanoTextMuted,
                        uncheckedTrackColor = AyanoSurfaceCardElevated
                    )
                )
            }
        }
    }
}

private val AyanoSurfaceDark = Color(0xFF0F172A)
