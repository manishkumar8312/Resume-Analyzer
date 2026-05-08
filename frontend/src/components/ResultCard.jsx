import React, { useState } from 'react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { FiUser, FiCheckSquare, FiTrendingUp } from 'react-icons/fi';

const ResultCard = ({ analysis }) => {
  const [activeTab, setActiveTab] = useState('review');

  if (!analysis) return null;

  const { score, skills, experience, education, matchPercentage, suggestions, aiReview, atsAnalysis, careerAdvice } = analysis;

  const hasAiContent = aiReview || atsAnalysis || careerAdvice;

  const tabs = [
    { id: 'review', label: 'AI Review', icon: <FiUser />, content: aiReview },
    { id: 'ats', label: 'ATS Analysis', icon: <FiCheckSquare />, content: atsAnalysis },
    { id: 'career', label: 'Career Advice', icon: <FiTrendingUp />, content: careerAdvice },
  ].filter(tab => tab.content);

  return (
    <div className="results-container">
      {/* Score Cards */}
      <div className="score-grid animate-fade-in">
        <div className="score-card overall">
          <div className="score-label">Overall Resume Score</div>
          <div className="score-value blue">
            {score}<span className="score-suffix">/100</span>
          </div>
        </div>
        {matchPercentage !== null && matchPercentage !== undefined && (
          <div className="score-card match">
            <div className="score-label">Job Match</div>
            <div className="score-value green">
              {matchPercentage}<span className="score-suffix">%</span>
            </div>
          </div>
        )}
      </div>

      {/* Skills Section */}
      <div className="section-card animate-fade-in animate-fade-in-delay-1">
        <div className="section-title">
          <span className="icon">💡</span> Skills Detected
        </div>
        <div className="skills-grid">
          {skills && skills.map((skill, i) => (
            <span key={i} className="skill-chip">
              {skill}
            </span>
          ))}
          {(!skills || skills.length === 0) && (
            <span style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
              No specific skills detected
            </span>
          )}
        </div>
      </div>

      {/* Experience & Education */}
      <div className="section-card animate-fade-in animate-fade-in-delay-2">
        <div className="section-title">
          <span className="icon">💼</span> Experience
        </div>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', lineHeight: '1.7', whiteSpace: 'pre-wrap' }}>
          {experience}
        </p>
      </div>

      <div className="section-card animate-fade-in animate-fade-in-delay-3">
        <div className="section-title">
          <span className="icon">🎓</span> Education
        </div>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', lineHeight: '1.7', whiteSpace: 'pre-wrap' }}>
          {education}
        </p>
      </div>

      {/* Suggestions */}
      {suggestions && suggestions.length > 0 && (
        <div className="section-card animate-fade-in animate-fade-in-delay-4">
          <div className="section-title">
            <span className="icon">⚡</span> Quick Suggestions
          </div>
          {suggestions.map((s, i) => (
            <div key={i} className="suggestion-item">
              <div className="suggestion-bullet"></div>
              <div className="suggestion-text">{s}</div>
            </div>
          ))}
        </div>
      )}

      {/* ═══════════════════════════════════════════
          AI REVIEW SECTION — Gemini Powered
          ═══════════════════════════════════════════ */}
      {hasAiContent ? (
        <div className="ai-section animate-fade-in animate-fade-in-delay-5">
          <div className="ai-section-header">
            <span style={{ fontSize: '1.75rem' }}>✨</span>
            <div className="ai-section-title">Gemini AI Analysis</div>
          </div>

          {/* Tabs */}
          {tabs.length > 1 && (
            <div className="ai-tabs">
              {tabs.map(tab => (
                <button
                  key={tab.id}
                  className={`ai-tab ${activeTab === tab.id ? 'active' : ''}`}
                  onClick={() => setActiveTab(tab.id)}
                >
                  {tab.label}
                </button>
              ))}
            </div>
          )}

          {/* Tab Content */}
          <div className="ai-content">
            <div className="ai-powered-badge">
              <span className="dot"></span>
              Powered by Google Gemini AI
            </div>

            {tabs.map(tab => (
              <div
                key={tab.id}
                style={{ display: activeTab === tab.id ? 'block' : 'none' }}
              >
                <div className="markdown-content">
                  <ReactMarkdown remarkPlugins={[remarkGfm]}>
                    {tab.content}
                  </ReactMarkdown>
                </div>
              </div>
            ))}
          </div>
        </div>
      ) : (
        <div className="section-card animate-fade-in animate-fade-in-delay-5">
          <div className="no-ai-message">
            <span className="icon">🤖</span>
            <p>AI analysis was not generated for this resume.</p>
            <p style={{ fontSize: '0.8rem', marginTop: '0.5rem' }}>
              This can happen if the Gemini API key is not configured or the API is temporarily unavailable.
            </p>
          </div>
        </div>
      )}
    </div>
  );
};

export default ResultCard;
