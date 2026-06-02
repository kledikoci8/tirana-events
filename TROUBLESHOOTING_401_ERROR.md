# 🔧 Troubleshooting 401 "Session expired or unauthorized" Error

## ✅ FIXED - Automatic Token Refresh Implemented

The 401 error has been fixed with automatic token refresh functionality. The app will now:

1. **Automatically refresh expired tokens** - No more manual re-login required
2. **Queue requests during refresh** - All API calls wait for token refresh
3. **Fallback to logout** - Only logs out if refresh truly fails
4. **Better error messages** - Shows specific error reasons

---

## 🔍 What Was the Issue?

**Problem:** JWT tokens expire after a certain time, causing 401 errors when trying to access protected endpoints.

**Before:** App would immediately log out on 401 error
**After:** App tries to refresh the token first, only logs out if refresh fails

---

## 🛠️ What I Fixed

### 1. Added Token Refresh Interceptor (`api.js`)
```javascript
// Automatically refreshes token on 401 error
// Queues pending requests during refresh
// Retries original request with new token
```

### 2. Improved Error Handling (`AuthContext.js`)
```javascript
// Better error messages for login/register
// Fallback for missing refresh tokens
// More resilient profile loading
```

---

## 🚀 How It Works Now

```
User → API Request → 401 Error
         ↓
    Token Refresh (automatic)
         ↓
    Retry Original Request
         ↓
    Success! ✅
```

If refresh fails:
```
Token Refresh Failed
         ↓
    Clear All Tokens
         ↓
    Redirect to Login
```

---

## 🧪 Testing the Fix

### Test 1: Normal Flow (Should Work)
1. Login to the app
2. Use the app normally
3. Token refreshes automatically when it expires
4. No 401 errors!

### Test 2: Expired Refresh Token (Should Log Out)
1. Login to the app
2. Wait for both access token AND refresh token to expire
3. Try to use app
4. Should gracefully redirect to login screen

### Test 3: Network Error (Should Show Error)
1. Turn off backend server
2. Try to use app
3. Should show network error, not 401 error

---

## 🔍 Quick Diagnostics

If you still see 401 errors, check:

### 1. Backend is Running
```bash
cd backend
./mvnw spring-boot:run
```

### 2. Database is Running
```bash
# Check if MySQL is running on port 3308
mysql -u root -h 127.0.0.1 -P 3308 -e "SELECT 1;"
```

### 3. JWT Secret is Set
```bash
echo $JWT_SECRET
# Should print a long random string
# If empty, run: export JWT_SECRET=$(./backend/generate-jwt-secret.sh)
```

### 4. Token Exists in App
Open React Native Debugger and check AsyncStorage:
- `token` - Should have JWT value
- `refreshToken` - Should have refresh token value
- `user` - Should have user data

---

## 🔄 Manual Fix Steps (If Needed)

### Clear App Data and Re-login
1. Stop the mobile app
2. Clear AsyncStorage:
```javascript
// In React Native Debugger Console:
AsyncStorage.clear()
```
3. Restart app
4. Login again

### Reset Backend Auth State
1. Stop backend
2. Clear database tokens:
```sql
USE tiranaevents;
DELETE FROM refresh_tokens WHERE revoked = true;
```
3. Restart backend
4. Try logging in again

---

## 📝 Common 401 Scenarios

| Scenario | Old Behavior | New Behavior |
|----------|-------------|--------------|
| Token expires during use | Immediate logout | Auto-refresh, continue |
| Refresh token expires | Immediate logout | Logout (expected) |
| Invalid token | Immediate logout | Try refresh, then logout |
| Backend restart | Immediate logout | Try refresh, then logout |
| Network error | Could cause 401 | Shows network error |

---

## ✅ Verification Checklist

- [x] Token refresh interceptor added to `api.js`
- [x] Better error handling in `AuthContext.js`
- [x] Fallback for missing refresh tokens
- [x] Request queueing during token refresh
- [x] Specific error messages for different scenarios
- [x] Backend `/auth/refresh` endpoint exists
- [x] Refresh token stored in AsyncStorage

---

## 🎯 Expected Behavior Now

### When Token Expires:
1. App makes API request
2. Gets 401 error
3. Automatically calls `/auth/refresh`
4. Gets new token
5. Retries original request
6. Success! User doesn't notice anything

### When Refresh Fails:
1. App tries to refresh token
2. Refresh fails (maybe refresh token also expired)
3. Clears all tokens from storage
4. Shows "Session expired" message
5. Redirects to login screen
6. User logs in again

---

## 🐛 Still Having Issues?

If you're still seeing 401 errors after these fixes:

1. **Check the console logs** - Look for `[API]` and `[Auth]` prefixed messages
2. **Verify backend is running** - Try accessing `http://localhost:8080/api/auth/login` directly
3. **Check JWT_SECRET** - Make sure it's set in backend environment
4. **Clear app data** - Remove AsyncStorage and login fresh
5. **Check token expiration time** - Maybe tokens are expiring too quickly

### Backend Token Configuration
Check `application.properties`:
```properties
# Access token expires in 15 minutes (900000 ms)
jwt.expiration=900000

# Refresh token expires in 7 days (604800000 ms)
jwt.refresh-expiration=604800000
```

If tokens expire too quickly, increase these values.

---

## 📞 Need More Help?

Check these files for implementation details:
- `/mobile/src/services/api.js` - Token refresh interceptor
- `/mobile/src/context/AuthContext.js` - Login/logout logic
- `/backend/src/main/java/com/tirana/events/controller/AuthController.java` - Refresh endpoint
- `/backend/src/main/java/com/tirana/events/service/AuthService.java` - Token generation

All authentication logic is well-documented with comments.

---

## ✨ Summary

**The 401 error is now FIXED!** The app will automatically refresh expired tokens and only log you out if refresh truly fails. This provides a much better user experience.

**Key Improvement:** Instead of immediately logging out on 401, the app now:
1. Tries to refresh the token
2. Queues pending requests
3. Retries with new token
4. Only logs out if refresh fails

Your app is now more resilient and user-friendly! 🎉
