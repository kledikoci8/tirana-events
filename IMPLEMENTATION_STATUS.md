# Tirana Events - Implementation Status

## 🔴 PRIORITY 1 - MUST-HAVE FEATURES

### ✅ [1] PERSONALISED FEED ALGORITHM - **BACKEND COMPLETE**

**Completed Backend Components:**
- ✅ `UserInteraction.java` - Tracks views (1), saves (3), purchases (5), shares (2), clicks (1)
- ✅ `UserPreference.java` - Onboarding data, notification settings, home location
- ✅ `UserInteractionRepository.java` - Query methods for interactions
- ✅ `UserPreferenceRepository.java` - User preferences queries
- ✅ `PersonalizationService.java` - Complete algorithm with:
  - Collaborative filtering
  - Recency bias (+4.0 for events in next 48h)
  - Category preferences (+3.0)
  - Location proximity (+1.5 within 5km)
  - View penalty (-1.0 for recently viewed)
- ✅ `PersonalizationController.java` - All API endpoints
- ✅ `OnboardingRequest.java` - DTO for onboarding
- ✅ `TrackInteractionRequest.java` - DTO for tracking

**API Endpoints:**
- `GET /api/personalization/feed?limit=20` - Get personalized feed
- `POST /api/personalization/track` - Track interaction (VIEW, SAVE, PURCHASE, SHARE, CLICK)
- `GET /api/personalization/onboarding/status` - Check if user needs onboarding
- `POST /api/personalization/onboarding/complete` - Complete 5-tap interest picker

**TODO - Frontend:**
- Create `OnboardingScreen.js` with 5-tap category selector
- Update `HomeScreen.js` to use personalized feed endpoint
- Add interaction tracking on all event card interactions
- Show "Recommended for you" badge on personalized events

---

### ✅ [2] FRIEND ACTIVITY FEED & SOCIAL LAYER - **BACKEND COMPLETE**

**Completed Backend Components:**
- ✅ `FriendActivity.java` - Activity types: PURCHASED_TICKET, SAVED_EVENT, ATTENDING, INTERESTED
- ✅ `EventChat.java` - Group chat messages with reply support
- ✅ `EventInvite.java` - Invite system with tokens and deep links
- ✅ `FriendActivityRepository.java` - Friend activity queries
- ✅ `EventChatRepository.java` - Chat message queries
- ✅ `EventInviteRepository.java` - Invite queries
- ✅ `SocialService.java` - Complete social logic:
  - Record and retrieve friend activities
  - Count friends attending events
  - Event group chat (ticket holders only)
  - Create and accept invites with deep links
  - Follow/unfollow users
- ✅ `SocialController.java` - All API endpoints
- ✅ DTOs: `UserDTO`, `FriendActivityDTO`, `EventChatDTO`, `EventInviteDTO`, `FriendsAttendingDTO`, `SendChatRequest`, `CreateInviteRequest`

**API Endpoints:**
- `GET /api/social/friends` - Get friends list
- `POST /api/social/friends/{userId}/follow` - Follow user
- `DELETE /api/social/friends/{userId}/unfollow` - Unfollow user
- `GET /api/social/activity/feed?limit=50` - Get friend activity feed
- `GET /api/social/events/{eventId}/attendees` - Get friends attending event
- `POST /api/social/events/{eventId}/chat` - Send chat message
- `GET /api/social/events/{eventId}/chat?since=` - Get chat messages
- `POST /api/social/invites/create` - Create invite with deep link
- `POST /api/social/invites/{token}/accept` - Accept invite
- `GET /api/social/invites/{token}` - Get invite details

**Deep Link Format:** `tiranaevents://event/{eventId}?invite={token}`

**TODO - Frontend:**
- Create `FriendsScreen.js` in Profile tab
- Create `EventChatScreen.js` for group chat
- Create `ShareEventModal.js` with Instagram, WhatsApp, Viber options
- Update `EventDetailScreen.js` to show "3 of your friends are going"
- Configure deep linking in `app.json` and handle in `App.js`
- Add share functionality with native share sheet

---

### 🔄 [3] ADVANCED FILTER SYSTEM - **TODO**

**Backend Components Needed:**
- `EventFilter.java` - DTO with filter criteria
- `FilterPreset.java` - Model for saved presets
- Update `Event.java` - Add: price, ticketsAvailable, isOutdoor, accessibility fields
- `FilterPresetRepository.java`
- `FilterService.java` - Complex filtering logic
- Update `EventController.java` - Add filter endpoints

**Filter Criteria:**
- Price range: Free, 0-500 ALL, 500-2000 ALL, 2000+ ALL
- Distance: 1km, 3km, 5km, 10km from user location
- Time of day: Morning (6-12), Afternoon (12-18), Evening (18-22), Late night (22-6)
- Date range: Today, This weekend, This week, Custom
- Accessibility: Wheelchair, hearing loop, seated venue
- Category: Multiple selection
- Indoor/Outdoor toggle

