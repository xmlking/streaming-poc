package org.sumo.klogs.util

import org.springframework.http.MediaType.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

// --------------------
// Date/Time extensions
// --------------------

fun LocalDateTime.toRFC3339(): String = ZonedDateTime.of(this, ZoneOffset.UTC) .format(rfc3339Formatter)

private val rfc3339Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")

private fun getOrdinal(n: Int) =
        when {
            n in 11..13 -> "${n}th"
            n % 10 == 1 -> "${n}st"
            n % 10 == 2 -> "${n}nd"
            n % 10 == 3 -> "${n}rd"
            else -> "${n}th"
        }

// ----------------
//  Date Extension methods
// ----------------

fun LocalDate.toStr(format:String = "dd/MM/yyyy") = DateTimeFormatter.ofPattern(format).format(this)
fun String.toLocalDate(format:String = "dd/MM/yyyy") = LocalDate.parse(this, DateTimeFormatter.ofPattern(format))
