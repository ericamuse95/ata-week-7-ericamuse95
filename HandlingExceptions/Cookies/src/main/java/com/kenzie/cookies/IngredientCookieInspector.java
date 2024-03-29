package com.kenzie.cookies;

import com.kenzie.cookies.cookie.ChocolateChipCookie;
import com.kenzie.cookies.cookie.CookieIngredient;
import com.kenzie.cookies.exception.AllergenContaminantException;

/**
 * Takes an extraction from a cookie and can molecularly evaluate it to determine which ingredients are present in the
 * cookie.
 */
public class IngredientCookieInspector implements CookieInspector {

    /**
     * Inspects and validates a cookie to ensure that it can be sold by Whole Foods Market.
     * @param cookie The cookie to validate
     * @throws AllergenContaminantException Thrown if a cookie contains an allergen like peanuts.
     */
    @Override
    public void inspect(ChocolateChipCookie cookie) throws AllergenContaminantException {
        if (cookie.getIngredients().contains(CookieIngredient.PEANUT)) {
            throw new AllergenContaminantException();
        }
    }
}