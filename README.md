# 🐾 PetMate

PetMate is an Android mobile application built using Kotlin. The app helps pet owners manage their pets, track activities, and connect with other pet lovers.

The application follows modern Android development practices including MVVM architecture, Jetpack Compose, and Firebase integration.

---

## 📸 Screenshots

### Home Screen
![Home Screen](https://drive.google.com/uc?export=view&id=19CcxU5okNIoHY8auA5HoKJQkSV-iwxII)

---

## 📱 Features

- Add and manage multiple pets
- Track pet activities (walking, feeding, vet visits, etc.)
- Maintain pet care records
- Activity reminders and notifications
- Connect with other pet lovers
- Secure authentication using Firebase
- Modern Material Design UI
- Built with Jetpack Compose

---

## 🛠️ Tech Stack

- **Language:** Kotlin
- **Architecture:** MVVM
- **UI Toolkit:** Jetpack Compose
- **Backend & Database:** Firebase (Authentication & Firestore)
- **Design System:** Material Design
- **Async Handling:** Coroutines & Flow

---

## 📂 Project Structure

```
com.yourpackage.petmate
│
├── data            # Data sources, models, repositories
├── ui              # Screens and Compose UI components
├── viewmodel       # ViewModels (MVVM)
├── utils           # Utility classes
└── di              # Dependency injection (if used)
```

---

## 🚀 Getting Started

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/Chamalka20/PetMate
```

### 2️⃣ Open in Android Studio

- Open Android Studio
- Select **Open an Existing Project**
- Choose the cloned folder

### 3️⃣ Firebase Setup

1. Go to Firebase Console
2. Create a new project
3. Add an Android app
4. Download `google-services.json`
5. Place it inside the `app/` directory
6. Enable Authentication and Cloud Firestore

## ☁️ Cloudinary Setup (Image Upload)

PetMate uses **Cloudinary** to upload and manage pet images.

### 1️⃣ Create Cloudinary Account

1. Go to https://cloudinary.com
2. Create a free account
3. Open the **Dashboard**
4. Copy the following credentials:

- Cloud Name
- API Key
- API Secret

### 2️⃣ Add Credentials to `local.properties`

Open the `local.properties` file in the project root and add:

```properties
cloudinary.cloud.name=YOUR_CLOUD_NAME
cloudinary.api.key=YOUR_API_KEY
cloudinary.api.secret=YOUR_API_SECRET
```

Example:

```properties
cloudinary.cloud.name=petmatecloud
cloudinary.api.key=123456789012345
cloudinary.api.secret=abc123xyz456secret
```

⚠️ `local.properties` is ignored by Git, so your credentials stay secure.

---

### 4️⃣ Run the App

Connect a device or emulator and press **Run ▶**

---

## 🧱 Architecture Overview

PetMate follows the **MVVM (Model-View-ViewModel)** architecture:

- **Model** → Handles data and business logic
- **View** → Jetpack Compose UI
- **ViewModel** → Manages UI-related data and state

This ensures clean separation of concerns, scalability, and maintainability.

---

## 🤝 Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License.



