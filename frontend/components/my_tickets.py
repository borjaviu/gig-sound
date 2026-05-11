"""My Tickets page component."""

import reflex as rx

from frontend.state import AppState


def ticket_card(order: dict) -> rx.Component:
    """Individual ticket card with QR code."""
    return rx.card(
        rx.vstack(
            rx.hstack(
                rx.vstack(
                    rx.hstack(
                        rx.icon("calendar", size=20, color="green"),
                        rx.heading(order["event_name"], size="5"),
                        spacing="2",
                    ),
                    rx.hstack(
                        rx.icon("map-pin", size=14, color="gray"),
                        rx.text(order["event_venue"], size="2"),
                        spacing="1",
                    ),
                    rx.hstack(
                        rx.icon("calendar-days", size=14, color="gray"),
                        rx.text(order["event_date"], size="2"),
                        spacing="1",
                    ),
                    rx.hstack(
                        rx.icon("ticket", size=14, color="blue"),
                        rx.text(f"{order['quantity']} ticket(s)", size="2", weight="bold", color="blue"),
                        spacing="1",
                    ),
                    rx.hstack(
                        rx.icon("hash", size=14, color="gray"),
                        rx.text(f"Order: {order['id'][:8]}...", size="1", color="gray"),
                        spacing="1",
                    ),
                    spacing="2",
                    align="start",
                    flex="1",
                ),
                rx.vstack(
                    rx.box(
                        rx.html(
                            '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100" width="120" height="120"><rect width="100" height="100" fill="white"/><path d="M10,10 h10 v10 h-10 z M30,10 h10 v10 h-10 z M50,10 h10 v10 h-10 z M70,10 h10 v10 h-10 z M10,30 h10 v10 h-10 z M50,30 h10 v10 h-10 z M70,30 h10 v10 h-10 z M10,50 h10 v10 h-10 z M30,50 h10 v10 h-10 z M70,50 h10 v10 h-10 z M10,70 h10 v10 h-10 z M30,70 h10 v10 h-10 z M50,70 h10 v10 h-10 z M70,70 h10 v10 h-10 z" fill="black"/></svg>'
                        ),
                        border="2px solid #e2e8f0",
                        border_radius="8px",
                        padding="2",
                        background="white",
                    ),
                    rx.text("QR Code", size="1", color="gray", text_align="center"),
                    spacing="1",
                    align="center",
                ),
                spacing="4",
                width="100%",
                align="center",
            ),
            rx.divider(),
            rx.hstack(
                rx.badge(
                    rx.icon("check", size=14),
                    "Active",
                    color_scheme="green",
                    size="2",
                ),
                rx.spacer(),
                rx.button(
                    rx.icon("trash-2", size=16),
                    "Cancel Order",
                    on_click=AppState.cancel_user_order(order["id"]),
                    loading=AppState.cancelling_order_id == order["id"],
                    color_scheme="red",
                    variant="soft",
                    size="2",
                ),
                width="100%",
                align="center",
            ),
            spacing="3",
            width="100%",
        ),
        width="100%",
    )


def my_tickets_section() -> rx.Component:
    """Display user's purchased tickets."""
    return rx.vstack(
        rx.hstack(
            rx.icon("ticket", size=32, color="pink"),
            rx.heading("My Tickets", size="8"),
            rx.spacer(),
            rx.button(
                rx.hstack(
                    rx.icon("refresh-cw", size=16),
                    rx.text("Refresh"),
                    spacing="2",
                ),
                on_click=AppState.load_user_orders,
                variant="soft",
                size="2",
            ),
            spacing="3",
            align="center",
            width="100%",
        ),
        rx.cond(
            AppState.user_orders_loading,
            rx.center(
                rx.spinner(size="3"),
                padding="8",
            ),
            rx.cond(
                AppState.user_orders.length() == 0,
                rx.card(
                    rx.vstack(
                        rx.icon("ticket", size=48, color="gray"),
                        rx.heading("No Tickets Yet", size="6", color="gray"),
                        rx.text(
                            "Purchase tickets from the Events page to see them here!",
                            size="3",
                            color="gray",
                            text_align="center",
                        ),
                        spacing="3",
                        align="center",
                        padding="8",
                    ),
                    width="100%",
                ),
                rx.vstack(
                    rx.foreach(
                        AppState.user_orders,
                        ticket_card,
                    ),
                    spacing="4",
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
        on_mount=AppState.load_user_orders,
    )
