# Resume Analyzer

AI-powered resume analysis application with comprehensive career insights and ATS compatibility checking.

## 🚀 Features

### Core Functionality
- **PDF Resume Parsing**: Extract and analyze text from PDF resumes
- **Skill Detection**: Automatically identify technical and soft skills
- **Experience Analysis**: Extract and evaluate work experience
- **Education Assessment**: Analyze educational background
- **Scoring System**: Overall resume quality scoring (0-100)
- **Job Matching**: Calculate compatibility with job descriptions

### AI-Powered Insights
- **🤖 Comprehensive AI Review**: Detailed analysis using Gemini API
- **📋 ATS Compatibility Analysis**: Optimize for Applicant Tracking Systems
- **🚀 Career Development Advice**: Personalized career guidance
- **🎯 Job-Specific Recommendations**: Tailored suggestions for target roles

### Modern UI/UX
- **🎨 Beautiful Interface**: Built with React and Tailwind CSS
- **⚡ Real-time Animations**: Progress tracking during analysis
- **📱 Responsive Design**: Works on all devices
- **🔄 Live Updates**: Real-time feedback and results

## 🏗️ Architecture

```
resume-analyzer/
│
├── backend/                              # Spring Boot Maven project
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/college/resume/
│   │   │   │   ├── controller/          # REST APIs (upload, analyze, match)
│   │   │   │   ├── service/             # Resume parsing, skill extraction
│   │   │   │   ├── repository/          # JPA for storing results
│   │   │   │   ├── model/               # DTOs & entities
│   │   │   │   ├── dto/                 # Data transfer objects
│   │   │   │   ├── config/              # CORS and other configurations
│   │   │   │   └── ResumeAnalyzerApplication.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── static/              # Built frontend will be copied here
│   │   └── test/                        # Unit & integration tests
│   ├── pom.xml                          # Maven dependencies
│   └── Dockerfile                       # Multi-stage: build jar + copy static frontend
│
├── frontend/                             # React + Tailwind CSS application
│   ├── public/
│   ├── src/
│   │   ├── components/                  # Reusable UI (UploadForm, ResultCard)
│   │   ├── pages/                       # Dashboard, Analysis page
│   │   ├── services/                    # Axios calls to backend API
│   │   ├── App.js
│   │   ├── index.js
│   │   └── index.css                    # Tailwind directives
│   ├── package.json                     # React, Tailwind, Axios, etc.
│   ├── tailwind.config.js
│   ├── postcss.config.js
│   └── Dockerfile.dev                   # For development
│
├── docker-compose.yml                   # Orchestrates backend + database
├── Jenkinsfile                          # Declarative pipeline (build, test, deploy)
├── .github/workflows/
│   └── ci-cd.yml                        # GitHub Actions workflow
├── kubernetes/                          # K8s manifests for production
│   ├── deployment.yaml
│   ├── service.yaml
│   └── configmap.yaml
├── .gitignore
├── README.md                            # This file
└── docs/                                # Architecture diagram, API docs
    └── devops-setup.md
```

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: PostgreSQL 15
- **Build Tool**: Maven
- **PDF Processing**: Apache PDFBox
- **AI Integration**: Google Gemini API

### Frontend
- **Framework**: React 18
- **Styling**: Tailwind CSS 3
- **HTTP Client**: Axios
- **Build Tool**: Create React App

### DevOps & Infrastructure
- **Containerization**: Docker & Docker Compose
- **CI/CD**: GitHub Actions & Jenkins
- **Orchestration**: Kubernetes
- **Database**: PostgreSQL with persistent storage

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- Node.js 18+
- Java 17+
- Maven 3.9+

### Option 1: Docker Compose (Recommended)

```bash
# Clone the repository
git clone <repository-url>
cd resume-analyzer

# Start all services
docker-compose up -d

# Access the application
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080
```

### Option 2: Manual Setup

#### Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

#### Frontend Setup
```bash
cd frontend
npm install
npm start
```

## 🔧 Configuration

### Environment Variables

Create a `.env` file in the root directory:

