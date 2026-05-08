# DevOps Setup Guide

This document provides comprehensive instructions for setting up the CI/CD pipeline and deployment infrastructure for the Resume Analyzer application.

## Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │   PostgreSQL    │
│   (React)       │────│  (Spring Boot)  │────│   Database       │
│   Port: 3000    │    │   Port: 8080    │    │   Port: 5432    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Local Development Setup

### Prerequisites
- Docker & Docker Compose
- Node.js 18+
- Java 17+
- Maven 3.9+
- PostgreSQL 15+ (optional, can use Docker)

### Quick Start

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd resume-analyzer
   ```

2. **Start services with Docker Compose**
   ```bash
   docker-compose up -d
   ```

3. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - Database: localhost:5432

### Manual Setup

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

## CI/CD Pipeline

### GitHub Actions Workflow

The `.github/workflows/ci-cd.yml` file defines the automated pipeline:

1. **Backend Tests**
   - Runs Maven tests with PostgreSQL
   - Uses cached Maven dependencies

2. **Frontend Tests**
   - Runs Jest tests with coverage
   - Uses cached npm dependencies

3. **Build & Deploy**
   - Builds Docker images
   - Pushes to container registry
   - Deploys to production (main branch only)

### Jenkins Pipeline

The `Jenkinsfile` provides an alternative CI/CD pipeline:

1. **Build Stage**: Compiles backend and frontend
2. **Test Stage**: Runs unit and integration tests
3. **Docker Stage**: Builds and pushes container images
4. **Deploy Stage**: Applies Kubernetes manifests

## Docker Configuration

### Multi-stage Backend Dockerfile
- **Stage 1**: Maven build environment
- **Stage 2**: Runtime environment with JRE
- **Optimization**: Minimal final image size

### Frontend Dockerfile
- Development environment with hot reload
- Volume mounts for live code changes

## Kubernetes Deployment

### Namespace Structure
- `production`: Production workloads
- `staging`: Staging environment

### Key Resources
- **Deployments**: 3 backend replicas, 2 frontend replicas
- **Services**: Internal backend service, external frontend service
- **ConfigMaps**: Application configuration
- **Secrets**: Database credentials, API keys

### Health Checks
- **Liveness Probe**: Container health monitoring
- **Readiness Probe**: Traffic routing readiness

## Environment Variables

### Required Environment Variables
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

## Monitoring and Logging

### Application Monitoring
- Spring Boot Actuator endpoints
- Health checks at `/api/resume/health`
- Metrics collection

### Container Monitoring
- Resource limits and requests
- Pod health monitoring
- Service availability

## Security Considerations

### API Security
- CORS configuration
- Input validation
- SQL injection prevention

### Container Security
- Non-root user execution
- Minimal base images
- Security scanning in pipeline

### Secrets Management
- Kubernetes secrets for sensitive data
- Environment-specific configurations
- No hardcoded credentials

## Performance Optimization

### Backend Optimization
- Database connection pooling
- Caching strategies
- Resource limits

### Frontend Optimization
- Code splitting
- Lazy loading
- Asset optimization

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Check PostgreSQL service status
   - Verify connection string
   - Confirm credentials

2. **Frontend Build Fails**
   - Clear npm cache: `npm cache clean --force`
   - Delete node_modules and reinstall
   - Check Node.js version compatibility

3. **Docker Build Issues**
   - Verify Docker daemon status
   - Check disk space
   - Review build logs

### Debug Commands
```bash
# Check pod status
kubectl get pods -n production

# View pod logs
kubectl logs <pod-name> -n production

# Debug container
kubectl exec -it <pod-name> -n production -- /bin/bash

# Check service endpoints
kubectl get services -n production
```

## Deployment Strategies

### Blue-Green Deployment
- Zero downtime deployments
- Instant rollback capability
- Traffic switching

### Rolling Updates
- Gradual pod replacement
- Health check verification
- Automatic rollback on failure

## Scaling Considerations

### Horizontal Scaling
- Increase pod replicas
- Load balancer configuration
- Auto-scaling policies

### Vertical Scaling
- Resource allocation
- Performance monitoring
- Capacity planning

## Backup and Recovery

### Database Backups
- Automated backup schedules
- Point-in-time recovery
- Cross-region replication

### Application State
- Stateless application design
- External storage for uploads
- Session management

## Cost Optimization

### Resource Efficiency
- Right-sizing containers
- Auto-scaling policies
- Spot instance usage

### Monitoring Costs
- Resource utilization tracking
- Cost allocation
- Budget alerts
