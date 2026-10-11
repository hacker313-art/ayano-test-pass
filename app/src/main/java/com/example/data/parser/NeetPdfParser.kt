package com.example.data.parser

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.model.QuestionModel
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.io.InputStream
import java.util.UUID

object NeetPdfParser {
    private const val TAG = "NeetPdfParser"

    fun extractTextFromPdfUri(context: Context, uri: Uri): String {
        return try {
            PDFBoxResourceLoader.init(context)
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                extractTextFromInputStream(inputStream)
            } ?: ""
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting text from PDF", e)
            ""
        }
    }

    fun extractTextFromInputStream(inputStream: InputStream): String {
        return try {
            PDDocument.load(inputStream).use { document ->
                val stripper = PDFTextStripper()
                stripper.sortByPosition = true
                stripper.getText(document)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading PDDocument", e)
            ""
        }
    }

    fun parseNeetPaperText(rawText: String): List<QuestionModel> {
        if (rawText.isBlank()) return emptyList()

        val normalized = rawText
            .replace("\r\n", "\n")
            .replace("\r", "\n")

        // First, check if there is a separate Answer Key section at the end
        val answerKeyMap = extractAnswerKeyMap(normalized)

        // Split text before answer key section
        val questionSection = extractQuestionSection(normalized)

        // Parse questions from the section
        val questions = parseQuestionsFromText(questionSection, answerKeyMap)

        return if (questions.isNotEmpty()) {
            questions
        } else {
            // Fallback: heuristic line-by-line block parsing
            fallbackLineParser(normalized, answerKeyMap)
        }
    }

    private fun extractQuestionSection(text: String): String {
        val markers = listOf(
            "ANSWER KEY",
            "ANSWERS",
            "ANSWER-KEY",
            "HINTS AND SOLUTIONS",
            "HINTS & SOLUTIONS",
            "SOLUTIONS"
        )
        for (marker in markers) {
            val idx = text.indexOf(marker, ignoreCase = true)
            if (idx > 200) { // ensure not right at the beginning
                return text.substring(0, idx)
            }
        }
        return text
    }

    private fun extractAnswerKeyMap(text: String): Map<Int, Int> {
        val map = mutableMapOf<Int, Int>()
        val markers = listOf("ANSWER KEY", "ANSWERS", "ANSWER-KEY", "HINTS & SOLUTIONS", "SOLUTIONS")
        var answerKeyText = ""

        for (marker in markers) {
            val idx = text.indexOf(marker, ignoreCase = true)
            if (idx >= 0) {
                answerKeyText = text.substring(idx)
                break
            }
        }

        if (answerKeyText.isNotBlank()) {
            // Regex patterns like: 1. (2) or 1 - (3) or 1.(4) or Q.1 (2) or 1. B or 1) 2
            val regex = Regex("""(?:Q\.?\s*)?(\d{1,3})\s*[\.:\-\)]?\s*[\(\[]?\s*([1-4]|[A-Da-d])\s*[\)\]]?""")
            regex.findAll(answerKeyText).forEach { match ->
                val qNum = match.groupValues[1].toIntOrNull()
                val ansStr = match.groupValues[2].trim()
                val ansInt = when (ansStr.uppercase()) {
                    "1", "A" -> 1
                    "2", "B" -> 2
                    "3", "C" -> 3
                    "4", "D" -> 4
                    else -> 1
                }
                if (qNum != null && qNum in 1..200) {
                    map[qNum] = ansInt
                }
            }
        }
        return map
    }

    private fun parseQuestionsFromText(text: String, answerKeyMap: Map<Int, Int>): List<QuestionModel> {
        val list = mutableListOf<QuestionModel>()

        // Split text by Question markers: e.g., \n1. or \nQ1. or \nQuestion 1
        // Lookahead pattern matching question boundaries
        val qSplitRegex = Regex("""(?m)(?=^(?:Q(?:uestion)?\.?\s*\d{1,3}[\.\)\:\-]?|\d{1,3}[\.\)\-]))""")
        val blocks = text.split(qSplitRegex).filter { it.isNotBlank() }

        var currentSubject = "Physics"

        for (block in blocks) {
            val trimmed = block.trim()
            if (trimmed.length < 10) continue

            // Detect subject headers inside block
            val upper = trimmed.uppercase()
            when {
                upper.contains("PHYSICS") -> currentSubject = "Physics"
                upper.contains("CHEMISTRY") -> currentSubject = "Chemistry"
                upper.contains("BOTANY") -> currentSubject = "Botany"
                upper.contains("ZOOLOGY") -> currentSubject = "Zoology"
                upper.contains("BIOLOGY") -> currentSubject = "Botany"
            }

            // Extract question number
            val numMatch = Regex("""^(?:Q(?:uestion)?\.?\s*)?(\d{1,3})[\.\)\:\-]?""").find(trimmed)
            val qNum = numMatch?.groupValues?.get(1)?.toIntOrNull() ?: (list.size + 1)

            // Auto-detect subject by NEET question number range if default
            val detectedSubject = autoDetectSubject(qNum, currentSubject)

            // Extract options (1), (2), (3), (4)
            val parsed = extractQuestionAndOptions(trimmed, qNum)

            val correctAns = answerKeyMap[qNum] ?: parsed.detectedAnswer ?: 1

            list.add(
                QuestionModel(
                    id = UUID.randomUUID().toString(),
                    number = qNum,
                    text = parsed.questionText,
                    options = parsed.options,
                    correctOption = correctAns,
                    solution = parsed.solution.ifBlank { "Solution for Question $qNum: Option ($correctAns) is the correct answer according to NEET guidelines." },
                    subject = detectedSubject
                )
            )
        }

        // Sort by question number
        return list.sortedBy { it.number }
    }

    data class ParsedQuestionData(
        val questionText: String,
        val options: List<String>,
        val detectedAnswer: Int?,
        val solution: String
    )

    private fun extractQuestionAndOptions(rawBlock: String, qNum: Int): ParsedQuestionData {
        var cleanBlock = rawBlock.replace(Regex("""^(?:Q(?:uestion)?\.?\s*)?\d{1,3}[\.\)\:\-]?\s*"""), "").trim()

        var inlineAns: Int? = null
        var solutionText = ""

        // Check for inline Answer: or Ans:
        val ansRegex = Regex("""(?:Ans(?:wer)?|Correct\s*Option)\s*[:=\-]\s*[\(\[]?\s*([1-4]|[A-Da-d])\s*[\)\]]?""", RegexOption.IGNORE_CASE)
        val ansMatch = ansRegex.find(cleanBlock)
        if (ansMatch != null) {
            val ansChar = ansMatch.groupValues[1].uppercase()
            inlineAns = when (ansChar) {
                "1", "A" -> 1
                "2", "B" -> 2
                "3", "C" -> 3
                "4", "D" -> 4
                else -> 1
            }
            cleanBlock = cleanBlock.replace(ansMatch.value, "").trim()
        }

        // Check for inline Solution: or Explanation:
        val solRegex = Regex("""(?:Solution|Explanation|Hint)\s*[:=\-](.*)$""", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
        val solMatch = solRegex.find(cleanBlock)
        if (solMatch != null) {
            solutionText = solMatch.groupValues[1].trim()
            cleanBlock = cleanBlock.substring(0, solMatch.range.first).trim()
        }

        // Extract options: pattern 1, 2, 3, 4 (or A, B, C, D)
        // Check standard NEET format: (1) ... (2) ... (3) ... (4) ...
        val options = mutableListOf<String>()

        val optionRegex1 = Regex("""(?:\(1\)|1\.)\s*(.*?)(?:\(2\)|2\.)\s*(.*?)(?:\(3\)|3\.)\s*(.*?)(?:\(4\)|4\.)\s*(.*)""", RegexOption.DOT_MATCHES_ALL)
        val m1 = optionRegex1.find(cleanBlock)

        if (m1 != null) {
            val qText = cleanBlock.substring(0, m1.range.first).trim()
            options.add(m1.groupValues[1].trim())
            options.add(m1.groupValues[2].trim())
            options.add(m1.groupValues[3].trim())
            options.add(m1.groupValues[4].trim())
            return ParsedQuestionData(qText.ifBlank { "Question $qNum" }, options, inlineAns, solutionText)
        }

        // Alternative check: (A) ... (B) ... (C) ... (D) ...
        val optionRegexA = Regex("""(?:\(A\)|A\.)\s*(.*?)(?:\(B\)|B\.)\s*(.*?)(?:\(C\)|C\.)\s*(.*?)(?:\(D\)|D\.)\s*(.*)""", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
        val mA = optionRegexA.find(cleanBlock)
        if (mA != null) {
            val qText = cleanBlock.substring(0, mA.range.first).trim()
            options.add(mA.groupValues[1].trim())
            options.add(mA.groupValues[2].trim())
            options.add(mA.groupValues[3].trim())
            options.add(mA.groupValues[4].trim())
            return ParsedQuestionData(qText.ifBlank { "Question $qNum" }, options, inlineAns, solutionText)
        }

        // Fallback: split by lines and look for 4 lines or generate options
        val lines = cleanBlock.lines().map { it.trim() }.filter { it.isNotBlank() }
        if (lines.size >= 5) {
            val qText = lines.take(lines.size - 4).joinToString("\n")
            val rawOpts = lines.takeLast(4)
            val cleanedOpts = rawOpts.mapIndexed { idx, opt ->
                opt.replace(Regex("""^(?:\(${idx + 1}\)|${idx + 1}[\.\)]|\([A-Da-d]\)|[A-Da-d][\.\)])\s*"""), "").trim()
            }
            return ParsedQuestionData(qText, cleanedOpts, inlineAns, solutionText)
        }

        // Default placeholder options if PDF has non-standard layout
        val fallbackOpts = listOf(
            "Option 1",
            "Option 2",
            "Option 3",
            "Option 4"
        )
        return ParsedQuestionData(cleanBlock.ifBlank { "Question $qNum text" }, fallbackOpts, inlineAns, solutionText)
    }

    private fun fallbackLineParser(text: String, answerKeyMap: Map<Int, Int>): List<QuestionModel> {
        val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
        val questions = mutableListOf<QuestionModel>()
        var counter = 1

        for (i in lines.indices step 5) {
            if (i >= lines.size) break
            val qText = lines[i]
            val opts = mutableListOf<String>()
            for (j in 1..4) {
                if (i + j < lines.size) {
                    opts.add(lines[i + j].replace(Regex("""^\([1-4]\)\s*"""), ""))
                } else {
                    opts.add("Option $j")
                }
            }
            val correct = answerKeyMap[counter] ?: 1
            questions.add(
                QuestionModel(
                    id = UUID.randomUUID().toString(),
                    number = counter,
                    text = qText,
                    options = opts,
                    correctOption = correct,
                    solution = "Detailed explanation for Question $counter.",
                    subject = autoDetectSubject(counter, "Physics")
                )
            )
            counter++
            if (counter > 180) break
        }
        return questions
    }

    fun autoDetectSubject(qNum: Int, currentHeader: String): String {
        return when {
            qNum in 1..45 -> "Physics"
            qNum in 46..90 -> "Chemistry"
            qNum in 91..135 -> "Botany"
            qNum in 136..180 -> "Zoology"
            else -> currentHeader
        }
    }
}
