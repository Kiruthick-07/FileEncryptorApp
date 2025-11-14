# File Encryptor Web Application - Render Deployment Guide

This guide explains how to deploy the File Encryptor Web Application to Render.

## Prerequisites

1. A Render account (sign up at [render.com](https://render.com))
2. Your code pushed to a Git repository (GitHub, GitLab, or Bitbucket)

## Deployment Steps

### Method 1: Using render.yaml (Recommended)

1. **Push your code to a Git repository** that includes the `render.yaml` file at the repository root.

2. **Connect to Render:**
   - Go to [render.com](https://render.com) and sign in
   - Click "New +" and select "Blueprint"
   - Connect your Git repository
   - Select the repository containing your File Encryptor app
   - Render will automatically detect the root-level `render.yaml` file

3. **Deploy:**
   - Render will automatically start building and deploying your application
   - The build process will run `mvn clean package -DskipTests`
   - Once deployed, your app will be available at the provided Render URL

### Method 2: Manual Web Service Creation

1. **Create a new Web Service:**
   - Go to [render.com](https://render.com) and sign in
   - Click "New +" and select "Web Service"
   - Connect your Git repository

2. **Configure the service:**
   - **Name:** `file-encryptor-web`
   - **Environment:** `Java`
   - **Root Directory:** `website` (if your code is in a subdirectory)
   - **Build Command:** `mvn clean package -DskipTests`
   - **Start Command:** `java -jar target/file-encryptor-web-0.0.1-SNAPSHOT.jar`

3. **Environment Variables:**
   Add these environment variables in the Render dashboard:
   ```
   JAVA_TOOL_OPTIONS = -Xmx512m -Xms256m
   SERVER_PORT = 10000
   ```

4. **Deploy:**
   - Click "Create Web Service"
   - Render will build and deploy your application

## Configuration Details

### Port Configuration
The application is configured to use the `PORT` environment variable provided by Render, with a fallback to port 8080 for local development.

### Memory Settings
The `JAVA_TOOL_OPTIONS` environment variable is set to limit memory usage to 512MB, which is suitable for Render's free tier.

### Build Process
- Maven builds the application with `mvn clean package -DskipTests`
- Tests are skipped to reduce build time
- The resulting JAR file is executed with `java -jar`

## Accessing Your Application

Once deployed, your application will be available at:
```
https://your-app-name.onrender.com
```

## Troubleshooting

### Build Issues
- Ensure your `pom.xml` has the correct Spring Boot Maven plugin configuration
- Check that all dependencies are properly declared
- Review build logs in the Render dashboard

### Runtime Issues
- Check application logs in the Render dashboard
- Verify environment variables are set correctly
- Ensure the application starts within Render's timeout limits

### Performance
- The free tier may experience cold starts after periods of inactivity
- Consider upgrading to a paid plan for better performance
- Monitor memory usage through Render's metrics

## Local Testing

Before deploying, test the build process locally:

```bash
cd website
mvn clean package -DskipTests
java -jar target/file-encryptor-web-0.0.1-SNAPSHOT.jar
```

The application should start and be accessible at `http://localhost:8080`.

## Continuous Deployment

Render automatically redeploys your application when you push changes to your connected Git branch. This enables seamless continuous deployment.

## Security Considerations

- Render provides HTTPS by default
- Consider adding authentication if handling sensitive files
- Review and configure appropriate file size limits
- Ensure secure handling of encryption keys

## Support

For Render-specific issues, consult:
- [Render Documentation](https://render.com/docs)
- [Render Community Forum](https://community.render.com)

For application-specific issues, refer to the main project documentation.