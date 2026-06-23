# ChatGames (Folia Supported)

Plugin Minecraft obrolan interaktif yang menyelenggarakan berbagai macam mini-game langsung di chat server Anda. Versi ini telah dirancang ulang dan dibangun kembali dari awal untuk mendukung server **Paper** dan **Folia** secara penuh.

## Fitur Utama
* **Kompatibilitas Penuh Folia**: Menggunakan scheduler wilayah Folia (`GlobalRegionScheduler` dan `EntityScheduler`) untuk memastikan stabilitas thread server multi-threaded.
* **Paper Brigadier Command API**: Perintah `/chatgames` didaftarkan secara native menggunakan sistem Brigadier Minecraft modern yang memberikan autocomplete (tab completion) dinamis berdasarkan daftar game yang aktif dan izin pemain.
* **8 Mode Permainan Obrolan**:
  1. `unscramble` — Menyusun kata acak.
  2. `unreverse` — Menulis kembali kata yang dibalik.
  3. `reaction` — Menulis kata secepat mungkin.
  4. `fillout` — Melengkapi huruf yang hilang dalam sebuah kata.
  5. `random` — Menulis deretan karakter acak.
  6. `math` — Menyelesaikan soal matematika.
  7. `variable` — Mencari nilai variabel dalam persamaan.
  8. `trivia` — Menjawab pertanyaan umum (tanya jawab).
* **Integrasi PlaceholderAPI**: Mendukung placeholder untuk menampilkan jumlah kemenangan/poin pemain serta daftar papan peringkat (Top 10).
* **Dukungan Kustomisasi Pesan**: Semua pengumuman game, pesan benar/salah, dan waktu habis diterjemahkan sepenuhnya ke dalam bahasa Indonesia.
* **Manajemen Suara Aman**: Dilengkapi dengan penanganan kesalahan sound config otomatis untuk mencegah server crash akibat penggunaan nama suara legacy (seperti `LEVEL_UP`) pada versi Minecraft modern (1.9 ke atas).

---

## Persyaratan Sistem
* **Server**: Paper / Folia (versi `1.21.11-R0.1-SNAPSHOT` atau lebih baru)
* **Java**: JDK 25 (untuk kompilasi) / Java runtime 21+ (untuk server)
* **Ketergantungan Opsional**: PlaceholderAPI (versi `2.12.2` atau lebih baru)

---

## Perintah & Izin (Permissions)

| Perintah | Deskripsi | Izin (Permission) |
| :--- | :--- | :--- |
| `/chatgames` | Menampilkan menu bantuan perintah | `chatgames.help` |
| `/chatgames <game>` | Memulai permainan obrolan tertentu secara instan | `chatgames.start.<game>` / `chatgames.start.*` |
| `/chatgames top` | Menampilkan 10 pemain teratas dengan poin terbanyak | `chatgames.top` |
| `/chatgames toggle` | Mengaktifkan/menonaktifkan pengumuman event bagi diri sendiri | `chatgames.toggle` |
| `/chatgames reload` | Memuat ulang file konfigurasi (`config.yml`, `messages.yml`, dll.) | `chatgames.reload` |

---

## Konfigurasi Utama (`config.yml`)
* `time_minutes`: Jeda waktu (dalam menit) sebelum event game berikutnya dimulai secara otomatis.
* `timeToGuess_seconds`: Batas waktu (dalam detik) bagi pemain untuk menjawab setelah game dimulai.
* `sound`: Suara yang dimainkan ke semua pemain saat game dimulai (Secara default menggunakan `ENTITY_PLAYER_LEVELUP`).

---

## Pengembangan & Kompilasi

Proyek ini menggunakan sistem Gradle untuk manajemen build.

### 1. Kompilasi JAR
Untuk membangun file plugin JAR:
```bash
./gradlew build
```
File JAR hasil build akan berada di `build/libs/ChatGames-1.2.1.jar`.

### 2. Deploy Otomatis
Anda dapat melakukan deploy JAR secara otomatis ke server Minecraft lokal dengan menjalankan:
```bash
./gradlew deploy
```
*(Catatan: Anda dapat menyesuaikan direktori target server Anda pada variabel `pluginsDir` di bagian bawah file [build.gradle.kts](build.gradle.kts)).*
