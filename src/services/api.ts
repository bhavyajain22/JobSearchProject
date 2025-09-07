// src/services/api.ts
import axios from 'axios';

// Create axios instance with default configuration
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:3001/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
api.interceptors.request.use(
  (config) => {
    // Add auth token if available
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// -----------------
// API service calls
// -----------------

// Save preferences
export async function postPreferences(data: any) {
  const res = await api.post('/preferences', data);
  return res.data; // expected { prefId: "uuid" }
}

// Fetch jobs by preference ID
export async function getJobs(
  prefId: string,
  page = 0,
  size = 10,
  opts?: { source?: string; postedWithinDays?: number; companyContains?: string; sortBy?: 'relevance'|'recency' }
) {
  const res = await api.get('/jobs', {
    params: {
      prefId, page, size,
      source: opts?.source ?? 'all',
      postedWithinDays: opts?.postedWithinDays,
      companyContains: opts?.companyContains,
      sortBy: opts?.sortBy ?? 'relevance',
    }
  });
  return res.data;
}

// Example user profile endpoints (keep for later auth features)
export const userAPI = {
  getProfile: () => api.get('/user/profile'),
  updateProfile: (data: any) => api.put('/user/profile', data),
};

export default api;
