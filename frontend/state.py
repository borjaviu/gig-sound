"""State management for the frontend application."""

from typing import Optional

import reflex as rx

from frontend.api_client import (
    cancel_order,
    create_event,
    create_user,
    delete_event,
    get_event_sales,
    get_events,
    get_users,
    purchase_order,
)


class AppState(rx.State):
    """Main application state."""

    # User management
    users: list[dict] = []
    selected_user_id: Optional[str] = None
    user_name: str = ""
    user_email: str = ""
    user_loading: bool = False
    user_error: str = ""

    # Event management
    events: list[dict] = []
    selected_event_id: Optional[str] = None
    event_name: str = ""
    event_date: str = ""
    event_venue: str = ""
    event_capacity: str = ""
    event_price: str = "50"
    event_loading: bool = False
    event_error: str = ""

    # Order management
    order_quantity: str = "1"
    order_loading: bool = False
    order_error: str = ""
    order_success: str = ""
    orders: list[dict] = []
    show_payment_dialog: bool = False
    payment_processing: bool = False
    payment_amount: float = 0.0
    payment_order_id: str = ""
    user_orders: list[dict[str, str]] = []
    user_orders_loading: bool = False
    cancelling_order_id: str = ""
    all_orders: list[dict[str, str]] = []
    all_orders_loading: bool = False

    # Sales dashboard
    sales_data: Optional[dict] = None
    sales_loading: bool = False
    sales_error: str = ""
    sales_event_name: str = ""
    sales_total_sales: int = 0
    sales_total_revenue: float = 0.0
    sales_orders: list[dict] = []
    sales_available_tickets: int = 0

    # Navigation and theme
    current_page: str = "events"

    # Login/Authentication
    is_logged_in: bool = False
    logged_in_user_name: str = ""
    login_loading: bool = False

    def set_current_page(self, page: str):
        """Set the current page."""
        self.current_page = page

    async def login_as_user(self, user_id: str):
        """Login as a specific user."""
        self.login_loading = True
        try:
            # Find the user in the users list
            user = next((u for u in self.users if u["id"] == user_id), None)
            if user:
                self.selected_user_id = user_id
                self.logged_in_user_name = user["name"]
                self.is_logged_in = True
        finally:
            self.login_loading = False

    def logout(self):
        """Logout the current user."""
        self.is_logged_in = False
        self.logged_in_user_name = ""
        self.selected_user_id = None

    def open_purchase_dialog(self, event_id: str):
        """Open purchase dialog for a specific event."""
        self.selected_event_id = event_id
        self.show_payment_dialog = True
        self.order_quantity = "1"
        self.order_error = ""
        self.order_success = ""

    def set_selected_user_id(self, user_id: str):
        """Set the selected user ID or deselect if already selected."""
        if self.selected_user_id == user_id:
            self.selected_user_id = None
        else:
            self.selected_user_id = user_id

    def clear_selected_user(self):
        """Clear the selected user."""
        self.selected_user_id = None

    def set_selected_event_id(self, event_id: str):
        """Set the selected event ID or deselect if already selected."""
        if self.selected_event_id == event_id:
            self.selected_event_id = None
        else:
            self.selected_event_id = event_id

    def clear_selected_event(self):
        """Clear the selected event."""
        self.selected_event_id = None

    def set_user_name(self, name: str):
        """Set the user name."""
        self.user_name = name

    def set_user_email(self, email: str):
        """Set the user email."""
        self.user_email = email

    def set_event_name(self, name: str):
        """Set the event name."""
        self.event_name = name

    def set_event_date(self, date: str):
        """Set the event date."""
        self.event_date = date

    def set_event_venue(self, venue: str):
        """Set the event venue."""
        self.event_venue = venue

    def set_event_capacity(self, capacity: str):
        """Set the event capacity."""
        self.event_capacity = capacity

    def set_event_price(self, price: str):
        """Set the event price."""
        self.event_price = price

    def set_order_quantity(self, quantity: str):
        """Set the order quantity."""
        self.order_quantity = quantity

    async def load_user_orders(self):
        """Load orders for the current logged-in user."""
        if not self.selected_user_id:
            return

        self.user_orders_loading = True
        self.order_error = ""
        try:
            # Load all events to get sales data
            all_orders = []
            for event in self.events:
                try:
                    sales_data = await get_event_sales(event["id"])
                    # Filter orders for current user and exclude cancelled orders
                    for order in sales_data.get("orders", []):
                        if (
                            order.get("user_id") == self.selected_user_id
                            and order.get("status") != "cancelled"
                            and order.get("cancelled_at") is None
                        ):
                            # Add event info to order
                            order["event_name"] = event["name"]
                            order["event_venue"] = event["venue"]
                            order["event_date"] = event["date"]
                            all_orders.append(order)
                except Exception:
                    continue
            self.user_orders = all_orders
        except Exception as e:
            self.order_error = f"Error loading orders: {str(e)}"
        finally:
            self.user_orders_loading = False

    async def cancel_user_order(self, order_id: str):
        """Cancel a user's order."""
        self.cancelling_order_id = order_id
        self.order_error = ""
        try:
            await cancel_order(order_id)
            # Reload orders after cancellation
            await self.load_user_orders()
            await self.load_events()  # Refresh events to update available tickets
        except Exception as e:
            self.order_error = f"Error cancelling order: {str(e)}"
        finally:
            self.cancelling_order_id = ""

    async def load_all_orders(self):
        """Load all orders for admin management."""
        self.all_orders_loading = True
        self.order_error = ""
        try:
            # Load all events to get sales data
            all_orders = []
            for event in self.events:
                try:
                    sales_data = await get_event_sales(event["id"])
                    # Get all orders
                    for order in sales_data.get("orders", []):
                        # Add event info to order
                        order["event_name"] = event["name"]
                        order["event_venue"] = event["venue"]
                        order["event_date"] = event["date"]
                        # Find user name
                        user = next((u for u in self.users if u["id"] == order.get("user_id")), None)
                        order["user_name"] = user["name"] if user else "Unknown"
                        all_orders.append(order)
                except Exception:
                    continue
            self.all_orders = all_orders
        except Exception as e:
            self.order_error = f"Error loading orders: {str(e)}"
        finally:
            self.all_orders_loading = False

    async def cancel_order_admin(self, order_id: str):
        """Cancel an order from admin panel."""
        self.cancelling_order_id = order_id
        self.order_error = ""
        try:
            await cancel_order(order_id)
            # Reload orders after cancellation
            await self.load_all_orders()
            await self.load_events()  # Refresh events to update available tickets
        except Exception as e:
            self.order_error = f"Error cancelling order: {str(e)}"
        finally:
            self.cancelling_order_id = ""

    async def load_users(self):
        """Load all users from the API."""
        self.user_loading = True
        self.user_error = ""
        try:
            self.users = await get_users()
        except Exception as e:
            self.user_error = f"Error loading users: {str(e)}"
        finally:
            self.user_loading = False

    async def create_user(self):
        """Create a new user."""
        if not self.user_name or not self.user_email:
            self.user_error = "Name and email are required"
            return

        self.user_loading = True
        self.user_error = ""
        try:
            user = await create_user(self.user_name, self.user_email)
            self.users.append(user)
            self.user_name = ""
            self.user_email = ""
            await self.load_users()
        except Exception as e:
            self.user_error = f"Error creating user: {str(e)}"
        finally:
            self.user_loading = False

    async def load_events(self):
        """Load all events."""
        self.event_loading = True
        self.event_error = ""
        try:
            self.events = await get_events()
        except Exception as e:
            self.event_error = f"Error loading events: {str(e)}"
        finally:
            self.event_loading = False

    async def delete_event_handler(self, event_id: str):
        """Delete an event."""
        self.event_loading = True
        self.event_error = ""
        try:
            await delete_event(event_id)
            await self.load_events()
        except Exception as e:
            self.event_error = f"Error deleting event: {str(e)}"
        finally:
            self.event_loading = False

    async def create_event(self):
        """Create a new event."""
        if not all([self.event_name, self.event_date, self.event_venue, self.event_capacity]):
            self.event_error = "All fields are required"
            return

        try:
            capacity = int(self.event_capacity)
            price = int(self.event_price) if self.event_price else 50
        except ValueError:
            self.event_error = "Capacity and price must be numbers"
            return

        self.event_loading = True
        self.event_error = ""
        try:
            event = await create_event(
                self.event_name,
                self.event_date,
                self.event_venue,
                capacity,
                price,
            )
            self.events.append(event)
            self.event_name = ""
            self.event_date = ""
            self.event_venue = ""
            self.event_capacity = ""
            self.event_price = "50"
            await self.load_events()
        except Exception as e:
            self.event_error = f"Error creating event: {str(e)}"
        finally:
            self.event_loading = False

    def open_payment_dialog(self):
        """Open payment dialog and calculate amount."""
        if not self.selected_event_id:
            return

        try:
            quantity = int(self.order_quantity)
            # Find the selected event to get the price
            for event in self.events:
                if event.get("id") == self.selected_event_id:
                    self.payment_amount = float(event.get("ticket_price", 0)) * quantity
                    break
            self.show_payment_dialog = True
        except (ValueError, KeyError):
            self.order_error = "Invalid quantity or event data"

    def cancel_payment(self):
        """Cancel the payment dialog."""
        self.show_payment_dialog = False
        self.payment_processing = False

    async def process_payment(self):
        """Simulate payment processing and complete the order."""
        import asyncio

        self.payment_processing = True

        # Simulate payment gateway redirect delay
        await asyncio.sleep(2)

        # Now actually create the order
        try:
            quantity = int(self.order_quantity)
            order = await purchase_order(
                self.selected_event_id,
                self.selected_user_id,
                quantity,
            )
            self.payment_order_id = order["id"]
            self.order_success = f"Payment successful! Order ID: {order['id']}"
            self.order_quantity = "1"
            self.show_payment_dialog = False
            await self.load_events()  # Refresh events to update available tickets
        except Exception as e:
            self.order_error = f"Payment failed: {str(e)}"
            self.show_payment_dialog = False
        finally:
            self.payment_processing = False

    async def purchase_order(self):
        """Initiate purchase flow - open payment dialog."""
        if not self.selected_user_id or not self.selected_event_id:
            self.order_error = "Please select a user and event"
            return

        try:
            quantity = int(self.order_quantity)
            if quantity <= 0:
                self.order_error = "Quantity must be greater than 0"
                return
        except ValueError:
            self.order_error = "Quantity must be a number"
            return

        self.order_error = ""
        self.order_success = ""
        self.open_payment_dialog()

    async def cancel_order(self, order_id: str):
        """Cancel an order."""
        self.order_loading = True
        self.order_error = ""
        self.order_success = ""
        try:
            await cancel_order(order_id)
            self.order_success = f"Order {order_id} cancelled successfully"
            await self.load_event_sales()
            await self.load_events()  # Refresh events to update available tickets
        except Exception as e:
            self.order_error = f"Error cancelling order: {str(e)}"
        finally:
            self.order_loading = False

    async def load_event_sales(self):
        """Load sales data for selected event."""
        if not self.selected_event_id:
            self.sales_error = "Please select an event"
            return

        self.sales_loading = True
        self.sales_error = ""
        try:
            self.sales_data = await get_event_sales(self.selected_event_id)
            # Extract data into typed state vars
            if self.sales_data:
                self.sales_event_name = self.sales_data.get("event_name", "")
                self.sales_total_sales = self.sales_data.get("total_sales", 0)
                self.sales_total_revenue = self.sales_data.get("total_revenue", 0.0)
                self.sales_orders = self.sales_data.get("orders", [])
                # Get available tickets from event data
                event_data = self.sales_data.get("event", {})
                if isinstance(event_data, dict):
                    self.sales_available_tickets = event_data.get("available_tickets", 0)
                else:
                    self.sales_available_tickets = 0
        except Exception as e:
            self.sales_error = f"Error loading sales: {str(e)}"
        finally:
            self.sales_loading = False
