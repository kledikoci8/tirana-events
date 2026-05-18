import Constants from 'expo-constants';

const ENV = {
  dev: {
    apiUrl: 'http://localhost:8080/api',
  },
  staging: {
    apiUrl: 'https://staging-api.tiranaevents.com/api',
  },
  prod: {
    apiUrl: 'https://api.tiranaevents.com/api',
  },
};

const getEnvVars = () => {
  if (__DEV__) {
    return ENV.dev;
  } else if (Constants.manifest?.releaseChannel === 'staging') {
    return ENV.staging;
  } else {
    return ENV.prod;
  }
};

export default getEnvVars();
