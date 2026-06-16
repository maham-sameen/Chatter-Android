<h1 align="center">💬 Chatter</h1>

<p align="center">
  <b>A real-time chat app — built end-to-end.</b><br/>
  1-to-1 & group chat · Voice & video calls · Works in the background
</p>

<p align="center">
  Jetpack Compose · Koin · Room · Ktor WebSockets · Firebase · Stream Video SDK
</p>

<p align="center">
  <img alt="Platform" src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white"/>
  <img alt="Kotlin"   src="https://img.shields.io/badge/Kotlin-2.x-7F52FF?logo=kotlin&logoColor=white"/>
  <img alt="Compose"  src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white"/>
  <img alt="minSdk"   src="https://img.shields.io/badge/minSdk-24-blue"/>
  <img alt="Arch"     src="https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM-success"/>
</p>

---

## 🎬 Live Demo

> Watching is faster than reading. Tap the thumbnail 👇

[![Watch the Chatter demo on YouTube](https://img.youtube.com/vi/pO29lKkpbnY/maxresdefault.jpg)](https://youtu.be/pO29lKkpbnY?si=LCaARmWErFaKHm7n)

---

## ✨ What is Chatter?

Chatter is a modern, real-time messaging app I designed and built **end-to-end** — both the Android client *and* the Ktor + MongoDB backend. Authentication, sockets, presence, push, voice + video calls — every layer is mine. This repo holds the Android client; the server lives here → **[Chatter-Server »](https://github.com/maham-sameen/Chatter-Server)**

---

## 🚀 Main Features

### 🔐 1. Custom JWT authentication with 2FA
- Email verification + password — true two-factor auth
- Server (Ktor) issues JWTs; the client stores them encrypted in DataStore
- Passwords stored securely on the server (no plaintext)

### 💬 2. One-to-one **and** group chat (1-to-many)
- Both modes ride the same Ktor WebSocket pipeline — instant delivery, persisted locally with Room
- Group rooms with unlimited members, same UX as DMs

### 📥 3. Read / unread tracking — done properly
Two cases, both handled:
1. Message is **read while the recipient already has the chat open** → marked read immediately via socket
2. Message is **read before/after** the recipient opens the chat → synced on open, counters reconciled

### 🔔 4. FCM push notifications (two paths)
- **Messages** → pushed by our **custom Ktor server** through Firebase Cloud Messaging — sender-trust controlled by us, not a third party
- **Calls** → Stream SDK automatically notifies the callee

### 📞 5. Voice & video calls — 1-to-1 and group
- Powered by the **Stream Video SDK**
- Full ringing + outgoing call screens
- HD video, voice-only mode, group calls

### 🛡 6. Calls and messages **work in the background**
- Incoming calls ring even when the app is closed — ringing screen shows up automatically
- New messages arrive in real time via FCM whether the app is foreground, background, or killed
- Background sync coordinated with **WorkManager**

### 🟢 7. Online / Offline / Last seen / Typing
- Dedicated presence WebSocket
- Live online dot, "typing…" indicator, last-seen timestamps
- Driven by `AppLifecycleObserver` → the server always knows your real state

---

## 🧩 Tech Stack

| Layer            | Tools |
|------------------|-------|
| **UI**           | Jetpack Compose, Material 3, Navigation Compose |
| **Architecture** | Clean Architecture + MVVM (`core/data`, `core/domain`, `core/presentation` + feature modules) |
| **DI**           | Koin (`koin-bom`, `koin-compose-viewmodel`, `koin-androidx-workmanager`) |
| **Networking**   | Ktor Client (CIO), Ktor WebSockets, Content Negotiation, kotlinx.serialization |
| **Local DB**     | Room + Room Paging |
| **Prefs**        | DataStore Preferences (encrypted token storage) |
| **Background**   | WorkManager |
| **Async**        | Kotlin Coroutines + Flow |
| **Firebase**     | Auth, Cloud Messaging (FCM), Storage, Firebase AI |
| **Calls**        | Stream Video SDK (voice + video, 1:1 + group) |
| **Push**         | Stream Android Push (FCM transport) |
| **Lifecycle**    | `ProcessLifecycleOwner` → presence socket |

---

## 🏗 Architecture

Clean Architecture, organized **feature-first** on top of a shared `core`:

```
app/src/main/java/com/example/chatter
├── core
│   ├── di           ← Koin modules (CoreAppModule, CoreViewModelModule)
│   ├── data         ← data sources, mappers, encrypted serializers
│   ├── domain       ← models + use-cases (color, phone validation, time formatting)
│   └── presentation ← shared UI: buttons, text fields, loaders, snackbars
└── features
    ├── auth          ← email + password + JWT, 2FA flow
    ├── chats         ← 1-to-1 and group, message stream, read/unread
    ├── calls         ← Stream voice + video, ringing & outgoing screens
    ├── contacts      ← discovery, add-by-number
    ├── notifications ← FCM + Stream push routing
    └── utils         ← cross-feature helpers
```

The **client speaks to the backend over Ktor WebSockets** — one socket per chat room, one global presence socket. Calls run through the Stream Video SDK in parallel.

---

## 🛠 Run it locally

```bash
git clone https://github.com/maham-sameen/Chatter-Android.git
cd Chatter-Android
```

1. Open in **Android Studio (Koala or newer)**, **JDK 17**.
2. The included `app/google-services.json` is wired to my dev Firebase project — replace it with your own if you want to point at your Firebase.
3. Start the backend → **[Chatter-Server](https://github.com/maham-sameen/Chatter-Server)** (default port `8083`).
4. Set the backend URL in `Constants.kt` if it differs from the default.
5. ▶ Run on a device or emulator (API 24+).

---

## 👩‍💻 Author

**Maham Sameen** — Android developer.
I built Chatter end-to-end because I wanted to understand a real-time product *from the socket up*: from the 2FA handshake to the moment a green dot turns on next to someone's name, from the ringing screen that survives a closed app to the unread badge that reconciles on reconnect. Every line here is mine, and every layer taught me something. If any piece of this is useful to you — borrow it, learn from it, beat it. That's the whole point.

📫 mahamsameenhere@gmail.com

---

<p align="center"><i>Built with ❤️ in Kotlin. If this inspired you, ⭐ the repo.</i></p>
