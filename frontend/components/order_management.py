"""Order management components."""

import reflex as rx

from frontend.state import AppState


def purchase_order_form() -> rx.Component:
    """Form for purchasing tickets."""
    return rx.card(
        rx.vstack(
            rx.hstack(
                rx.icon("user", size=18, color="blue"),
                rx.text("Selected User: ", weight="bold", size="3"),
                rx.cond(
                    AppState.selected_user_id,
                    rx.foreach(
                        AppState.users,
                        lambda user: rx.cond(
                            user["id"] == AppState.selected_user_id,
                            rx.text(f"{user['name']} ({user['email']})", weight="bold", size="3"),
                        ),
                    ),
                    rx.text("None selected", color="gray", size="3"),
                ),
                spacing="2",
                align="center",
                width="100%",
            ),
            rx.hstack(
                rx.icon("calendar", size=18, color="green"),
                rx.text("Selected Event: ", weight="bold", size="3"),
                rx.cond(
                    AppState.selected_event_id,
                    rx.foreach(
                        AppState.events,
                        lambda event: rx.cond(
                            event["id"] == AppState.selected_event_id,
                            rx.text(f"{event['name']}", weight="bold", size="3"),
                        ),
                    ),
                    rx.text("None selected", color="gray", size="3"),
                ),
                spacing="2",
                align="center",
                width="100%",
            ),
            rx.divider(),
            rx.hstack(
                rx.icon("ticket", size=18, color="purple"),
                rx.input(
                    placeholder="Quantity",
                    type="number",
                    value=AppState.order_quantity,
                    on_change=AppState.set_order_quantity,
                    width="100%",
                ),
                spacing="2",
                width="100%",
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
                AppState.order_success,
                rx.callout(
                    AppState.order_success,
                    icon="check",
                    color_scheme="green",
                    width="100%",
                ),
            ),
            rx.cond(
                AppState.selected_user_id & AppState.selected_event_id,
                rx.button(
                    rx.icon("shopping-cart", size=16),
                    "Purchase Tickets",
                    on_click=AppState.purchase_order,
                    loading=AppState.order_loading,
                    width="100%",
                    color_scheme="purple",
                    size="3",
                ),
                rx.button(
                    rx.icon("lock", size=16),
                    "Purchase Tickets",
                    disabled=True,
                    width="100%",
                    color_scheme="gray",
                    size="3",
                ),
            ),
            spacing="3",
            width="100%",
        ),
        width="100%",
    )


def payment_dialog() -> rx.Component:
    """Payment dialog for processing ticket purchase."""
    return rx.alert_dialog.root(
        rx.alert_dialog.content(
            rx.alert_dialog.title(
                rx.hstack(
                    rx.icon("credit-card", size=24, color="purple"),
                    rx.text("Complete Payment"),
                    spacing="2",
                    align="center",
                ),
            ),
            rx.alert_dialog.description(
                rx.vstack(
                    rx.callout(
                        "You will be redirected to PayPal to complete your payment.",
                        icon="info",
                        color_scheme="blue",
                        width="100%",
                    ),
                    rx.divider(),
                    rx.vstack(
                        rx.hstack(
                            rx.text("Total Amount:", weight="bold", size="4"),
                            rx.spacer(),
                            rx.heading(
                                f"${AppState.payment_amount:.2f}",
                                size="6",
                                color="green",
                            ),
                            width="100%",
                            align="center",
                        ),
                        rx.hstack(
                            rx.text("Quantity:", size="3", color="gray"),
                            rx.spacer(),
                            rx.text(f"{AppState.order_quantity} tickets", size="3"),
                            width="100%",
                        ),
                        spacing="2",
                        width="100%",
                        padding="4",
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
                        color_scheme="blue",
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


def order_section() -> rx.Component:
    """Complete order management section."""
    return rx.vstack(
        rx.hstack(
            rx.icon("shopping-cart", size=32, color="purple"),
            rx.heading("Purchase Tickets", size="8"),
            spacing="3",
            align="center",
        ),
        rx.callout(
            "Instructions: 1. Go to User Management to create/select a user. "
            "2. Go to Event Management to create/select an event. "
            "3. Return here to purchase tickets",
            icon="info",
            color_scheme="blue",
            width="100%",
        ),
        purchase_order_form(),
        payment_dialog(),
        spacing="4",
        width="100%",
    )
