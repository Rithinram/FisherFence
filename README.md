# 🌊 FisherFence

### 🛟 Intelligent Maritime Safety System

> **Track. Warn. Protect. Respond.**

**FisherFence** is a mobile-first maritime safety platform designed to improve the safety of fishermen operating at sea through **real-time location tracking, maritime boundary alerts, cyclone and weather warnings, and emergency SOS communication**.

The system creates an invisible digital safety boundary around fishing vessels — helping fishermen stay aware of maritime borders while providing a connected safety layer for emergencies and severe weather conditions.

---

<div align="center">

### 🚨 Protecting Those Who Work Beyond the Shore

**GPS Tracking • Maritime Geofencing • Weather Alerts • SOS • Coast Guard Monitoring • Family Safety**

</div>

---

## ✨ Why FisherFence?

Fishing at sea comes with risks that are difficult to manage from land.

Fishermen may:

* 🌊 Accidentally approach or cross maritime boundaries
* 🌀 Be exposed to rapidly changing weather and cyclones
* 📍 Become difficult to locate during emergencies
* 📡 Lose access to reliable communication offshore
* 🆘 Have limited options to communicate their exact location during distress
* 👨‍👩‍👦 Leave families without visibility into their safety status

FisherFence brings these safety requirements together into a **single mobile-oriented platform**.

Instead of treating tracking, boundary alerts, weather warnings and emergency communication as separate systems, FisherFence combines them into one safety workflow:

```text
       📍 TRACK
          ↓
     🌐 DETECT
          ↓
       ⚠️ ALERT
          ↓
       🆘 RESPOND
```

---

# 🎯 Project Vision

> **To create a technology-driven safety layer for fishermen that improves situational awareness at sea and connects fishermen, authorized authorities and families through location and emergency information.**

FisherFence is designed with a future-ready architecture that can be extended beyond smartphone connectivity through **dedicated IoT hardware, RF communication and satellite communication systems**.

---

# 🧭 Core Features

## 📍 Real-Time Location Tracking

Track the current or last-known position of a fishing vessel using GPS/location services.

### Capabilities

* GPS-based position acquisition
* Latitude and longitude information
* Vessel location visualization
* Last-known location support
* Route/voyage history potential
* Location sharing with authorized users

---

## 🚧 Maritime Border Geofencing

One of the central features of FisherFence is its digital maritime boundary awareness system.

The application can compare the vessel's location against a predefined geographical boundary.

### Alert Concept

```text
                  INTERNATIONAL
                MARITIME BOUNDARY
        ─────────────────────────────────

             🛥️
              \
               \    ⚠️ WARNING ZONE
                \
                 \

        🟢 SAFE → 🟡 WARNING → 🔴 RESTRICTED
```

The system can provide progressive warnings as the vessel approaches a configured maritime boundary.

### Why it matters

The ocean has no visible physical boundary. A digital geofence provides fishermen with an additional layer of situational awareness.

---

# 🌀 Cyclone & Weather Awareness

FisherFence can integrate weather information from external meteorological/weather services.

The application can surface relevant information such as:

* 🌧️ Severe weather
* 💨 Strong winds
* 🌊 Dangerous conditions
* 🌀 Cyclone-related warnings
* 📢 Weather notifications

### Important Design Principle

FisherFence **does not independently predict cyclones**.

Instead, it acts as a safety and communication layer that consumes available meteorological information and presents relevant warnings to users.

---

# 🆘 Emergency SOS

When an emergency occurs, the fisherman should not have to navigate through complicated menus.

FisherFence therefore uses a simple emergency workflow:

```text
              🆘 SOS
                │
                ▼
        Capture Location
                │
                ▼
       Generate Distress Alert
                │
          ┌─────┴─────┐
          ▼           ▼
     👮 Authority   👨‍👩‍👦 Family
```

Depending on available connectivity and deployment infrastructure, the SOS mechanism can communicate the fisherman's location to authorized recipients.

---

# 👮 Coast Guard / Authority Monitoring

FisherFence can provide an authorized monitoring interface for authorities.

