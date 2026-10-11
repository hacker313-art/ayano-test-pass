package com.example.model

import androidx.compose.ui.graphics.Color

data class TestModel(
    val testId: String = "",
    val name: String = "",
    val date: String = "",
    val duration: Int = 180, // in minutes
    val totalQuestions: Int = 180,
    val subjects: List<String> = listOf("Physics", "Chemistry", "Botany", "Zoology"),
    val markingScheme: Map<String, Int> = mapOf("correct" to 4, "wrong" to -1, "unattempted" to 0),
    val createdBy: String = "",
    val publishedAt: Long = 0L,
    val isPublished: Boolean = true
)

data class QuestionModel(
    val id: String = "",
    val number: Int = 1,
    val text: String = "",
    val options: List<String> = listOf("", "", "", ""),
    val correctOption: Int = 1, // 1, 2, 3, or 4
    val solution: String = "",
    val subject: String = "Physics"
)

data class AttemptModel(
    val attemptId: String = "",
    val userId: String = "",
    val testId: String = "",
    val answers: Map<String, Int> = emptyMap(), // questionId -> selectedOption (1..4)
    val markedForReview: List<String> = emptyList(), // questionIds
    val visitedQuestions: List<String> = emptyList(), // questionIds
    val timeTaken: Long = 0L, // in seconds
    val score: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val unattemptedCount: Int = 0,
    val subjectScores: Map<String, Int> = emptyMap(),
    val submittedAt: Long = 0L
)

data class LeaderboardEntry(
    val userId: String = "",
    val userName: String = "",
    val userClass: String = "",
    val score: Int = 0,
    val rank: Int = 1,
    val timeTaken: Long = 0L, // seconds (tie-breaker)
    val submittedAt: Long = 0L
)

enum class QuestionState(val displayName: String, val colorHex: Long) {
    NOT_VISITED("Not Visited", 0xFF64748B),
    NOT_ANSWERED("Not Answered", 0xFFEF4444),
    ANSWERED("Answered", 0xFF22C55E),
    MARKED_FOR_REVIEW("Marked for Review", 0xFFA855F7),
    ANSWERED_AND_MARKED("Answered & Marked", 0xFF6366F1)
}
