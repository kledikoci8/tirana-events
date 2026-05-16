# Tirana Events - Complete Event Management Platform

A full-stack event management application with React Native mobile app and Spring Boot backend.

## 🎯 Features

### Mobile App (React Native + Expo)
- **Authentication**: Login & Registration with JWT
- **Home Screen**: Browse upcoming events with categories
- **Explore Screen**: Interactive map view of events
- **Event Details**: Full event information with location map
- **Create Events**: Organize your own events
- **Tickets**: Purchase and manage event tickets with QR codes
- **Profile**: User profile with interests and settings
- **Save Events**: Bookmark favorite events

### Backend (Spring Boot)
- **RESTful API**: Complete REST API for all operations
- **JWT Authentication**: Secure authentication system
- **Event Management**: CRUD operations for events
- **Ticket System**: Purchase and manage tickets with QR codes
- **Category System**: Event categorization
- **User Management**: User profiles and preferences
- **H2 Database**: In-memory database for development
- **PostgreSQL Ready**: Easy switch to PostgreSQL for production

## 🚀 Getting Started

### Prerequisites
- **Backend**: Java 17+, Maven
- **Mobile**: Node.js 16+, npm/yarn, Expo CLI
- **Optional**: Android Studio / Xcode for emulators

### Backend Setup

1. Navigate to backend folder:
```bash
cd backend
```

2. Run the Spring Boot application:
```bash
./mvnw spring-boot:run
```

Or if you're on Windows:
```bash
mvnw.cmd spring-boot:run
```

The backend will start on `http://localhost:8080`

**Default Demo Account:**
- Email: `demo@tirana.com`
- Password: `demo123`

**H2 Console:** `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:tiranaevents`
- Username: `sa`
- Password: (leave empty)

### Mobile Setup

1. Navigate to mobile folder:
```bash
cd mobile
```

2. Install dependencies:
```bash
npm install
```

3. Update API URL in `mobile/src/services/api.js`:
   - For iOS Simulator: `http://localhost:8080/api`
   - For Android Emulator: `http://10.0.2.2:8080/api`
   - For Physical Device: `http://YOUR_COMPUTER_IP:8080/api`

4. Start the Expo development server:
```bash
npm start
```

5. Run on your device:
   - Press `i` for iOS simulator
   - Press `a` for Android emulator
   - Scan QR code with Expo Go app on physical device

## 📱 App Screens

1. **Login/Register** - User authentication
2. **Home** - Browse events with search and categories
3. **Explore** - Map view of nearby events
4. **Create Event** - Form to create new events
5. **Event Detail** - Full event information
6. **Tickets** - View purchased tickets with QR codes
7. **Profile** - User profile and settings

## 🔧 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user

### Events
- `GET /api/events/upcoming` - Get upcoming events
- `GET /api/events/search?query={query}` - Search events
- `GET /api/events/{id}` - Get event details
- `POST /api/events` - Create event (authenticated)
- `POST /api/events/{id}/save` - Save event (authenticated)
- `DELETE /api/events/{id}/save` - Unsave event (authenticated)

### Tickets
- `POST /api/tickets/purchase/{eventId}` - Purchase ticket (authenticated)
- `GET /api/tickets/my-tickets` - Get user tickets (authenticated)

### Categories
- `GET /api/categories` - Get all categories

## 🎨 Tech Stack

### Mobile
- React Native
- Expo
- React Navigation
- Axios
- React Native Maps
- Expo Linear Gradient
- React Native QR Code SVG
- AsyncStorage

### Backend
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- JWT (JSON Web Tokens)
- H2 Database
- Lombok
- Maven

## 📦 Project Structure

```
tirana-events/
├── mobile/                 # React Native app
│   ├── src/
│   │   ├── context/       # React Context (Auth)
│   │   ├── screens/       # App screens
│   │   └── services/      # API services
│   ├── App.js
│   └── package.json
│
└── backend/               # Spring Boot API
    ├── src/main/java/com/tirana/events/
    │   ├── config/        # Configuration classes
    │   ├── controller/    # REST controllers
    │   ├── dto/          # Data Transfer Objects
    │   ├── model/        # Entity models
    │   ├── repository/   # JPA repositories
    │   ├── security/     # Security & JWT
    │   └── service/      # Business logic
    └── pom.xml
```

## 🔐 Security

- JWT-based authentication
- Password encryption with BCrypt
- Secure endpoints with Spring Security
- Token-based API access

## 🌟 Sample Data

The application comes with pre-populated sample data:
- 5 Categories (Music, University, Culture, Volunteering, More)
- 5 Sample Events
- 1 Demo User

## 📝 Configuration

### Backend Configuration (`application.properties`)
- Server port: 8080
- Database: H2 (in-memory)
- JWT secret and expiration
- CORS configuration

### Mobile Configuration (`src/services/api.js`)
- API base URL
- Request/response interceptors

## 🚢 Production Deployment

### Backend
1. Switch to PostgreSQL in `application.properties`
2. Build: `./mvnw clean package`
3. Run: `java -jar target/events-backend-1.0.0.jar`

### Mobile
1. Build for iOS: `expo build:ios`
2. Build for Android: `expo build:android`
3. Or use EAS Build: `eas build`

## 🤝 Contributing

Feel free to fork this project and submit pull requests!

## 📄 License

This project is open source and available under the MIT License.

## 👨‍💻 Author

Built with ❤️ for the Tirana Events community

---

**Happy Coding! 🎉**
