package com.nco.enums;

public enum Stats {
    INTELLIGENCE,
    REFLEXES,
    DEXTERITY,
    TECHNIQUE,
    COOL,
    WILLPOWER,
    LUCK,
    USABLE_LUCK,
    MOVEMENT,
    BODY,
    CURRENT_EMPATHY,
    MAX_EMPATHY,
    CURRENT_HP,
    MAX_HP,
    CURRENT_HUMANITY,
    MAX_HUMANITY;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
