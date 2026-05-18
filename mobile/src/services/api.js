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

// RESPONSE INTERCEPTOR: Handle 401/403 errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      console.error(`[API] ${error.response.status} - Session expired or unauthorized`);
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
