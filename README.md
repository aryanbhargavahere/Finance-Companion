# 📟 Finance Companion

A professional, minimalist, and privacy-first **Money Management** application built for Android. This app is designed to give you full control over your finances with modern UI and a offline architecture.

---

## 📑 Table of Contents
1. ✨ [Features](#-features)
2. 🛠️ [Tech Stack](#-tech-stack)
3. 📂 [Project Structure](#-project-structure)
4. 🌐 [API Used](#-api-used)
5. 🚀 [Getting Started](#-getting-started)
    - 📋 [Prerequisites](#prerequisites)
    - ▶️ [Steps to Run](#steps-to-run)
6. 🧠 [How the App Works](#-how-the-app-works)
7. 🔮 [Future Enhancements](#-future-enhancements)
8. 👤 [Author](#author)

---

## ✨ Features

- 📊 Money Dashboard: View your total balance, monthly income, and expenses in a high-contrast "Midnight" card.
- 🎯 Savings Goals: Set a monthly target and track your progress with dynamic progress bars.
- 💸 Quick Actions: One-tap buttons to add transactions, transfer to savings, or view insights.
- 📜 Transaction History: A clean, scrollable list of your recent financial activities.
- 🎨 Customization: Toggle between Light and Dark modes and change your primary currency (USD, EUR, GBP, INR, JPY).
- 👤 Profile Management: Update your personal info, including name, country, and language.

---

## 🛠️ Tech Stack

- **Kotlin**
- **Android SDK**
- **Jetpack Compose**
- **RoomDB and SQL Lite**
- **Biometric PromptAPI**

---

## 🏗️ Architecture
The app follows the **MVVM (Model-View-ViewModel)** design pattern:
- **UI Layer**: Built entirely with Jetpack Compose for a reactive and smooth user experience.
- **Domain Layer**: Uses a ViewModel to handle business logic and state management.
- **Data Layer**: Leverages RoomDB for user transactions and SharedPreferences for user settings.
---

## 🛡️ Security & Privacy
- **Local Database**: All financial records are stored in a local SQLite/Room database.All the data stays on the local device
- **Biometric Authentication**:Integrated fingerprint unlock to prevent unauthorized access.
- **Hide Balance**: A "Privacy Mode" that masks all balance amounts with asterisks (****) on the home screen.
- **Hard Reset**: A dedicated option to permanently wipe all local data instantly

## 📂 Project Structure

```bash
app/
 └── src/main/java/com/example/weatherchecker/
     ├── MainActivity.kt              # Main UI and logic
     ├── WeatherServiceapi.kt         # Retrofit API interface
     ├── utild/
     │   └── Constants.kt             # API constants & network check
     ├── model/
     │   ├── WeatherResponse.kt
     │   ├── Weather.kt
     │   ├── Wind.kt
     │   ├── Clouds.kt
     │   ├── Coord.kt
     │   └── Sys.kt
 └── src/main/res/
     ├── layout/
     │   └── activity_main.xml
     ├── drawable/
     │   └── bg_info_card.xml
     └── values/
```
---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (latest version recommended)
- **Android SDK 24** or above
- **Internet connection**

### Steps to Run
1. Download or clone the repository
2. Open the project in **Android Studio**
3. Add your **Weather API key**
4. Sync **Gradle**
5. Run the app on an **emulator** or **physical device**

---
## 🧠 How the App Works

1. User enters a **city name** or uses **location services**
2. App sends a request to the **weather API**
3. API responds with **JSON data**
4. Data is **parsed** and displayed on the **UI**

---
## 🔮 Future Enhancements

- 📅 **7-day weather forecast**
- 🌙 **Dark mode**
- 📍 **Auto-detect current location**
- 🌬️ **Wind speed & pressure details**
- 🗺️ **Map-based weather view**
---
## Author
- **Name:** Aryan
---
