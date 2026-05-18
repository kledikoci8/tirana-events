import { Platform } from 'react-native';
import Constants from 'expo-constants';

// LAN IP of your dev machine — required for physical devices
const DEV_MACHINE_IP = '192.168.1.6';

function getBaseUrl() {
  if (__DEV__) {
    if (Platform.OS === 'android') {
      return 'http://10.0.2.2:8080/api';
    }
    if (!Constants.isDevice) {
      return 'http://localhost:8080/api';
    }
    return `http://${DEV_MACHINE_IP}:8080/api`;
  }
  return 'https://api.tiranaevents.com/api';
}

export const API_BASE_URL = getBaseUrl();
