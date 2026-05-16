import axios from 'axios';

// Change this to your computer's IP address when testing on physical device
// For iOS simulator use: http://localhost:8080
// For Android emulator use: http://10.0.2.2:8080
// For physical device use: http://YOUR_IP:8080
const API_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export default api;
