package com.namaz.vakit

import android.app.*
import android.content.*
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build

object Sch {
    const val CH = "vakit"
    const val CN = "sayac"
    fun hm(t: Long): String = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(t))
    fun channels(c: Context) {
        val nm = c.getSystemService(NotificationManager::class.java)
        val ch = NotificationChannel(CH, "Namaz vakti alarmı", NotificationManager.IMPORTANCE_HIGH)
        ch.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_SONIC).build())
        ch.enableVibration(true)
        nm.createNotificationChannel(ch)
        nm.createNotificationChannel(NotificationChannel(CN, "Sıradaki vakit sayacı", NotificationManager.IMPORTANCE_LOW))
    }
    fun upcoming(c: Context): List<Pair<Long, Int>> {
        val (_, la, ln) = Prefs.place(c)
        val now = System.currentTimeMillis()
        val l = ArrayList<Pair<Long, Int>>()
        for (o in 0L..2L) {
            val t = Calc.times(la, ln, o)
            for (i in 0..5) if (t[i] > now) l.add(Pair(t[i], i))
        }
        return l
    }
    fun scheduleAll(c: Context) {
        channels(c)
        val am = c.getSystemService(AlarmManager::class.java)
        for ((n, p) in upcoming(c).take(12).withIndex()) {
            val i = Intent(c, AlarmReceiver::class.java).putExtra("i", p.second).putExtra("t", p.first)
            val pi = PendingIntent.getBroadcast(c, n, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            try {
                if (Build.VERSION.SDK_INT < 31 || am.canScheduleExactAlarms()) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, p.first, pi)
                else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, p.first, pi)
            } catch (e: Exception) {}
        }
        countdown(c)
    }
    fun countdown(c: Context) {
        val n = upcoming(c).firstOrNull() ?: return
        val pi = PendingIntent.getActivity(c, 99, Intent(c, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        val nt = Notification.Builder(c, CN).setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("${EMO[n.second]} ${NAMES[n.second]} vaktine kalan süre")
            .setContentText(Prefs.place(c).first + " · " + hm(n.first))
            .setWhen(n.first).setUsesChronometer(true).setChronometerCountDown(true).setShowWhen(true)
            .setOngoing(true).setContentIntent(pi).build()
        c.getSystemService(NotificationManager::class.java).notify(1, nt)
    }
}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(c: Context, i: Intent) {
        val k = i.getIntExtra("i", -1)
        if (k >= 0) {
            Sch.channels(c)
            val nx = Sch.upcoming(c).firstOrNull()
            val msg = if (nx != null) {
                val m = (nx.first - System.currentTimeMillis()) / 60000
                "Sıradaki: ${NAMES[nx.second]} ${Sch.hm(nx.first)} · ${m / 60} sa ${m % 60} dk kaldı"
            } else ""
            val pi = PendingIntent.getActivity(c, 98, Intent(c, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
            val nt = Notification.Builder(c, Sch.CH).setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("${EMO[k]} ${NAMES[k]} vakti girdi · ${Sch.hm(i.getLongExtra("t", 0L))}")
                .setContentText(msg).setStyle(Notification.BigTextStyle().bigText(msg))
                .setCategory(Notification.CATEGORY_ALARM).setAutoCancel(true).setContentIntent(pi).build()
            c.getSystemService(NotificationManager::class.java).notify(100 + k, nt)
        }
        Sch.scheduleAll(c)
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(c: Context, i: Intent) { Sch.scheduleAll(c) }
}
