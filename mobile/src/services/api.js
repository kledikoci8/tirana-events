import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { API_BASE_URL } from '../config/apiConfig';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

let onUnauthorized = null;

export const setUnauthorizedHandler = (handler) => {
  onUnauthorized = handler;
};

// REQUEST INTERCEPTOR: Automatically attach auth token to every request
api.interceptors.request.use(
  async (config) => {
    try {
      const token = await AsyncStorage.getItem('token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
        console.log(`[API] ${config.method.toUpperCase()} ${config.url}`);
        console.log(`[API] Token: ${token.substring(0, 10)}...`);
        console.log(`[API] Headers:`, config.headers);
      } else {
        console.log(`[API] ${config.method.toUpperCase()} ${config.url} - NO TOKEN`);
      }
    } catch (error) {
      console.error('[API] Error reading token:', error);
    }
    return config;
  },
  (error) => {
    console.error('[API] Request interceptor error:', error);
    return Promise.reject(error);
  }
);

// FIX: Add token refresh logic to handle 401 errors automatically
let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

// RESPONSE INTERCEPTOR: Handle 401/403 errors with token refresh
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // If error is 401 and we haven't tried to refresh yet
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        // If already refreshing, queue this request
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        }).then(token => {
          originalRequest.headers['Authorization'] = 'Bearer ' + token;
          return api(originalRequest);
        }).catch(err => {
          return Promise.reject(err);
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const refreshToken = await AsyncStorage.getItem('refreshToken');
        
        if (!refreshToken) {
          throw new Error('No refresh token available');
        }

        console.log('[API] Attempting token refresh...');
        const response = await axios.post(`${API_BASE_URL}/auth/refresh`, {
          refreshToken
        });

        const { token: newToken, refreshToken: newRefreshToken } = response.data;
        
        // FIX BUG #1: Save BOTH new access token AND new refresh token
        // Backend rotates refresh tokens, so we must save the new one
        await AsyncStorage.setItem('token', newToken);
        if (newRefreshToken) {
          await AsyncStorage.setItem('refreshToken', newRefreshToken);
        }
        
        api.defaults.headers.common['Authorization'] = `Bearer ${newToken}`;
        originalRequest.headers['Authorization'] = `Bearer ${newToken}`;
        
        processQueue(null, newToken);
        isRefreshing = false;
        
        console.log('[API] Token refresh successful');
        return api(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        isRefreshing = false;
        
        console.error('[API] Token refresh failed:', refreshError);
        
        // Clear tokens and notify logout
        await AsyncStorage.removeItem('token');
        await AsyncStorage.removeItem('refreshToken');
        await AsyncStorage.removeItem('user');
        
        if (onUnauthorized) {
          onUnauthorized();
        }
        
        return Promise.reject(refreshError);
      }
    }

    // Handle other 403 errors
    if (error.response?.status === 403) {
      console.error('[API] 403 - Forbidden');
      if (onUnauthorized) {
        onUnauthorized();
      }
    }

    if (error.code === 'ECONNABORTED') {
      error.message = 'Connection timed out. Tap to retry.';
    }
    
    return Promise.reject(error);
  }
);

export default api;
