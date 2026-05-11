"""Admin section with management tools."""

import reflex as rx

from frontend.state import AppState


def admin_section() -> rx.Component:
    """Admin section with tabs for event creation, user management, and sales."""
    return rx.vstack(
        rx.hstack(
            rx.icon("shield", size=32, color="red"),
            rx.heading("Admin Dashboard", size="8"),
            spacing="3",
            align="center",
        ),
        rx.tabs.root(
            rx.tabs.list(
                rx.tabs.trigger(
                    rx.hstack(
                        rx.icon("calendar-plus", size=16),
                        rx.text("Create Event"),
                        spacing="2",
                    ),
                    value="create_event",
                ),
                rx.tabs.trigger(
                    rx.hstack(
                        rx.icon("calendar", size=16),
                        rx.text("Event List"),
                        spacing="2",
                    ),
                    value="event_list",
                ),
                rx.tabs.trigger(
                    rx.hstack(
                        rx.icon("user-plus", size=16),
                        rx.text("Create User"),
                        spacing="2",
                    ),
                    value="create_user",
                ),
                rx.tabs.trigger(
                    rx.hstack(
                        rx.icon("users", size=16),
                        rx.text("User List"),
                        spacing="2",
                    ),
                    value="user_list",
                ),
                rx.tabs.trigger(
                    rx.hstack(
                        rx.icon("receipt", size=16),
                        rx.text("Orders"),
                        spacing="2",
                    ),
                    value="orders",
                ),
                rx.tabs.trigger(
                    rx.hstack(
                        rx.icon("chart-bar", size=16),
                        rx.text("Sales Dashboard"),
                        spacing="2",
                    ),
                    value="sales",
                ),
            ),
            rx.tabs.content(
                event_creation_tab(),
                value="create_event",
            ),
            rx.tabs.content(
                event_list_tab(),
                value="event_list",
            ),
            rx.tabs.content(
                user_creation_tab(),
                value="create_user",
            ),
            rx.tabs.content(
                user_list_tab(),
                value="user_list",
            ),
            rx.tabs.content(
                orders_tab(),
                value="orders",
            ),
            rx.tabs.content(
                sales_tab(),
                value="sales",
            ),
            default_value="create_event",
        ),
        spacing="4",
        width="100%",
    )


def event_creation_tab() -> rx.Component:
    """Event creation form for admin."""
    return rx.card(
        rx.vstack(
            rx.hstack(
                rx.icon("calendar-plus", size=24, color="green"),
                rx.heading("Create New Event", size="6"),
                spacing="2",
            ),
            rx.divider(),
            rx.vstack(
                rx.hstack(
                    rx.icon("tag", size=16, color="gray"),
                    rx.text("Event Name", weight="bold", size="2"),
                    spacing="1",
                ),
                rx.input(
                    placeholder="Event name",
                    value=AppState.event_name,
                    on_change=AppState.set_event_name,
                    width="100%",
                ),
                spacing="1",
                width="100%",
            ),
            rx.vstack(
                rx.hstack(
                    rx.icon("calendar-days", size=16, color="gray"),
                    rx.text("Date", weight="bold", size="2"),
                    spacing="1",
                ),
                rx.input(
                    type="date",
                    value=AppState.event_date,
                    on_change=AppState.set_event_date,
                    width="100%",
                ),
                spacing="1",
                width="100%",
            ),
            rx.vstack(
                rx.hstack(
                    rx.icon("map-pin", size=16, color="gray"),
                    rx.text("Venue", weight="bold", size="2"),
                    spacing="1",
                ),
                rx.input(
                    placeholder="Venue",
                    value=AppState.event_venue,
                    on_change=AppState.set_event_venue,
                    width="100%",
                ),
                spacing="1",
                width="100%",
            ),
            rx.hstack(
                rx.vstack(
                    rx.hstack(
                        rx.icon("users", size=16, color="gray"),
                        rx.text("Total Capacity", weight="bold", size="2"),
                        spacing="1",
                    ),
                    rx.input(
                        type="number",
                        placeholder="Capacity",
                        value=AppState.event_capacity,
                        on_change=AppState.set_event_capacity,
                        width="100%",
                    ),
                    spacing="1",
                    width="100%",
                ),
                rx.vstack(
                    rx.hstack(
                        rx.icon("dollar-sign", size=16, color="gray"),
                        rx.text("Ticket Price", weight="bold", size="2"),
                        spacing="1",
                    ),
                    rx.input(
                        type="number",
                        placeholder="Price",
                        value=AppState.event_price,
                        on_change=AppState.set_event_price,
                        width="100%",
                    ),
                    spacing="1",
                    width="100%",
                ),
                spacing="3",
                width="100%",
            ),
            rx.button(
                rx.hstack(
                    rx.icon("plus", size=16),
                    rx.text("Create Event"),
                    spacing="2",
                ),
                on_click=AppState.create_event,
                color_scheme="green",
                size="3",
                width="100%",
            ),
            spacing="3",
            width="100%",
        ),
        width="100%",
    )


