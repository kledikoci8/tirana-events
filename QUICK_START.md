# ⚡ QUICK START - Get Running in 5 Minutes!

## 🎯 Goal
Get both backend and mobile app running and see the app in action!

---

## Step 1️⃣: Start Backend (2 minutes)

Open Terminal 1:

```bash
cd backend
./mvnw spring-boot:run
```

**Wait for:** `Started TiranaEventsApplication in X seconds`

✅ Backend is now running on `http://localhost:8080`

---

## Step 2️⃣: Start Mobile App (3 minutes)

Open Terminal 2:

```bash
cd mobile
npm install
npm start
```

**Then:**
- Press `i` for iOS Simulator
- Press `a` for Android Emulator  
- Or scan QR code with Expo Go app

---

## Step 3️⃣: Login & Explore!

### Use Demo Account:
- **Email:** `demo@tirana.com`
- **Password:** `demo123`

### Or Create New Account:
Tap "Sign Up" and create your own!

---

## 🎉 What You'll See

### Home Screen
- 5 sample events (Sunset Festival, Friday Night Party, etc.)
- 5 categories (Music, University, Culture, etc.)
- Search bar
- Beautiful dark theme with purple gradients

### Explore Screen
- Interactive map with event markers
- Events plotted at their locations

### Event Detail
- Full event information
- Location map
- "Get Ticket" button

### Create Event
- Form to create your own events
- Category selection
- Image upload placeholder

### Tickets
- View purchased tickets
- QR codes for entry
- Organized by upcoming/past/cancelled

### Profile
- User information
- Stats (events, following, followers)
- Interests
- Settings menu

---

## 🔧 Troubleshooting

### Backend won't start?

**Check Java version:**
```bash
java -version
```
Need Java 17 or higher!

**Port 8080 in use?**
```bash
# Kill the process
lsof -ti:8080 | xargs kill -9
```

### Mobile app can't connect?

**For Physical Device:**
1. Find your computer's IP:
   ```bash
   ifconfig | grep inet
   ```
2. Edit `mobile/src/services/api.js`:
   ```javascript
   const API_URL = 'http://YOUR_IP:8080/api';
   ```

**For Android Emulator:**
```javascript
const API_URL = 'http://10.0.2.2:8080/api';
```

### Dependencies error?
```bash
cd mobile
rm -rf node_modules package-lock.json
npm install
```

---

## 📱 Test These Features

1. ✅ **Login** with demo account
2. ✅ **Browse events** on home screen
3. ✅ **Search** for "festival"
4. ✅ **View event details** by tapping an event
5. ✅ **Get a ticket** from event detail
6. ✅ **View your ticket** in Tickets tab (with QR code!)
7. ✅ **Explore map** view
8. ✅ **Create an event** in Create tab
9. ✅ **View profile** in Profile tab
10. ✅ **Logout** and create new account

---

## 🎨 Customize It!

### Change Primary Color
Find and replace `#8B5CF6` with your color in screen files!

### Add More Events
Edit: `backend/src/main/java/com/tirana/events/config/DataInitializer.java`

### Change App Name
Edit: `mobile/app.json` → change "name" and "slug"

---

## 📚 Need More Help?

- **Full Documentation:** [README.md](README.md)
- **Detailed Setup:** [SETUP.md](SETUP.md)
- **Project Overview:** [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)

---

## 🚀 You're All Set!

Your complete event management platform is running!

**Backend:** `http://localhost:8080`  
**Mobile:** Running in Expo  
**Demo Login:** demo@tirana.com / demo123

**Enjoy! 🎉**
