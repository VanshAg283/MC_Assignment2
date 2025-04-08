# ✈️ JetRack Flight Tracker App

This application consists of two main features:

1. **Live Flight Location Tracker**
2. **Weekly Average Flight Duration Viewer**

Each feature is built using **Jetpack Compose**, **MVVM architecture**, and **Kotlin Coroutines**, with data fetched either live (via network) or from a local database.

---

## 📍 1. Flight Location Tracker

### ✅ Features:
- Track the real-time location of a flight using its **callsign**
- Updates the location every 60 seconds
- Displays detailed flight information:
  - Latitude & Longitude
  - Altitude
  - Speed
  - Direction
  - Origin Country
  - Time of last update
- Displays a live **OpenStreetMap** view of the flight’s location
- Handles errors gracefully and displays messages

### 🔧 Implementation Details:

#### 🧠 ViewModel: `FlightViewModel.kt`
- Maintains state using a `MutableStateFlow<FlightState>`
- Uses `viewModelScope.launch` to fetch live data every 60 seconds
- Provides methods:
  - `fetchFlightLocationEveryMinute(flightNumber, context)`: Starts tracking coroutine
  - `stopTracking()`: Cancels the coroutine and resets tracking state
  - `clearLocation()`: Clears location and error state
  - `clearError()`: (Optional) Can be used to clear error state explicitly

#### 🖼 UI: `FlightTrackerScreen.kt`
- Composable screen with:
  - Text field to enter flight number
  - Start/Stop tracking buttons
  - Live refresh countdown
  - Displays flight data inside a stylized `Card`
  - `CircularProgressIndicator` for loading state
  - `ElevatedCard` for error display
  - `OpenStreetMap` composable to render the current position
- Handles keyboard dismissal and focus clearing when location is found

---

## 📊 2. Weekly Average Flight Duration Viewer

### ✅ Features:
- Displays average weekly flight durations stored in a local Room database
- For each entry, shows:
  - Flight code
  - Average duration (in readable format)
- Adds a divider between cards for visual clarity

### 🔧 Implementation Details:

#### 🧠 ViewModel: `FlightDataViewModel.kt`
- Provides a `LiveData<List<AverageDurationEntity>>`
- Data is retrieved from a `Room` database via DAO

#### 🖼 UI: `WeeklyDurationScreen.kt`
- Uses `LazyColumn` to display data
- Each item is wrapped in a `Card`
- `Divider` is added between cards (except after the last one)
- Shows a fallback message if no data is available

---

## 🧱 Architecture

- **MVVM (Model-View-ViewModel)** architecture
- **Jetpack Compose** for UI components
- **Room** for local database support
- **Retrofit** or equivalent assumed for network calls
- **Kotlin Coroutines** for async background tasks
- **StateFlow** and **LiveData** for reactive state management

---