### Monitoring possibilities

* 🛥️ Registered vessel locations
* ⚠️ Active boundary alerts
* 🌀 Weather-related warnings
* 🆘 SOS incidents
* 📍 Last-known positions
* 🗺️ Map-based vessel visibility

This changes the workflow from:

> **"Something happened — now search for the vessel."**

towards:

> **"The system has already identified the vessel and its last known position."**

---

# 👨‍👩‍👦 Family Safety View

Families can receive a restricted view of the fisherman's status.

Potential information includes:

* Last known location
* Current/last known status
* Emergency status
* Voyage information
* Safety alerts

The family interface is intended to remain **simple and read-only**, avoiding unnecessary complexity.

---

# 🏗️ System Architecture

FisherFence follows a layered architecture designed to separate presentation, business logic and data handling.

```text
                         ┌──────────────────────┐
                         │        USERS         │
                         └──────────┬───────────┘
                                    │
             ┌──────────────────────┼──────────────────────┐
             │                      │                      │
             ▼                      ▼                      ▼
       👨‍✈️ FISHERMAN           👮 AUTHORITY           👨‍👩‍👦 FAMILY
             │                      │                      │
             └──────────────────────┼──────────────────────┘
                                    ▼
                         ┌──────────────────────┐
                         │   PRESENTATION UI    │
                         │ Tracking • Alerts    │
                         │ Weather • SOS        │
                         └──────────┬───────────┘
                                    ▼
                         ┌──────────────────────┐
                         │      VIEWMODEL       │
                         └──────────┬───────────┘
                                    ▼
                         ┌──────────────────────┐
                         │      USE CASES       │
                         │ Location             │
                         │ Geofence             │
                         │ Weather              │
                         │ SOS                  │
                         └──────────┬───────────┘
                                    ▼
                         ┌──────────────────────┐
                         │     REPOSITORY       │
                         └──────────┬───────────┘
                                    │
                  ┌─────────────────┴─────────────────┐
                  ▼                                   ▼
        ┌──────────────────┐                ┌──────────────────┐
        │   LOCAL DATA     │                │   REMOTE DATA    │
        │                  │                │                  │
        │ Room Database    │                │ REST APIs        │
        │ Offline Cache    │                │ Firebase         │
        │ Voyage Data      │                │ Weather Services │
        └──────────────────┘                └──────────────────┘
```

---

# 🛠️ Technology Stack

| Layer            | Technology                    | Purpose                         |
| ---------------- | ----------------------------- | ------------------------------- |
| 📱 Platform      | Android                       | Mobile application              |
| 💻 Language      | Kotlin                        | Application development         |
| 🎨 UI            | Jetpack Compose               | Modern Android UI               |
| 🏛️ Architecture | MVVM                          | Separation of UI and logic      |
| 🧱 Design        | Clean Architecture            | Maintainability and scalability |
| 📦 Data Pattern  | Repository Pattern            | Abstract data sources           |
| 📍 Location      | Fused Location Provider / GPS | Vessel tracking                 |
| 🗺️ Maps         | Google Maps / Map SDK         | Location visualization          |
| 💾 Local Storage | Room                          | Offline/local data              |
| 🌐 Networking    | Retrofit / REST               | API communication               |
| ☁️ Backend       | Firebase                      | Cloud services                  |
| 🔔 Notifications | Firebase Cloud Messaging      | Push notifications              |
| 🌦️ Weather      | Weather/Meteorological API    | Weather information             |

---

# 🧠 Why This Architecture?

Maritime applications have a unique challenge:

> **Connectivity cannot always be guaranteed.**

Therefore, FisherFence is designed around the idea that the application should not depend entirely on continuous network availability.

### Local-first thinking

```text
                 GPS
                  │
                  ▼
            ┌───────────┐
            │   APP     │
            └─────┬─────┘
                  │
          ┌───────┴────────┐
          ▼                ▼
     📱 LOCAL           ☁️ CLOUD
      Room             Firebase/API
          │                │
          └───────┬────────┘
                  ▼
             SYNCHRONIZE
          WHEN CONNECTION
             IS AVAILABLE
```

