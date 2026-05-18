import { Alert } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

export const handleApiError = async (error, navigation) => {
  if (error.response) {
    const status = error.response.status;
    const message = error.response.data?.message || error.response.data?.error;
    
    switch (status) {
      case 401:
      case 403:
        // Unauthorized - logout
        await AsyncStorage.clear();
        Alert.alert(
          'Session Expired',
          'Please login again',
          [{ 
            text: 'OK', 
            onPress: () => navigation?.replace('Login')
          }]
        );
        break;
        
      case 404:
        Alert.alert('Not Found', message || 'The requested resource was not found');
        break;
        
      case 429:
        Alert.alert('Too Many Requests', 'Please slow down and try again in a moment');
        break;
        
      case 500:
        Alert.alert('Server Error', 'Something went wrong. Please try again later.');
        break;
        
      default:
        Alert.alert('Error', message || 'An error occurred');
    }
  } else if (error.request) {
    // Network error
    Alert.alert(
      'Network Error',
      'Please check your internet connection and try again'
    );
  } else {
    Alert.alert('Error', 'An unexpected error occurred');
  }
  
  // Log error for debugging
  console.error('API Error:', error);
};

export const isNetworkError = (error) => {
  return error.request && !error.response;
};

export const getErrorMessage = (error) => {
  if (error.response?.data?.message) {
    return error.response.data.message;
  }
  if (error.response?.data?.error) {
    return error.response.data.error;
  }
  if (error.message) {
    return error.message;
  }
  return 'An unexpected error occurred';
};
