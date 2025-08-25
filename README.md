# JobFlow - React + Tailwind Homepage with Preferences

A modern React application built with TypeScript, Tailwind CSS, and a Node.js backend API for managing user preferences.

## Features

- 🏠 **Modern Homepage** - Beautiful landing page with multiple sections
- ⚙️ **Preferences Page** - User settings management with real-time updates
- 🔄 **API Integration** - Full CRUD operations for user preferences
- 📱 **Responsive Design** - Mobile-first approach with Tailwind CSS
- 🚀 **Fast Development** - Built with Vite for lightning-fast builds
- 🔒 **Type Safety** - Full TypeScript support

## Project Structure

```
project/
├── src/
│   ├── components/          # Reusable UI components
│   ├── pages/              # Page components
│   ├── hooks/              # Custom React hooks
│   └── App.tsx             # Main application component
├── backend/                # Express.js API server
│   ├── server.js           # Main server file
│   └── package.json        # Backend dependencies
└── package.json            # Frontend dependencies
```

## Quick Start

### Prerequisites

- Node.js (v16 or higher)
- npm or yarn

### Frontend Setup

1. **Install dependencies:**
   ```bash
   npm install
   ```

2. **Start development server:**
   ```bash
   npm run dev
   ```

3. **Open your browser:**
   Navigate to `http://localhost:5173`

### Backend Setup

1. **Prerequisites:**
   - Java 17 or higher
   - Maven 3.6 or higher

2. **Navigate to backend directory:**
   ```bash
   cd backend
   ```

3. **Start the Spring Boot server:**
   ```bash
   mvn spring-boot:run
   ```

4. **API will be available at:**
   - Health check: `http://localhost:3001/api/health`
   - Preferences: `http://localhost:3001/api/preferences`

## Available Scripts

### Frontend
- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

### Backend
- `mvn spring-boot:run` - Start development server
- `mvn clean package` - Build JAR file
- `java -jar target/jobflow-backend-1.0.0.jar` - Run JAR file

## API Endpoints

### Preferences
- `GET /api/preferences` - Get user preferences
- `PUT /api/preferences` - Update user preferences

### Additional Endpoints
- `GET /api/health` - Health check
- `GET /api/jobs` - Get job listings (mock data)
- `GET /api/user/profile` - Get user profile (mock data)

## Technologies Used

### Frontend
- **React 18** - UI library
- **TypeScript** - Type safety
- **Tailwind CSS** - Utility-first CSS framework
- **React Router** - Client-side routing
- **Axios** - HTTP client
- **Lucide React** - Icon library
- **Vite** - Build tool

### Backend
- **Spring Boot 3.2** - Java framework
- **Spring Web** - REST API support
- **Spring Validation** - Input validation
- **Spring Actuator** - Health checks and monitoring
- **Jackson** - JSON processing

## Customization

### Adding New Preferences

1. Update the `Preferences` interface in `src/hooks/usePreferences.ts`
2. Add the new field to the preferences form in `src/pages/PreferencesPage.tsx`
3. Update the backend validation in `backend/server.js`

### Styling

The application uses Tailwind CSS for styling. You can customize the design by:
- Modifying `tailwind.config.js` for theme customization
- Adding custom CSS in `src/index.css`
- Using Tailwind utility classes in components

## Development

### Code Structure
- Components are organized by feature
- Custom hooks handle API logic
- TypeScript interfaces ensure type safety
- Responsive design with mobile-first approach

### Best Practices
- Use TypeScript for all new code
- Follow React hooks patterns
- Implement proper error handling
- Use semantic HTML elements
- Ensure accessibility compliance

## Deployment

### Frontend
The frontend can be deployed to any static hosting service:
- Vercel
- Netlify
- GitHub Pages
- AWS S3

### Backend
The Spring Boot backend can be deployed to:
- Heroku
- Railway
- DigitalOcean
- AWS EC2
- Google Cloud Platform
- Azure App Service

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

MIT License - see LICENSE file for details 