This approach provides a foundation for future offshore communication technologies.

---

# 🔐 Role-Based System

FisherFence is designed around multiple user roles.

### 🛥️ Fisherman

Can:

* View location
* Receive boundary warnings
* Receive weather alerts
* Trigger SOS
* View safety information

### 👮 Authority / Coast Guard

Can:

* Monitor vessels
* View alerts
* Identify emergency situations
* Access authorized location information

### 👨‍👩‍👦 Family Member

Can:

* View last-known location
* View safety status
* Receive relevant emergency information

---

# 🔄 Core Workflow

## Normal Operation

```text
GPS Location
     ↓
Location Processing
     ↓
Geofence Check
     ↓
Weather Check
     ↓
Display Safety Information
```

## Border Warning

```text
Boat Location
     ↓
Boundary Distance Calculation
     ↓
Warning Zone?
   ↙       ↘
 YES        NO
  ↓          ↓
⚠️ ALERT    Continue
```

## Emergency

```text
🆘 SOS
  ↓
Capture Location
  ↓
Create Distress Event
  ↓
Notify Authorized Recipients
  ↓
Emergency Response
```

---

# 📊 Problem → Solution Mapping

| Maritime Challenge            | FisherFence Response               |
| ----------------------------- | ---------------------------------- |
| Invisible maritime boundaries | 🧭 Digital geofencing              |
| Unintentional border approach | ⚠️ Proximity warnings              |
| Severe weather at sea         | 🌀 Weather information             |
| Emergency communication       | 🆘 SOS                             |
| Unknown vessel location       | 📍 GPS tracking                    |
| Family uncertainty            | 👨‍👩‍👦 Safety view               |
| Multiple vessels              | 👮 Monitoring dashboard            |
| Intermittent connectivity     | 💾 Local storage / synchronization |

---

# 🌍 Expected Impact

## 🧑‍🤝‍🧑 Human Impact

FisherFence aims to improve awareness and communication for people working in challenging maritime environments.

Potential benefits include:

* Earlier awareness of boundary proximity
* Better access to safety information
* Faster sharing of emergency location information
* Improved visibility for families

---

## 💰 Economic Impact

Fishing is the livelihood of many coastal communities.

Improved safety awareness can help protect:

* 👨‍👩‍👦 Fishing families
* 🛥️ Fishing vessels
* 🎣 Fishing operations
* 💼 Household income

---

## 👮 Operational Impact

For authorized authorities, a centralized digital system can provide:

* Vessel visibility
* Alert information
* Last-known positions
* Emergency event information

This can support more informed emergency response.

---

# 🚀 Future Scope

FisherFence is designed to grow beyond a smartphone-only system.

### 🛰️ Satellite Connectivity

Integrate satellite communication for areas outside cellular coverage.

Possible future architecture:

```text
📱 Mobile App
      ↕
🛰️ Satellite
      ↕
☁️ Cloud
      ↕
👮 Monitoring Centre
```

---

### 📡 Dedicated IoT Hardware

A waterproof tracking device installed on the vessel could provide:

* GPS
* Low-power communication
* SOS button
* Buzzer
* Battery monitoring
* Solar charging

---

### 🤖 AI-Based Risk Analysis

Future versions could analyze:

* Vessel movement
* Weather conditions
* Historical routes
* Distance from boundaries
* Environmental conditions

to provide additional risk insights.

---

### 🗣️ Multilingual & Voice Alerts

Designed for real-world usability:

* Regional languages
* Voice warnings
* Audio alerts
* Large controls
* Low-literacy-friendly interface

---

### 🔋 Low-Power Operation

Future hardware can use:

* Adaptive GPS intervals
* Low-power communication
* Solar charging
* Battery health monitoring

---

# ⚠️ Current Limitations

FisherFence is designed as a software safety layer and has several practical limitations.

### 📡 Offshore Connectivity

A smartphone cannot guarantee network connectivity far from shore.

**Future solution:** Satellite/RF communication.

