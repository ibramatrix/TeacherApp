# 📚 Teacher App  

![Made with Kotlin](https://img.shields.io/badge/Made%20with-Kotlin-7F52FF?logo=kotlin&logoColor=white)  
![Firebase](https://img.shields.io/badge/Backend-Firebase-FFCA28?logo=firebase&logoColor=black)  
![Android Studio](https://img.shields.io/badge/IDE-Android%20Studio-3DDC84?logo=androidstudio&logoColor=white)  
![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)  

A mobile application built with **Android Studio (Kotlin)** that allows teachers to manage and share academic information with students in a simple and efficient way.  

---

## ✨ Features  

- 🔑 **Teacher Login**  
  Secure login using **Firebase Realtime Database**.  

- 📝 **Student Marks Management**  
  Teachers can upload marks for:  
  - Assignments  
  - Tests  
  - Exams  

- 📖 **Digital Diary**  
  - Upload daily notes for students  
  - Share images and important announcements  

---

## 🛠️ Tech Stack  

- **Language:** Kotlin  
- **IDE:** Android Studio  
- **Backend:** Firebase Realtime Database  
- **Storage:** Firebase Storage (for images in diary)  

---

## 🚀 Getting Started  

### Prerequisites  
- Android Studio installed  
- Firebase project set up  

### Setup Instructions  
1. Clone the repository:  
   ```bash
   git clone https://github.com/your-username/teacher-app.git
   cd teacher-app
2. Open the project in Android Studio.

3. Add your Firebase configuration:

     ◦ Place your google-services.json file inside app/ folder.

     ◦ Add API keys (if required) in local.properties (⚠️ Do not commit keys).

4. Sync the Gradle files and run the app on an emulator or physical device.

---

## 📂 Project Structure


app/
 ├── java/com/example/teacherapp/
 │    ├── activities/        # UI screens
 │    ├── adapters/          # RecyclerView adapters
 │    ├── models/            # Data models
 │    └── utils/             # Helper classes
 ├── res/
 │    ├── layout/            # XML layouts
 │    └── values/            # Styles, colors, strings
 └── google-services.json    # (Ignored from Git)
