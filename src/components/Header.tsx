import React from 'react';
import { Link } from 'react-router-dom';
import { Search, Menu, X, Settings } from 'lucide-react';

interface HeaderProps {
  mobileMenuOpen: boolean;
  setMobileMenuOpen: (open: boolean) => void;
}

const Header: React.FC<HeaderProps> = ({ mobileMenuOpen, setMobileMenuOpen }) => {
  return (
    <header className="bg-white/95 backdrop-blur-sm border-b border-gray-100 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          <div className="flex items-center">
            <Search className="h-8 w-8 text-blue-600" />
            <span className="ml-2 text-xl font-bold text-gray-900">JobFlow</span>
          </div>
          
          {/* Desktop Navigation */}
          <nav className="hidden md:flex space-x-8">
            <a href="#features" className="text-gray-700 hover:text-blue-600 transition-colors duration-200 font-medium">Features</a>
            <a href="#how-it-works" className="text-gray-700 hover:text-blue-600 transition-colors duration-200 font-medium">How It Works</a>
            <a href="#pricing" className="text-gray-700 hover:text-blue-600 transition-colors duration-200 font-medium">Pricing</a>
            <a href="#about" className="text-gray-700 hover:text-blue-600 transition-colors duration-200 font-medium">About</a>
          </nav>

          <div className="hidden md:flex items-center space-x-4">
            <Link
              to="/preferences"
              className="text-gray-700 hover:text-blue-600 transition-colors duration-200 font-medium flex items-center space-x-1"
            >
              <Settings className="h-4 w-4" />
              <span>Preferences</span>
            </Link>
            <button className="text-gray-700 hover:text-blue-600 transition-colors duration-200 font-medium">
              Sign In
            </button>
            <button className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg transition-colors duration-200 font-medium">
              Get Started
            </button>
          </div>

          {/* Mobile menu button */}
          <div className="md:hidden">
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="text-gray-700 hover:text-blue-600 transition-colors duration-200"
            >
              {mobileMenuOpen ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
            </button>
          </div>
        </div>

        {/* Mobile Navigation */}
        {mobileMenuOpen && (
          <div className="md:hidden bg-white border-t border-gray-100">
            <div className="px-2 pt-2 pb-3 space-y-1">
              <a href="#features" className="block px-3 py-2 text-gray-700 hover:text-blue-600 transition-colors duration-200">Features</a>
              <a href="#how-it-works" className="block px-3 py-2 text-gray-700 hover:text-blue-600 transition-colors duration-200">How It Works</a>
              <a href="#pricing" className="block px-3 py-2 text-gray-700 hover:text-blue-600 transition-colors duration-200">Pricing</a>
              <a href="#about" className="block px-3 py-2 text-gray-700 hover:text-blue-600 transition-colors duration-200">About</a>
              <div className="border-t border-gray-100 pt-3 mt-3">
                <Link
                  to="/preferences"
                  className="block px-3 py-2 text-gray-700 hover:text-blue-600 transition-colors duration-200 flex items-center space-x-2"
                >
                  <Settings className="h-4 w-4" />
                  <span>Preferences</span>
                </Link>
                <button className="block w-full text-left px-3 py-2 text-gray-700 hover:text-blue-600 transition-colors duration-200">
                  Sign In
                </button>
                <button className="block w-full mt-2 bg-blue-600 hover:bg-blue-700 text-white px-3 py-2 rounded-lg transition-colors duration-200">
                  Get Started
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </header>
  );
};

export default Header;