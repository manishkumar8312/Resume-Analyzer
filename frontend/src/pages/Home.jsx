import React, { useState, useEffect } from 'react';
import UploadForm from '../components/UploadForm';
import ResultCard from '../components/ResultCard';
import { FiUser, FiCheckSquare, FiTrendingUp, FiFileText } from 'react-icons/fi';

const Home = () => {
  const [analysis, setAnalysis] = useState(null);
  const [fileUrl, setFileUrl] = useState(null);

  useEffect(() => {
    // Cleanup the object URL when component unmounts or fileUrl changes
    return () => {
      if (fileUrl) {
        URL.revokeObjectURL(fileUrl);
      }
    };
  }, [fileUrl]);

  const handleAnalysisComplete = (result) => {
    setAnalysis(result);
  };

  const handleFileSelect = (file) => {
    if (file) {
      const url = URL.createObjectURL(file);
      setFileUrl(url);
    } else {
      setFileUrl(null);
    }
  };

  const handleReset = () => {
    setAnalysis(null);
    setFileUrl(null);
  };

  return (
    <div className={`home-container ${fileUrl ? 'has-preview' : ''}`}>
      <div className="main-content">
        {!analysis ? (
          <div>
            {/* AI Features Showcase */}
            <div className="features-grid animate-fade-in">
              <div className="feature-card">
                <FiUser className="feature-icon" />
                <div className="feature-title">AI Comprehensive Review</div>
                <div className="feature-desc">
                  Powered by Groq AI to deliver executive-level resume analysis with actionable insights
                </div>
              </div>
              <div className="feature-card">
                <FiCheckSquare className="feature-icon" />
                <div className="feature-title">ATS Compatibility Tracker</div>
                <div className="feature-desc">
                  AI-driven scoring for Applicant Tracking Systems with keyword optimization tips
                </div>
              </div>
              <div className="feature-card">
                <FiTrendingUp className="feature-icon" />
                <div className="feature-title">Career Coaching</div>
                <div className="feature-desc">
                  Personalized career development roadmap with skill gap analysis & growth strategy
                </div>
              </div>
            </div>

            {/* Upload Form */}
            <UploadForm 
              onAnalysisComplete={handleAnalysisComplete} 
              onFileSelect={handleFileSelect}
            />
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

      {/* Sidebar Resume Preview */}
      {fileUrl && (
        <div className="resume-sidebar animate-slide-in">
          <div className="sidebar-header">
            <FiFileText />
            <span>Resume Preview</span>
          </div>
          <div className="preview-container">
            <iframe
              src={`${fileUrl}#toolbar=0&navpanes=0`}
              title="Resume Preview"
              width="100%"
              height="100%"
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default Home;
