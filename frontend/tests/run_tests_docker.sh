#!/bin/bash
# Script to run frontend tests in Docker

set -e

echo "Building Docker image for frontend tests..."
docker build -f Dockerfile.test-frontend -t gig-sound-frontend-tests .

echo "Running frontend tests in Docker..."
docker run --rm \
  -v "$(pwd):/app" \
  -v "$(pwd)/test-results:/app/test-results" \
  --network host \
  -e BACKEND_API_URL=http://localhost:8080 \
  -e REFLEX_URL=http://localhost:3000 \
  gig-sound-frontend-tests \
  pytest frontend/tests/ -v --tb=short

echo "Tests completed! Check test-results/ for output."
