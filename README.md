# 🚦 Traffic Monitor — AI Real-Time Traffic Monitoring & Violation Detection

An Android application built with Kotlin and Jetpack Compose (Material 3), converted from the AI-Based Traffic Monitoring System. Features real-time CCTV computer vision surveillance simulation, automatic number plate recognition (ANPR), violation detection & e-challan generation, and adaptive traffic signal control.

---

## 🚀 How to Export as ZIP & Push to GitHub

### Option 1: Direct ZIP Export from AI Studio
1. In Google AI Studio, look at the top menu / project settings bar.
2. Click **Export as ZIP** or **Download Project**.
3. Extract the downloaded ZIP anywhere on your system and open it directly in **Android Studio**.



# 5. Link to your GitHub remote repository
git remote add origin https://github.com/<YOUR_GITHUB_USERNAME>/<YOUR_REPOSITORY_NAME>.git

# 6. Push to GitHub
git push -u origin main
```

---

## 📱 Features

- **🔐 Operator Authentication**:
  - Secure login screen matching the original Flask authentication design.
  - Default Credentials: `admin` / `1234` (with demo quick-fill button & error alert handling).
- **📹 Live CCTV Multi-Feed Surveillance**:
  - Simulated real-time optical feed across 4 major traffic intersections.
  - YOLOv8 computer vision simulation rendering dynamic vehicle bounding boxes, velocity indicators, and OCR license plate tags.
  - Snapshot tool for archiving high-resolution incident frames.
- **📊 Real-Time Metrics & Density Tracking**:
  - **Vehicles Count**: Live counter of moving vehicles.
  - **Traffic Density**: Dynamic levels (`Low`, `Medium`, `High`, `Congested`).
  - **Violations**: Incident counters for red light jumps, overspeeding, helmet violations, and wrong-way entries.
  - **Number Plates**: Automatic Number Plate Recognition (ANPR) count and database sync.
- **🚨 Violations Hub & E-Challan Workflow**:
  - Filter violations by offense type (Speeding, Red Light, No Helmet, Wrong Way).
  - Review vehicle details, speed telemetry, fine amounts, and issue official e-challans or record payments.
- **🔍 ANPR Plate Registry**:
  - Searchable vehicle registry with owner information, state of registration, and OCR accuracy scoring.
  - Instant hotlist / police stolen vehicle blacklist alerts.
- **🚥 Traffic Signal Control & Emergency Corridor**:
  - 4-way intersection signal phasing with real-time countdown timers.
  - Emergency Green Corridor switch to prioritize ambulances and emergency responders.

---

## 🛠️ Build & Run

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or higher
- Android SDK 34 / 36

### Build via Gradle
```bash
# Build Debug APK
gradle assembleDebug

# Run Unit Tests
gradle :app:testDebugUnitTest
```
The generated APK will be available under `app/build/outputs/apk/debug/app-debug.apk`.
