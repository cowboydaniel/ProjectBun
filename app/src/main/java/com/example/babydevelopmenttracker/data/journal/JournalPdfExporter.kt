package com.example.babydevelopmenttracker.data.journal

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class JournalPdfExporter(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend fun export(
        entries: List<JournalEntry>,
        zoneId: ZoneId = ZoneId.systemDefault(),
        fileName: String = defaultFileName(),
    ): File {
        require(entries.isNotEmpty()) { "Cannot export an empty journal" }
        return withContext(dispatcher) {
            val pdfDocument = PdfDocument()
            val titlePaint = Paint().apply {
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = 18f
            }
            val headerPaint = Paint().apply {
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = 14f
            }
            val bodyPaint = Paint().apply {
                textSize = 12f
            }

            val pageWidth = 595
            val pageHeight = 842
            val horizontalPadding = 48f
            val verticalPadding = 48f
            val maxTextWidth = pageWidth - (horizontalPadding * 2)
            val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.getDefault())
                .withZone(zoneId)

            var pageNumber = 1
            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas = page.canvas
            var cursorY = verticalPadding

            canvas.drawText("Pregnancy journal", horizontalPadding, cursorY, titlePaint)
            cursorY += 32f

            entries.forEachIndexed { index, entry ->
                if (cursorY > pageHeight - verticalPadding - 120f) {
                    pdfDocument.finishPage(page)
                    pageNumber += 1
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    cursorY = verticalPadding
                }

                val timestampText = dateFormatter.format(entry.timestamp)
                canvas.drawText(timestampText, horizontalPadding, cursorY, headerPaint)
                cursorY += 20f

                val moodText = "Mood: ${entry.mood.name.lowercase(Locale.getDefault()).replaceFirstChar { it.titlecase(Locale.getDefault()) }}"
                canvas.drawText(moodText, horizontalPadding, cursorY, bodyPaint)
                cursorY += 18f

                wrapText(entry.body, bodyPaint, maxTextWidth).forEach { line ->
                    if (cursorY > pageHeight - verticalPadding - 60f) {
                        pdfDocument.finishPage(page)
                        pageNumber += 1
                        pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                        page = pdfDocument.startPage(pageInfo)
                        canvas = page.canvas
                        cursorY = verticalPadding
                    }
                    canvas.drawText(line, horizontalPadding, cursorY, bodyPaint)
                    cursorY += 16f
                }

                if (entry.attachments.isNotEmpty()) {
                    val attachmentsLabel = "Attachments:"
                    if (cursorY > pageHeight - verticalPadding - 60f) {
                        pdfDocument.finishPage(page)
                        pageNumber += 1
                        pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                        page = pdfDocument.startPage(pageInfo)
                        canvas = page.canvas
                        cursorY = verticalPadding
                    }
                    canvas.drawText(attachmentsLabel, horizontalPadding, cursorY, bodyPaint)
                    cursorY += 16f
                    entry.attachments.forEach { attachment ->
                        wrapText(attachment, bodyPaint, maxTextWidth).forEach { attachmentLine ->
                            if (cursorY > pageHeight - verticalPadding - 40f) {
                                pdfDocument.finishPage(page)
                                pageNumber += 1
                                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                                page = pdfDocument.startPage(pageInfo)
                                canvas = page.canvas
                                cursorY = verticalPadding
                            }
                            canvas.drawText(attachmentLine, horizontalPadding + 16f, cursorY, bodyPaint)
                            cursorY += 16f
                        }
                    }
                }

                if (index != entries.lastIndex) {
                    cursorY += 24f
                }
            }

            pdfDocument.finishPage(page)

            val outputDir = File(context.cacheDir, "journal")
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }
            val outputFile = File(outputDir, fileName)
            FileOutputStream(outputFile).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            pdfDocument.close()
            outputFile
        }
    }

    private fun defaultFileName(): String =
        "journal_${System.currentTimeMillis()}.pdf"

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        if (text.isBlank()) return listOf("")
        val lines = mutableListOf<String>()
        text.split('\n').forEach { paragraph ->
            val words = paragraph.split(" ")
            val builder = StringBuilder()
            words.forEach { word ->
                if (word.isEmpty()) return@forEach
                val candidate = if (builder.isEmpty()) word else "${builder} $word"
                val candidateWidth = paint.measureText(candidate)
                if (candidateWidth <= maxWidth) {
                    if (builder.isEmpty()) {
                        builder.append(word)
                    } else {
                        builder.append(' ').append(word)
                    }
                } else {
                    if (builder.isNotEmpty()) {
                        lines.add(builder.toString())
                        builder.clear()
                    }
                    if (paint.measureText(word) <= maxWidth) {
                        builder.append(word)
                    } else {
                        splitLongWord(word, paint, maxWidth, lines)
                        builder.clear()
                    }
                }
            }
            if (builder.isNotEmpty()) {
                lines.add(builder.toString())
                builder.clear()
            }
        }
        if (lines.isEmpty()) {
            lines.add("")
        }
        return lines
    }

    private fun splitLongWord(word: String, paint: Paint, maxWidth: Float, lines: MutableList<String>) {
        var index = 0
        while (index < word.length) {
            var endIndex = word.length
            while (endIndex > index) {
                val segment = word.substring(index, endIndex)
                if (paint.measureText(segment) <= maxWidth) {
                    lines.add(segment)
                    index = endIndex
                    break
                }
                endIndex--
            }
            if (endIndex == index) {
                // fallback to avoid infinite loop
                lines.add(word.substring(index, index + 1))
                index += 1
            }
        }
    }
}
