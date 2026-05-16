# Tirana Events - Complete Implementation Plan

## Project Overview
Building a comprehensive event discovery and ticketing platform for Tirana, Albania with 27 features across 4 priority tiers.

**Tech Stack:**
- **Backend:** Spring Boot 3.2.0 + Java 17 + MySQL + JWT Auth
- **Frontend:** React Native (Expo 50) + React Navigation
- **Design:** Dark theme with purple accent (#7F77DD)

---

## 🔴 PRIORITY 1 - MUST-HAVE FEATURES

### [1] PERSONALISED FEED ALGORITHM ✅ IN PROGRESS

**Backend Components:**
- ✅ `UserInteraction` model - tracks views, saves, purchases
- ✅ `UserPreference` model - stores onboarding data & settings
- ✅ `PersonalizationService` - collaborative filtering algorithm
- ✅ `PersonalizationController` - API endpoints
- ✅ Repositories for interactions and preferences

**API Endpoints:**
- `GET /api/personalization/feed?limit=20` - Get personalized feed
- `POST /api/personalization/track` - Track user interaction
- `GET /api/personalization/onboarding/status` - Check if onboarding needed
- `POST /api/personalization/onboarding/complete` - Complete onboarding

**Frontend Screens:**
- `OnboardingScreen.js` - 5-tap interest picker on first launch
- Update `HomeScreen.js` - Use personalized feed endpoint
- Track interactions on event card clicks, saves, purchases

**Algorithm Logic:**
1. Track: views (weight 1), saves (weight 3), purchases (weight 5)
2. Collaborative filtering: "Users like you also attended..."
3. Recency bias: +4.0 weight for events in next 48 hours
4. Category preferences from onboarding: +3.0 weight
5. Location proximity: +1.5 weight if within 5km
6. Penalize recently viewed: -1.0 weight

---

### [2] FRIEND ACTIVITY FEED & SOCIAL LAYER ✅ IN PROGRESS

**Backend Components:**
- ✅ `FriendActivity` model - tracks friend actions
- ✅ `EventChat` model - group chat per event
- ✅ `EventInvite` model - invite system with deep links
- 🔄 `FriendActivityRepository`
- 🔄 `EventChatRepository`
- 🔄 `EventInviteRepository`
- 🔄 `SocialService` - friend activity logic
- 🔄 `SocialController` - API endpoints

**API Endpoints:**
- `GET /api/social/friends` - Get user's friends list
- `POST /api/social/friends/add` - Send friend request
- `GET /api/social/activity/feed` - Get friend activity feed
- `GET /api/social/events/{eventId}/attendees` - Get friends attending event
- `POST /api/social/events/{eventId}/chat` - Send chat message
- `GET /api/social/events/{eventId}/chat` - Get event chat messages
- `POST /api/social/invites/create` - Create invite with deep link
- `GET /api/social/invites/{token}` - Accept invite via deep link

**Frontend Screens:**
- `FriendsScreen.js` - Friends tab in Profile
- `EventChatScreen.js` - Group chat for ticket holders
- `ShareEventModal.js` - Share to Instagram, WhatsApp, Viber
- Update `EventDetailScreen.js` - Show "3 friends are going"
- Deep linking configuration in `app.json`

**Social Features:**
- Friend connections (follow/unfollow)
- Activity feed showing friend purchases/saves
- Event-specific group chat (ticket holders only)
- Share to social media with preview cards
- Deep link format: `tiranaevents://event/{eventId}?invite={token}`

---

### [3] ADVANCED FILTER SYSTEM

**Backend Components:**
- 🔄 `EventFilter` DTO - filter criteria
- 🔄 `FilterPreset` model - saved filter presets
- 🔄 Update `EventRepository` - add complex query methods
- 🔄 `FilterService` - filter logic
- 🔄 Update `EventController` - add filter endpoints

**API Endpoints:**
- `POST /api/events/filter` - Filter events with criteria
- `POST /api/filters/presets` - Save filter preset
- `GET /api/filters/presets` - Get user's saved presets
- `DELETE /api/filters/presets/{id}` - Delete preset

**Filter Criteria:**
- Price range: Free / 0-500 ALL / 500-2000 ALL / 2000+ ALL
- Distance: 1km / 3km / 5km / 10km from user location
- Time of day: Morning (6-12) / Afternoon (12-18) / Evening (18-22) / Late night (22-6)
- Date range: Today / This weekend / This week / Custom
- Accessibility: Wheelchair access, hearing loop, seated venue
- Category: Multiple selection
- Indoor/Outdoor

**Frontend Screens:**
- `FilterScreen.js` - Full filter interface
- `FilterPresetsModal.js` - Manage saved presets
- Update `ExploreScreen.js` - Add filter button
- Filter chips showing active filters

---

### [4] PUSH NOTIFICATION SYSTEM

**Backend Components:**
- 🔄 Add Firebase Admin SDK dependency to `pom.xml`
- 🔄 `PushNotification` model - notification log
- 🔄 `DeviceToken` model - store FCM tokens
- 🔄 `NotificationService` - send notifications
- 🔄 `NotificationScheduler` - scheduled notifications
- 🔄 `NotificationController` - manage preferences

**API Endpoints:**
- `POST /api/notifications/register` - Register device token
- `PUT /api/notifications/preferences` - Update notification settings
- `GET /api/notifications/preferences` - Get notification settings
- `GET /api/notifications/history` - Get notification history

**Notification Types:**
1. **Event Reminder:** 24h and 2h before event
2. **Price Drop:** When saved event reduces price
3. **Friend Activity:** "Your friend bought ticket to X"
4. **Nearby Event:** Geofenced push within 500m
5. **Quiet Hours:** Respect 23:00-08:00 (configurable)

**Frontend Integration:**
- Install `expo-notifications`
- Request notification permissions
- Register FCM token with backend
- Handle notification taps (deep linking)
- Notification preferences in Settings

---

### [5] OFFLINE TICKET ACCESS & WALLET INTEGRATION

**Backend Components:**
- 🔄 Update `Ticket` model - add wallet pass data
- 🔄 `WalletService` - generate Apple/Google Wallet passes
- 🔄 Add wallet pass generation libraries
- 🔄 Update `TicketController` - wallet endpoints

**API Endpoints:**
- `GET /api/tickets/{id}/download` - Download ticket for offline
- `GET /api/tickets/{id}/wallet/apple` - Generate Apple Wallet pass
- `GET /api/tickets/{id}/wallet/google` - Generate Google Wallet pass
- `POST /api/tickets/{id}/verify` - Verify ticket at entry (NFC)

**Frontend Features:**
- Download QR code to device storage at purchase
- "Downloaded" badge on ticket cards
- Apple Wallet integration (iOS)
- Google Wallet integration (Android)
- NFC tap-to-enter for VIP tickets
- Offline-first ticket storage using AsyncStorage

**Libraries:**
- `expo-file-system` - Save QR codes locally
- `expo-sharing` - Share to Wallet apps
- `react-native-nfc-manager` - NFC support

---

### [6] REAL-TIME ORGANISER ANALYTICS DASHBOARD

**Backend Components:**
- 🔄 `EventAnalytics` model - aggregated stats
- 🔄 `AnalyticsService` - calculate metrics
- 🔄 `AnalyticsController` - dashboard endpoints
- 🔄 Scheduled job to update analytics every 30 seconds

**API Endpoints:**
- `GET /api/analytics/events/{id}/overview` - Live ticket sales counter
- `GET /api/analytics/events/{id}/funnel` - Sales funnel data
- `GET /api/analytics/events/{id}/demographics` - Audience demographics
- `GET /api/analytics/events/{id}/traffic` - Traffic sources
- `GET /api/analytics/events/{id}/revenue` - Revenue summary
- `GET /api/analytics/events/{id}/export` - CSV export

**Metrics Tracked:**
- Views, saves, ticket page visits, purchases, completion rate
- Age groups, neighborhoods, interest categories
- Traffic sources: search, home feed, map, friend share, direct link
- Revenue: daily, weekly, total
- Real-time updates every 30 seconds

**Frontend Screens:**
- `OrganizerDashboardScreen.js` - Analytics dashboard
- Only accessible to users who created events
- Live updating charts and graphs
- CSV export button

---

## 🟡 PRIORITY 2 - HIGH IMPACT FEATURES

### [7] SMART EVENT BUNDLES
- Restaurant recommendations near venue (Google Places API)
- Transport options integration
- Weather-aware alternatives
- Group planner with cost splitting

### [8] EVENT CHECK-IN & MEMORIES WALL
- QR scan generates "proof of attendance" badge
- Community photo wall (48h after event)
- Annual "Your Tirana in Review" screen

### [9] REVIEWS & VIBE RATING SYSTEM
- Post-event rating prompt (2h after event)
- Vibe tags: "Too crowded", "Great music", etc.
- Verified-attendee-only reviews
- Organizer can reply

### [10] "HAPPENING NOW" LIVE TAB
- Events starting in next 2 hours
- Last-minute deal pricing (30-50% off)
- Live capacity indicator
- Geofenced push notifications

### [11] DYNAMIC PRICING & URGENCY DISPLAY
- Early bird countdown timers
- Price history chart
- "Only X tickets left" scarcity badge
- Waitlist feature for sold-out events

### [12] PROMOTIONAL TOOLS FOR ORGANISERS
- Paid boost feature
- Discount codes (STUDENT20, EARLYBIRD)
- Affiliate links for influencers
- Auto-generate share assets

### [13] GROUP TICKET SPLITTING & PAYMENT
- "Pay for friends" flow
- Albanian banking integration (Raiffeisen, BKT)
- Ticket transfer feature
- Refund policy display

### [14] POINTS & REWARDS LOYALTY SYSTEM
- Earn points: 100/ticket, 50/review, 200/referral
- Redeem for discounts and perks
- VIP tier for top 5% users
- Streak rewards

---

## 🔵 PRIORITY 3 - UNIQUE DIFFERENTIATORS

### [15] AI MOOD-BASED DISCOVERY
- Natural language search: "I feel like dancing tonight"
- NLP intent parsing
- 6 mood buttons: Energetic, Chill, Social, Cultural, Romantic, Adventurous
- AI learns from attendance patterns

### [16] WEATHER-AWARE EVENT CARDS
- Open-Meteo API integration (free)
- Weather icon on outdoor event cards
- 5-day forecast on event detail page
- Smart warning banners
- Push notification with weather update

### [17] AR VENUE PREVIEW MODE
- ARCore (Android) and ARKit (iOS)
- Point camera to see floating event cards
- "Look around" pan gesture
- Tap card to view event details

### [18] TIRANA EXPLORER BADGE SYSTEM
- "Culture Vulture", "Night Owl", "Tirana Original", etc.
- Badges displayed on profile
- Celebration animation on unlock
- Shareable badge cards

### [19] LOCAL INFLUENCER CURATOR PROGRAM
- Verified curator profiles
- "This Week in Tirana" curated lists
- Follow curators
- Commission on ticket sales

### [20] EVENT DUPLICATION & ORGANISER TEMPLATES
- "Duplicate this event" button
- Save as reusable template
- Series management (3-night festival)
- Bulk ticket import via CSV

### [21] TIRANA CITY GUIDE INTEGRATION
- "Getting There" section with bus stops
- Nearby restaurants (Google Places API)
- Travel time estimator
- Neighbourhood event guides

---

## 🟢 PRIORITY 4 - NICE TO HAVE

### [22] COMMUNITY BOARDS
- Interest-based mini-forums
- "Looking for someone to go with" threads
- Organizer teasers and lineup reveals
- Upvote system and moderation

### [23] MULTILINGUAL SUPPORT
- Albanian + English
- Auto-detect device language
- One-tap translate button
- Tourist mode

### [24] ACCESSIBILITY FEATURES
- VoiceOver/TalkBack compliance
- Font size preferences
- High contrast mode
- Filter by accessibility features

### [25] EVENT SERIES & MULTI-DAY EVENTS
- Multi-day event creation
- Day-by-day schedule view
- "Add all days to calendar"
- Individual day tickets vs full pass

### [26] IN-APP LIVE STREAMING PREVIEW
- 60-second live preview before sold-out events
- "Live Now" badge
- Reaction bar with emojis
- Direct CTA to waitlist/buy

### [27] SAVED ITINERARY PLANNER
- Drag and combine multiple events
- Travel time calculation
- Overlap warnings
- Share itinerary as link/image
- "Plan my weekend" AI suggestion

---

## Implementation Order

**Week 1-2:** Priority 1 Features [1-6]
**Week 3-4:** Priority 2 Features [7-14]
**Week 5-6:** Priority 3 Features [15-21]
**Week 7:** Priority 4 Features [22-27]
**Week 8:** Testing, polish, deployment

---

## Database Schema Updates Needed

### New Tables:
1. `user_interactions` - Track all user-event interactions
2. `user_preferences` - Onboarding and notification settings
3. `friend_activities` - Social activity feed
4. `event_chats` - Group chat messages
5. `event_invites` - Invite system with tokens
6. `filter_presets` - Saved filter configurations
7. `push_notifications` - Notification log
8. `device_tokens` - FCM tokens for push
9. `event_analytics` - Aggregated analytics data
10. `reviews` - Event reviews and ratings
11. `badges` - User achievement badges
12. `loyalty_points` - Points and rewards tracking
13. `discount_codes` - Promotional codes
14. `waitlist` - Sold-out event waitlist

### Updated Tables:
- `events` - Add: price, ticketsAvailable, isOutdoor, accessibility fields
- `tickets` - Add: walletPassUrl, nfcEnabled, transferredTo
- `users` - Add: points, tier, badges
- `categories` - Add: moodTags

---

## Third-Party Integrations

1. **Firebase Cloud Messaging** - Push notifications
2. **Open-Meteo API** - Weather data (free, no key)
3. **Google Places API** - Nearby venues and restaurants
4. **ARCore/ARKit** - AR venue preview
5. **Stripe** - Payment processing
6. **Apple Wallet / Google Wallet** - Digital tickets
7. **Instagram/WhatsApp/Viber** - Social sharing
8. **Albanian Banks API** - Local payment methods

---

## Design System

**Colors:**
- Background: `#0A0A0F`
- Card Background: `#1A1A24`
- Primary Purple: `#7F77DD`
- Text Primary: `#FFFFFF`
- Text Secondary: `#A0A0B0`
- Success: `#4CAF50`
- Warning: `#FF9800`
- Error: `#F44336`

**Typography:**
- Headings: Bold, 24-32px
- Body: Regular, 14-16px
- Captions: Regular, 12px

**Components:**
- Cards with rounded corners (16px)
- Purple gradient accents
- Bottom sheet modals
- Smooth animations (200-300ms)

---

## Next Steps

1. ✅ Complete Priority 1 backend models
2. 🔄 Create all repositories and services
3. 🔄 Build API controllers with proper error handling
4. 🔄 Create frontend screens and components
5. 🔄 Integrate APIs with frontend
6. 🔄 Test each feature thoroughly
7. 🔄 Move to Priority 2

**Current Status:** Implementing Priority 1 - Features [1] and [2] in progress
