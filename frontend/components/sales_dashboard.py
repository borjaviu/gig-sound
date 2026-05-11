"""Sales dashboard components."""

import reflex as rx

from frontend.state import AppState


def sales_dashboard() -> rx.Component:
    """Sales dashboard for event sales."""
    return rx.vstack(
        rx.hstack(
            rx.icon("chart-bar", size=32, color="blue"),
            rx.heading("Sales Dashboard", size="8"),
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
        rx.card(
            rx.vstack(
                rx.vstack(
                    rx.hstack(
                        rx.icon("calendar", size=20, color="green"),
                        rx.text("Select Event", weight="bold", size="3"),
                        spacing="2",
                    ),
                    rx.cond(
                        AppState.event_loading,
                        rx.hstack(
                            rx.spinner(size="2"),
                            rx.text("Loading events...", size="2", color="gray"),
                            spacing="2",
                        ),
                        rx.cond(
                            AppState.events.length() == 0,
                            rx.callout(
                                "No events available. Create events in the Admin section.",
                                icon="calendar-x",
                                color_scheme="gray",
                                width="100%",
                            ),
                            rx.vstack(
                                rx.foreach(
                                    AppState.events,
                                    lambda event: rx.button(
                                        rx.hstack(
                                            rx.cond(
                                                AppState.selected_event_id == event["id"],
                                                rx.icon("check", size=16, color="green"),
                                                rx.icon("calendar", size=16, color="gray"),
                                            ),
                                            rx.vstack(
                                                rx.text(event["name"], size="3", weight="bold"),
                                                rx.text(
                                                    f"{event['venue']} - {event['date']}",
                                                    size="1",
                                                    color="gray",
                                                ),
                                                spacing="0",
                                                align="start",
                                            ),
                                            spacing="2",
                                            width="100%",
                                            align="center",
                                        ),
                                        on_click=AppState.set_selected_event_id(event["id"]),
                                        variant=rx.cond(
                                            AppState.selected_event_id == event["id"],
                                            "solid",
                                            "outline",
                                        ),
                                        color_scheme=rx.cond(
                                            AppState.selected_event_id == event["id"],
                                            "green",
                                            "gray",
                                        ),
                                        width="100%",
                                        size="3",
                                        padding="4",
                                    ),
                                ),
                                spacing="2",
                                width="100%",
                            ),
                        ),
                    ),
                    spacing="2",
                    width="100%",
                ),
                rx.cond(
                    AppState.selected_event_id,
                    rx.vstack(
                        rx.divider(),
                        rx.button(
                            rx.hstack(
                                rx.icon("bar-chart-2", size=16),
                                rx.text("Load Sales Data"),
                                spacing="2",
                            ),
                            on_click=AppState.load_event_sales,
                            loading=AppState.sales_loading,
                            color_scheme="blue",
                            width="100%",
                            size="3",
                        ),
                        rx.cond(
                            AppState.sales_loading,
                            rx.text("Loading sales data...", color="gray"),
                            rx.cond(
                                AppState.sales_data,
                                rx.vstack(
                                    rx.heading(
                                        AppState.sales_event_name,
                                        size="6",
                                        margin_bottom="4",
                                    ),
                                    rx.grid(
                                        rx.card(
                                            rx.vstack(
                                                rx.hstack(
                                                    rx.icon("ticket", size=20, color="blue"),
                                                    rx.text("Total Sales", size="2", weight="bold", color="gray"),
                                                    spacing="2",
                                                    align="center",
                                                ),
                                                rx.heading(AppState.sales_total_sales, size="8", color="blue"),
                                                rx.text("tickets sold", size="1", color="gray"),
                                                spacing="2",
                                                align="center",
                                            ),
                                            width="100%",
                                        ),
                                        rx.card(
                                            rx.vstack(
                                                rx.hstack(
                                                    rx.icon("dollar-sign", size=20, color="green"),
                                                    rx.text("Total Revenue", size="2", weight="bold", color="gray"),
                                                    spacing="2",
                                                    align="center",
                                                ),
                                                rx.heading(
                                                    f"${AppState.sales_total_revenue:.2f}", size="8", color="green"
                                                ),
                                                rx.text("USD", size="1", color="gray"),
                                                spacing="2",
                                                align="center",
                                            ),
                                            width="100%",
                                        ),
                                        columns="2",
                                        spacing="4",
                                        width="100%",
                                    ),
                                    spacing="4",
                                    width="100%",
                                ),
                            ),
                        ),
                        rx.cond(
                            AppState.sales_error,
                            rx.text(AppState.sales_error, color="red", size="2"),
                        ),
                        spacing="4",
                        width="100%",
                    ),
                ),
                spacing="4",
                width="100%",
            ),
            width="100%",
        ),
        spacing="4",
        width="100%",
        on_mount=AppState.load_events,
    )

