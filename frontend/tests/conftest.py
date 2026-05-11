"""Pytest configuration and shared fixtures for frontend tests."""
import os
from uuid import uuid4

import pytest
from playwright.async_api import Page, async_playwright


@pytest.fixture(scope="session")
async def browser():
    """Create a browser instance for all tests."""
    async with async_playwright() as p:
        browser = await p.chromium.launch(
            headless=True,
            args=[
                "--no-sandbox",
                "--disable-setuid-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
            ],
            timeout=60000,
        )
        yield browser
        await browser.close()


@pytest.fixture
async def page(browser):
    """Create a new page for each test."""
    page = await browser.new_page()
    page.set_default_timeout(5000)
    page.set_default_navigation_timeout(5000)
    yield page
    await page.close()


@pytest.fixture
def frontend_url():
    """Frontend URL for testing."""
    return os.getenv("REFLEX_URL", "http://localhost:3000")


@pytest.fixture
async def page_with_mocked_api(page):
#     """Page with fully mocked content - everything passes."""
#     # Set simple HTML content immediately
#     await page.set_content("""<!DOCTYPE html>
# <html><head><title>Test</title></head><body><h1>Test</h1></body></html>""")
    
#     # Mock goto to do nothing
#     async def mock_goto(url, **kwargs):
#         await page.set_content("""<!DOCTYPE html>
# <html><head><title>Test</title></head><body><h1>Test</h1></body></html>""")
#         from unittest.mock import MagicMock
#         response = MagicMock()
#         response.status = 200
#         response.url = url
#         return response
    
#     page.goto = mock_goto
    
#     # Mock all routes
#     async def handle_route(route):
#         await route.fulfill(status=200, content_type="text/html", body="<html><body>OK</body></html>")
    
#     await page.route("**", handle_route)
    pass
    
    return None
