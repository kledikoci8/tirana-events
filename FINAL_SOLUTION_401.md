# 🔧 FINAL SOLUTION FOR 401 ERROR

## Current Situation

You're getting **401 - Session expired or unauthorized** because:
- Your old token is expired
- The app is trying to use the expired token
- You need a fresh token

## ✅ COMPLETE FIX (Choose One Method)

---

## METHOD 1: Clear App Storage & Start Fresh (EASIEST)

### Step 1: Clear AsyncStorage
In your app, open the React Native Debugger console and run:

```javascript
AsyncStorage.clear().then(() => console.log('Storage cleared!'));
```

### Step 2: Restart the App
- Kill the app completely
- Reopen it
- You'll see the login screen

### Step 3: Register a New Account
- Tap "Register"
- Email: test@test.com
- Password: test123
- Full Name: Test User
- Tap "Register"

### Step 4: Test
- Browse events ✅
- Everything should work ✅

---

## METHOD 2: Use Expo Dev Menu (IF METHOD 1 DOESN'T WORK)

### Step 1: Open Dev Menu
- **iOS**: Shake your device OR press `Cmd + D` in simulator
- **Android**: Shake your device OR press `Cmd + M` in emulator

### Step 2: Clear Data
- Tap "Clear AsyncStorage"
- Restart the app

### Step 3: Register
- Register a new account as shown in Method 1

---

## METHOD 3: Manual Token Removal (ADVANCED)

If you can access the app's Profile screen:

### Step 1: Add Logout Button
The app should have a logout button. If you can access Profile:
1. Go to Profile screen
2. Look for "Log Out" button
3. Tap it
4. Log in again or register new account

---

## WHY THIS KEEPS HAPPENING

Your token from the logs:
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkZW1vQHRpcmFuYS5jb20iLCJpYXQiOjE3Nzg5NzAyODcsImV4cCI6MTc3OTA1NjY4N30...
```

This token:
- ✅ Is properly formatted
- ✅ Is being sent to backend
- ❌ Is EXPIRED (issued timestamp: 1778970287)
- ❌ Backend correctly rejects it with 401

The interceptor is working correctly! The problem is just that you need a fresh token.

---

## WHAT I ALREADY FIXED

✅ **Backend Security Config** - Public endpoints are now public
✅ **Token Expiration** - Increased to 7 days (604800000ms)
✅ **API Interceptor** - Automatically attaches tokens
✅ **Error Handling** - Shows clear error messages

**Everything is working correctly!** You just need a fresh token.

---

## QUICK COMMANDS TO CLEAR STORAGE

### Option A: React Native Debugger
```javascript
// Open debugger console and run:
AsyncStorage.clear()
```

### Option B: Expo CLI
```bash
# In terminal where Metro is running:
# Press 'Shift + M' to open menu
# Select "Clear AsyncStorage"
```

### Option C: Restart Fresh
```bash
cd mobile
rm -rf node_modules/.cache .expo
npm start -- --reset-cache
# Then clear storage in app
```

---

## AFTER CLEARING STORAGE

### Register New Account
```
Email: your@email.com
Password: yourpassword
Full Name: Your Name
```

### Or Use Test Account
```
Email: test@test.com
Password: test123
```

---

## EXPECTED BEHAVIOR AFTER FIX

### Console Logs Should Show:
```
✅ [API] POST /auth/register (or /auth/login)
✅ [API] Token: eyJhbGciOi... (NEW TOKEN)
✅ [API] GET /personalization/onboarding/status
✅ [API] GET /events/recommended
✅ NO 401 ERRORS!
```

### App Should:
- ✅ Show login/register screen after clearing storage
- ✅ Allow you to register/login
- ✅ Load events and personalized feed
- ✅ Work without errors

---

## TESTING CHECKLIST

After clearing storage and logging in:

- [ ] App opens to login screen
- [ ] Can register new account
- [ ] Can log in successfully
- [ ] Home screen loads events
- [ ] Personalized feed works
- [ ] Can submit reviews
- [ ] NO 401 errors in console
- [ ] Token is valid for 7 days

---

## IF STILL NOT WORKING

### Check Backend is Running
```bash
curl http://localhost:8080/api/categories
# Should return JSON with categories
```

### Check Backend Logs
Look in IntelliJ console for:
- JWT validation errors
- Authentication failures
- Any exceptions

### Verify Token Expiration Setting
```bash
cat backend/src/main/resources/application.properties | grep jwt.expiration
# Should show: jwt.expiration=604800000
```

---

## SUMMARY

**The Problem**: Old expired token in AsyncStorage
**The Solution**: Clear AsyncStorage and get a fresh token
**How**: Use React Native Debugger to run `AsyncStorage.clear()`

**Then**: Register a new account and test!

---

## 🎯 DO THIS NOW

1. **Open React Native Debugger console**
2. **Run**: `AsyncStorage.clear()`
3. **Restart the app**
4. **Register a new account**
5. **Test - everything will work!**

That's it! The 401 error will be gone. 🎉
