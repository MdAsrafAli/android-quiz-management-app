# Android Quiz Management App

An Android application for managing quizzes built with Kotlin and Firebase. Teachers can create and schedule quizzes; students can follow teachers, take quizzes in real time, and review their results.

## Features

### Teacher
- Create MCQ and Short Answer quizzes with scheduled start times and durations
- View per-quiz leaderboards showing student scores ranked by performance
- Manage quizzes from a grouped teacher dashboard

### Student
- Follow teachers and browse their quizzes grouped by teacher
- Take live quizzes with a countdown timer, question progress bar, and per-question navigation
- View upcoming quizzes (notification screen) with human-readable time remaining
- View currently running quizzes and jump in directly
- Review results after submission — correct/incorrect per question, score, and points

### General
- User authentication (login/signup)
- Firebase Realtime Database for live data sync
- Search/filter quizzes by title
- MCQ and Short Answer tabs throughout the app

## Screenshots

![WhatsApp Image 2025-06-18 at 18 00 47_115fcd43](https://github.com/user-attachments/assets/2897e821-0b9b-48b2-8958-d7d8f323d535)
![WhatsApp Image 2025-06-18 at 18 00 48_13cf8864](https://github.com/user-attachments/assets/ab3ce71b-0442-4e59-a895-282690abd9aa)
![WhatsApp Image 2025-06-18 at 18 00 48_a14a2b73](https://github.com/user-attachments/assets/7d701e55-4518-48cf-a0aa-a87dbfba601a)

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| Platform | Android (min SDK 24) |
| Database | Firebase Realtime Database |
| Auth | Firebase Authentication |
| UI | Material Design 3, RecyclerView, ViewBinding |
| IDE | Android Studio |

## Firebase Database Structure

```
/MCQ/{teacherPhone}/{quizId}        — MCQ quizzes
/Qizzs/{teacherPhone}/{quizId}      — Short answer quizzes
/results/{quizTitle}/{studentPhone} — Quiz results
/users/{phone}/name                 — User display names
```

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/MdAsrafAli/android-quiz-management-app.git
   ```
2. Open in Android Studio.
3. Connect your Firebase project — add your `google-services.json` to `app/`.
4. Build and run on a device or emulator (API 24+).

## Project Structure

```
app/src/main/java/com/example/quiz/
├── activities/       — All screens (MainActivity, QuestionActivity, ResultActivity, …)
├── adapters/         — RecyclerView adapters
├── models/           — Data classes (Quiz, ShortQuiz, QuizResult, …)
└── utils/            — ColorPicker, IconPicker helpers
```
