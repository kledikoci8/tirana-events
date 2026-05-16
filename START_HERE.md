# 🎉 WELCOME TO TIRANA EVENTS!

## 🎯 Your Complete Event Management Platform is Ready!

This is a **100% complete** full-stack application with:
- ✅ React Native mobile app (iOS & Android)
- ✅ Spring Boot REST API backend
- ✅ JWT authentication
- ✅ Event management with maps
- ✅ Ticket system with QR codes
- ✅ Beautiful dark theme UI
- ✅ Sample data included

---

## 🚀 QUICK START (Choose One)

### Option 1: Super Quick (5 minutes)
Read: **[QUICK_START.md](QUICK_START.md)**

### Option 2: Detailed Setup
Read: **[SETUP.md](SETUP.md)**

### Option 3: Just Run It!

**Terminal 1 - Backend:**
```bash
cd backend
./mvnw spring-boot:run
```

**Terminal 2 - Mobile:**
```bash
cd mobile
npm install
npm start
```

Then press `i` for iOS or `a` for Android!

**Login with:**
- Email: `demo@tirana.com`
- Password: `demo123`

---

## 📚 DOCUMENTATION

| File | Description |
|------|-------------|
| **[QUICK_START.md](QUICK_START.md)** | Get running in 5 minutes |
| **[SETUP.md](SETUP.md)** | Detailed setup instructions |
| **[README.md](README.md)** | Complete project documentation |
| **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** | What's been built |
| **[FILE_STRUCTURE.txt](FILE_STRUCTURE.txt)** | Visual file tree |

---

## 🎨 WHAT YOU GET

### Mobile App Features
- 🔐 **Login & Registration** - Secure authentication
- 🏠 **Home Feed** - Browse upcoming events
- 🗺️ **Map View** - Explore events on interactive map
- 📝 **Event Details** - Full information with location
- ➕ **Create Events** - Organize your own events
- 🎫 **Tickets** - Purchase and view tickets with QR codes
- 👤 **Profile** - User profile and settings
- 🔍 **Search** - Find events by name or description
- 📂 **Categories** - Filter by Music, Culture, etc.
- ❤️ **Save Events** - Bookmark your favorites

### Backend API Features
- 🔒 **JWT Authentication** - Secure token-based auth
- 📊 **Event Management** - Full CRUD operations
- 🎟️ **Ticket System** - Purchase and manage tickets
- 🗂️ **Categories** - Event categorization
- 🔍 **Search** - Advanced event search
- 👥 **User Management** - Profile and preferences
- 🗄️ **H2 Database** - In-memory database (dev)
- 🐘 **PostgreSQL Ready** - Easy production switch

---

## 📱 SCREENS INCLUDED

1. **Login Screen** - Beautiful gradient authentication
2. **Register Screen** - New user signup
3. **Home Screen** - Event feed with categories
4. **Explore Screen** - Interactive map view
5. **Event Detail Screen** - Full event information
6. **Create Event Screen** - Event creation form
7. **Tickets Screen** - View tickets with QR codes
8. **Profile Screen** - User profile and settings

---

## 🔌 API ENDPOINTS

### Authentication
- `POST /api/auth/register` - Register
- `POST /api/auth/login` - Login

### Events
- `GET /api/events/upcoming` - List events
- `GET /api/events/search?query=...` - Search
- `GET /api/events/{id}` - Get details
- `POST /api/events` - Create event
- `POST /api/events/{id}/save` - Save event

### Tickets
- `POST /api/tickets/purchase/{eventId}` - Buy ticket
- `GET /api/tickets/my-tickets` - My tickets

### Categories
- `GET /api/categories` - List all

---

## 🛠️ TECH STACK

### Mobile
- React Native 0.73
- Expo 50
- React Navigation 6
- Axios
- React Native Maps
- QR Code Generation
- AsyncStorage

### Backend
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- JWT (JJWT)
- H2 Database
- Lombok
- Maven

---

## 📊 PROJECT STATS

