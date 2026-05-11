"""Login page component."""

import reflex as rx

from frontend.state import AppState


def login_page() -> rx.Component:
    """Login page where users select their account."""
    return rx.center(
        rx.card(
            rx.vstack(
                rx.vstack(
                    rx.icon("ticket", size=48, color="purple"),
                    rx.heading("GiG Sound", size="9"),
                    rx.text("Select your account to continue", size="3", color="gray"),
                    spacing="2",
                    align="center",
                ),
                rx.divider(),
                rx.cond(
                    AppState.user_loading,
                    rx.center(
                        rx.vstack(
                            rx.spinner(size="3"),
                            rx.text("Loading users...", size="2", color="gray"),
                            spacing="3",
                            align="center",
                        ),
                        padding="8",
                    ),
                    rx.cond(
                        AppState.users.length() == 0,
                        rx.vstack(
                            rx.callout(
                                "No users found. Please contact an administrator to create an account.",
                                icon="user-x",
                                color_scheme="red",
                                width="100%",
                            ),
                            rx.button(
                                rx.hstack(
                                    rx.icon("refresh-cw", size=16),
                                    rx.text("Retry"),
                                    spacing="2",
                                ),
                                on_click=AppState.load_users,
                                width="100%",
                                variant="soft",
                            ),
                            spacing="3",
                            width="100%",
                        ),
                        rx.vstack(
                            rx.text("Select User:", weight="bold", size="3"),
                            rx.vstack(
                                rx.foreach(
                                    AppState.users,
                                    lambda user: rx.button(
                                        rx.hstack(
                                            rx.icon("user", size=20),
                                            rx.vstack(
                                                rx.text(user["name"], size="4", weight="bold"),
                                                rx.text(user["email"], size="2", color="gray"),
                                                spacing="0",
                                                align="start",
                                            ),
                                            spacing="3",
                                            width="100%",
                                        ),
                                        on_click=AppState.login_as_user(user["id"]),
                                        width="100%",
                                        variant="outline",
                                        size="3",
                                        color_scheme="blue",
                                    ),
                                ),
                                spacing="2",
                                width="100%",
                            ),
                            spacing="3",
                            width="100%",
                        ),
                    ),
                ),
                spacing="5",
                width="100%",
            ),
            max_width="500px",
            width="100%",
            padding="8",
        ),
        width="100%",
        height="100vh",
        on_mount=AppState.load_users,
    )
