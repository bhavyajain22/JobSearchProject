import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/Header';
import HeroSection from './components/HeroSection';
import AISection from './components/AISection';
import HowItWorks from './components/HowItWorks';
import TelegramSection from './components/TelegramSection';
import Testimonials from './components/Testimonials';
import Footer from './components/Footer';
import ResultsPage from './pages/ResultsPage';
import PreferencesPage from './pages/PreferencesPage';

// Simple test components
const HomePage = () => (
  <div className="min-h-screen bg-white">
    <Header mobileMenuOpen={false} setMobileMenuOpen={() => {}} />
      <HeroSection />
      <HowItWorks />
      <AISection />
      
      <TelegramSection />
      <Testimonials />
      <Footer />
  </div>
);

// const PreferencesPage = () => (
//   <div className="min-h-screen bg-gray-50 p-8">
//     <h1 className="text-4xl font-bold text-purple-600">Preferences Page</h1>
//     <p className="text-xl text-gray-700 mt-4">Preferences page is working!</p>
//     <a href="/" className="mt-4 inline-block px-4 py-2 bg-purple-600 text-white rounded hover:bg-purple-700">
//       Back to Home
//     </a>
//   </div>
// );

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-white">
         <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/preferences" element={<PreferencesPage />} />
          <Route path="/results" element={<ResultsPage />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;