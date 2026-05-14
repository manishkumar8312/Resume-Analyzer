import React, { useState, useEffect } from 'react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { FiUser, FiCheckSquare, FiTrendingUp } from 'react-icons/fi';
import { getAnalysisById } from '../services/api';

const ResultCard = ({ analysis: initialAnalysis }) => {
  const [activeTab, setActiveTab] = useState('review');
  const [analysis, setAnalysis] = useState(initialAnalysis);

  // Update local state if initialAnalysis prop changes
  useEffect(() => {
    setAnalysis(initialAnalysis);
  }, [initialAnalysis]);

  useEffect(() => {
    let pollInterval;
    
    // Check if we need to poll (if any AI field is missing)
    const needsPolling = analysis && analysis.id && (!analysis.aiReview || !analysis.atsAnalysis || !analysis.careerAdvice);
    
    if (needsPolling) {
      pollInterval = setInterval(async () => {
        try {
          const updatedData = await getAnalysisById(analysis.id);
          // If we got the AI data, update and stop polling
          if (updatedData.aiReview && updatedData.atsAnalysis && updatedData.careerAdvice) {
            setAnalysis(updatedData);
            clearInterval(pollInterval);
          } else if (JSON.stringify(updatedData) !== JSON.stringify(analysis)) {
            // Update if something changed (like fallback data)
            setAnalysis(updatedData);
          }
        } catch (error) {
          console.error("Error polling for analysis updates:", error);
        }
      }, 3000);
    }

    return () => {
      if (pollInterval) clearInterval(pollInterval);
    };
  }, [analysis]);

  if (!analysis) return null;

  const { aiReview, atsAnalysis, careerAdvice } = analysis;

  const tabs = [
    { id: 'review', label: 'AI Review', icon: <FiUser />, content: aiReview },
    { id: 'ats', label: 'ATS Analysis', icon: <FiCheckSquare />, content: atsAnalysis },
    { id: 'career', label: 'Career Advice', icon: <FiTrendingUp />, content: careerAdvice },
  ];

  return (
    <div className="results-container animate-fade-in">
      <div className="ai-tabs">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            className={`ai-tab ${activeTab === tab.id ? 'active' : ''}`}
            onClick={() => setActiveTab(tab.id)}
          >
            {tab.icon}
            <span>{tab.label}</span>
          </button>
        ))}
      </div>

      <div className="ai-content">
        <div className="ai-powered-badge">
          <span className="dot"></span>
          AI Powered Analysis
        </div>
        
        <div className="markdown-content">
          {tabs.find(t => t.id === activeTab)?.content ? (
            <ReactMarkdown remarkPlugins={[remarkGfm]}>
              {tabs.find(t => t.id === activeTab).content}
            </ReactMarkdown>
          ) : (
            <div className="ai-loading-placeholder">
              <div className="pulse-icon"><FiTrendingUp /></div>
              <p>AI is still analyzing your resume for this section...</p>
              <p className="small">This usually takes 5-10 seconds. Please wait or refresh shortly.</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ResultCard;
