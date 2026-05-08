import React from 'react';
import Home from './pages/Home';
import './index.css';

function App() {
  return (
    <div className="App">
      <header className="app-header">
        <div className="header-content">
          <h1 className="header-title">Resume Analyzer</h1>
          <p className="header-subtitle">
            AI-Powered Resume Analysis & Career Insights
            <span className="gemini-badge">
              <span className="dot"></span>
              Gemini AI
            </span>
          </p>
        </div>
      </header>
      <main style={{ padding: '2.5rem 1rem' }}>
        <Home />
      </main>
    </div>
  );
}

export default App;
