import React, { useState } from 'react';
import './App.css';
import Dashboard from './components/Dashboard';
import Login from './components/Login';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  if (!isAuthenticated) {
    return <Login onLogin={setIsAuthenticated} />;
  }

  return (
    <div className="app-container">
      <header className="header">
        <h1>Kafka Event Stream</h1>
        <div className="header-status">
          <div className="status-dot"></div>
          <span className="status-text">Services Online</span>
          <button
            onClick={() => setIsAuthenticated(false)}
            className="logout-btn"
          >
            Logout
          </button>
        </div>
      </header>

      <main>
        <Dashboard />
      </main>
    </div>
  );
}

export default App;
