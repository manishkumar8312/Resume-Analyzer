import React, { useState } from 'react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { FiUser, FiCheckSquare, FiTrendingUp } from 'react-icons/fi';

const ResultCard = ({ analysis }) => {
  const [activeTab, setActiveTab] = useState('review');

  if (!analysis) return null;

  const { aiReview, atsAnalysis, careerAdvice } = analysis;

  const tabs = [
    { id: 'review', label: 'AI Review', icon: <FiUser />, content: aiReview },
    { id: 'ats', label: 'ATS Analysis', icon: <FiCheckSquare />, content: atsAnalysis },
    { id: 'career', label: 'Career Advice', icon: <FiTrendingUp />, content: careerAdvice },
  ].filter(tab => tab.content);

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
          <ReactMarkdown remarkPlugins={[remarkGfm]}>
            {tabs.find(t => t.id === activeTab)?.content || ''}
          </ReactMarkdown>
        </div>
      </div>
    </div>
  );
};

export default ResultCard;
