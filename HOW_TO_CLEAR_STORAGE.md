# 🔧 How to Clear Storage - Simple Methods

## ✅ METHOD 1: Use Expo Dev Menu (EASIEST - NO DEBUGGER NEEDED)

### Step 1: Open Dev Menu
**On iPhone/Simulator:**
- Shake your device, OR
- Press `Cmd + D` in iOS Simulator

**On Android/Emulator:**
- Shake your device, OR
- Press `Cmd + M` in Android Emulator

### Step 2: Enable Debug Mode
You'll see a menu. Tap **"Debug Remote JS"**

### Step 3: Browser Opens
A browser tab will open automatically showing "Debugger session active"

### Step 4: Open Browser Console
- **Chrome/Brave**: Press `Cmd + Option + J` (Mac) or `Ctrl + Shift + J` (Windows)
- **Safari**: Press `Cmd + Option + C` (Mac)
- **Firefox**: Press `Cmd + Option + K` (Mac) or `Ctrl + Shift + K` (Windows)

### Step 5: Clear Storage
In the console, type:
```javascript
AsyncStorage.clear()
```
Press Enter. You'll see: `Promise {<pending>}`

### Step 6: Restart App
- Close the app completely (swipe up to kill it)
- Reopen the app
- You'll see the login screen!

---

## ✅ METHOD 2: Use Metro Bundler Menu (EVEN EASIER)

### Step 1: Look at Your Terminal
Where you ran `npm start`, you should see Metro Bundler running.

### Step 2: Press 'Shift + M'
In the terminal, press `Shift + M` to open the menu.

### Step 3: Select Option
Look for an option like:
- "Clear AsyncStorage"
- "Reset cache and clear AsyncStorage"

Select it and press Enter.

### Step 4: Restart App
Kill and reopen the app.

---

## ✅ METHOD 3: Add a Clear Button to Your App (PERMANENT SOLUTION)

I can add a "Clear Storage" button to your Profile screen so you can do this anytime!

Would you like me to add this button?

---

## ✅ METHOD 4: Use Expo Go App Menu (If Using Expo Go)

### Step 1: Open Expo Go Menu
Shake your device or press `Cmd + D`

### Step 2: Look for Options
You might see:
- "Clear AsyncStorage"
- "Clear cache"

Tap it!

---

## 🎯 RECOMMENDED: METHOD 1 (Dev Menu + Browser Console)

This is the most reliable method. Here's a visual guide:

```
1. In your app → Shake device or press Cmd+D
   ↓
2. Tap "Debug Remote JS"
   ↓
3. Browser opens automatically
   ↓
4. Press Cmd+Option+J (Mac) to open console
   ↓
5. Type: AsyncStorage.clear()
   ↓
6. Press Enter
   ↓
7. Close and reopen app
   ↓
8. Register new account
   ↓
9. DONE! ✅
```

---

## 📱 Visual Guide for Opening Console

### On Mac (Chrome/Brave):
```
Cmd + Option + J
```

### On Mac (Safari):
```
1. Enable Developer Menu:
   Safari → Preferences → Advanced → Show Develop menu
2. Then press: Cmd + Option + C
```

### On Windows (Chrome):
```
Ctrl + Shift + J
```

---

## 🆘 If Nothing Works: Manual Method

### Create a Logout Button

I can add code to your ProfileScreen that includes a "Clear All Data" button. This will:
1. Clear AsyncStorage
2. Log you out
3. Reset the app

Would you like me to add this?

---

## 🎯 DO THIS NOW (Step by Step)

1. **Make sure your app is running**
   ```bash
   cd mobile
   npm start
   ```

2. **In the app, shake your device** (or press `Cmd + D` in simulator)

3. **Tap "Debug Remote JS"**

4. **A browser tab opens** - You'll see "Debugger session active"

5. **Open browser console**:
   - Mac Chrome: `Cmd + Option + J`
   - Mac Safari: `Cmd + Option + C`
   - Windows: `Ctrl + Shift + J`

6. **Type this and press Enter**:
   ```javascript
   AsyncStorage.clear()
   ```

7. **Close the app** (swipe up to kill it)

8. **Reopen the app** - You'll see login screen!

9. **Register a new account**:
   - Email: test@test.com
   - Password: test123

10. **DONE!** ✅

---

## 🔍 Troubleshooting

### "Debug Remote JS" option is grayed out
- Make sure Metro bundler is running
- Try restarting Metro: `npm start -- --reset-cache`

### Browser doesn't open
- Manually open: http://localhost:19000/debugger-ui/
- Or: http://localhost:19001/debugger-ui/

### Can't find console in browser
- Look for "Console" tab at the bottom or top of browser
- Try pressing F12 to open developer tools

### AsyncStorage is not defined
- Make sure you're in the right console (the one that opened from "Debug Remote JS")
- Try: `window.AsyncStorage.clear()`

---

## 💡 Alternative: I Can Add a Button

If this is too complicated, I can add a "Clear Storage & Logout" button to your Profile screen. 

Just say "add clear storage button" and I'll do it!

---

## Summary

**Easiest Method**:
1. Shake device → Tap "Debug Remote JS"
2. Browser opens → Press `Cmd + Option + J`
3. Type: `AsyncStorage.clear()`
4. Restart app → Register new account
5. Done! ✅

**Can't do it?** → Ask me to add a "Clear Storage" button to your app!
