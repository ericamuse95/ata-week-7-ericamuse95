package com.kenzie.cookies;

import com.kenzie.cookies.cookie.ChocolateChipCookie;
import com.kenzie.cookies.cookie.Size;
import com.kenzie.cookies.exception.CookieTooLargeException;
import com.kenzie.cookies.exception.CookieTooSmallException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class DeepLensCookieInspectorTest {
    private DeepLensCookieInspector inspector;

    @BeforeEach
    public void setup() {
        inspector = new DeepLensCookieInspector();
    }

    @Test
    public void inspect_cookieTooLarge_throwsCookieTooLargeException() throws Exception {
        // GIVEN
        ChocolateChipCookie cookie = new ChocolateChipCookie(Size.LARGE, 10, new ArrayList<>());

        // WHEN + THEN
        assertThrows(CookieTooLargeException.class, () -> inspector.inspect(cookie));
    }

    @Test
    public void inspect_cookieTooSmall_throwsCookieTooSmallException() throws Exception {
        // GIVEN
        ChocolateChipCookie cookie = new ChocolateChipCookie(Size.SMALL, 10, new ArrayList<>());

        // WHEN + THEN
        assertThrows(CookieTooSmallException.class, () -> inspector.inspect(cookie));
    }
}