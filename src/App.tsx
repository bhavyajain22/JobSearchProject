// src/App.tsx
import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { Toaster } from "react-hot-toast";

import Header from "./components/Header";
import HeroSection from "./components/HeroSection";
import AISection from "./components/AISection";
import HowItWorks from "./components/HowItWorks";
import TelegramSection from "./components/TelegramSection";
import Testimonials from "./components/Testimonials";
import Footer from "./components/Footer";

import ResultsPage from "./pages/ResultsPage";
import PreferencesPage from "./pages/PreferencesPage";
import ManageAlerts from "./pages/ManageAlerts";

const HomePage: React.FC = () => (
  <main className="min-h-screen bg-white">
    <HeroSection />
    <HowItWorks />
    <AISection />
    <TelegramSection />
    <Testimonials />
    <Footer />
  </main>
);

export default function App() {
  return (
    <Router>
      {/* Global header visible on all routes */}
      <Header mobileMenuOpen={false} setMobileMenuOpen={() => {}} />

      {/* Routes: ONLY <Route> elements here */}
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/preferences" element={<PreferencesPage />} />
        <Route path="/results" element={<ResultsPage />} />
        <Route path="/alerts" element={<ManageAlerts />} />
      </Routes>

      {/* Global UI helpers */}
      <Toaster position="bottom-right" />
    </Router>
  );
}
