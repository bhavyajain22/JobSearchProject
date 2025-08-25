// Configuration file for the application
const config = {
  // API Configuration
  api: {
    baseURL: 'http://localhost:3001/api',
    timeout: 10000,
  },
  
  // App Configuration
  app: {
    name: 'JobFlow',
    version: '1.0.0',
  },
  
  // Default preferences
  defaultPreferences: {
    notifications: true,
    darkMode: false,
    language: 'en',
    privacy: 'public',
    theme: 'light'
  }
};

export default config; 