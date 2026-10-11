package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.sample.SampleTests
import com.example.model.AttemptModel
import com.example.model.LeaderboardEntry
import com.example.model.QuestionModel
import com.example.model.TestModel
import com.example.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AyanoRepository(private val context: Context) {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    companion object {
        private const val TAG = "AyanoRepository"

        @Volatile
        private var INSTANCE: AyanoRepository? = null

        fun getInstance(context: Context): AyanoRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AyanoRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    // In-memory fallback caches — keeps the app fast even when offline
    private val localTests = mutableMapOf<String, TestModel>()
    private val localQuestions = mutableMapOf<String, MutableList<QuestionModel>>()
    private val localAttempts = mutableMapOf<String, AttemptModel>() // key: userId_testId
    private val localLeaderboard = mutableMapOf<String, MutableList<LeaderboardEntry>>() // testId -> list

    init {
        // BUG FIX #4: setPersistenceEnabled(true) is deprecated in newer Firebase SDKs
        // and throws an exception if Firestore is already initialized.
        // The new API uses setLocalCacheSettings() with PersistentCacheSettings.
        // We fall back to MemoryCacheSettings if persistent cache setup fails
        // (e.g., on devices with no storage space).
        try {
            val settings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(
                    PersistentCacheSettings.newBuilder()
                        .setSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                        .build()
                )
                .build()
            firestore.firestoreSettings = settings
            Log.d(TAG, "Firestore: persistent cache enabled (offline support active)")
        } catch (e: Exception) {
            // Already initialised elsewhere or storage unavailable — fall back to memory cache
            try {
                val settings = FirebaseFirestoreSettings.Builder()
                    .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
                    .build()
                firestore.firestoreSettings = settings
                Log.d(TAG, "Firestore: using memory cache fallback. Reason: ${e.message}")
            } catch (e2: Exception) {
                Log.d(TAG, "Firestore settings already locked — using defaults. ${e2.message}")
            }
        }

        // Seed default NEET sample test so the app has content on first launch
        val sample = SampleTests.sampleNeetTest
        localTests[sample.testId] = sample
        localQuestions[sample.testId] = SampleTests.getSampleQuestions(sample.testId).toMutableList()

        // Seed realistic mock leaderboard scores for instant competitive feel
        localLeaderboard[sample.testId] = mutableListOf(
            LeaderboardEntry("u1", "Aarav Sharma",  "Dropper", 680, 1, 9200,  System.currentTimeMillis() - 7200000),
            LeaderboardEntry("u2", "Priya Verma",   "12",      665, 2, 9450,  System.currentTimeMillis() - 14400000),
            LeaderboardEntry("u3", "Rohan Gupta",   "Dropper", 650, 3, 9800,  System.currentTimeMillis() - 21600000),
            LeaderboardEntry("u4", "Sneha Patel",   "12",      632, 4, 10200, System.currentTimeMillis() - 28800000),
            LeaderboardEntry("u5", "Ananya Singh",  "11",      610, 5, 10500, System.currentTimeMillis() - 36000000)
        )
    }

    // ─── Current User ────────────────────────────────────────────────────────

    fun getCurrentUserId(): String {
        return auth.currentUser?.uid
            ?: "ayano_guest_${android.os.Build.DEVICE.hashCode().toString().takeLast(6)}"
    }

    suspend fun saveUserProfile(user: User): Result<Unit> {
        return try {
            firestore.collection("users").document(user.userId).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "Could not save user profile to Firestore (continuing locally): ${e.message}")
            Result.success(Unit) // local-only is fine — not blocking
        }
    }

    suspend fun getUserProfile(userId: String): User? {
        return try {
            val doc = firestore.collection("users").document(userId).get().await()
            if (doc.exists()) doc.toObject(User::class.java) else null
        } catch (e: Exception) {
            Log.w(TAG, "Error fetching user profile: ${e.message}")
            null
        }
    }

    // ─── Tests ───────────────────────────────────────────────────────────────

    fun observeTests(): Flow<List<TestModel>> = callbackFlow {
        // Emit local cache immediately so the UI never shows empty
        trySend(localTests.values.toList().sortedByDescending { it.publishedAt })

        val listener = firestore.collection("tests")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Firestore tests listener error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val remoteTests = snapshot.documents
                        .mapNotNull { it.toObject(TestModel::class.java) }
                    if (remoteTests.isNotEmpty()) {
                        remoteTests.forEach { test -> localTests[test.testId] = test }
                    }
                    trySend(localTests.values.toList().sortedByDescending { it.publishedAt })
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveTest(test: TestModel, questions: List<QuestionModel>): Result<String> {
        val testId = if (test.testId.isBlank()) UUID.randomUUID().toString() else test.testId
        val finalTest = test.copy(
            testId = testId,
            totalQuestions = questions.size,
            publishedAt = System.currentTimeMillis()
        )

        // Always update local cache first — Firestore is best-effort
        localTests[testId] = finalTest
        localQuestions[testId] = questions.toMutableList()

        try {
            val batch = firestore.batch()
            val testDoc = firestore.collection("tests").document(testId)
            batch.set(testDoc, finalTest)

            val qCollection = testDoc.collection("questions")
            questions.forEach { q ->
                val qDoc = qCollection.document(q.id.ifBlank { UUID.randomUUID().toString() })
                batch.set(qDoc, q)
            }
            batch.commit().await()
            Log.d(TAG, "Test $testId saved to Firestore successfully")
        } catch (e: Exception) {
            Log.w(TAG, "Firestore write failed — test saved locally only: ${e.message}")
        }

        return Result.success(testId)
    }

    suspend fun deleteTest(testId: String): Result<Unit> {
        localTests.remove(testId)
        localQuestions.remove(testId)
        try {
            firestore.collection("tests").document(testId).delete().await()
        } catch (e: Exception) {
            Log.w(TAG, "Error deleting test from Firestore: ${e.message}")
        }
        return Result.success(Unit)
    }

    suspend fun togglePublishTest(testId: String, publish: Boolean): Result<Unit> {
        val existing = localTests[testId]
            ?: return Result.failure(Exception("Test not found: $testId"))
        val updated = existing.copy(isPublished = publish)
        localTests[testId] = updated
        try {
            firestore.collection("tests").document(testId)
                .update("isPublished", publish).await()
        } catch (e: Exception) {
            Log.w(TAG, "Error updating publish state: ${e.message}")
        }
        return Result.success(Unit)
    }

    suspend fun getQuestionsForTest(testId: String): List<QuestionModel> {
        // Return from cache immediately if available
        val cached = localQuestions[testId]
        if (!cached.isNullOrEmpty()) {
            return cached.sortedBy { it.number }
        }

        return try {
            val snapshot = firestore
                .collection("tests").document(testId)
                .collection("questions")
                .get().await()
            val list = snapshot.documents.mapNotNull { it.toObject(QuestionModel::class.java) }
            if (list.isNotEmpty()) {
                localQuestions[testId] = list.toMutableList()
                list.sortedBy { it.number }
            } else {
                // No remote questions found — fall back to sample data
                SampleTests.getSampleQuestions(testId).also {
                    localQuestions[testId] = it.toMutableList()
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error fetching questions from Firestore: ${e.message}")
            SampleTests.getSampleQuestions(testId)
        }
    }

    // ─── Attempts ────────────────────────────────────────────────────────────

    suspend fun checkHasAttempted(userId: String, testId: String): AttemptModel? {
        val key = "${userId}_$testId"
        localAttempts[key]?.let { return it }

        return try {
            val snapshot = firestore.collection("attempts")
                .whereEqualTo("userId", userId)
                .whereEqualTo("testId", testId)
                .limit(1)
                .get().await()
            val attempt = snapshot.documents.firstOrNull()?.toObject(AttemptModel::class.java)
            if (attempt != null) localAttempts[key] = attempt
            attempt
        } catch (e: Exception) {
            Log.w(TAG, "Error checking attempt: ${e.message}")
            localAttempts[key]
        }
    }

    suspend fun submitAttempt(
        userId: String,
        userName: String,
        userClass: String,
        testId: String,
        answers: Map<String, Int>,
        markedForReview: List<String>,
        visitedQuestions: List<String>,
        timeTaken: Long
    ): AttemptModel {
        val questions = getQuestionsForTest(testId)
        var correctCount = 0
        var wrongCount = 0
        var unattemptedCount = 0

        val subjectCorrect = mutableMapOf<String, Int>()
        val subjectWrong = mutableMapOf<String, Int>()

        questions.forEach { q ->
            val chosen = answers[q.id]
            val sub = q.subject
            when {
                chosen == null -> unattemptedCount++
                chosen == q.correctOption -> {
                    correctCount++
                    subjectCorrect[sub] = (subjectCorrect[sub] ?: 0) + 1
                }
                else -> {
                    wrongCount++
                    subjectWrong[sub] = (subjectWrong[sub] ?: 0) + 1
                }
            }
        }

        // NEET marking scheme: +4 correct, -1 wrong, 0 unattempted
        val totalScore = (correctCount * 4) - wrongCount

        val subjectScores = listOf("Physics", "Chemistry", "Botany", "Zoology")
            .associateWith { sub ->
                val c = subjectCorrect[sub] ?: 0
                val w = subjectWrong[sub] ?: 0
                (c * 4) - w
            }

        val attemptId = UUID.randomUUID().toString()
        val attempt = AttemptModel(
            attemptId = attemptId,
            userId = userId,
            testId = testId,
            answers = answers,
            markedForReview = markedForReview,
            visitedQuestions = visitedQuestions,
            timeTaken = timeTaken,
            score = totalScore,
            correctCount = correctCount,
            wrongCount = wrongCount,
            unattemptedCount = unattemptedCount,
            subjectScores = subjectScores,
            submittedAt = System.currentTimeMillis()
        )

        val key = "${userId}_$testId"
        localAttempts[key] = attempt

        // Update local leaderboard — score DESC, then time ASC (tie-breaker)
        val leaderboardList = localLeaderboard.getOrPut(testId) { mutableListOf() }
        leaderboardList.removeAll { it.userId == userId }
        leaderboardList.add(
            LeaderboardEntry(
                userId = userId,
                userName = userName.ifBlank { "NEET Aspirant" },
                userClass = userClass,
                score = totalScore,
                rank = 1,
                timeTaken = timeTaken,
                submittedAt = attempt.submittedAt
            )
        )
        val ranked = leaderboardList
            .sortedWith(compareByDescending<LeaderboardEntry> { it.score }.thenBy { it.timeTaken })
            .mapIndexed { index, entry -> entry.copy(rank = index + 1) }
        localLeaderboard[testId] = ranked.toMutableList()

        // Sync to Firestore in the background — failures are non-blocking
        try {
            firestore.collection("attempts").document(attemptId).set(attempt).await()

            val userRankEntry = ranked.find { it.userId == userId }
                ?: LeaderboardEntry(
                    userId = userId,
                    userName = userName,
                    userClass = userClass,
                    score = totalScore,
                    rank = ranked.indexOfFirst { it.userId == userId } + 1,
                    timeTaken = timeTaken,
                    submittedAt = attempt.submittedAt
                )

            firestore.collection("leaderboard")
                .document(testId)
                .collection("entries")
                .document(userId)
                .set(userRankEntry)
                .await()

            Log.d(TAG, "Attempt $attemptId synced to Firestore")
        } catch (e: Exception) {
            Log.w(TAG, "Firestore attempt sync warning (saved locally): ${e.message}")
        }

        return attempt
    }

    // ─── Leaderboard ─────────────────────────────────────────────────────────

    fun observeLeaderboard(testId: String): Flow<List<LeaderboardEntry>> = callbackFlow {
        // Emit local data immediately for instant display
        trySend(localLeaderboard[testId] ?: emptyList())

        val listener = firestore
            .collection("leaderboard")
            .document(testId)
            .collection("entries")
            .orderBy("score", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Leaderboard listener error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val remoteEntries = snapshot.documents
                        .mapNotNull { it.toObject(LeaderboardEntry::class.java) }
                    val ranked = remoteEntries
                        .sortedWith(compareByDescending<LeaderboardEntry> { it.score }.thenBy { it.timeTaken })
                        .mapIndexed { index, entry -> entry.copy(rank = index + 1) }
                    localLeaderboard[testId] = ranked.toMutableList()
                    trySend(ranked)
                }
            }
        awaitClose { listener.remove() }
    }
}
