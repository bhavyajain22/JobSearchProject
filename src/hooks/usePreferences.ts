import { useState, useEffect } from 'react';
import { preferencesAPI } from '../services/api';

interface Preferences {
  notifications: boolean;
  darkMode: boolean;
  language: string;
  privacy: string;
  theme: string;
}

export function usePreferences() {
  const [preferences, setPreferences] = useState<Preferences | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Fetch preferences from API
  const fetchPreferences = async () => {
    try {
      setLoading(true);
      setError(null);
      
      const response = await preferencesAPI.get();
      setPreferences(response.data);
    } catch (err) {
      console.error('Error fetching preferences:', err);
      // Fallback to default preferences if API is not available
      setPreferences({
        notifications: true,
        darkMode: false,
        language: 'en',
        privacy: 'public',
        theme: 'light'
      });
      setError('Failed to load preferences from server. Using default settings.');
    } finally {
      setLoading(false);
    }
  };

  // Update preferences via API
  const updatePreferences = async (newPreferences: Preferences) => {
    try {
      setLoading(true);
      setError(null);
      
      const response = await preferencesAPI.update(newPreferences);
      setPreferences(response.data);
      
      // Show success message (you could add a toast notification here)
      console.log('Preferences updated successfully');
    } catch (err) {
      console.error('Error updating preferences:', err);
      setError('Failed to save preferences. Please try again.');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  // Load preferences on component mount
  useEffect(() => {
    fetchPreferences();
  }, []);

  return {
    preferences,
    updatePreferences,
    loading,
    error,
    refetch: fetchPreferences
  };
} 