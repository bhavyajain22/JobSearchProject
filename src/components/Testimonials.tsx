import React from 'react';
import { Star, Quote } from 'lucide-react';

const testimonials = [
  {
    name: "Priya Sharma",
    role: "Software Engineer",
    company: "Tech Mahindra",
    image: "https://images.pexels.com/photos/3785077/pexels-photo-3785077.jpeg?auto=compress&cs=tinysrgb&w=150&h=150&dpr=2",
    content: "JobFlow saved me weeks of job hunting. The AI matching is incredibly accurate, and I found my dream job within 10 days of signing up!",
    rating: 5,
    salary: "40% salary increase"
  },
  {
    name: "Rahul Gupta",
    role: "Product Manager",
    company: "Flipkart",
    image: "https://images.pexels.com/photos/3778876/pexels-photo-3778876.jpeg?auto=compress&cs=tinysrgb&w=150&h=150&dpr=2",
    content: "The Telegram notifications are a game-changer. I got alerts for relevant positions even while I was busy with my current job. Landed an offer with 50% hike!",
    rating: 5,
    salary: "50% salary increase"
  },
  {
    name: "Anita Desai",
    role: "Data Scientist",
    company: "Zomato",
    image: "https://images.pexels.com/photos/3756679/pexels-photo-3756679.jpeg?auto=compress&cs=tinysrgb&w=150&h=150&dpr=2",
    content: "Finally, a job platform that understands what I'm looking for. No more sifting through irrelevant postings. Every recommendation was spot-on.",
    rating: 5,
    salary: "Dream role achieved"
  }
];

const Testimonials: React.FC = () => {
  return (
    <section className="py-20 bg-gradient-to-br from-blue-50 to-purple-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <h2 className="text-3xl sm:text-4xl font-bold text-gray-900 mb-4">
            Success Stories
          </h2>
          <p className="text-xl text-gray-600 max-w-2xl mx-auto">
            Join thousands of professionals who found their dream jobs through JobFlow
          </p>
        </div>

        <div className="grid md:grid-cols-3 gap-8">
          {testimonials.map((testimonial, index) => (
            <div key={index} className="bg-white rounded-2xl p-8 shadow-sm hover:shadow-lg transition-all duration-300 border border-gray-100">
              <div className="flex items-center mb-6">
                {[...Array(testimonial.rating)].map((_, i) => (
                  <Star key={i} className="w-5 h-5 text-yellow-400 fill-current" />
                ))}
              </div>
              
              <div className="relative mb-6">
                <Quote className="absolute -top-2 -left-2 w-8 h-8 text-blue-200" />
                <p className="text-gray-700 leading-relaxed pl-6">"{testimonial.content}"</p>
              </div>

              <div className="flex items-center">
                <img 
                  src={testimonial.image} 
                  alt={testimonial.name}
                  className="w-12 h-12 rounded-full object-cover mr-4"
                />
                <div className="flex-1">
                  <h4 className="font-semibold text-gray-900">{testimonial.name}</h4>
                  <p className="text-gray-600 text-sm">{testimonial.role}</p>
                  <p className="text-blue-600 text-sm font-medium">{testimonial.company}</p>
                </div>
                <div className="text-right">
                  <div className="inline-flex items-center px-3 py-1 rounded-full bg-green-100 text-green-800 text-xs font-medium">
                    {testimonial.salary}
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* Stats section */}
        <div className="mt-16 grid grid-cols-2 md:grid-cols-4 gap-8 text-center">
          <div>
            <div className="text-3xl font-bold text-blue-600 mb-2">10K+</div>
            <div className="text-gray-600 text-sm">Jobs Matched Daily</div>
          </div>
          <div>
            <div className="text-3xl font-bold text-purple-600 mb-2">95%</div>
            <div className="text-gray-600 text-sm">Success Rate</div>
          </div>
          <div>
            <div className="text-3xl font-bold text-teal-600 mb-2">5K+</div>
            <div className="text-gray-600 text-sm">Happy Users</div>
          </div>
          <div>
            <div className="text-3xl font-bold text-orange-600 mb-2">24H</div>
            <div className="text-gray-600 text-sm">Average Job Find Time</div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default Testimonials;