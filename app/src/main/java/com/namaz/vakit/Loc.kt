package com.namaz.vakit

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast

fun MainActivity.useLoc(l: Location) {
    var b = PL[0]; var bd = 1e9
    for (p in PL) {
        val d = Math.pow(p.la - l.latitude, 2.0) + Math.pow((p.ln - l.longitude) * Math.cos(Math.toRadians(l.latitude)), 2.0)
        if (d < bd) { bd = d; b = p }
    }
    Prefs.save(this, "Konumum (yakın: ${b.n})", l.latitude, l.longitude)
    Sch.scheduleAll(this); ref()
}

fun MainActivity.loc() {
    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 2)
        return
    }
    val lm = getSystemService(LocationManager::class.java)
    var best: Location? = null
    for (p in lm.getProviders(true)) {
        val l = lm.getLastKnownLocation(p)
        if (l != null && (best == null || l.time > best.time)) best = l
    }
    if (best != null) { useLoc(best); return }
    Toast.makeText(this, "Konum aranıyor, açık alanda bekleyin…", Toast.LENGTH_LONG).show()
    for (p in lm.getProviders(true)) lm.requestSingleUpdate(p, object : LocationListener {
        override fun onLocationChanged(l: Location) { useLoc(l) }
        override fun onProviderEnabled(q: String) {}
        override fun onProviderDisabled(q: String) {}
        override fun onStatusChanged(q: String?, s: Int, e: Bundle?) {}
    }, null)
}