```
📱 Mobile App:
   • 8 Screens
   • 10 JavaScript files
   • Full navigation setup
   • Context API state management

⚙️ Backend API:
   • 26 Java files
   • 4 Controllers
   • 4 Models
   • 4 Repositories
   • 4 Services
   • JWT Security
   • Sample data included

📄 Documentation:
   • 5 Markdown guides
   • 2 Quick start scripts
   • Complete API docs
```

---

## 🎯 SAMPLE DATA

The app comes pre-loaded with:
- **5 Categories:** Music, University, Culture, Volunteering, More
- **5 Events:** Sunset Festival, Friday Night Party, Techno Night, etc.
- **1 Demo User:** demo@tirana.com / demo123

---

## 🔧 CONFIGURATION

### Mobile API URL
Edit `mobile/src/services/api.js`:
```javascript
const API_URL = 'http://localhost:8080/api';  // iOS Simulator
// const API_URL = 'http://10.0.2.2:8080/api';  // Android Emulator
// const API_URL = 'http://YOUR_IP:8080/api';   // Physical Device
```

### Backend Database
Edit `backend/src/main/resources/application.properties`:
- Currently using H2 (in-memory)
- Switch to PostgreSQL for production

---

## 🎨 CUSTOMIZATION

### Change Colors
Find and replace in screen files:
- Primary: `#8B5CF6` (Purple)
- Background: `#0A0A0F` (Dark)
- Cards: `#1F1F2E` (Dark Gray)

### Add Categories
Edit: `backend/src/main/java/com/tirana/events/config/DataInitializer.java`

### Add Events
Use the Create Event screen or add to DataInitializer

---

## 🚢 DEPLOYMENT

### Backend
```bash
cd backend
./mvnw clean package
java -jar target/events-backend-1.0.0.jar
```

### Mobile
```bash
cd mobile
expo build:ios
expo build:android
```

Or use EAS Build for modern builds.

---

## 🆘 TROUBLESHOOTING

### Backend won't start?
- Check Java version: `java -version` (need 17+)
- Port in use: `lsof -ti:8080 | xargs kill -9`

### Mobile can't connect?
- Check backend is running
- Update API_URL in `api.js`
- For physical device, use your computer's IP

### Dependencies error?
```bash
cd mobile
rm -rf node_modules package-lock.json
npm install
```

---

## 📖 LEARNING RESOURCES

This project demonstrates:
- ✅ Full-stack mobile development
- ✅ RESTful API design
- ✅ JWT authentication
- ✅ React Native best practices
- ✅ Spring Boot architecture
- ✅ JPA relationships
- ✅ Security configuration
- ✅ Map integration
- ✅ QR code generation

---

## 🎓 NEXT STEPS

1. **Run the app** - Follow QUICK_START.md
2. **Explore features** - Login and test everything
3. **Customize** - Change colors, add features
4. **Deploy** - Put it in production
5. **Learn** - Study the code structure

---

## 📞 SUPPORT

- Read the documentation files
- Check troubleshooting section
- Review the code comments
- Explore the sample data

---

## ✨ FEATURES CHECKLIST

- ✅ User authentication (login/register)
- ✅ Browse events with images
- ✅ Search events
- ✅ Filter by categories
- ✅ View event details
- ✅ Interactive maps
- ✅ Create new events
- ✅ Purchase tickets
- ✅ QR code generation
- ✅ Save favorite events
- ✅ User profile
- ✅ Dark theme UI
- ✅ Gradient designs
- ✅ Smooth navigation
- ✅ Sample data included

---

## 🎉 YOU'RE ALL SET!

Everything is ready to go. Just follow the quick start guide and you'll be running in minutes!

**Choose your path:**
- 🚀 **Fast:** [QUICK_START.md](QUICK_START.md)
- 📖 **Detailed:** [SETUP.md](SETUP.md)
- 📚 **Complete:** [README.md](README.md)

**Demo Login:**
- Email: `demo@tirana.com`
- Password: `demo123`

---

**Happy Coding! 🎊**

Built with ❤️ for the Tirana Events community
