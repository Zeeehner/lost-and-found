# Lost & Found – *Find what’s been lost.*

**Lost something or found an item? Our app connects people to help reunite lost belongings – quickly, locally, and anonymously.**

**Lost & Found** is a streamlined Android app that lets users report lost or found items. Upload a photo, describe the object, set the location and time, and help others reclaim what’s theirs.

---

### 🎯 Who is it for?

For honest finders, desperate searchers – and everyone who wants to help.

---

### ❓ What makes it special?

- 💬 Ad-supported model keeps the app free and available  
- 📍 Location-based & visual reporting – fast, intuitive, clear  
- 🔐 Secure Firebase login  
- 🎨 Built with clean architecture and modern UI patterns

---

## 📲 Features

- 📸 Report lost/found items (photo, description, location, time)  
- 🔐 Firebase-based login and registration  
- 📍 Location detection (via GPS or manual entry)  
- 🗂️ Filterable item list (Lost / Found / Date)  
- 🧭 App navigation: Login → Dashboard → Details → New Report  
- 💾 Cloud-based Firestore storage  
- 🔎 Reverse geocoding via external API (e.g., OpenCage)  
- 🛡️ Error handling (auth, upload, network)  
- 🧪 Built-in test data for demo purposes  
- 🗨️ Commenting under posts  
- 🌐 Multilingual support (DE/EN)

---

## 📸 Screenshots

<p align="center">
  <img src="img/loginScreen.png" alt="Login Screen" width="160"/>
  <img src="img/screenMain.png" alt="Main Screen" width="160"/>
  <img src="img/detailScreen.png" alt="Detail Screen" width="160"/>
  <img src="img/MapScreen.png" alt="Map Screen" width="160"/>
  <img src="img/chatScreen.png" alt="Chat Screen" width="160"/>
</p>

---

## 🛠 Technical Overview

### 🧱 Architecture

The app follows the **MVVM** pattern with a **Repository Layer** to separate UI, business logic, and data handling.  
Navigation is implemented using Jetpack Navigation; UI is broken into reusable **components** (Composables).

### 💾 Data Storage

Uses **Firebase Firestore** for item reports and **Firebase Auth** for user login.  
Chosen for:
- Cloud scalability  
- Realtime updates  
- Offline support with auto-sync

### 🌐 API Integration

Location names are resolved using a reverse geocoding API (e.g., **OpenCage**), accessed via **Retrofit** and parsed with **Moshi**.

### 📦 Tech Stack

- 🔥 Firebase Auth & Firestore  
- 📡 Retrofit & Moshi  
- 📍 Google Maps / Location Services  
- 🧭 Jetpack Navigation  
- 🧪 *(Planned)* Room for local persistence

---

## 🔮 Roadmap

✨ The foundation is set – future enhancements planned:

- 🚨 Push notifications for nearby matches  
- 🗺️ Map view to browse all reports visually  
- 🛡️ Admin mode for content moderation  
- 🧑‍🤝‍🧑 Social features (thank-you notes, messaging, rating)  
- 🌙 Dark Mode  
- 📱 iOS version (Kotlin Multiplatform)  
- 🌐 Full internationalization (DE/EN/...)
