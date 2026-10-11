package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.data.repository.AyanoRepository
import com.example.model.AttemptModel
import com.example.model.QuestionModel
import com.example.model.TestModel
import com.example.model.User
import com.example.ui.screens.AnalysisScreen
import com.example.ui.screens.CbtExamScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ResultScreen
import com.example.ui.screens.TestListScreen
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.admin.CreateTestScreen
import com.example.ui.theme.AyanoBackgroundDark
import com.example.ui.theme.AyanoPrimaryCyan
import com.example.ui.theme.AyanoTestPassTheme
import kotlinx.coroutines.launch

enum class AyanoScreen {
    LOGIN,
    TEST_LIST,
    CBT_EXAM,
    RESULT,
    ANALYSIS,
    LEADERBOARD,
    ADMIN_DASHBOARD,
    CREATE_TEST
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AyanoTestPassTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AyanoBackgroundDark
                ) {
                    AyanoApp()
                }
            }
        }
    }
}

@Composable
fun AyanoApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repository = remember { AyanoRepository.getInstance(context) }
    val scope = rememberCoroutineScope()

    // BUG FIX #1: Was hardcoded to a default user — auth was being bypassed completely.
    // Now starts as null so the login screen always shows on first launch.
    var currentUser by remember { mutableStateOf<User?>(null) }

    // BUG FIX #2: Was starting at TEST_LIST, skipping login entirely.
    // Now correctly starts at LOGIN.
    var currentScreen by remember { mutableStateOf(AyanoScreen.LOGIN) }

    // Active test session states
    var selectedTest by remember { mutableStateOf<TestModel?>(null) }
    var testQuestions by remember { mutableStateOf<List<QuestionModel>>(emptyList()) }
    var currentAttempt by remember { mutableStateOf<AttemptModel?>(null) }
    var isLoadingQuestions by remember { mutableStateOf(false) }

    val allTests by repository.observeTests().collectAsState(initial = emptyList())

    when (currentScreen) {
        AyanoScreen.LOGIN -> {
            LoginScreen(
                onLoginSuccess = { user ->
                    currentUser = user
                    // Route admin users to their dashboard, students to test list
                    if (user.isAdmin) {
                        currentScreen = AyanoScreen.ADMIN_DASHBOARD
                    } else {
                        currentScreen = AyanoScreen.TEST_LIST
                    }
                }
            )
        }

        AyanoScreen.TEST_LIST -> {
            // IMPROVEMENT: Safer fallback — never crash if user is somehow null here
            val user = currentUser ?: User(name = "NEET Aspirant")
            TestListScreen(
                user = user,
                tests = allTests,
                onStartTest = { test ->
                    selectedTest = test
                    isLoadingQuestions = true
                    scope.launch {
                        testQuestions = repository.getQuestionsForTest(test.testId)
                        isLoadingQuestions = false
                        currentScreen = AyanoScreen.CBT_EXAM
                    }
                },
                onViewResult = { test, attempt ->
                    selectedTest = test
                    currentAttempt = attempt
                    scope.launch {
                        testQuestions = repository.getQuestionsForTest(test.testId)
                        currentScreen = AyanoScreen.RESULT
                    }
                },
                onOpenLeaderboard = { test ->
                    selectedTest = test
                    currentScreen = AyanoScreen.LEADERBOARD
                },
                onOpenAdmin = {
                    if (user.isAdmin) {
                        currentScreen = AyanoScreen.ADMIN_DASHBOARD
                    }
                },
                onLogout = {
                    // IMPROVEMENT: Clear all session state on logout — previous code
                    // only reset user and screen but left stale test/attempt data.
                    currentUser = null
                    selectedTest = null
                    testQuestions = emptyList()
                    currentAttempt = null
                    currentScreen = AyanoScreen.LOGIN
                }
            )

            if (isLoadingQuestions) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AyanoPrimaryCyan)
                }
            }
        }

        AyanoScreen.CBT_EXAM -> {
            val test = selectedTest
            val user = currentUser ?: User(name = "NEET Aspirant")
            if (test != null) {
                CbtExamScreen(
                    user = user,
                    test = test,
                    questions = testQuestions,
                    onSubmitTest = { answers, marked, visited, timeTaken ->
                        scope.launch {
                            val attempt = repository.submitAttempt(
                                userId = user.userId,
                                userName = user.name,
                                userClass = user.userClass,
                                testId = test.testId,
                                answers = answers,
                                markedForReview = marked,
                                visitedQuestions = visited,
                                timeTaken = timeTaken
                            )
                            currentAttempt = attempt
                            currentScreen = AyanoScreen.RESULT
                        }
                    },
                    onCancelTest = {
                        currentScreen = AyanoScreen.TEST_LIST
                    }
                )
            } else {
                currentScreen = AyanoScreen.TEST_LIST
            }
        }

        AyanoScreen.RESULT -> {
            val test = selectedTest
            val attempt = currentAttempt
            val user = currentUser ?: User(name = "NEET Aspirant")
            if (test != null && attempt != null) {
                BackHandler {
                    currentScreen = AyanoScreen.TEST_LIST
                }
                ResultScreen(
                    user = user,
                    test = test,
                    attempt = attempt,
                    onViewAnalysis = {
                        currentScreen = AyanoScreen.ANALYSIS
                    },
                    onViewLeaderboard = {
                        currentScreen = AyanoScreen.LEADERBOARD
                    },
                    onBackToHome = {
                        currentScreen = AyanoScreen.TEST_LIST
                    }
                )
            } else {
                currentScreen = AyanoScreen.TEST_LIST
            }
        }

        AyanoScreen.ANALYSIS -> {
            val test = selectedTest
            val attempt = currentAttempt
            if (test != null && attempt != null) {
                BackHandler {
                    currentScreen = AyanoScreen.RESULT
                }
                AnalysisScreen(
                    test = test,
                    questions = testQuestions,
                    attempt = attempt,
                    onBack = {
                        currentScreen = AyanoScreen.RESULT
                    }
                )
            } else {
                currentScreen = AyanoScreen.TEST_LIST
            }
        }

        AyanoScreen.LEADERBOARD -> {
            val test = selectedTest ?: allTests.firstOrNull()
            val user = currentUser ?: User(name = "NEET Aspirant")
            if (test != null) {
                BackHandler {
                    currentScreen = if (currentAttempt != null) AyanoScreen.RESULT else AyanoScreen.TEST_LIST
                }
                LeaderboardScreen(
                    user = user,
                    test = test,
                    onBack = {
                        currentScreen = if (currentAttempt != null) AyanoScreen.RESULT else AyanoScreen.TEST_LIST
                    }
                )
            } else {
                currentScreen = AyanoScreen.TEST_LIST
            }
        }

        AyanoScreen.ADMIN_DASHBOARD -> {
            val user = currentUser
            if (user == null || !user.isAdmin) {
                currentScreen = AyanoScreen.TEST_LIST
            } else {
                BackHandler {
                    currentScreen = AyanoScreen.TEST_LIST
                }
                AdminDashboardScreen(
                    user = user,
                    tests = allTests,
                    onCreateTest = {
                        currentScreen = AyanoScreen.CREATE_TEST
                    },
                    onBack = {
                        currentScreen = AyanoScreen.TEST_LIST
                    }
                )
            }
        }

        AyanoScreen.CREATE_TEST -> {
            val user = currentUser
            if (user == null || !user.isAdmin) {
                currentScreen = AyanoScreen.TEST_LIST
            } else {
                BackHandler {
                    currentScreen = AyanoScreen.ADMIN_DASHBOARD
                }
                CreateTestScreen(
                    user = user,
                    onTestCreated = {
                        currentScreen = AyanoScreen.ADMIN_DASHBOARD
                    },
                    onBack = {
                        currentScreen = AyanoScreen.ADMIN_DASHBOARD
                    }
                )
            }
        }
    }
}
