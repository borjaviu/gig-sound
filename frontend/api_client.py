"""API client for backend communication."""

# Backend API base URL
# Use backend service name in Docker, localhost for local development
import os

import httpx

API_BASE_URL = os.getenv("BACKEND_API_URL", "http://localhost:8080")


async def create_user(name: str, email: str) -> dict:
    """Create a new user."""
    async with httpx.AsyncClient(base_url=API_BASE_URL, timeout=30.0) as client:
        response = await client.post(
            "/users",
            json={"name": name, "email": email},
        )
        response.raise_for_status()
        return response.json()


async def get_users() -> list[dict]:
    """Get all users."""
    async with httpx.AsyncClient(base_url=API_BASE_URL, timeout=30.0) as client:
        response = await client.get("/users")
        response.raise_for_status()
        return response.json()


async def create_event(
    name: str,
    date: str,
    venue: str,
    total_capacity: int,
    ticket_price: int = 50,
) -> dict:
    """Create a new event."""
    async with httpx.AsyncClient(base_url=API_BASE_URL, timeout=30.0) as client:
        response = await client.post(
            "/events",
            json={
                "name": name,
                "date": date,
                "venue": venue,
                "total_capacity": total_capacity,
                "ticket_price": ticket_price,
            },
        )
        response.raise_for_status()
        return response.json()


async def get_events() -> list[dict]:
    """Get all events."""
    async with httpx.AsyncClient(base_url=API_BASE_URL, timeout=30.0) as client:
        response = await client.get("/events")
        response.raise_for_status()
        return response.json()


async def delete_event(event_id: str) -> None:
    """Delete an event."""
    async with httpx.AsyncClient(base_url=API_BASE_URL, timeout=30.0) as client:
        response = await client.delete(f"/events/{event_id}")
        response.raise_for_status()


async def purchase_order(event_id: str, user_id: str, quantity: int) -> dict:
    """Purchase tickets for an event."""
    async with httpx.AsyncClient(base_url=API_BASE_URL, timeout=30.0) as client:
        response = await client.post(
            "/orders/purchase",
            json={
                "event_id": event_id,
                "user_id": user_id,
                "quantity": quantity,
            },
        )
        response.raise_for_status()
        return response.json()


async def cancel_order(order_id: str) -> dict:
    """Cancel an order."""
    async with httpx.AsyncClient(base_url=API_BASE_URL, timeout=30.0) as client:
        response = await client.request(
            "DELETE",
            "/orders/cancel",
            json={"order_id": order_id},
        )
        response.raise_for_status()
        return response.json()


async def get_event_sales(event_id: str) -> dict:
    """Get sales report for an event."""
    async with httpx.AsyncClient(base_url=API_BASE_URL, timeout=30.0) as client:
        response = await client.get(f"/orders/event/{event_id}/sales")
        response.raise_for_status()
        return response.json()
