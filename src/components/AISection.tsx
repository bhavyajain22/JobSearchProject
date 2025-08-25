import React from 'react';
import { Brain, Cpu, TrendingUp, Users } from 'lucide-react';

const features = [
  {
    icon: Brain,
    title: "Smart Matching",
    description: "Advanced algorithms analyze your profile and match you with the most relevant opportunities."
  },
  {
    icon: Cpu,
    title: "Real-time Processing",
    description: "Our AI processes thousands of new job postings every hour to keep your feed fresh."
  },
  {
    icon: TrendingUp,
    title: "Success Prediction",
    description: "Get insights on your application success probability before you apply."
  },
  {
    icon: Users,
    title: "Behavioral Learning",
    description: "The more you use JobFlow, the better it gets at understanding your preferences."
  }
];

const AISection: React.FC = () => {
  return (
    <section className="py-20 bg-gradient-to-br from-gray-50 to-blue-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid lg:grid-cols-2 gap-12 items-center">
          {/* Left side - Content */}
          <div>
            <div className="inline-flex items-center px-4 py-2 rounded-full bg-purple-100 text-purple-800 text-sm font-medium mb-6">
              <Brain className="w-4 h-4 mr-2" />
              AI-Powered Technology
            </div>
            
            <h2 className="text-3xl sm:text-4xl font-bold text-gray-900 mb-6">
              Intelligent Job Matching
              <br />
              <span className="bg-gradient-to-r from-purple-600 to-blue-600 bg-clip-text text-transparent">
                That Actually Works
              </span>
            </h2>
            
            <p className="text-xl text-gray-600 mb-8 leading-relaxed">
              Our advanced AI doesn't just match keywords. It understands your career goals, 
              growth trajectory, and preferences to find opportunities that truly fit your aspirations.
            </p>

            <div className="grid sm:grid-cols-2 gap-6">
              {features.map((feature, index) => {
                const Icon = feature.icon;
                return (
                  <div key={index} className="bg-white p-6 rounded-xl shadow-sm hover:shadow-md transition-shadow duration-300">
                    <div className="inline-flex items-center justify-center w-12 h-12 bg-gradient-to-r from-purple-100 to-blue-100 rounded-lg mb-4">
                      <Icon className="h-6 w-6 text-purple-600" />
                    </div>
                    <h3 className="font-semibold text-gray-900 mb-2">{feature.title}</h3>
                    <p className="text-gray-600 text-sm leading-relaxed">{feature.description}</p>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Right side - Visual */}
          <div className="relative">
            <div className="bg-gradient-to-r from-blue-600 to-purple-600 rounded-2xl p-8 text-white">
              <div className="space-y-6">
                <div className="flex items-center justify-between">
                  <span className="text-blue-100">Job Match Score</span>
                  <span className="font-bold">95%</span>
                </div>
                <div className="w-full bg-blue-400/30 rounded-full h-2">
                  <div className="bg-white h-2 rounded-full w-[95%]"></div>
                </div>
                
                <div className="grid grid-cols-2 gap-4 pt-4">
                  <div className="bg-white/10 rounded-lg p-4">
                    <div className="text-2xl font-bold">127</div>
                    <div className="text-blue-100 text-sm">New Jobs Today</div>
                  </div>
                  <div className="bg-white/10 rounded-lg p-4">
                    <div className="text-2xl font-bold">89%</div>
                    <div className="text-blue-100 text-sm">Match Quality</div>
                  </div>
                </div>

                <div className="border-t border-white/20 pt-4">
                  <div className="text-sm text-blue-100 mb-2">Top Skills Match</div>
                  <div className="flex flex-wrap gap-2">
                    <span className="bg-white/20 px-3 py-1 rounded-full text-xs">React</span>
                    <span className="bg-white/20 px-3 py-1 rounded-full text-xs">TypeScript</span>
                    <span className="bg-white/20 px-3 py-1 rounded-full text-xs">Node.js</span>
                  </div>
                </div>
              </div>
            </div>
            
            {/* Floating elements */}
            <div className="absolute -top-4 -right-4 w-20 h-20 bg-yellow-400 rounded-full flex items-center justify-center shadow-lg">
              <TrendingUp className="h-8 w-8 text-yellow-800" />
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default AISection;