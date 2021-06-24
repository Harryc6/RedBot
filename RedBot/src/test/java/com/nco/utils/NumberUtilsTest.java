package com.nco.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumberUtilsTest {

    @Test
    void isNumeric() {
        assertTrue(NumberUtils.isNumeric("-13"));
        assertTrue(NumberUtils.isNumeric("+13"));
        assertTrue(NumberUtils.isNumeric("0.13"));
        assertFalse(NumberUtils.isNumeric(null));
        assertFalse(NumberUtils.isNumeric("null"));
        assertFalse(NumberUtils.isNumeric("+null"));
        assertFalse(NumberUtils.isNumeric("+-1"));
        assertFalse(NumberUtils.isNumeric("-+1"));
        assertFalse(NumberUtils.isNumeric("1/3"));
    }

    @Test
    void asPositive() {
        assertEquals(13, NumberUtils.asPositive("-13"));
        assertEquals(13, NumberUtils.asPositive("+13"));
        assertThrows(NumberFormatException.class, () -> NumberUtils.asPositive("0.13"));
        assertThrows(NumberFormatException.class, () -> NumberUtils.asPositive(null));
    }
}