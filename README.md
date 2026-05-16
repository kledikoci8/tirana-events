# Tirana Events

Full-stack event management platform with React Native mobile app and Spring Boot backend.

## Prerequisites

- **Backend**: Java 17, Maven, MySQL (via XAMPP)
- **Mobile**: Node.js 16+, npm, Expo CLI

## Quick Start

### Backend Setup

**Important:** This project requires Java 17 (not Java 24) due to Lombok compatibility.

1. Make sure XAMPP MySQL is running on port 3306
2. Start the backend:

```bash
cd backend
./start.sh
```

Or manually:
```bash
cd backend
JAVA_HOME=/Users/kledi/Library/Java/JavaVirtualMachines/ms-17.0.16/Contents/Home mvn spring-boot:run
```

Backend runs on: `http://localhost:8080`

The database `tirana-events` will be created automatically in MySQL.

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
   - iOS Simulator: `http://localhost:8080/api` (default)
   - Android Emulator: `http://10.0.2.2:8080/api`
   - Physical Device: `http://192.168.1.6:8080/api`

3. Start the app:
```bash
npm start
```

Press `i` for iOS or `a` for Android.

**Note:** If you see font loading errors, clear the cache:
```bash
npm start --clear
```

## Tech Stack

- **Mobile**: React Native, Expo, React Navigation
- **Backend**: Spring Boot, Spring Security, JWT, JPA, MySQL Database
- **Database**: MySQL 8.0+ (via XAMPP)

## Configuration

### Database Configuration
The backend is configured to use MySQL with these settings:
- Host: `localhost:3306`
- Database: `tirana-events` (auto-created)
- Username: `root`
- Password: (empty)

You can view the database in phpMyAdmin: http://localhost/phpmyadmin/

### Java Version
The project uses Java 17 for compilation. If you have multiple Java versions installed, the backend uses Java 17 automatically via the `.mvn/jvm.config` file.
