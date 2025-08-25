// src/pages/PreferencesPage.tsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import { postPreferences } from '../services/api.ts';

type FormState = {
  jobTitle: string;
  experience: '0-1' | '1-3' | '3-5' | '5+';
  location: string;
  remoteOnly: boolean;
};



export default function PreferencesPage() {
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [form, setForm] = useState<FormState>({
    jobTitle: '',
    experience: '1-3',
    location: '',
    remoteOnly: false,
  });
  const [loading, setLoading] = useState(false);
  const [errMsg, setErrMsg] = useState<string | null>(null);

  function onChange(
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) {
    const { name, value, type } = e.target;
    const v =
      type === 'checkbox'
        ? (e.target as HTMLInputElement).checked
        : value;
    setForm((prev) => ({ ...prev, [name]: v }));
  }

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setErrMsg(null);

    if (!form.jobTitle.trim()) {
      setErrMsg('Job title is required');
      return;
    }
    // anywhere in PreferencesPage on submit (temporarily)
console.log("VITE_API_URL =", import.meta.env.VITE_API_URL);


    setLoading(true);
    try {
      const payload = {
        jobTitle: form.jobTitle.trim(),
        experience: form.experience,
        location: form.location.trim(),
        remoteOnly: form.remoteOnly,
      };
      // Backend should return { prefId: "uuid" }
      const res = await postPreferences(payload);
      const prefId = res?.prefId;
      if (!prefId) throw new Error('Invalid response from server');
      navigate(`/results?prefId=${encodeURIComponent(prefId)}`);
    } catch (err: any) {
      setErrMsg(err?.message || 'Failed to submit preferences');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen bg-white">
      <Header
        mobileMenuOpen={mobileMenuOpen}
        setMobileMenuOpen={setMobileMenuOpen}
      />
      <main className="max-w-3xl mx-auto px-4 py-10">
        <h1 className="text-3xl font-bold mb-6">Tell us what you’re looking for</h1>

        {errMsg && (
          <div className="mb-4 rounded-md border border-red-300 bg-red-50 p-3 text-red-700">
            {errMsg}
          </div>
        )}

        <form
          onSubmit={onSubmit}
          className="space-y-6 bg-gray-50 rounded-xl p-6 border"
        >
          <div>
            <label className="block text-sm font-medium mb-2">
              Job Title<span className="text-red-500">*</span>
            </label>
            <input
              name="jobTitle"
              value={form.jobTitle}
              onChange={onChange}
              placeholder="e.g. Java Developer"
              className="w-full rounded-lg border px-3 py-2"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-2">Experience</label>
            <select
              name="experience"
              value={form.experience}
              onChange={onChange}
              className="w-full rounded-lg border px-3 py-2"
            >
              <option value="0-1">0–1 years</option>
              <option value="1-3">1–3 years</option>
              <option value="3-5">3–5 years</option>
              <option value="5+">5+ years</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium mb-2">
              Location (optional)
            </label>
            <input
              name="location"
              value={form.location}
              onChange={onChange}
              placeholder="e.g. Bengaluru or Remote"
              className="w-full rounded-lg border px-3 py-2"
            />
            <p className="text-xs text-gray-600 mt-1">
              Leave empty for “All India”
            </p>
          </div>

          <label className="flex items-center gap-2">
            <input
              id="remoteOnly"
              name="remoteOnly"
              type="checkbox"
              checked={form.remoteOnly}
              onChange={onChange}
              className="h-4 w-4"
            />
            <span className="text-sm">Remote only</span>
          </label>

          <button
            type="submit"
            disabled={loading}
            className="rounded-lg bg-blue-600 px-4 py-2 text-white hover:bg-blue-700 disabled:opacity-60"
          >
            {loading ? 'Finding jobs…' : 'Find Jobs'}
          </button>
        </form>
      </main>
    </div>
  );
}
