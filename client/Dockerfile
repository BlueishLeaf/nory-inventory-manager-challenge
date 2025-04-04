# Build stage
FROM node:20-alpine AS build
WORKDIR /app

# Copy package.json and install dependencies
COPY package.json package-lock.json ./
RUN npm ci

# Copy the rest of the application code
COPY . .

# Build the Angular application
RUN npm run build -- --configuration production

# Runtime stage with Nginx
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# Remove default nginx static assets
RUN rm -rf ./*

# Copy the built Angular app from the build stage
COPY --from=build /app/dist/client/browser/. .

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80
EXPOSE 80

# Start Nginx server
CMD ["nginx", "-g", "daemon off;"]
