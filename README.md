# Resume Analyzer

![React](https://img.shields.io/badge/Frontend-React-61DAFB?logo=react&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Backend-SpringBoot-6DB33F?logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white)
![Docker](https://img.shields.io/badge/Containerized-Docker-2496ED?logo=docker&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-yellow)

AI-powered resume analysis platform designed to evaluate resumes for ATS compatibility, skill relevance, and career insights.

**[Live Demo](https://resume-analyzer-1-4bhm.onrender.com)** (Backend on Render)

---

## Overview

Resume Analyzer is a full-stack application that enables users to upload resumes and receive intelligent analysis powered by AI. The platform provides detailed feedback on resume quality, ATS optimization, skills, experience, and job compatibility.

The project demonstrates practical implementation of:
- Full-stack development
- RESTful API architecture
- AI integration
- Containerization
- CI/CD pipelines
- Cloud-native deployment practices

---

## Key Features

### Resume Processing
- PDF resume parsing
- Skill extraction and categorization
- Experience analysis
- Education analysis
- Resume quality scoring
- Job description matching

### AI-Powered Insights
- AI-generated resume review
- ATS compatibility analysis
- Resume improvement suggestions
- Career guidance and recommendations

### Engineering Features
- Responsive user interface
- REST API architecture
- Dockerized deployment
- Kubernetes-ready infrastructure
- Automated CI/CD pipelines
- Secure environment configuration

---

## Tech Stack

### Frontend
- React
- Tailwind CSS
- Axios

### Backend
- Spring Boot
- Java 17
- Maven

### Database
- PostgreSQL

### AI Integration
- Google Gemini API
- Groq API (for AI-powered analysis)

### DevOps & Infrastructure
- Docker
- Docker Compose
- Jenkins
- GitHub Actions
- Kubernetes

---

## System Architecture

```bash
resume-analyzer/
│
├── backend/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── model/
│   └── config/
│
├── frontend/
│   ├── components/
│   ├── pages/
│   ├── services/
│   └── assets/
│
├── kubernetes/
├── .github/workflows/
├── docker-compose.yml
├── Jenkinsfile
└── README.md
````

---

## API Endpoints

| Method | Endpoint              | Description              |
| ------ | --------------------- | ------------------------ |
| POST   | `/api/resume/analyze` | Analyze uploaded resume  |
| GET    | `/api/resume/history` | Fetch analysis history   |
| GET    | `/api/resume/{id}`    | Retrieve analysis by ID  |
| GET    | `/api/resume/health`  | Application health check |

---

## Local Development Setup

### Prerequisites

* Java 17+
* Node.js 18+
* Maven
* Docker & Docker Compose
* PostgreSQL

---

### Backend Setup

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

---

### Frontend Setup

```bash
cd frontend
npm install
npm start
```

---

## Docker Deployment

```bash
docker-compose up --build
```

---

## Environment Configuration

Create a `.env` file in the project root:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/resume_analyzer
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password

GEMINI_API_KEY=your_api_key

REACT_APP_API_URL=http://localhost:8080/api
```

---

## CI/CD Pipeline

The project includes:

* GitHub Actions workflows
* Jenkins pipeline automation
* Docker image generation
* Kubernetes deployment manifests
* Automated testing workflows

---

## Security Practices

* Environment-based configuration
* Secure API key handling
* Input validation and sanitization
* Container security best practices
* CORS configuration

---

## Future Enhancements

* LinkedIn profile integration
* Resume template generation
* Advanced analytics dashboard
* Multi-language support
* Interview preparation assistant
* AI-based career roadmap generation

---

## Deployment

### Frontend

Deployed using **Vercel**. The frontend is configured with a reverse proxy (`vercel.json`) to communicate with the Render backend securely.

### Backend

Deployed using **Render**. The API is available at `https://resume-analyzer-1-4bhm.onrender.com/api`.

---

## License

This project is licensed under the MIT License.


