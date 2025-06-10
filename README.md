Berikut adalah README yang dirancang secara profesional dan terstruktur untuk aplikasi SavEat:


SavEat 🍽️

Aplikasi Mobile untuk Mengelola Bahan Makanan & Menemukan Resep Cerdas

📱 Jenis Aplikasi

SavEat adalah aplikasi mobile berbasis Android yang dirancang untuk membantu pengguna dalam mengelola stok bahan makanan, mengurangi pemborosan, serta menemukan resep-resep berdasarkan bahan yang tersedia di rumah.


 🚀 Teknologi yang Digunakan

* Bahasa Pemrograman: Java
* IDE: Android Studio
* Database: SQLite (penyimpanan lokal)
* API:

  * [TheMealDB API](https://www.themealdb.com/) – Untuk data resep masakan
  * Google Gemini AI – Untuk fitur asisten AI cerdas melalui chat
* Plugin Gradle:

  * `com.android.application` version 8.1.0
  * `org.jetbrains.kotlin.android` version 1.8.10 *(opsional untuk dependensi Kotlin)*



🎯 Tujuan & Fungsi Utama

SavEat bertujuan untuk:

* Membantu pengguna mengelola stok bahan makanan.
* Memberikan pengingat masa kedaluwarsa untuk bahan makanan.
* Menyediakan rekomendasi resep berdasarkan bahan yang dimiliki pengguna.
* Mengurangi pemborosan makanan di rumah tangga.


 ✨ Fitur-fitur Utama

1. Manajemen Stok Bahan

   * Tambah, edit, dan hapus bahan makanan.
   * Simpan informasi: nama, jumlah, satuan, kategori, tanggal kedaluwarsa, dan gambar bahan.

2. Detail Bahan

   * Lihat detail bahan termasuk informasi lengkap dan gambar.

3. Pencarian Resep

   * Cari resep berdasarkan bahan makanan yang dimiliki pengguna.

4. Rekomendasi Resep Populer

   * Tampilkan resep trending dari API TheMealDB.

5. Resep Favorit

   * Simpan resep ke daftar favorit untuk akses cepat.

6. Asisten AI (Chatbot)

   * Gunakan fitur AI chat untuk tips memasak dan rekomendasi resep menggunakan Google Gemini AI.

7. Notifikasi Kadaluwarsa

   * Notifikasi otomatis saat bahan mendekati tanggal kedaluwarsa.

8. Manajemen Profil

   * Edit informasi profil: nama, email, gender, dan foto profil.

9. Riwayat Bahan

   * Lihat log penggunaan bahan makanan.


📋 Cara Menjalankan Aplikasi

1. Buka Aplikasi SavEat melalui Android Studio atau instal langsung di perangkat Android.
2. Login atau Daftar Akun:

   * Pengguna baru dapat mendaftar (Sign Up).
   * Pengguna lama dapat masuk (Sign In).
3. Setelah login, pengguna akan diarahkan ke halaman Home yang menampilkan resep populer.
4. Gunakan menu navigasi bawah untuk mengakses fitur:

   * Home: Resep-resep pilihan dan populer.
   * Stok: Lihat dan kelola bahan makanan.
   * AI Chat: Berinteraksi dengan asisten pintar.
   * Profil: Kelola akun, lihat riwayat bahan, dan logout.
5. Pada halaman Stok, klik tombol **+** untuk menambahkan bahan baru, atau klik bahan yang ada untuk mengedit atau menghapus.
6. Klik resep pada halaman utama untuk melihat detail resep lengkap, termasuk instruksi memasak dan video tutorial dari YouTube (jika tersedia).


## 🛠️ Developer Notes

* Pastikan perangkat memiliki koneksi internet untuk menggunakan fitur API (Resep dan AI Chat).
* SQLite digunakan untuk menyimpan data bahan secara lokal di perangkat.
* Fitur AI Chat menggunakan Google Gemini API, pastikan key/akses diatur dengan benar.


📌 Lisensi

SavEat adalah proyek tugas akademik yang dikembangkan untuk keperluan edukasi dan non-komersial.


