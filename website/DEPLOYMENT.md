# File Encryptor - Vercel Deployment Guide

This guide covers deploying the File Encryptor Spring Boot web application to Vercel.

## Important Note About Vercel Limitations

⚠️ **Vercel has limited support for Spring Boot applications** ⚠️

Vercel is primarily designed for frontend applications and serverless functions. While it has some Java support via `@vercel/java`, it has significant limitations for Spring Boot applications:

- **No Docker support** for Java applications
- **Function timeout limits** (10 seconds for Hobby, 15 minutes for Pro)
- **Memory limitations** (1008 MB max)
- **Cold start issues** for Java applications
- **File upload size limits** (may conflict with your encryption service)

## Alternative Recommended Platforms

For better Spring Boot support, consider these alternatives:

### 1. Google Cloud Run (Recommended)
```bash
# Build and push to Google Container Registry
gcloud builds submit --tag gcr.io/PROJECT_ID/file-encryptor-web

# Deploy to Cloud Run
gcloud run deploy file-encryptor-web \
  --image gcr.io/PROJECT_ID/file-encryptor-web \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --port 8080 \
  --memory 1Gi \
  --cpu 1
```

### 2. Railway
```bash
# Install Railway CLI
npm install -g @railway/cli

# Deploy (Railway auto-detects Dockerfile)
railway login
railway link
railway up
```

### 3. Render
- Connect your GitHub repository
- Select "Web Service"
- Use Docker runtime
- Set port to 8080

## If You Still Want to Try Vercel

### Prerequisites
- Vercel CLI installed: `npm i -g vercel`
- GitHub repository connected to Vercel

### Files Included

1. **Dockerfile** - Multi-stage build for production deployment
2. **vercel.json** - Vercel configuration (limited functionality)

### Deployment Steps

1. **Build your application locally first:**
```bash
cd D:\FileEncryptorApp\website
mvn clean package -DskipTests
```

2. **Deploy to Vercel:**
```bash
vercel --prod
```

### Expected Issues with Vercel

- **Startup time**: Java applications have longer cold starts
- **Memory usage**: Spring Boot uses more memory than Vercel's limits
- **File handling**: Large file encryption may timeout
- **Persistence**: No guarantee of container persistence between requests

## Production Configuration

### Environment Variables (for any platform)
```
SPRING_PROFILES_ACTIVE=production
SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE=10MB
SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE=10MB
SERVER_PORT=8080
JAVA_OPTS=-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0
```

### Security Considerations
- Enable HTTPS (handled automatically by most platforms)
- Set proper CORS headers if needed
- Consider rate limiting for encryption endpoints
- Monitor for abuse of file upload endpoints

## Testing Your Deployment

After deployment, test these endpoints:
- `GET /` - Should return your web interface
- `POST /encrypt` - Upload a file with secret key
- `POST /decrypt` - Upload encrypted file with secret key

## Monitoring and Logs

Most platforms provide:
- Application logs
- Performance metrics
- Error tracking
- Request analytics

Check your platform's dashboard for monitoring capabilities.

## Cost Estimates

- **Google Cloud Run**: ~$0 for light usage (generous free tier)
- **Railway**: $5/month for Hobby plan
- **Render**: $7/month for basic web service
- **Vercel**: May work on free tier but with severe limitations

## Recommendation

**Use Google Cloud Run or Railway** for the best experience with Spring Boot applications. Vercel is better suited for frontend applications and Node.js serverless functions.