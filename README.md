# Tirana Events

Full-stack event management platform with React Native mobile app and Spring Boot backend.

## Prerequisites

- **Backend**: Java 17+, Maven
- **Mobile**: Node.js 16+, npm, Expo CLI

## Quick Start

### Backend Setup

```bash
cd backend
./mvnw spring-boot:run
```

Backend runs on: `http://localhost:8080`

**Demo Account:**
- Email: `demo@tirana.com`
- Password: `demo123`

### Mobile Setup

1. Install dependencies:
```bash
cd mobile
npm install
```

2. Update API URL in `mobile/src/services/api.js`:
   - iOS Simulator: `http://localhost:8080/api`
   - Android Emulator: `http://10.0.2.2:8080/api`
   - Physical Device: `http://YOUR_COMPUTER_IP:8080/api`

3. Start the app:
```bash
npm start
```

Press `i` for iOS or `a` for Android.

## Tech Stack

- **Mobile**: React Native, Expo, React Navigation
- **Backend**: Spring Boot, Spring Security, JWT, JPA, H2 Database
