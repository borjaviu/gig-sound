"""User management components."""

import reflex as rx

from frontend.state import AppState


def user_header() -> rx.Component:
    """Header showing selected user with clear option."""
    return rx.cond(
        AppState.selected_user_id,
        rx.card(
            rx.hstack(
                rx.icon("user-check", size=20, color="green"),
                rx.text("Selected User: ", weight="bold", size="3"),
                rx.foreach(
                    AppState.users,
                    lambda user: rx.cond(
                        user["id"] == AppState.selected_user_id,
                        rx.text(f"{user['name']} ({user['email']})", size="3"),
                    ),
                ),
                rx.spacer(),
                rx.button(
                    rx.icon("x", size=16),
                    "Clear Selection",
                    on_click=AppState.clear_selected_user,
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


def user_creation_form() -> rx.Component:
    """Form for creating a new user."""
    return rx.card(
        rx.vstack(
            rx.hstack(
                rx.icon("user", size=20),
                rx.input(
                    placeholder="Name",
                    value=AppState.user_name,
                    on_change=AppState.set_user_name,
                    width="100%",
                ),
                spacing="2",
                width="100%",
            ),
            rx.hstack(
                rx.icon("mail", size=20),
                rx.input(
                    placeholder="Email",
                    type="email",
                    value=AppState.user_email,
                    on_change=AppState.set_user_email,
                    width="100%",
                ),
                spacing="2",
                width="100%",
            ),
            rx.cond(
                AppState.user_error,
                rx.callout(
                    AppState.user_error,
                    icon="triangle-alert",
                    color_scheme="red",
                    width="100%",
                ),
            ),
            rx.button(
                rx.icon("user-plus", size=16),
                "Create User",
                on_click=AppState.create_user,
                loading=AppState.user_loading,
                width="100%",
                color_scheme="blue",
            ),
            spacing="3",
            width="100%",
        ),
        width="100%",
    )


def user_list() -> rx.Component:
    """List of all users."""
    return rx.vstack(
        rx.cond(
            AppState.user_loading,
            rx.center(
                rx.spinner(size="3"),
                padding="4",
            ),
            rx.cond(
                AppState.users.length() == 0,
                rx.callout(
                    "No users found. Create one in the 'Create User' tab.",
                    icon="info",
                    color_scheme="gray",
                ),
                rx.vstack(
                    rx.foreach(
                        AppState.users,
                        lambda user: rx.card(
                            rx.hstack(
                                rx.icon(
                                    "user",
                                    size=24,
                                    color=rx.cond(
                                        AppState.selected_user_id == user["id"],
                                        "green",
                                        "gray",
                                    ),
                                ),
                                rx.vstack(
                                    rx.hstack(
                                        rx.text(user["name"], weight="bold", size="4"),
                                        rx.cond(
                                            AppState.selected_user_id == user["id"],
                                            rx.badge("Selected", color_scheme="green"),
                                        ),
                                        spacing="2",
                                        align="center",
                                    ),
                                    rx.hstack(
                                        rx.icon("mail", size=14, color="gray"),
                                        rx.text(user["email"], size="2", color="gray"),
                                        spacing="1",
                                    ),
                                    spacing="1",
                                    align="start",
                                ),
                                rx.spacer(),
                                rx.checkbox(
                                    checked=AppState.selected_user_id == user["id"],
                                    on_click=AppState.set_selected_user_id(user["id"]),
                                    size="3",
                                ),
                                spacing="3",
                                align="center",
                                width="100%",
                            ),
                            width="100%",
                            variant=rx.cond(
                                AppState.selected_user_id == user["id"],
                                "surface",
                                "classic",
                            ),
                        ),
                    ),
                    spacing="2",
                    width="100%",
                ),
            ),
        ),
        width="100%",
    )


def user_section() -> rx.Component:
    """Complete user management section."""
    return rx.vstack(
        rx.hstack(
            rx.icon("users", size=32, color="blue"),
            rx.heading("User Management", size="8"),
            spacing="3",
            align="center",
        ),
        user_header(),
        rx.tabs.root(
            rx.tabs.list(
                rx.tabs.trigger("Create User", value="create"),
                rx.tabs.trigger("User List", value="list"),
            ),
            rx.tabs.content(
                user_creation_form(),
                value="create",
            ),
            rx.tabs.content(
                rx.vstack(
                    rx.hstack(
                        rx.button(
                            rx.icon("refresh-cw", size=16),
                            "Refresh",
                            on_click=AppState.load_users,
                            variant="soft",
                            size="2",
                        ),
                        spacing="2",
                    ),
                    user_list(),
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
        on_mount=AppState.load_users,
    )
