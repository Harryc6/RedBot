package com.nco.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RPGDiceTest {

    @Test
    void parse() {
        assertEquals("{\"rolls\": 1, \"faces\": 6, \"multiplier\": 1, \"additive\": 0}", RPGDice.parse("d6").toString());
        assertNull(RPGDice.parse("d6*"));
        assertEquals("{\"rolls\": 33, \"faces\": 6, \"multiplier\": 10, \"additive\": 0}", RPGDice.parse("33d6*10").toString());
        assertNull(RPGDice.parse("336*10"));
        assertNull(RPGDice.parse("d6/"));
        assertEquals("{\"rolls\": 1, \"faces\": 6, \"multiplier\": -5, \"additive\": 0}", RPGDice.parse("d6/5").toString());
        assertEquals("{\"rolls\": 1, \"faces\": 6, \"multiplier\": -5, \"additive\": 2}", RPGDice.parse("d6/5+2").toString());
        assertEquals("{\"rolls\": 2, \"faces\": 6, \"multiplier\": -5, \"additive\": -32}", RPGDice.parse("2d6/5-32").toString());
        assertNull(RPGDice.parse("2d6/5+-32"));
        assertEquals("{\"rolls\": 2, \"faces\": 6, \"multiplier\": -2, \"additive\": 0}", RPGDice.parse("2D6/2").toString());
        assertEquals("{\"rolls\": 2, \"faces\": 6, \"multiplier\": -2, \"additive\": 1}", RPGDice.parse("2D6 / 2 + 1").toString());
    }

    @Test
    void roll() {
        int roll = RPGDice.roll("d6");
        assertTrue(1 <= roll && roll <= 7);
        assertNull(RPGDice.parse("d6*"));
        roll = RPGDice.roll("33d6*10");
        assertTrue(330 <= roll && roll <= 1980);
        assertNull(RPGDice.parse("336*10"));
        assertNull(RPGDice.parse("d6/"));
        roll = RPGDice.roll("d6/5");
        assertTrue(1 <= roll && roll <= 2);
        roll = RPGDice.roll("d6/5+2");
        assertTrue(3 <= roll && roll <= 4);
        roll = RPGDice.roll("2d6/5-32");
        assertTrue(-32 <= roll && roll <= -29);
        assertNull(RPGDice.parse("2d6/5+-32"));
        roll = RPGDice.roll("2D6");
        assertTrue(2 <= roll && roll <= 12);
        roll = RPGDice.roll("2D6 / 2 + 1");
        assertTrue(2 <= roll && roll <= 12);
    }

}