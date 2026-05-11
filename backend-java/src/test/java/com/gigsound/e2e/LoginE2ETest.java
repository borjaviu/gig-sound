package com.gigsound.e2e;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginE2ETest extends AbstractE2ETest {

    @Test
    @DisplayName("User can log in by clicking their account on the login screen")
    void login_success() {
        loginAsFirstUser();

        assertTrue(pageContainsText("Events"), "Navbar should appear after login");
        assertTrue(pageContainsText("My Tickets"), "Navbar should appear after login");
    }
}
