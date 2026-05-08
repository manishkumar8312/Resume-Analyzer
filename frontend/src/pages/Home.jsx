import React, { useState } from 'react';
import UploadForm from '../components/UploadForm';
import ResultCard from '../components/ResultCard';

const Home = () => {
  const [analysis, setAnalysis] = useState(null);

  const handleAnalysisComplete = (result) => {
    setAnalysis(result);
  };

  const handleReset = () => {
    setAnalysis(null);
  };

  return (
    <div>
      {!analysis ? (
        <div>
          {/* AI Features Showcase */}
          <div className="features-grid animate-fade-in">
            <div className="feature-card">
              <span className="feature-icon">🤖</span>
              <div className="feature-title">AI Comprehensive Review</div>
              <div className="feature-desc">
                Powered by Google Gemini to deliver executive-level resume analysis with actionable insights
              </div>
            </div>
            <div className="feature-card">
              <span className="feature-icon">📋</span>
              <div className="feature-title">ATS Compatibility</div>
              <div className="feature-desc">
                AI-driven scoring for Applicant Tracking Systems with keyword optimization tips
              </div>
            </div>
            <div className="feature-card">
              <span className="feature-icon">🚀</span>
              <div className="feature-title">Career Coaching</div>
              <div className="feature-desc">
                Personalized career development roadmap with skill gap analysis & growth strategy
              </div>
            </div>
          </div>

          {/* Upload Form */}
          <UploadForm onAnalysisComplete={handleAnalysisComplete} />
        </div>
      ) : (
        <div>
          <ResultCard analysis={analysis} />
          <div style={{ textAlign: 'center', marginTop: '2rem' }}>
            <button onClick={handleReset} className="btn-reset">
              ← Analyze Another Resume
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Home;
