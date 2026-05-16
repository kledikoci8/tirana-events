import axios from 'axios';

// Change this to your computer's IP address when testing on physical device
// For iOS simulator use: http://localhost:8080
// For Android emulator use: http://10.0.2.2:8080
// For physical device use: http://YOUR_IP:8080

// Choose the appropriate URL based on your testing environment:
const API_URL = 'http://localhost:8080/api';  // iOS Simulator
// const API_URL = 'http://10.0.2.2:8080/api';  // Android Emulator
// const API_URL = 'http://192.168.1.6:8080/api';  // Physical Device

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export default api;