**Frontend Components Needed:**
- `FilterScreen.js` - Full-screen filter interface
- `FilterPresetsModal.js` - Manage saved presets
- Update `ExploreScreen.js` - Add filter button
- Filter chips component

---

### 🔄 [4] PUSH NOTIFICATION SYSTEM - **TODO**

**Backend Components Needed:**
- Add Firebase Admin SDK to `pom.xml`
- `PushNotification.java` - Notification log model
- `DeviceToken.java` - Store FCM tokens
- `PushNotificationRepository.java`
- `DeviceTokenRepository.java`
- `NotificationService.java` - Send notifications via FCM
- `NotificationScheduler.java` - Scheduled notifications (@Scheduled)
- `NotificationController.java` - Manage preferences

**Notification Types:**
1. Event Reminder - 24h and 2h before event
2. Price Drop - When saved event reduces price
3. Friend Activity - "Your friend bought ticket to X"
4. Nearby Event - Geofenced push within 500m
5. Quiet Hours - Respect 23:00-08:00 (configurable)

**Frontend Components Needed:**
- Install `expo-notifications`
- Request permissions on app launch
- Register FCM token with backend
- Handle notification taps (deep linking)
- `NotificationSettingsScreen.js` in Profile > Settings

---

### 🔄 [5] OFFLINE TICKET ACCESS & WALLET INTEGRATION - **TODO**

**Backend Components Needed:**
- Update `Ticket.java` - Add: walletPassUrl, nfcEnabled, downloadedAt
- `WalletService.java` - Generate Apple/Google Wallet passes
- Add wallet pass libraries to `pom.xml`
- Update `TicketController.java` - Add wallet endpoints

**API Endpoints Needed:**
- `GET /api/tickets/{id}/download` - Download ticket for offline
- `GET /api/tickets/{id}/wallet/apple` - Generate .pkpass file
- `GET /api/tickets/{id}/wallet/google` - Generate Google Wallet pass
- `POST /api/tickets/{id}/verify` - Verify ticket (NFC/QR scan)

**Frontend Components Needed:**
- Install `expo-file-system`, `expo-sharing`, `react-native-nfc-manager`
- Download QR code to device storage at purchase
- "Downloaded" badge on ticket cards
- Apple Wallet button (iOS)
- Google Wallet button (Android)
- NFC tap-to-enter for VIP tickets
- Offline-first ticket storage using AsyncStorage

---

### 🔄 [6] REAL-TIME ORGANISER ANALYTICS DASHBOARD - **TODO**

**Backend Components Needed:**
- `EventAnalytics.java` - Aggregated stats model
- `EventAnalyticsRepository.java`
- `AnalyticsService.java` - Calculate metrics
- `AnalyticsController.java` - Dashboard endpoints
- `AnalyticsScheduler.java` - Update stats every 30 seconds

**Metrics to Track:**
- Live ticket sales counter
- Sales funnel: views → saves → ticket page → purchase → completion
- Demographics: age groups, neighborhoods, interest categories
- Traffic sources: search, home feed, map, friend share, direct link
- Revenue: daily, weekly, total

**API Endpoints Needed:**
- `GET /api/analytics/events/{id}/overview` - Live stats
- `GET /api/analytics/events/{id}/funnel` - Sales funnel
- `GET /api/analytics/events/{id}/demographics` - Audience data
- `GET /api/analytics/events/{id}/traffic` - Traffic sources
- `GET /api/analytics/events/{id}/revenue` - Revenue summary
- `GET /api/analytics/events/{id}/export` - CSV export

**Frontend Components Needed:**
- `OrganizerDashboardScreen.js` - Full analytics dashboard
- Real-time charts (use `react-native-chart-kit`)
- CSV export functionality
- Only accessible to event organizers

---

## 🟡 PRIORITY 2 - HIGH IMPACT FEATURES (8 features)

All Priority 2 features are **TODO**. These include:
- [7] Smart Event Bundles
- [8] Event Check-in & Memories Wall
- [9] Reviews & Vibe Rating System
- [10] "Happening Now" Live Tab
- [11] Dynamic Pricing & Urgency Display
- [12] Promotional Tools for Organisers
- [13] Group Ticket Splitting & Payment
- [14] Points & Rewards Loyalty System

---

## 🔵 PRIORITY 3 - UNIQUE DIFFERENTIATORS (7 features)

All Priority 3 features are **TODO**. These include:
- [15] AI Mood-Based Discovery
- [16] Weather-Aware Event Cards
- [17] AR Venue Preview Mode
- [18] Tirana Explorer Badge System
- [19] Local Influencer Curator Program
- [20] Event Duplication & Organiser Templates
- [21] Tirana City Guide Integration

---

## 🟢 PRIORITY 4 - NICE TO HAVE (6 features)

All Priority 4 features are **TODO**. These include:
- [22] Community Boards
- [23] Multilingual Support
- [24] Accessibility Features
- [25] Event Series & Multi-Day Events
- [26] In-App Live Streaming Preview
- [27] Saved Itinerary Planner

