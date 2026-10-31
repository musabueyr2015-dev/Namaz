package com.namaz.vakit

import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.*

class MainActivity : Activity() {
    val h = Handler(Looper.getMainLooper())
    lateinit var pl: TextView
    lateinit var nm: TextView
    lateinit var cd: TextView
    lateinit var sc: ScrollView
    val rows = ArrayList<LinearLayout>()
    val SKY = arrayOf(intArrayOf(0xFF0B1030.toInt(), 0xFF1D2A6B.toInt()), intArrayOf(0xFF3B3A7A.toInt(), 0xFFF2A07A.toInt()), intArrayOf(0xFF2A8FD8.toInt(), 0xFF9FDCF5.toInt()), intArrayOf(0xFF4A2C6B.toInt(), 0xFFF0724F.toInt()))
    fun dp(x: Int) = (x * resources.displayMetrics.density).toInt()
    fun bg(c: Int) = GradientDrawable().apply { setColor(c); cornerRadius = dp(18).toFloat() }
    fun tv(s: Float, b: Boolean = false) = TextView(this).apply {
        textSize = s; setTextColor(Color.WHITE); gravity = Gravity.CENTER
        if (b) setTypeface(typeface, Typeface.BOLD)
    }

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        if (Build.VERSION.SDK_INT >= 33) requestPermissions(arrayOf("android.permission.POST_NOTIFICATIONS"), 1)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(16), dp(48), dp(16), dp(24)) }
        sc = ScrollView(this).apply { addView(root) }
        pl = tv(14f); nm = tv(22f, true); cd = tv(54f, true)
        root.addView(tv(44f).apply { text = "🕌" })
        root.addView(pl); root.addView(nm); root.addView(cd)
        for (i in 0..5) {
            val r = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(dp(16), dp(14), dp(16), dp(14)) }
            r.addView(tv(19f).apply { text = EMO[i] + "  " + NAMES[i]; gravity = Gravity.START }, LinearLayout.LayoutParams(0, -2, 1f))
            r.addView(tv(19f, true))
            val lp = LinearLayout.LayoutParams(-1, -2); lp.topMargin = dp(8)
            root.addView(r, lp); rows.add(r)
        }
        val sp = Spinner(this)
        sp.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, PL.map { it.n })
        sp.setSelection(maxOf(0, PL.indexOfFirst { it.n == Prefs.place(this).first }), false)
        var first = true
        sp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(a: AdapterView<*>?, v: View?, i: Int, id: Long) {
                if (first) { first = false; return }
                val p = PL[i]
                Prefs.save(this@MainActivity, p.n, p.la, p.ln)
                Sch.scheduleAll(this@MainActivity); ref()
            }
            override fun onNothingSelected(a: AdapterView<*>?) {}
        }
        val lp2 = LinearLayout.LayoutParams(-1, -2); lp2.topMargin = dp(20)
        root.addView(sp, lp2)
        root.addView(Button(this).apply { text = "📍 Konumumu kullan"; setOnClickListener { loc() } }, lp2)
        setContentView(sc)
    }

    val tick = object : Runnable { override fun run() { ref(); h.postDelayed(this, 1000) } }
    override fun onResume() { super.onResume(); Sch.scheduleAll(this); h.post(tick) }
    override fun onPause() { super.onPause(); h.removeCallbacks(tick) }

    fun ref() {
        val (n, la, ln) = Prefs.place(this)
        val now = System.currentTimeMillis()
        val t = Calc.times(la, ln, 0)
        val nx = Sch.upcoming(this).firstOrNull() ?: return
        pl.text = "📍 $n"
        nm.text = "${EMO[nx.second]} ${NAMES[nx.second]} vaktine"
        val s = maxOf(0L, (nx.first - now) / 1000)
        cd.text = String.format("%02d:%02d:%02d", s / 3600, s % 3600 / 60, s % 60)
        for (i in 0..5) {
            val r = rows[i]
            (r.getChildAt(1) as TextView).text = Sch.hm(t[i])
            r.background = bg(if (i == nx.second) COL[i] else (COL[i] and 0xFFFFFF) or 0x66000000)
        }
        val ph = if (now < t[0] || now >= t[5]) 0 else if (now < t[1]) 1 else if (now < t[4]) 2 else 3
        sc.background = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, SKY[ph])
    }

    override fun onRequestPermissionsResult(c: Int, p: Array<out String>, r: IntArray) {
        super.onRequestPermissionsResult(c, p, r)
        if (c == 2 && r.isNotEmpty() && r[0] == PackageManager.PERMISSION_GRANTED) loc()
    }
}
