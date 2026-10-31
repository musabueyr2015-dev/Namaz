package com.namaz.vakit

import android.content.Context

val NAMES = arrayOf("İmsak", "Güneş", "Öğle", "İkindi", "Akşam", "Yatsı")
val EMO = arrayOf("🌌", "🌅", "☀️", "🌤️", "🌇", "🌙")
val COL = longArrayOf(0xFF5B6BD6, 0xFFF2A33A, 0xFFE3B616, 0xFF3D9AD6, 0xFFE0654F, 0xFF7A4FC2).map { it.toInt() }.toIntArray()
class Pl(val n: String, val la: Double, val ln: Double)
val PL = (
    "Ayvacık / Tuzla köyü,39.55,26.17;Adana,37.00,35.32;Adıyaman,37.76,38.28;Afyonkarahisar,38.76,30.54;Ağrı,39.72,43.05;Aksaray,38.37,34.03;Amasya,40.65,35.83;Ankara,39.93,32.86;Antalya,36.90,30.69;Ardahan,41.11,42.70;Artvin,41.18,41.82;Aydın,37.85,27.84;Balıkesir,39.65,27.89;Bartın,41.64,32.34;Batman,37.88,41.13;Bayburt,40.26,40.23;Bilecik,40.14,29.98;Bingöl,38.88,40.50;Bitlis,38.40,42.11;Bolu,40.74,31.61;" +
    "Burdur,37.72,30.29;Bursa,40.19,29.06;Çanakkale,40.15,26.41;Çankırı,40.60,33.62;Çorum,40.55,34.95;Denizli,37.78,29.09;Diyarbakır,37.91,40.24;Düzce,40.84,31.16;Edirne,41.68,26.56;Elazığ,38.67,39.22;Erzincan,39.75,39.49;Erzurum,39.90,41.27;Eskişehir,39.78,30.52;Gaziantep,37.07,37.38;Giresun,40.91,38.39;Gümüşhane,40.46,39.48;Hakkari,37.58,43.74;Hatay,36.40,36.35;Iğdır,39.92,44.04;Isparta,37.76,30.55;" +
    "İstanbul,41.01,28.98;İzmir,38.42,27.14;Kahramanmaraş,37.58,36.94;Karabük,41.20,32.63;Karaman,37.18,33.22;Kars,40.60,43.10;Kastamonu,41.38,33.78;Kayseri,38.72,35.49;Kilis,36.72,37.12;Kırıkkale,39.85,33.52;Kırklareli,41.74,27.23;Kırşehir,39.15,34.16;Kocaeli,40.77,29.92;Konya,37.87,32.48;Kütahya,39.42,29.98;Malatya,38.35,38.31;Manisa,38.61,27.43;Mardin,37.31,40.74;Mersin,36.80,34.64;Muğla,37.22,28.36;" +
    "Muş,38.74,41.49;Nevşehir,38.62,34.71;Niğde,37.97,34.68;Ordu,40.98,37.88;Osmaniye,37.07,36.25;Rize,41.02,40.52;Sakarya,40.76,30.40;Samsun,41.29,36.33;Siirt,37.93,41.94;Sinop,42.03,35.15;Sivas,39.75,37.02;Şanlıurfa,37.16,38.79;Şırnak,37.52,42.46;Tekirdağ,40.98,27.51;Tokat,40.31,36.55;Trabzon,41.00,39.72;Tunceli,39.11,39.55;Uşak,38.68,29.41;Van,38.49,43.38;Yalova,40.65,29.27;" +
    "Yozgat,39.82,34.81;Zonguldak,41.46,31.80;Çanakkale / Ayvacık,39.60,26.40;Çanakkale / Bayramiç,39.81,26.61;Çanakkale / Biga,40.23,27.24;Çanakkale / Bozcaada,39.84,26.07;Çanakkale / Çan,40.03,27.05;Çanakkale / Eceabat,40.19,26.36;Çanakkale / Ezine,39.79,26.34;Çanakkale / Gelibolu,40.41,26.67;Çanakkale / Gökçeada,40.17,25.89;Çanakkale / Lapseki,40.34,26.69;Çanakkale / Yenice,39.93,27.26;Mekke,21.42,39.83;Medine,24.47,39.61"
).split(";").map { val a = it.split(","); Pl(a[0], a[1].toDouble(), a[2].toDouble()) }

object Prefs {
    fun sp(c: Context) = c.getSharedPreferences("p", 0)
    fun place(c: Context): Triple<String, Double, Double> {
        val s = sp(c)
        return Triple(s.getString("n", PL[0].n)!!, s.getString("la", "39.55")!!.toDouble(), s.getString("ln", "26.17")!!.toDouble())
    }
    fun save(c: Context, n: String, la: Double, ln: Double) {
        sp(c).edit().putString("n", n).putString("la", la.toString()).putString("ln", ln.toString()).apply()
    }
}
