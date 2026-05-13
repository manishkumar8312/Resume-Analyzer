import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'https://resume-analyzer-1-4bhm.onrender.com/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const uploadResume = async (formData) => {
  const response = await api.post('/resume/analyze', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return response.data;
};

export const getAnalysisHistory = async () => {
  const response = await api.get('/resume/history');
  return response.data;
};

export const getAnalysisById = async (id) => {
  const response = await api.get(`/resume/${id}`);
  return response.data;
};

export const deleteResume = async (id) => {
  const response = await api.delete(`/resume/${id}`);
  return response.data;
};

export default api;
