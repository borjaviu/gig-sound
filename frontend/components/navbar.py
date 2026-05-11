"""Navigation bar component."""

import reflex as rx

from frontend.state import AppState


def navbar() -> rx.Component:
    """Navigation bar with theme toggle."""
    return rx.box(
        rx.card(
            rx.hstack(
                rx.hstack(
                    rx.icon("ticket", size=32, color="purple"),
                    rx.heading("GiG Sound", size="7"),
                    spacing="3",
                    align="center",
                ),
                rx.spacer(),
                rx.hstack(
                    rx.button(
                        rx.icon("calendar", size=18),
                        "Events",
                        on_click=lambda: AppState.set_current_page("events"),
                        variant=rx.cond(
                            AppState.current_page == "events",
                            "solid",
                            "soft",
                        ),
                        color_scheme="green",
                        size="2",
                    ),
                    rx.button(
                        rx.icon("ticket", size=18),
                        "My Tickets",
                        on_click=lambda: AppState.set_current_page("tickets"),
                        variant=rx.cond(
                            AppState.current_page == "tickets",
                            "solid",
                            "soft",
                        ),
                        color_scheme="pink",
                        size="2",
                    ),
                    rx.divider(orientation="vertical", height="30px"),
                    rx.button(
                        rx.icon("shield", size=18),
                        "Admin",
                        on_click=lambda: AppState.set_current_page("admin"),
                        variant=rx.cond(
                            AppState.current_page == "admin",
                            "solid",
                            "soft",
                        ),
                        color_scheme="red",
                        size="2",
                    ),
                    spacing="2",
                ),
                rx.hstack(
                    rx.vstack(
                        rx.text(f"👤 {AppState.logged_in_user_name}", size="2", weight="bold"),
                        spacing="0",
                        align="end",
                    ),
                    rx.color_mode.button(),
                    rx.button(
                        rx.icon("log-out", size=18),
                        "Logout",
                        on_click=AppState.logout,
                        variant="soft",
                        color_scheme="red",
                        size="2",
                        aria_label="Logout",
                    ),
                    spacing="2",
                    align="center",
                ),
                spacing="4",
                align="center",
                width="100%",
                justify="between",
            ),
            width="100%",
            padding="4",
        ),
        width="100%",
        max_width="1200px",
        margin="0 auto",
    )