def user_creation_tab() -> rx.Component:
    """User creation form for admin."""
    return rx.card(
        rx.vstack(
            rx.hstack(
                rx.icon("user-plus", size=24, color="blue"),
                rx.heading("Create New User", size="6"),
                spacing="2",
            ),
            rx.divider(),
            rx.vstack(
                rx.hstack(
                    rx.icon("user", size=16, color="gray"),
                    rx.text("Username", weight="bold", size="2"),
                    spacing="1",
                ),
                rx.input(
                    placeholder="Username",
                    value=AppState.user_name,
                    on_change=AppState.set_user_name,
                    width="100%",
                ),
                spacing="1",
                width="100%",
            ),
            rx.vstack(
                rx.hstack(
                    rx.icon("mail", size=16, color="gray"),
                    rx.text("Email", weight="bold", size="2"),
                    spacing="1",
                ),
                rx.input(
                    type="email",
                    placeholder="Email",
                    value=AppState.user_email,
                    on_change=AppState.set_user_email,
                    width="100%",
                ),
                spacing="1",
                width="100%",
            ),
            rx.button(
                rx.hstack(
                    rx.icon("plus", size=16),
                    rx.text("Create User"),
                    spacing="2",
                ),
                on_click=AppState.create_user,
                color_scheme="blue",
                size="3",
                width="100%",
            ),
            spacing="3",
            width="100%",
        ),
        width="100%",
    )


def event_list_tab() -> rx.Component:
    """Event list view for admin."""
    return rx.vstack(
        rx.hstack(
            rx.icon("calendar", size=24, color="green"),
            rx.heading("All Events", size="6"),
            rx.spacer(),
            rx.button(
                rx.hstack(
                    rx.icon("refresh-cw", size=16),
                    rx.text("Refresh"),
                    spacing="2",
                ),
                on_click=AppState.load_events,
                variant="soft",
                size="2",
            ),
            spacing="3",
            width="100%",
            align="center",
        ),
        rx.divider(),
        rx.cond(
            AppState.event_loading,
            rx.center(
                rx.spinner(size="3"),
                padding="8",
            ),
            rx.cond(
                AppState.events.length() == 0,
                rx.callout(
                    "No events found. Create one in the Create Event tab.",
                    icon="calendar-x",
                    color_scheme="gray",
                    width="100%",
                ),
                rx.vstack(
                    rx.foreach(
                        AppState.events,
                        lambda event: rx.card(
                            rx.hstack(
                                rx.vstack(
                                    rx.hstack(
                                        rx.icon("calendar", size=18, color="green"),
                                        rx.heading(event["name"], size="4"),
                                        spacing="2",
                                    ),
                                    rx.hstack(
                                        rx.icon("map-pin", size=14, color="gray"),
                                        rx.text(event["venue"], size="2"),
                                        spacing="1",
                                    ),
                                    rx.hstack(
                                        rx.icon("calendar-days", size=14, color="gray"),
                                        rx.text(event["date"], size="2"),
                                        spacing="1",
                                    ),
                                    rx.hstack(
                                        rx.icon("ticket", size=14, color="blue"),
                                        rx.text(
                                            f"Available: {event['available_tickets']}/{event['total_capacity']}",
                                            size="2",
                                            color="blue",
                                        ),
                                        spacing="1",
                                    ),
                                    rx.hstack(
                                        rx.icon("dollar-sign", size=14, color="green"),
                                        rx.text(f"${event['ticket_price']}", size="2", color="green"),
                                        spacing="1",
                                    ),
                                    spacing="2",
                                    align="start",
                                ),
                                rx.spacer(),
                                rx.button(
                                    rx.icon("trash-2", size=18),
                                    "Delete",
                                    on_click=AppState.delete_event_handler(event["id"]),
                                    color_scheme="red",
                                    variant="soft",
                                    size="3",
                                    aria_label=f"Delete event {event['name']}",
                                ),
                                spacing="4",
                                width="100%",
                                align="center",
                            ),
                            width="100%",
                        ),
                    ),
                    spacing="3",
                    width="100%",
                ),
            ),
        ),
        spacing="4",
        width="100%",
    )


