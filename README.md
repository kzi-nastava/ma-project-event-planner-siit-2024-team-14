# Event Planner — Android Application

**Java | Android SDK | Team Project**

Event Planner is a multi-platform event management system designed to simplify the organization, discovery, and management of events.

This repository contains the **native Android application**, developed as part of a collaborative university project for Mobile Applications course at the Faculty of Technical Sciences, University of Novi Sad.

The Android application connects to the shared Spring Boot backend and provides mobile access to event management, service bookings, user communication, and notifications.

## Key Features

### Event Discovery
- Browse and search public events
- Filter events by date, category, and location
- View event details and organizer information
- Explore available services and products

### Event Management
- Create, update, and manage events
- Manage event-related information
- Invite attendees and track invitations
- Access role-specific event management features

### Service and Product Management
- Browse services and products
- Filter available offerings
- View offering details and pricing
- Submit service booking requests
- Manage booking requests as a provider

### User Accounts and Profiles
- User registration and login
- Role-based functionality
- Event organizer profiles
- Service provider profiles
- Account management

### Communication and Notifications
- Direct messaging between users
- Real-time communication using WebSockets
- In-app notifications
- Invitation and booking updates
- User blocking and reporting features

## User Roles

The application supports multiple user roles:

| Role | Responsibilities |
|---|---|
| Event Attendee | Discover events and manage participation |
| Event Organizer | Create events, manage invitations, and organize bookings |
| Service and Product Provider | Manage offerings and respond to booking requests |

Available functionality depends on the authenticated user's role.

## Technology Stack

| Category | Technologies |
|---|---|
| Language | Java |
| Platform | Android SDK |
| Build System | Gradle |
| UI | Android XML, Material Design |
| Networking | Retrofit, OkHttp, Gson, Volley |
| Real-time Communication | STOMP WebSocket, RxJava |
| Image Loading | Glide |
| Architecture Components | ViewModel, LiveData, View Binding |
| Authentication | JWT integration |
| Testing Dependencies | JUnit 4, AndroidX Test, Espresso |

**Android configuration**
- Minimum SDK: API 30 (Android 11)
- Target SDK: API 34
- Compile SDK: API 34

## Project Structure

The application separates data handling from user interface components.

```text id="3kk8kq"
app/
└── src/
    └── main/
        ├── java/
        │   └── com/example/eventplanner/
        │       ├── data/
        │       │   ├── model/
        │       │   └── network/
        │       └── ui/
        ├── res/
        └── AndroidManifest.xml
```

**Main components:**

- **Data models:** Represent application entities and API data.
- **Network layer:** Handles communication with backend services.
- **UI layer:** Contains Android screens and interface components.
- **Resources:** Store layouts, images, strings, and other Android resources.

## Getting Started

### Prerequisites

- Android Studio
- Android SDK 34
- Android emulator or physical device running Android 11 or newer
- Java and Gradle environment compatible with the project
- Event Planner backend for server-dependent features

### Installation

1. Clone the repository:

```bash id="wkn16c"
git clone https://github.com/kzi-nastava/ma-project-event-planner-siit-2024-team-14.git
```

2. Open the project in Android Studio.

3. Allow Gradle to synchronize the project dependencies.

4. Configure the backend server address in the project's local configuration.

The Gradle build reads the `ip_addr` property from `local.properties`. Add the appropriate address for your development environment.

5. Start the Spring Boot backend.

6. Connect an Android device or start an emulator.

7. Build and run the application from Android Studio.

**Note:** Backend-dependent features require network connectivity between the Android device and the backend server.

## Testing

The project declares dependencies for:

- JUnit 4
- AndroidX Test
- Espresso UI testing

These provide support for local unit tests and Android instrumentation tests.

To run configured tests, use the testing tools in Android Studio or the corresponding Gradle tasks. The presence of testing dependencies does not indicate that all test categories have implemented test cases.

## System Architecture

Event Planner consists of three connected applications:

| Component | Technology | Repository |
|---|---|---|
| Android Application | Java, Android SDK | [Android](https://github.com/kzi-nastava/ma-project-event-planner-siit-2024-team-14) |
| Web Application | Angular, TypeScript | [Frontend](https://github.com/kzi-nastava/iks-project-event-planner-siit-2024-team-14) |
| Backend | Spring Boot, PostgreSQL | [Backend](https://github.com/kzi-nastava/iss-project-event-planner-siit-2024-team-14) |

The Android and web applications share the same backend services and database.

The Android client uses HTTP networking libraries to communicate with the backend and WebSocket technologies for supported real-time functionality.
