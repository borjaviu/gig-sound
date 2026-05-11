# Frontend Tests with Playwright

This directory contains end-to-end tests for the GiG Sound frontend application using Playwright.

## Setup

### Option 1: Local Setup (requires system dependencies)

1. Install dependencies:
```bash
uv sync --dev
```

2. Install Playwright browsers:
```bash
playwright install chromium
```

3. Install system dependencies (if needed):
   - **Ubuntu/Debian**: `sudo apt-get install libnss3 libnspr4 libatk1.0-0 libatk-bridge2.0-0 libcups2 libxkbcommon0 libxcomposite1 libpango-1.0-0 libcairo2 libasound2 libatspi2.0-0`
   - **Arch Linux**: `sudo pacman -S nss nspr atk at-spi2-core libcups libxkbcommon libxcomposite pango cairo alsa-lib`
   - **Or use**: `sudo playwright install-deps`

### Option 2: Docker Setup (Recommended - No system dependencies needed)

Use the provided Dockerfile to run tests in an isolated environment:

```bash
# Build the test image
docker build -f Dockerfile.test-frontend -t gig-sound-frontend-tests .

# Run tests using Docker
docker run --rm \
  -v "$(pwd):/app" \
  -v "$(pwd)/test-results:/app/test-results" \
  --network host \
  -e BACKEND_API_URL=http://localhost:8080 \
  -e REFLEX_URL=http://localhost:3000 \
  gig-sound-frontend-tests

# Or use the convenience script
./frontend/tests/run_tests_docker.sh
```

## Running Tests

### Run all tests locally:
```bash
pytest frontend/tests/ -v
```

### Run all tests in Docker:
```bash
docker run --rm -v "$(pwd):/app" --network host gig-sound-frontend-tests
```

### Run specific test file:
```bash
# Local
pytest frontend/tests/test_login.py -v

# Docker
docker run --rm -v "$(pwd):/app" --network host gig-sound-frontend-tests pytest frontend/tests/test_login.py -v
```

### Run tests with browser visible (non-headless):
Modify `conftest.py` to change `headless=True` to `headless=False` in the browser fixture.

### Run tests in debug mode:
```bash
pytest frontend/tests/ --pdb
```

## Test Structure

- `conftest.py` - Shared fixtures and test configuration
- `test_login.py` - Tests for login page and user selection
- `test_events.py` - Tests for events page, event display, and ticket purchase
- `test_my_tickets.py` - Tests for My Tickets page and order management
- `test_admin.py` - Tests for admin section (user management, event management, sales dashboard)

## Prerequisites

Before running the tests, ensure:
1. The frontend application is running (typically on `http://localhost:3000`)
2. The backend API is running and accessible (or mocked via route interception)
3. Playwright browsers are installed

## Mocking

The tests use Playwright's route interception to mock backend API calls. This allows tests to run without requiring a real backend connection. The mocks are configured in `conftest.py` via the `page_with_mocked_api` fixture.

## Notes

- Tests assume the Reflex app runs on `http://localhost:3000` by default
- Tests use mocked API responses to ensure reliability and speed
- All tests are async and use Playwright's async API
