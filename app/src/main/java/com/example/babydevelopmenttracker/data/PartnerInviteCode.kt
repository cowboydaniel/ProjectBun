package com.example.babydevelopmenttracker.data

import java.security.MessageDigest

private const val CODE_PREFIX = "BUN"
private const val CODE_OFFSET = 2_000_000L

object PartnerInviteCode {
    fun generate(dueDateEpochDay: Long): String {
        val normalized = dueDateEpochDay + CODE_OFFSET
        val payload = normalized.toString(36).uppercase()
        val checkValue = checksum(payload)
        val checkChar = checkValue.toString(36).uppercase()[0]
        val rawCode = CODE_PREFIX + payload + checkChar
        return rawCode.chunked(4).joinToString(separator = "-")
    }

    fun parse(input: String): Long? {
        val normalized = normalizedCode(input) ?: return null
        val payload = normalized.substring(CODE_PREFIX.length, normalized.length - 1)
        val normalizedValue = runCatching { payload.toLong(36) }.getOrNull() ?: return null
        val epochDay = normalizedValue - CODE_OFFSET
        return if (epochDay >= 0) epochDay else null
    }

    fun familyIdForInviteCode(input: String): String? {
        val normalized = normalizedCode(input) ?: return null
        return normalized.sha256()
    }

    private fun normalizedCode(input: String): String? {
        if (input.isBlank()) return null
        val cleaned = buildString {
            input.uppercase().forEach { char ->
                if (char.isLetterOrDigit()) append(char)
            }
        }
        if (!cleaned.startsWith(CODE_PREFIX) || cleaned.length <= CODE_PREFIX.length + 1) {
            return null
        }
        val payload = cleaned.substring(CODE_PREFIX.length, cleaned.length - 1)
        val providedCheck = cleaned.last()
        val expectedCheck = checksum(payload).toString(36).uppercase()[0]
        if (providedCheck != expectedCheck) {
            return null
        }
        return cleaned
    }

    private fun checksum(payload: String): Int {
        return payload.fold(0) { acc, char ->
            (acc + char.code) % 36
        }
    }

    private fun String.sha256(): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(toByteArray(Charsets.UTF_8))
        return digest.joinToString(separator = "") { byte ->
            "%02x".format(byte)
        }
    }
}
