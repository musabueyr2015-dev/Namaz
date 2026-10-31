package com.namaz.vakit

import java.time.*
import kotlin.math.*

object Calc {
    fun fix(x: Double, a: Double) = ((x % a) + a) % a
    fun times(lat: Double, lng: Double, off: Long): LongArray {
        val day = LocalDate.now().plusDays(off)
        val mid = day.atStartOfDay(ZoneId.systemDefault())
        val tz = mid.offset.totalSeconds / 3600.0
        val r = PI / 180
        val d = day.toEpochDay() + 2440587.5 - lng / 360 - 2451545.0
        val g = fix(357.529 + 0.98560028 * d, 360.0)
        val q = fix(280.459 + 0.98564736 * d, 360.0)
        val l = fix(q + 1.915 * sin(g * r) + 0.02 * sin(2 * g * r), 360.0)
        val e = 23.439 - 3.6e-7 * d
        val ra = fix(atan2(cos(e * r) * sin(l * r), cos(l * r)) / r / 15, 24.0)
        val dec = asin(sin(e * r) * sin(l * r))
        val eqt = q / 15 - ra
        val la = lat * r
        val noon = fix(12 + tz - lng / 15 - eqt, 24.0)
        val k = cos(la) * cos(dec)
        fun ang(a: Double) = acos(((-sin(a * r) - sin(la) * sin(dec)) / k).coerceIn(-1.0, 1.0)) / r / 15
        val aa = atan(1 / (1 + tan(abs(la - dec))))
        val asr = acos(((sin(aa) - sin(la) * sin(dec)) / k).coerceIn(-1.0, 1.0)) / r / 15
        val h = doubleArrayOf(noon - ang(18.0), noon - ang(0.833), noon + 0.03, noon + asr, noon + ang(0.833), noon + ang(17.0))
        val base = mid.toInstant().toEpochMilli()
        return LongArray(6) { base + (h[it] * 60.0).roundToLong() * 60000L }
    }
}