### 🔋 Battery Consumption

Continuous GPS tracking can consume significant battery power.

**Future solution:** Dedicated low-power hardware.

### 🗺️ Boundary Data

Production deployment requires accurate and authoritative maritime boundary data.

### 🌦️ External Weather Dependency

Weather information depends on the availability and accuracy of upstream meteorological services.

### 📱 Hardware Dependency

A production-grade offshore system may require dedicated waterproof GPS/communication hardware rather than relying exclusively on a smartphone.

---

# 🧪 Development Considerations

A production deployment should additionally consider:

* GPS accuracy
* False-positive alerts
* Network loss
* Battery consumption
* Data privacy
* Authentication
* Role-based access
* Secure location transmission
* Official maritime boundary datasets
* Reliable emergency communication

---

# 📁 Project Structure

The project is an Android application built using Gradle/Kotlin and contains the main application module under `app/`. The repository also includes the Gradle configuration required to build the project.

A typical high-level structure is:

```text
FisherFence/
│
├── 📱 app/
│   └── Android application
│
├── ⚙️ gradle/
│   └── Gradle configuration
│
├── 🧩 build.gradle.kts
├── ⚙️ settings.gradle.kts
├── 🔧 gradle.properties
├── 🪟 gradlew.bat
└── 📄 README.md
```

---

# 🚀 Getting Started

## Prerequisites

Make sure you have:

* Android Studio
* Android SDK
* JDK compatible with the project's Gradle/Android configuration
* Android device or emulator
* Internet connection for dependency resolution

---

## Clone the Repository

```bash
git clone https://github.com/Rithinram/FisherFence.git
```

```bash
cd FisherFence
```

Open the project in **Android Studio** and allow Gradle to synchronize.

---

## Build the Application

On Windows:

```bash
gradlew.bat assembleDebug
```

On macOS/Linux:

```bash
./gradlew assembleDebug
```

Install the generated APK on a compatible Android device or emulator.

---

# 📱 Recommended Demo Flow

For demonstrations and presentations, the application can be presented in this sequence:

```text
1️⃣ Login / Role Selection
        ↓
2️⃣ Fisherman Dashboard
        ↓
3️⃣ Live Location
        ↓
4️⃣ Maritime Boundary
        ↓
5️⃣ Border Warning
        ↓
6️⃣ Weather / Cyclone Alert
        ↓
7️⃣ SOS
        ↓
8️⃣ Authority Dashboard
        ↓
9️⃣ Family Tracking
```

This demonstrates the complete **Track → Warn → Protect → Respond** workflow.

---

# 🛡️ Safety Philosophy

FisherFence is intended to **support**, not replace, established maritime safety procedures, navigation systems, emergency communication infrastructure or official authorities.

For real-world deployment, the system would require appropriate validation, authoritative maritime datasets, reliable communication infrastructure and operational approval from relevant authorities.

---

# 🌱 Scalability

The underlying concept can be adapted for different coastal environments.

Potential future applications include:

* 🌊 Coastal safety
* 🛥️ Fishing vessel monitoring
* 🌀 Cyclone-prone regions
* 🚢 Maritime logistics
* 🛟 Search-and-rescue support
* 🏝️ Coastal community safety

---

# 💡 The Big Picture

FisherFence is built around a simple idea:

> **A fisherman should not have to discover that they are in danger only after the danger has already happened.**

By combining location awareness, digital boundaries, weather information and emergency communication, FisherFence creates a software foundation for a more connected maritime safety ecosystem.

---

# 👨‍💻 Project

### FisherFence

**Intelligent Maritime Safety System**

Built with ❤️ using **Kotlin • Jetpack Compose • GPS • Geofencing • Firebase • REST APIs**

---

## ⭐ Support the Project

If you find the concept interesting, consider giving the repository a ⭐ on GitHub.

**Repository:**
https://github.com/Rithinram/FisherFence

---

<div align="center">

### 🌊 FisherFence

**Track. Warn. Protect. Respond.**

*Technology for safer seas.*

</div>
