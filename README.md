# 📟 Finance Companion

A professional, minimalist, and privacy-first **Money Management** application built for Android. This app is designed to give you full control over your finances with modern UI and a offline architecture.

---

## 📑 Table of Contents
1. ✨ [Features](#-features)
2. 🛠️ [Tech Stack](#-tech-stack)
3. 🏗️ [Architecture](#-architecture)
4. 🛡️ [Security And Privacy](#-security-and-privacy)
5. 📂 [Project Structure](#-project-structure)
6. 🚀 [Getting Started](#-getting-started)
    - 📋 [Prerequisites](#prerequisites)
    - ▶️ [Steps to Run](#steps-to-run)
7. 🧠 [How the App Works](#-how-the-app-works)
8. 📲 [Screenshots](#-screenshots)
9. 👤 [Author](#author)

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
com.example.financecompanion
├──  Authentication          
├──  currency                 
├──  dataModel.model       
├──  HomeScreen               
│   ├── Homescreen.kt          
│   ├── InsightScreen.kt        
│   ├── ProfileScreen.kt        
│   └── TransactionScreen.kt    
├──  ProfileInScreens        
│   ├── AppearanceChange.kt     
│   ├── FinanceCompanionCurrencychange.kt
│   ├── FinanceCompanionNotificationScreen.kt
│   ├── PersonalInfo.kt        
│   └── SecurityandPrivacy.kt   
├──  Room                     
├──  ui.theme               
├──  viewmodels             
└──  MainActivity.kt         
```
---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (latest version recommended)
- **Android SDK 24** or above
- **A device with Biometric capabilities**

### Steps to Run
1. Download or clone the repository
2. Open the project in **Android Studio**
3. Click the Run button to install on your emulator or physical device.

---
## 🧠 How the App Works
Here is a simple breakdown of the app's core operations:

1. **Secure Entry & Authentication** :
   - **Biometric Authentication**: Uses the Android BiometricPrompt API to verify your identity (fingerprint) before granting access to the app.
2. **UI Layer**:
   - **Live Updates**: Built with **Jetpack Compose** so the UI observe's your data. When you add a transaction, the balance updates instantly.
   - **Privacy Masking**:The HomeScreen checks your hide balance setting in real-time. If enabled, it replaces sensitive numbers with (****) before they open the screen.
3. **Local Database**:
   - **Room Database**: All financial records are stored in a local SQLite database.
4. **Data Flow**:
   - You enter a new expense or income.
   - The ViewModel calculates the new totals and updates the internal state.
   - The UI reacts to the data change and shows the new entry in your "Recent Activity" list.

---
## 📲 Screenshots

| <img width="100" height="150" alt="image" src="https://github.com/user-attachments/assets/bc61e81d-f707-481f-8e0e-05db8ac09895" />|
| <img width="100" height="150" alt="image" src="https://github.com/user-attachments/assets/d243962c-58f1-4cea-8004-c01fbc4f778f" />|
| <img width="100" height="150" alt="image" src="https://github.com/user-attachments/assets/efc2844d-5914-490d-b5b7-5d2fa5ac6249" />|
|<img width="100" height="150" alt="image" src="https://github.com/user-attachments/assets/2e5bd656-505c-4eda-bbf2-55bd3accb2e8" />|
| <img width="100" height="150" alt="image" src="https://github.com/user-attachments/assets/0a5526d3-b2a6-46d7-9be3-f4712d24cda4" />|

---
## Author
- **Name:** Aryan
---
