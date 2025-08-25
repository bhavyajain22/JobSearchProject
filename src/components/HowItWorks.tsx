import React from 'react';
import { Settings, Target, Zap } from 'lucide-react';

const steps = [
  {
    icon: Settings,
    title: "Enter Preferences",
    description: "Tell us your skills, experience level, preferred location, and salary expectations to create your perfect job profile.",
    step: "01"
  },
  {
    icon: Target,
    title: "Get Curated Jobs",
    description: "Our AI scans thousands of jobs from LinkedIn and Naukri daily, matching them precisely to your preferences.",
    step: "02"
  },
  {
    icon: Zap,
    title: "Apply Instantly",
    description: "Click through to apply directly on the original platform with all your information pre-filled and ready to go.",
    step: "03"
  }
];

const HowItWorks: React.FC = () => {
  return (
    <section id="how-it-works" className="py-20 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <h2 className="text-3xl sm:text-4xl font-bold text-gray-900 mb-4">
            How It Works
          </h2>
          <p className="text-xl text-gray-600 max-w-2xl mx-auto">
            Get your dream job in three simple steps. No more endless scrolling through irrelevant listings.
          </p>
        </div>

        <div className="grid md:grid-cols-3 gap-8 lg:gap-12">
          {steps.map((step, index) => {
            const Icon = step.icon;
            return (
              <div key={index} className="relative text-center group">
                {/* Connection line */}
                {index < steps.length - 1 && (
                  <div className="hidden md:block absolute top-16 left-1/2 w-full h-0.5 bg-gradient-to-r from-blue-200 to-purple-200 transform translate-x-8"></div>
                )}
                
                {/* Step number */}
                <div className="inline-flex items-center justify-center w-12 h-12 bg-gradient-to-r from-blue-100 to-purple-100 rounded-full mb-6 relative z-10">
                  <span className="text-blue-600 font-bold text-lg">{step.step}</span>
                </div>

                {/* Icon */}
                <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-r from-blue-600 to-purple-600 rounded-2xl mb-6 group-hover:scale-110 transition-transform duration-300">
                  <Icon className="h-8 w-8 text-white" />
                </div>

                {/* Content */}
                <h3 className="text-xl font-semibold text-gray-900 mb-4">{step.title}</h3>
                <p className="text-gray-600 leading-relaxed">{step.description}</p>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
};

export default HowItWorks;