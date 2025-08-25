import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import Header from '../components/Header';
import HeroSection from '../components/HeroSection';
import HowItWorks from '../components/HowItWorks';
import AISection from '../components/AISection';
import TelegramSection from '../components/TelegramSection';
import Testimonials from '../components/Testimonials';
import Footer from '../components/Footer';

function HomePage() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <>
      <Header mobileMenuOpen={mobileMenuOpen} setMobileMenuOpen={setMobileMenuOpen} />
      <HeroSection />
      <HowItWorks />
      <AISection />
      <TelegramSection />
      <Testimonials />
      <Footer />
    </>
  );
}

export default HomePage; 