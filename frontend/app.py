"""Main Reflex application."""

import reflex as rx

from frontend.components.admin_section import admin_section
from frontend.components.event_management import events_carousel_view
from frontend.components.login import login_page
from frontend.components.my_tickets import my_tickets_section
from frontend.components.navbar import navbar
from frontend.state import AppState


def index() -> rx.Component:
    """Main page with navigation."""
    return rx.cond(
        AppState.is_logged_in,
        # Main app (logged in)
        rx.vstack(
            navbar(),
            rx.center(
                rx.container(
                    rx.vstack(
                        rx.match(
                            AppState.current_page,
                            ("events", events_carousel_view()),
                            ("tickets", my_tickets_section()),
                            ("admin", admin_section()),
                            events_carousel_view(),  # default
                        ),
                        spacing="6",
                        width="100%",
                    ),
                    max_width="1200px",
                    padding="6",
                ),
                width="100%",
            ),
            spacing="4",
            width="100%",
        ),
        # Login page (not logged in)
        login_page(),
    )


# Create the app
app = rx.App(
    style={
        "font_family": "system-ui, -apple-system, sans-serif",
    },
)

app.add_page(index, route="/", title="GiG Sound Tickets")