def user_list_tab() -> rx.Component:
    """User list view for admin."""
    return rx.vstack(
        rx.hstack(
            rx.icon("users", size=24, color="blue"),
            rx.heading("All Users", size="6"),
            rx.spacer(),
            rx.button(
                rx.hstack(
                    rx.icon("refresh-cw", size=16),
                    rx.text("Refresh"),
                    spacing="2",
                ),
                on_click=AppState.load_users,
                variant="soft",
                size="2",
            ),
            spacing="3",
            width="100%",
            align="center",
        ),
        rx.divider(),
        rx.cond(
            AppState.user_loading,
            rx.center(
                rx.spinner(size="3"),
                padding="8",
            ),
            rx.cond(
                AppState.users.length() == 0,
                rx.callout(
                    "No users found. Create one in the Create User tab.",
                    icon="user-x",
                    color_scheme="gray",
                    width="100%",
                ),
                rx.vstack(
                    rx.foreach(
                        AppState.users,
                        lambda user: rx.card(
                            rx.hstack(
                                rx.vstack(
                                    rx.hstack(
                                        rx.icon("user", size=18, color="blue"),
                                        rx.heading(user["name"], size="4"),
                                        spacing="2",
                                    ),
                                    rx.hstack(
                                        rx.icon("mail", size=14, color="gray"),
                                        rx.text(user["email"], size="2"),
                                        spacing="1",
                                    ),
                                    spacing="2",
                                    align="start",
                                ),
                                spacing="4",
                                width="100%",
                            ),
                            width="100%",
                        ),
                    ),
                    spacing="3",
                    width="100%",
                ),
            ),
        ),
        spacing="4",
        width="100%",
    )


def orders_tab() -> rx.Component:
    """Orders management view for admin."""
    return rx.vstack(
        rx.hstack(
            rx.icon("receipt", size=24, color="purple"),
            rx.heading("All Orders", size="6"),
            rx.spacer(),
            rx.button(
                rx.hstack(
                    rx.icon("refresh-cw", size=16),
                    rx.text("Refresh"),
                    spacing="2",
                ),
                on_click=AppState.load_all_orders,
                variant="soft",
                size="2",
            ),
            spacing="3",
            width="100%",
            align="center",
        ),
        rx.divider(),
        rx.cond(
            AppState.all_orders_loading,
            rx.center(
                rx.spinner(size="3"),
                padding="8",
            ),
            rx.cond(
                AppState.all_orders.length() == 0,
                rx.callout(
                    "No orders found.",
                    icon="inbox",
                    color_scheme="gray",
                    width="100%",
                ),
                rx.vstack(
                    rx.foreach(
                        AppState.all_orders,
                        lambda order: rx.card(
                            rx.vstack(
                                rx.hstack(
                                    rx.vstack(
                                        rx.hstack(
                                            rx.icon("calendar", size=18, color="green"),
                                            rx.heading(order["event_name"], size="4"),
                                            spacing="2",
                                        ),
                                        rx.hstack(
                                            rx.icon("user", size=14, color="blue"),
                                            rx.text(order["user_name"], size="2"),
                                            spacing="1",
                                        ),
                                        rx.hstack(
                                            rx.icon("ticket", size=14, color="purple"),
                                            rx.text(f"{order['quantity']} ticket(s)", size="2", weight="bold"),
                                            spacing="1",
                                        ),
                                        rx.hstack(
                                            rx.icon("dollar-sign", size=14, color="green"),
                                            rx.text(f"${order['total_price']}", size="2", color="green"),
                                            spacing="1",
                                        ),
                                        spacing="2",
                                        align="start",
                                        flex="1",
                                    ),
                                    rx.vstack(
                                        rx.cond(
                                            order["status"] == "cancelled",
                                            rx.badge(
                                                rx.icon("x", size=14),
                                                "Cancelled",
                                                color_scheme="red",
                                                size="2",
                                            ),
                                            rx.badge(
                                                rx.icon("check", size=14),
                                                "Active",
                                                color_scheme="green",
                                                size="2",
                                            ),
                                        ),
                                        rx.cond(
                                            order["status"] != "cancelled",
                                            rx.button(
                                                rx.icon("trash-2", size=16),
                                                "Cancel",
                                                on_click=AppState.cancel_order_admin(order["id"]),
                                                loading=AppState.cancelling_order_id == order["id"],
                                                color_scheme="red",
                                                variant="soft",
                                                size="2",
                                            ),
                                        ),
                                        spacing="2",
                                        align="end",
                                    ),
                                    spacing="4",
                                    width="100%",
                                    align="center",
                                ),
                                rx.hstack(
                                    rx.icon("hash", size=12, color="gray"),
                                    rx.text(f"Order ID: {order['id'][:13]}...", size="1", color="gray"),
                                    spacing="1",
                                ),
                                spacing="2",
                                width="100%",
                            ),
                            width="100%",
                        ),
                    ),
                    spacing="3",
                    width="100%",
                ),
            ),
        ),
        rx.cond(
            AppState.order_error,
            rx.callout(
                AppState.order_error,
                icon="triangle-alert",
                color_scheme="red",
                width="100%",
            ),
        ),
        spacing="4",
        width="100%",
    )


def sales_tab() -> rx.Component:
    """Sales dashboard for admin."""
    from frontend.components.sales_dashboard import sales_dashboard

    return sales_dashboard()
