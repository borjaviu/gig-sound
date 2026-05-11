"""Event management components."""

import reflex as rx

from frontend.state import AppState


def purchase_dialog_for_events() -> rx.Component:
    """Purchase dialog that opens from event cards."""
    return rx.alert_dialog.root(
        rx.alert_dialog.content(
            rx.alert_dialog.title(
                rx.hstack(
                    rx.icon("shopping-cart", size=24, color="purple"),
                    rx.text("Purchase Tickets"),
                    spacing="2",
                    align="center",
                ),
            ),
            rx.alert_dialog.description(
                rx.vstack(
                    rx.vstack(
                        rx.hstack(
                            rx.icon("calendar", size=18, color="green"),
                            rx.text("Event: ", weight="bold", size="3"),
                            rx.cond(
                                AppState.selected_event_id,
                                rx.foreach(
                                    AppState.events,
                                    lambda event: rx.cond(
                                        event["id"] == AppState.selected_event_id,
                                        rx.text(f"{event['name']}", size="3"),
                                    ),
                                ),
                            ),
                            spacing="2",
                            width="100%",
                        ),
                        rx.hstack(
                            rx.icon("user", size=18, color="blue"),
                            rx.text("User: ", weight="bold", size="3"),
                            rx.text(AppState.logged_in_user_name, size="3"),
                            spacing="2",
                            width="100%",
                        ),
                        spacing="2",
                        width="100%",
                    ),
                    rx.divider(),
                    rx.vstack(
                        rx.hstack(
                            rx.icon("ticket", size=16, color="gray"),
                            rx.text("Number of Tickets:", weight="bold", size="3"),
                            spacing="2",
                        ),
                        rx.input(
                            placeholder="Quantity",
                            type="number",
                            value=AppState.order_quantity,
                            on_change=AppState.set_order_quantity,
                            width="100%",
                            min="1",
                            default_value="1",
                        ),
                        spacing="2",
                        width="100%",
                    ),
                    rx.cond(
                        AppState.selected_event_id,
                        rx.foreach(
                            AppState.events,
                            lambda event: rx.cond(
                                event["id"] == AppState.selected_event_id,
                                rx.vstack(
                                    rx.divider(),
                                    rx.hstack(
                                        rx.text("Price per ticket:", size="2", color="gray"),
                                        rx.spacer(),
                                        rx.text(f"${event['ticket_price']}", size="3", weight="bold", color="green"),
                                        width="100%",
                                        align="center",
                                    ),
                                    rx.hstack(
                                        rx.text("Total:", size="3", weight="bold"),
                                        rx.spacer(),
                                        rx.text(
                                            "Calculated on purchase",
                                            size="3",
                                            color="green",
                                            weight="bold",
                                        ),
                                        width="100%",
                                        align="center",
                                    ),
                                    spacing="2",
                                    width="100%",
                                ),
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
                    rx.cond(
                        AppState.payment_processing,
                        rx.vstack(
                            rx.spinner(size="3"),
                            rx.text("Processing payment via PayPal...", size="2", color="gray"),
                            spacing="2",
                            align="center",
                            padding="4",
                        ),
                    ),
                    spacing="3",
                    width="100%",
                ),
            ),
            rx.hstack(
                rx.alert_dialog.cancel(
                    rx.button(
                        rx.icon("x", size=16),
                        "Cancel",
                        variant="soft",
                        color_scheme="gray",
                        disabled=AppState.payment_processing,
                    ),
                ),
                rx.alert_dialog.action(
                    rx.button(
                        rx.icon("credit-card", size=16),
                        "Pay with PayPal",
                        on_click=AppState.process_payment,
                        loading=AppState.payment_processing,
                        color_scheme="purple",
                        disabled=AppState.payment_processing,
                    ),
                ),
                spacing="3",
                justify="end",
                width="100%",
            ),
        ),
        open=AppState.show_payment_dialog,
        on_open_change=AppState.cancel_payment,
    )


def event_card_carousel(event: dict) -> rx.Component:
    """Single event card for carousel display."""
    return rx.card(
        rx.vstack(
            rx.hstack(
                rx.icon("calendar", size=24, color="green"),
                rx.heading(event["name"], size="6"),
                spacing="2",
                align="center",
            ),
            rx.divider(),
            rx.vstack(
                rx.hstack(
                    rx.icon("map-pin", size=16, color="gray"),
                    rx.text(event["venue"], size="3", weight="bold"),
                    spacing="1",
                ),
                rx.hstack(
                    rx.icon("calendar-days", size=16, color="gray"),
                    rx.text(event["date"], size="2", color="gray"),
                    spacing="1",
                ),
                rx.divider(),
                rx.hstack(
                    rx.vstack(
                        rx.text("Available", size="1", color="gray"),
                        rx.heading(
                            f"{event['available_tickets']}/{event['total_capacity']}",
                            size="5",
                            color="blue",
                        ),
                        spacing="0",
                        align="center",
                    ),
                    rx.divider(orientation="vertical"),
                    rx.vstack(
                        rx.text("Price", size="1", color="gray"),
                        rx.heading(f"${event['ticket_price']}", size="5", color="green"),
                        spacing="0",
                        align="center",
                    ),
                    spacing="3",
                    width="100%",
                    justify="center",
                ),
                rx.button(
                    rx.hstack(
                        rx.icon("shopping-cart", size=16),
                        rx.text("Buy Tickets"),
                        spacing="2",
                    ),
                    on_click=AppState.open_purchase_dialog(event["id"]),
                    color_scheme="purple",
                    variant="solid",
                    width="100%",
                    size="3",
                ),
                spacing="3",
                align="start",
                width="100%",
            ),
            spacing="3",
            width="100%",
        ),
        width="100%",
        max_width="350px",
    )


def events_carousel_view() -> rx.Component:
    """Carousel view of all events."""
    return rx.vstack(
        rx.hstack(
            rx.icon("calendar", size=32, color="green"),
            rx.heading("Upcoming Events", size="8"),
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
            align="center",
            width="100%",
        ),
        rx.cond(
            AppState.event_loading,
            rx.center(
                rx.spinner(size="3"),
                padding="8",
            ),
            rx.cond(
                AppState.events.length() == 0,
                rx.callout(
                    "No events available yet. Check back soon or contact admin to create events!",
                    icon="calendar-x",
                    color_scheme="gray",
                    width="100%",
                ),
                rx.vstack(
                    rx.grid(
                        rx.foreach(
                            AppState.events,
                            event_card_carousel,
                        ),
                        columns="3",
                        spacing="4",
                        width="100%",
                    ),
                    width="100%",
                ),
            ),
        ),
        purchase_dialog_for_events(),
        spacing="4",
        width="100%",
        on_mount=AppState.load_events,
    )


def event_header() -> rx.Component:
    """Header showing selected event with clear option."""
    return rx.cond(
        AppState.selected_event_id,
        rx.card(
            rx.hstack(
                rx.icon("calendar-check", size=20, color="green"),
                rx.text("Selected Event: ", weight="bold", size="3"),
                rx.foreach(
                    AppState.events,
                    lambda event: rx.cond(
                        event["id"] == AppState.selected_event_id,
                        rx.text(f"{event['name']} - {event['venue']}", size="3"),
                    ),
                ),
                rx.spacer(),
                rx.button(
                    rx.icon("x", size=16),
                    "Clear Selection",
                    on_click=AppState.clear_selected_event,
                    size="2",
                    variant="soft",
                    color_scheme="gray",
                ),
                spacing="2",
                align="center",
                width="100%",
            ),
            width="100%",
        ),
    )


def event_creation_form() -> rx.Component:
    """Form for creating a new event."""
    return rx.card(
        rx.heading("Create Event", size="6", margin_bottom="4"),
        rx.vstack(
            rx.input(
                placeholder="Event Name",
                value=AppState.event_name,
                on_change=AppState.set_event_name,
                width="100%",
            ),
            rx.input(
                placeholder="Date (YYYY-MM-DDTHH:MM)",
                type="datetime-local",
                value=AppState.event_date,
                on_change=AppState.set_event_date,
                width="100%",
            ),
            rx.input(
                placeholder="Venue",
                value=AppState.event_venue,
                on_change=AppState.set_event_venue,
                width="100%",
            ),
            rx.hstack(
                rx.input(
                    placeholder="Total Capacity",
                    type="number",
                    value=AppState.event_capacity,
                    on_change=AppState.set_event_capacity,
                    width="50%",
                ),
                rx.input(
                    placeholder="Ticket Price",
                    type="number",
                    value=AppState.event_price,
                    on_change=AppState.set_event_price,
                    width="50%",
                ),
                width="100%",
                spacing="2",
            ),
            rx.cond(
                AppState.event_error,
                rx.text(AppState.event_error, color="red", size="2"),
            ),
            rx.button(
                "Create Event",
                on_click=AppState.create_event,
                loading=AppState.event_loading,
                width="100%",
                color_scheme="green",
            ),
            spacing="3",
            width="100%",
        ),
        width="100%",
    )


def event_list() -> rx.Component:
    """List of all events."""
    return rx.card(
        rx.heading("Events", size="6", margin_bottom="4"),
        rx.cond(
            AppState.event_loading,
            rx.text("Loading...", color="gray"),
            rx.cond(
                AppState.events.length() == 0,
                rx.text("No events found. Create one above.", color="gray"),
                rx.vstack(
                    rx.foreach(
                        AppState.events,
                        lambda event: rx.card(
                            rx.hstack(
                                rx.vstack(
                                    rx.text(event["name"], weight="bold", size="4"),
                                    rx.text(f"Venue: {event['venue']}", size="2", color="gray"),
                                    rx.hstack(
                                        rx.text("Date: ", size="2", color="gray"),
                                        rx.text(event["date"], size="2", color="gray"),
                                        spacing="1",
                                    ),
                                    rx.text(
                                        f"Available: {event['available_tickets']}/{event['total_capacity']}",
                                        size="2",
                                        color="blue",
                                    ),
                                    rx.text(
                                        f"Price: ${event['ticket_price']}",
                                        size="2",
                                        color="green",
                                    ),
                                    spacing="1",
                                    align="start",
                                ),
                                rx.hstack(
                                    rx.checkbox(
                                        checked=AppState.selected_event_id == event["id"],
                                        on_click=AppState.set_selected_event_id(event["id"]),
                                    ),
                                    rx.button(
                                        rx.icon("trash-2", size=16),
                                        on_click=AppState.delete_event_handler(event["id"]),
                                        color_scheme="red",
                                        variant="soft",
                                        size="2",
                                    ),
                                    spacing="2",
                                ),
                                spacing="4",
                                justify="between",
                                width="100%",
                            ),
                            width="100%",
                            padding="3",
                        ),
                    ),
                    spacing="2",
                    width="100%",
                ),
            ),
        ),
        width="100%",
    )


def event_section() -> rx.Component:
    """Complete event management section."""
    return rx.vstack(
        rx.hstack(
            rx.icon("calendar", size=32, color="green"),
            rx.heading("Event Management", size="8"),
            spacing="3",
            align="center",
        ),
        event_header(),
        rx.tabs.root(
            rx.tabs.list(
                rx.tabs.trigger("Create Event", value="create"),
                rx.tabs.trigger("Event List", value="list"),
            ),
            rx.tabs.content(
                event_creation_form(),
                value="create",
            ),
            rx.tabs.content(
                rx.vstack(
                    rx.button(
                        rx.icon("refresh-cw", size=16),
                        "Refresh",
                        on_click=AppState.load_events,
                        variant="soft",
                        size="2",
                    ),
                    event_list(),
                    spacing="4",
                    width="100%",
                ),
                value="list",
            ),
            default_value="create",
            width="100%",
        ),
        spacing="4",
        width="100%",
        on_mount=AppState.load_events,
    )

