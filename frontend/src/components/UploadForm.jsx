import React, { useState, useEffect, useRef } from 'react';
import { FiUpload, FiFile, FiAlertCircle, FiCpu, FiUser, FiTrendingUp, FiCheckSquare, FiStar } from 'react-icons/fi';
import { uploadResume } from '../services/api';

const UploadForm = ({ onAnalysisComplete, onFileSelect }) => {
  const [file, setFile] = useState(null);
  const [jobDescription, setJobDescription] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [currentStep, setCurrentStep] = useState(0);
  const fileInputRef = useRef(null);

  const progressSteps = [
    { text: 'Uploading resume...', icon: <FiUpload /> },
    { text: 'Extracting text content...', icon: <FiFile /> },
    { text: 'Analyzing skills & experience...', icon: <FiCpu /> },
    { text: 'Generating AI insights...', icon: <FiUser /> },
    { text: 'Running ATS compatibility check...', icon: <FiCheckSquare /> },
    { text: 'Preparing career recommendations...', icon: <FiTrendingUp /> },
    { text: 'Finalizing results...', icon: <FiStar /> }
  ];

  useEffect(() => {
    if (loading) {
      setCurrentStep(0);
      const interval = setInterval(() => {
        setCurrentStep(prev => {
          if (prev >= progressSteps.length - 1) {
            return prev;
          }
          return prev + 1;
        });
      }, 2500);
      return () => clearInterval(interval);
    } else {
      setCurrentStep(0);
    }
  }, [loading, progressSteps.length]);

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile && selectedFile.type === 'application/pdf') {
      setFile(selectedFile);
      if (onFileSelect) onFileSelect(selectedFile);
      setError('');
    } else {
      setError('Please select a valid PDF file');
      setFile(null);
      if (onFileSelect) onFileSelect(null);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!file) {
      setError('Please select a file');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('jobDescription', jobDescription);

      const result = await uploadResume(formData);
      onAnalysisComplete(result);
    } catch (err) {
      setError('Failed to analyze resume. Make sure the backend is running on port 8081.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="upload-container animate-fade-in animate-fade-in-delay-2">
      {!loading && (
        <>
          <h2 className="upload-title">Upload Your Resume</h2>
          <p className="upload-subtitle">
            Get AI-powered analysis, ATS scoring & career insights in seconds
          </p>
        </>
      )}

      {loading && (
        <div className="loading-status-badge animate-pulse">
          <FiCpu /> Processing Analysis...
        </div>
      )}

      <form onSubmit={handleSubmit} className={loading ? 'form-loading' : ''}>
        {/* File Upload Zone - Only show if not loading */}
        {!loading && (
          <div className="form-group">
            <label className="form-label">Resume (PDF)</label>
            <div
              className={`file-drop-zone ${file ? 'has-file' : ''}`}
              onClick={() => fileInputRef.current?.click()}
            >
              <span className="file-drop-icon">{file ? <FiCheckSquare /> : <FiUpload />}</span>
              {file ? (
                <div>
                  <div className="file-name">{file.name}</div>
                  <div className="file-drop-text" style={{ marginTop: '0.25rem' }}>
                    Click to change file
                  </div>
                </div>
              ) : (
                <div>
                  <div className="file-drop-text">
                    Click to select a <strong>PDF file</strong>
                  </div>
                </div>
              )}
              <input
                ref={fileInputRef}
                type="file"
                accept=".pdf"
                onChange={handleFileChange}
                disabled={loading}
                style={{ display: 'none' }}
              />
            </div>
          </div>
        )}

        {/* Job Description - Only show if not loading */}
        {!loading && (
          <div className="form-group">
            <label className="form-label">Job Description (Optional)</label>
            <textarea
              value={jobDescription}
              onChange={(e) => setJobDescription(e.target.value)}
              placeholder="Paste the target job description here for AI-powered job match analysis and tailored recommendations..."
              className="form-textarea"
              disabled={loading}
              rows="4"
            />
          </div>
        )}

        {/* Error */}
        {error && <div className="error-message"><FiAlertCircle /> {error}</div>}

        {/* Submit Button - Hide if loading to show progress container instead */}
        {!loading && (
          <button
            type="submit"
            disabled={loading || !file}
            className={`btn-analyze ${loading ? 'loading' : ''}`}
          >
            <FiUser /> Analyze Resume with AI
          </button>
        )}

        {/* Progress Steps */}
        {loading && (
          <div className="progress-container immersive">
            <div className="scanner-line"></div>
            <div className="progress-title">
              <FiCpu className="animate-spin-slow" /> AI Analysis Pipeline
            </div>
            {progressSteps.map((step, index) => {
              let stepClass = 'progress-step';
              if (index < currentStep) stepClass += ' completed';
              else if (index === currentStep) stepClass += ' active';

              return (
                <div key={index} className={stepClass} style={{ animationDelay: `${index * 0.1}s` }}>
                  <div className="progress-dot"></div>
                  <span>{step.icon} {step.text}</span>
                </div>
              );
            })}
          </div>
        )}
      </form>
    </div>
  );
};

export default UploadForm;
