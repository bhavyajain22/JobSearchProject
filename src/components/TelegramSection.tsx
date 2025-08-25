import React from 'react';
import { Send, Bell, Clock, Star } from 'lucide-react';

const TelegramSection: React.FC = () => {
  return (
    <section className="py-20 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid lg:grid-cols-2 gap-12 items-center">
          {/* Left side - Visual/Mock */}
          <div className="relative">
            <div className="bg-gradient-to-br from-teal-50 to-blue-50 rounded-2xl p-8">
              {/* Telegram-style chat interface mockup */}
              <div className="bg-white rounded-xl shadow-sm overflow-hidden">
                <div className="bg-blue-600 text-white p-4 flex items-center">
                  <div className="w-10 h-10 bg-blue-500 rounded-full flex items-center justify-center mr-3">
                    <Send className="w-5 h-5" />
                  </div>
                  <div>
                    <div className="font-semibold">JobFlow Bot</div>
                    <div className="text-blue-100 text-sm">online</div>
                  </div>
                </div>
                
                <div className="p-4 space-y-4 h-64 overflow-hidden">
                  <div className="flex">
                    <div className="bg-gray-100 rounded-2xl rounded-tl-sm p-3 max-w-xs">
                      <div className="text-sm">🎯 <strong>5 New Jobs Found!</strong></div>
                      <div className="text-xs text-gray-600 mt-1">Senior React Developer • ₹15-20 LPA</div>
                    </div>
                  </div>
                  
                  <div className="flex">
                    <div className="bg-gray-100 rounded-2xl rounded-tl-sm p-3 max-w-xs">
                      <div className="text-sm">📍 Location: Bangalore, Mumbai</div>
                      <div className="text-xs text-gray-600 mt-1">97% match with your profile</div>
                    </div>
                  </div>
                  
                  <div className="flex justify-end">
                    <div className="bg-blue-600 text-white rounded-2xl rounded-tr-sm p-3 max-w-xs">
                      <div className="text-sm">Show me details</div>
                    </div>
                  </div>
                  
                  <div className="flex">
                    <div className="bg-gray-100 rounded-2xl rounded-tl-sm p-3 max-w-xs">
                      <div className="text-sm">🔗 Direct Application Links:</div>
                      <div className="text-xs text-blue-600 mt-1">LinkedIn • Naukri • Company Site</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            {/* Floating notification */}
            <div className="absolute -top-4 -left-4 bg-teal-500 text-white p-3 rounded-full shadow-lg animate-pulse">
              <Bell className="w-6 h-6" />
            </div>
          </div>

          {/* Right side - Content */}
          <div>
            <div className="inline-flex items-center px-4 py-2 rounded-full bg-teal-100 text-teal-800 text-sm font-medium mb-6">
              <Send className="w-4 h-4 mr-2" />
              Telegram Integration
            </div>
            
            <h2 className="text-3xl sm:text-4xl font-bold text-gray-900 mb-6">
              Daily Job Updates
              <br />
              <span className="bg-gradient-to-r from-teal-600 to-blue-600 bg-clip-text text-transparent">
                Right in Telegram
              </span>
            </h2>
            
            <p className="text-xl text-gray-600 mb-8 leading-relaxed">
              Never miss an opportunity again. Get personalized job alerts delivered straight 
              to your Telegram with instant application links and match scores.
            </p>

            <div className="space-y-6">
              <div className="flex items-start">
                <div className="flex-shrink-0 w-12 h-12 bg-teal-100 rounded-lg flex items-center justify-center mr-4">
                  <Clock className="w-6 h-6 text-teal-600" />
                </div>
                <div>
                  <h3 className="font-semibold text-gray-900 mb-2">Real-time Notifications</h3>
                  <p className="text-gray-600">Get instant alerts as soon as new jobs matching your criteria are posted.</p>
                </div>
              </div>

              <div className="flex items-start">
                <div className="flex-shrink-0 w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mr-4">
                  <Star className="w-6 h-6 text-blue-600" />
                </div>
                <div>
                  <h3 className="font-semibold text-gray-900 mb-2">Smart Filtering</h3>
                  <p className="text-gray-600">Only receive notifications for high-quality matches that align with your goals.</p>
                </div>
              </div>

              <div className="flex items-start">
                <div className="flex-shrink-0 w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center mr-4">
                  <Send className="w-6 h-6 text-purple-600" />
                </div>
                <div>
                  <h3 className="font-semibold text-gray-900 mb-2">One-Click Apply</h3>
                  <p className="text-gray-600">Direct links to application pages with your profile details pre-filled.</p>
                </div>
              </div>
            </div>

            <div className="mt-8">
              <button className="bg-teal-600 hover:bg-teal-700 text-white px-8 py-3 rounded-lg font-semibold transition-colors duration-200 flex items-center">
                <Send className="w-5 h-5 mr-2" />
                Connect Telegram
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default TelegramSection;