# 🚀 Quick Setup Guide - Tirana Events

## Step 1: Start the Backend (Spring Boot)

### Option A: Using Maven Wrapper (Recommended)

```bash
cd backend
./mvnw spring-boot:run
```

On Windows:
```bash
cd backend
mvnw.cmd spring-boot:run
```

### Option B: Using Maven directly

```bash
cd backend
mvn spring-boot:run
```

### Option C: Using your IDE
1. Open the `backend` folder in IntelliJ IDEA or Eclipse
2. Run `TiranaEventsApplication.java` as a Java Application

**Backend will start on:** `http://localhost:8080`

**Test it:** Open `http://localhost:8080/api/categories` in your browser

---

## Step 2: Start the Mobile App (React Native)

### First Time Setup

```bash
cd mobile
npm install
```

### Update API URL

Edit `mobile/src/services/api.js` and change the API_URL:

- **iOS Simulator:** `http://localhost:8080/api`
- **Android Emulator:** `http://10.0.2.2:8080/api`  
- **Physical Device:** `http://YOUR_COMPUTER_IP:8080/api`

To find your IP:
- **macOS/Linux:** `ifconfig | grep inet`
- **Windows:** `ipconfig`

### Start the App

```bash
cd mobile
npm start
```

Then:
- Press `i` for iOS Simulator
- Press `a` for Android Emulator
- Scan QR code with Expo Go app on your phone

---

## 🎯 Test the App

### Demo Account
- **Email:** `demo@tirana.com`
- **Password:** `demo123`

### Or Create New Account
Just tap "Sign Up" in the app!

---

## 🔧 Troubleshooting

### Backend Issues

**Problem:** Port 8080 already in use
```bash
# Find and kill the process
lsof -ti:8080 | xargs kill -9
```

**Problem:** Java version error
- Make sure you have Java 17 or higher
- Check: `java -version`

### Mobile Issues

**Problem:** Can't connect to backend
- Make sure backend is running
- Check the API_URL in `mobile/src/services/api.js`
- For physical device, use your computer's IP address

**Problem:** Expo not installed
```bash
npm install -g expo-cli
```

**Problem:** Dependencies error
```bash
cd mobile
rm -rf node_modules package-lock.json
npm install
```

---

## 📱 Features to Try

1. **Browse Events** - See upcoming events on the home screen
2. **Search** - Search for specific events
3. **Map View** - Explore events on a map
4. **Create Event** - Organize your own event
5. **Get Tickets** - Purchase tickets with QR codes
6. **Save Events** - Bookmark your favorites

---

## 🎨 Customization

### Change App Colors
Edit the color values in the screen files:
- Primary: `#8B5CF6` (Purple)
- Background: `#0A0A0F` (Dark)
- Card: `#1F1F2E` (Dark Gray)

### Add More Categories
Edit `backend/src/main/java/com/tirana/events/config/DataInitializer.java`

### Change Database
Edit `backend/src/main/resources/application.properties` to use PostgreSQL or MySQL

---

## 📚 Next Steps

- Read the full [README.md](README.md) for detailed documentation
- Explore the API at `http://localhost:8080/h2-console`
- Customize the app to your needs
- Deploy to production!

---

**Need Help?** Check the README.md or create an issue!

**Happy Coding! 🎉**
