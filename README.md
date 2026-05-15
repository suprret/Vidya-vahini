Vidya-Vahini 
Vidya-Vahini is a crowdsourced Android application designed to help students track transportation status and estimated arrival times (ETA) in real-time. By leveraging community-driven data, the app provides accurate updates to ensure students never miss their ride.

* Features
Crowdsourced Tracking: Real-time location pings from users to provide live transit updates.

Intelligent Advisories: Integration with Google Gemini API for smart delay notifications and transit advice.

Route Visualization: A custom horizontal Route Line UI for easy progress tracking.

ETA Algorithm: Custom logic based on historical transit times and current data.

Guardian Notifications: Integrated SMS gateway for sending alerts to guardians.

Lightweight Design: Optimized for broad compatibility (Minimum SDK: API 21).

* Tech Stack
Language: Kotlin

UI Framework: Jetpack Compose

Architecture: MVVM (Model-View-ViewModel)

Database: * Firebase Realtime Database (Live location pings)

Cloud Firestore (Route topology and static data)

Cloud Messaging: Firebase Cloud Messaging (FCM) for breakdown and emergency alerts.

AI: Google Gemini API for intelligent status advisories.

* How it Works
Ping: Users on the transit vehicle "ping" their location.

Process: The backend calculates the current position relative to the route topology in Firestore.

Notify: Waiting students receive real-time updates and an ETA via the Compose-based dashboard.





##  Installation & Setup
To run this project locally, follow these steps:

1. **Clone the repository:**
   `git clone https://github.com/suprret/Vidya-vahini.git`
2. **Open in Android Studio:**
   Select "Open an Existing Project" and navigate to the folder.
3. **Firebase Configuration:**
   - Create a project on the Firebase Console.
   - Add an Android app with your package name.
   - Download the `google-services.json` and place it in the `app/` directory.
4. **Build and Run:**
   Clean the project and click the 'Run' button in Android Studio.
