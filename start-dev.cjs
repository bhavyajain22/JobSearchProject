const { spawn } = require('child_process');
const path = require('path');

console.log('🚀 Starting JobFlow development environment...\n');

// Start backend server
console.log('📡 Starting Spring Boot backend server...');
const backend = spawn('mvn', ['spring-boot:run'], {
  cwd: path.join(__dirname, 'backend'),
  stdio: 'inherit',
  shell: true
});

// Wait a moment for backend to start, then start frontend
setTimeout(() => {
  console.log('🎨 Starting frontend development server...');
  const frontend = spawn('npm', ['run', 'dev'], {
    cwd: __dirname,
    stdio: 'inherit',
    shell: true
  });

  // Handle process termination
  process.on('SIGINT', () => {
    console.log('\n🛑 Shutting down development servers...');
    backend.kill();
    frontend.kill();
    process.exit();
  });

  frontend.on('close', (code) => {
    console.log(`Frontend process exited with code ${code}`);
    backend.kill();
  });
}, 2000);

backend.on('close', (code) => {
  console.log(`Backend process exited with code ${code}`);
});

console.log('✅ Development environment started!');
console.log('📱 Frontend: http://localhost:5173');
console.log('🔧 Backend:  http://localhost:3001');
console.log('📊 API Health: http://localhost:3001/api/health');
console.log('\nPress Ctrl+C to stop all servers\n'); 