---

## Summary

**Completed:** 2/27 features (Backend only)
**In Progress:** 0/27 features
**TODO:** 25/27 features

**Priority 1 Status:** 2/6 backend complete, 0/6 frontend complete

---

## Next Steps

1. **Complete Priority 1 Backend** (Features 3-6)
   - Advanced Filter System
   - Push Notification System
   - Offline Ticket Access & Wallet Integration
   - Real-Time Organiser Analytics Dashboard

2. **Build Priority 1 Frontend** (All 6 features)
   - Create all React Native screens
   - Integrate with backend APIs
   - Test user flows

3. **Move to Priority 2** (8 features)

4. **Continue with Priority 3 & 4** (13 features)

---

## Estimated Timeline

- **Priority 1 (6 features):** 2-3 weeks
- **Priority 2 (8 features):** 2-3 weeks
- **Priority 3 (7 features):** 2 weeks
- **Priority 4 (6 features):** 1 week
- **Testing & Polish:** 1 week

**Total:** 8-10 weeks for complete implementation

---

## Database Migrations Needed

Run these SQL commands to create new tables:

```sql
-- User Interactions
CREATE TABLE user_interactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    weight INT,
    timestamp DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (event_id) REFERENCES events(id),
    INDEX idx_user_timestamp (user_id, timestamp),
    INDEX idx_event (event_id)
);

-- User Preferences
CREATE TABLE user_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    onboarding_completed BOOLEAN DEFAULT FALSE,
    notify_event_reminder BOOLEAN DEFAULT TRUE,
    notify_price_drop BOOLEAN DEFAULT TRUE,
    notify_friend_activity BOOLEAN DEFAULT TRUE,
    notify_nearby_events BOOLEAN DEFAULT TRUE,
    quiet_hours_start INT DEFAULT 23,
    quiet_hours_end INT DEFAULT 8,
    home_latitude DOUBLE,
    home_longitude DOUBLE,
    home_address VARCHAR(255),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- User Preference Categories (Many-to-Many)
CREATE TABLE user_preference_categories (
    preference_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (preference_id, category_id),
    FOREIGN KEY (preference_id) REFERENCES user_preferences(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Friend Activities
CREATE TABLE friend_activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    activity_type VARCHAR(50) NOT NULL,
    timestamp DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (event_id) REFERENCES events(id),
    INDEX idx_user_timestamp (user_id, timestamp),
    INDEX idx_event (event_id)
);

-- Event Chats
CREATE TABLE event_chats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    message VARCHAR(1000) NOT NULL,
    timestamp DATETIME NOT NULL,
    reply_to_id BIGINT,
    FOREIGN KEY (event_id) REFERENCES events(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (reply_to_id) REFERENCES event_chats(id),
    INDEX idx_event_timestamp (event_id, timestamp)
);

-- Event Invites
CREATE TABLE event_invites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    inviter_id BIGINT NOT NULL,
    invitee_id BIGINT,
    invitee_email VARCHAR(255),
    invitee_phone VARCHAR(50),
    invite_token VARCHAR(255) UNIQUE NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    accepted_at DATETIME,
    FOREIGN KEY (event_id) REFERENCES events(id),
    FOREIGN KEY (inviter_id) REFERENCES users(id),
    FOREIGN KEY (invitee_id) REFERENCES users(id),
    INDEX idx_token (invite_token)
);
```

---

## Configuration Updates Needed

### application.properties
```properties
# Firebase Cloud Messaging
fcm.server-key=YOUR_FCM_SERVER_KEY

# Apple Wallet
apple.wallet.team-id=YOUR_TEAM_ID
apple.wallet.pass-type-id=YOUR_PASS_TYPE_ID
apple.wallet.certificate-path=/path/to/certificate.p12
apple.wallet.certificate-password=YOUR_PASSWORD

# Google Wallet
google.wallet.issuer-id=YOUR_ISSUER_ID
google.wallet.service-account-json=/path/to/service-account.json

# Open-Meteo API (no key needed)
weather.api.base-url=https://api.open-meteo.com/v1

# Google Places API
google.places.api-key=YOUR_GOOGLE_PLACES_API_KEY
```

### pom.xml additions needed
```xml
<!-- Firebase Admin SDK -->
<dependency>
    <groupId>com.google.firebase</groupId>
    <artifactId>firebase-admin</artifactId>
    <version>9.2.0</version>
</dependency>

<!-- Wallet Pass Generation -->
<dependency>
    <groupId>de.brendamour</groupId>
    <artifactId>jpasskit</artifactId>
    <version>0.2.0</version>
</dependency>

<!-- CSV Export -->
<dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>5.8</version>
</dependency>

<!-- Scheduling -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-quartz</artifactId>
</dependency>
```

---

**Last Updated:** Priority 1 - Features [1] and [2] backend complete
