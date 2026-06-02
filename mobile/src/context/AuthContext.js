import React, { createContext, useState, useContext, useEffect, useCallback } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import api, { setUnauthorizedHandler } from '../services/api';

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const logout = useCallback(async () => {
    try {
      // FIX: Clear ALL token storage locations to prevent phantom requests
      // Order matters: clear storage first, then headers, then state
      
      console.log('[Auth] Logging out...');
      
      // 1. Clear AsyncStorage (persistent storage)
      await AsyncStorage.multiRemove([
        'token',
        'refreshToken', 
        'user',
        'onboardingCompleted'
      ]);
      
      // 2. Clear axios default headers (in-memory)
      delete api.defaults.headers.common['Authorization'];
      
      // 3. Clear React state (triggers re-render and navigation)
      setUser(null);
      
      console.log('[Auth] Logout complete');
    } catch (error) {
      console.error('[Auth] Logout error:', error);
      // Force clear state even if AsyncStorage fails
      setUser(null);
      delete api.defaults.headers.common['Authorization'];
    }
  }, []);

  useEffect(() => {
    setUnauthorizedHandler(() => {
      console.log('[Auth] Unauthorized - triggering logout');
      logout();
    });
    loadUser();
  }, [logout]);

  const loadUser = async () => {
    try {
      const token = await AsyncStorage.getItem('token');
      const userData = await AsyncStorage.getItem('user');

      if (token && userData) {
        setUser(JSON.parse(userData));
        api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        try {
          const profileRes = await api.get('/users/me');
          setUser((prev) => ({ ...prev, ...profileRes.data }));
        } catch {
          // Profile fetch failed; keep stored user
        }
      }
    } catch (error) {
      console.error('Error loading user:', error);
    } finally {
      setLoading(false);
    }
  };

  const refreshProfile = async () => {
    try {
      const profileRes = await api.get('/users/me');
      setUser((prev) => {
        const merged = { ...(prev || {}), ...profileRes.data };
        AsyncStorage.setItem('user', JSON.stringify(merged));
        return merged;
      });
    } catch (error) {
      console.error('Error refreshing profile:', error);
    }
  };

  const login = async (email, password) => {
    try {
      const response = await api.post('/auth/login', { email, password });
      const { token, refreshToken, ...userData } = response.data;

      // Save tokens and user data
      await AsyncStorage.setItem('token', token);
      await AsyncStorage.setItem('refreshToken', refreshToken || token);
      await AsyncStorage.setItem('user', JSON.stringify(userData));

      // Set authorization header
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      setUser(userData);
      
      // Try to refresh profile, but don't fail login if this fails
      try {
        await refreshProfile();
      } catch (profileError) {
        console.log('[Auth] Profile refresh failed, continuing with login');
      }

      return { success: true };
    } catch (error) {
      console.error('[Auth] Login error:', error.response?.data || error.message);
      
      // Provide specific error messages
      let errorMessage = 'Login failed';
      if (error.response?.status === 401) {
        errorMessage = 'Invalid email or password';
      } else if (error.response?.status === 403) {
        errorMessage = 'Account is disabled or unauthorized';
      } else if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error.message) {
        errorMessage = error.message;
      }
      
      return {
        success: false,
        error: errorMessage,
      };
    }
  };

  const register = async (email, password, fullName) => {
    try {
      const response = await api.post('/auth/register', {
        email,
        password,
        fullName,
      });
      const { token, refreshToken, ...userData } = response.data;

      // Save tokens and user data
      await AsyncStorage.setItem('token', token);
      await AsyncStorage.setItem('refreshToken', refreshToken || token);
      await AsyncStorage.setItem('user', JSON.stringify(userData));

      // Set authorization header
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      setUser(userData);

      return { success: true };
    } catch (error) {
      console.error('[Auth] Registration error:', error.response?.data || error.message);
      
      // Provide specific error messages
      let errorMessage = 'Registration failed';
      if (error.response?.status === 409) {
        errorMessage = 'Email already exists';
      } else if (error.response?.status === 400) {
        errorMessage = error.response.data?.message || 'Invalid registration data';
      } else if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error.message) {
        errorMessage = error.message;
      }
      
      return {
        success: false,
        error: errorMessage,
      };
    }
  };

  return (
    <AuthContext.Provider
      value={{ user, loading, login, register, logout, refreshProfile }}
    >
      {children}
    </AuthContext.Provider>
  );
};