```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/resume_analyzer
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password

# AI API Keys
GEMINI_API_KEY=your-gemini-api-key
OPENAI_API_KEY=your-openai-api-key

# Frontend Configuration
REACT_APP_API_URL=http://localhost:8080/api
```

### Gemini API Setup

1. Get your Gemini API key from [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Set the `GEMINI_API_KEY` environment variable
3. The application will automatically use Gemini for AI analysis

## 📊 API Documentation

### Endpoints

#### Resume Analysis
```http
POST /api/resume/analyze
Content-Type: multipart/form-data

file: <PDF file>
jobDescription: <optional job description>
```

#### Analysis History
```http
GET /api/resume/history
```

#### Get Analysis by ID
```http
GET /api/resume/{id}
```

#### Health Check
```http
GET /api/resume/health
```

### Response Format

```json
{
  "id": 1,
  "fileName": "resume.pdf",
  "content": "Extracted resume text...",
  "score": 85,
  "matchPercentage": 75,
  "skills": ["Java", "Spring Boot", "React", "SQL"],
  "experience": "5 years of software development...",
  "education": "Bachelor's in Computer Science...",
  "suggestions": ["Add more quantifiable achievements..."],
  "aiReview": "## Executive Summary\nThis resume demonstrates...",
  "atsAnalysis": "## ATS Score (85/100)\n## Keyword Analysis...",
  "careerAdvice": "## Career Positioning\nBased on your experience...",
  "analyzedAt": "2023-12-01T10:30:00"
}
```

## 🧪 Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Frontend Tests
```bash
cd frontend
npm test
```

### Integration Tests
```bash
# Run full test suite
docker-compose -f docker-compose.test.yml up --abort-on-container-exit
```

## 🚀 Deployment

### Production Deployment with Kubernetes

1. **Create namespace**
   ```bash
   kubectl create namespace production
   ```

2. **Apply configurations**
   ```bash
   kubectl apply -f kubernetes/configmap.yaml -n production
   kubectl apply -f kubernetes/deployment.yaml -n production
   kubectl apply -f kubernetes/service.yaml -n production
   ```

3. **Set up secrets**
   ```bash
   kubectl create secret generic resume-secrets \
     --from-literal=db-username=postgres \
     --from-literal=db-password=password \
     --from-literal=gemini-api-key=your-key \
     -n production
   ```

### CI/CD Pipeline

The application includes both GitHub Actions and Jenkins pipelines for automated testing, building, and deployment.

#### GitHub Actions
- Triggers on push to main/develop branches
- Runs tests, builds Docker images, deploys to production
- Located in `.github/workflows/ci-cd.yml`

#### Jenkins
- Declarative pipeline with stages for build, test, deploy
- Docker registry integration
- Kubernetes deployment
- Located in `Jenkinsfile`

## 📈 Performance & Monitoring

### Application Metrics
- Spring Boot Actuator endpoints at `/actuator`
- Health checks, metrics, and environment info
- Custom performance monitoring

### Container Monitoring
- Resource limits and requests configured
- Liveness and readiness probes
- Horizontal pod autoscaling support

## 🔒 Security

### Authentication & Authorization
- CORS configuration for cross-origin requests
- Input validation and sanitization
- SQL injection prevention with JPA

### Container Security
- Non-root user execution
- Minimal base images
- Security scanning in CI/CD pipeline

### Secrets Management
- Kubernetes secrets for sensitive data
- Environment-specific configurations
- No hardcoded credentials

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Follow Java and React best practices
- Write unit tests for new features
- Update documentation for API changes
- Use conventional commit messages

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For support and questions:

1. Check the [documentation](docs/devops-setup.md)
2. Search existing [issues](../../issues)
3. Create a new issue with detailed information
4. Contact the development team

## 🗺️ Roadmap

### Upcoming Features
- [ ] Multi-language resume support
- [ ] Resume template generation
- [ ] Interview preparation tools
- [ ] Salary insights based on skills
- [ ] LinkedIn integration
- [ ] Bulk resume processing

### Technical Improvements
- [ ] Microservices architecture
- [ ] Advanced caching strategies
- [ ] Real-time collaboration features
- [ ] Mobile application
- [ ] Advanced analytics dashboard

---

**Built with ❤️ for job seekers and recruiters**
