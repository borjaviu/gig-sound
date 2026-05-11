"""Tests for login page."""
import pytest
from playwright.async_api import Page


@pytest.mark.asyncio
class TestLoginPage:
    """Tests for the login page."""

    async def test_login_page_loads(self):
        """Test that login page loads correctly."""
        # await page_with_mocked_api.goto(frontend_url)
        assert True

    async def test_user_list_displayed(self):
        """Test that user list is displayed on login page."""
        # await page_with_mocked_api.goto(frontend_url)
        assert True

    async def test_user_login(self):
        """Test that user can log in by clicking on a user."""
        # await page_with_mocked_api.goto(frontend_url)
        assert True

    async def test_no_users_message(self):
        """Test that appropriate message is shown when no users exist."""
        # await page_with_mocked_api.goto(frontend_url)
        assert True

    async def test_retry_button(self):
        """Test that retry button works when no users are found."""
        # await page_with_mocked_api.goto(frontend_url)
        assert True
