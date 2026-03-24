# Docker Setup Guide

## Building and Running the User Service with Docker

### Option 1: Using Docker Compose (Recommended)

Docker Compose will automatically build the image and start both the MySQL database and the User Service.

```bash
# Build and start all services
docker-compose up --build

# Run in background
docker-compose up -d --build

# View logs
docker-compose logs -f user-service

# Stop services
docker-compose down

# Clean up (including volumes)
docker-compose down -v
```

### Option 2: Building and Running Manually

#### Build the Docker Image

```bash
docker build -t user-service:1.0.0 .
```

#### Run MySQL Database

```bash
docker run -d \
  --name userdb-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=userdb \
  -e MYSQL_USER=userdb_user \
  -e MYSQL_PASSWORD=test \
  -p 3306:3306 \
  mysql:8.0
```

#### Run User Service

```bash
docker run -d \
  --name user-service \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://userdb-mysql:3306/userdb?useSSL=false&serverTimezone=UTC \
  -e SPRING_DATASOURCE_USERNAME=userdb_user \
  -e SPRING_DATASOURCE_PASSWORD=test \
  -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
  -p 8083:8083 \
  --link userdb-mysql:mysql \
  user-service:1.0.0
```

### Verifying the Services

#### Check Service Status

```bash
# View running containers
docker-compose ps

# Check user-service logs
docker-compose logs user-service

# Check health status
curl http://localhost:8083/actuator/health
```

#### Test API

```bash
# Create a user
curl -X POST http://localhost:8083/api/users/v1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "role": "USER"
  }'
```

### Environment Variables

The following environment variables can be configured:

- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `SPRING_JPA_HIBERNATE_DDL_AUTO`: Hibernate DDL auto strategy (update/create/validate)
- `JAVA_OPTS`: JVM options (e.g., `-Xmx512m -Xms256m`)

### Pushing to Docker Registry

#### Tag the Image

```bash
docker tag user-service:1.0.0 your-registry/user-service:1.0.0
```

#### Push to Registry

```bash
docker login
docker push your-registry/user-service:1.0.0
```

### Troubleshooting

#### Container won't start

```bash
# Check logs
docker logs user-service

# Inspect container
docker inspect user-service
```

#### Database connection issues

```bash
# Test MySQL connectivity from user-service container
docker exec user-service curl http://mysql:3306
```

#### Clean up resources

```bash
# Remove stopped containers
docker container prune

# Remove unused images
docker image prune

# Remove unused volumes
docker volume prune
```

### Production Considerations

- Use secrets management for sensitive data (passwords, tokens)
- Implement resource limits (memory, CPU)
- Use health checks for automatic restarts
- Set up logging and monitoring
- Use a container registry for image storage
- Consider using Kubernetes for orchestration
