const axios = require('axios');

const API_BASE_URL = 'http://localhost:3001/api';

async function testAPI() {
  console.log('🧪 Testing JobFlow API...\n');

  try {
    // Test health endpoint
    console.log('1. Testing health endpoint...');
    const healthResponse = await axios.get(`${API_BASE_URL}/health`);
    console.log('✅ Health check passed:', healthResponse.data);

    // Test get preferences
    console.log('\n2. Testing get preferences...');
    const getPrefsResponse = await axios.get(`${API_BASE_URL}/preferences`);
    console.log('✅ Get preferences passed:', getPrefsResponse.data);

    // Test update preferences
    console.log('\n3. Testing update preferences...');
    const newPreferences = {
      notifications: false,
      darkMode: true,
      language: 'es',
      privacy: 'private',
      theme: 'dark'
    };
    const updatePrefsResponse = await axios.put(`${API_BASE_URL}/preferences`, newPreferences);
    console.log('✅ Update preferences passed:', updatePrefsResponse.data);

    // Test jobs endpoint
    console.log('\n4. Testing jobs endpoint...');
    const jobsResponse = await axios.get(`${API_BASE_URL}/jobs`);
    console.log('✅ Jobs endpoint passed:', jobsResponse.data.length, 'jobs found');

    // Test user profile endpoint
    console.log('\n5. Testing user profile endpoint...');
    const profileResponse = await axios.get(`${API_BASE_URL}/user/profile`);
    console.log('✅ User profile endpoint passed:', profileResponse.data.name);

    console.log('\n🎉 All API tests passed successfully!');
    console.log('🚀 Your API is ready to use with the frontend application.');

  } catch (error) {
    console.error('\n❌ API test failed:', error.message);
    if (error.code === 'ECONNREFUSED') {
      console.log('💡 Make sure the Spring Boot backend server is running on port 3001');
      console.log('   Run: cd backend && mvn spring-boot:run');
    }
  }
}

// Run the test
testAPI(); 