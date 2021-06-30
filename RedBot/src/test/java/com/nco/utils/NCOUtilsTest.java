package com.nco.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NCOUtilsTest {

    @Test
    void getReputationFromFame() {
        assertEquals(0, NCOUtils.getReputationFromFame(9));
        assertEquals(1, NCOUtils.getReputationFromFame(10));
        assertEquals(1, NCOUtils.getReputationFromFame(11));
        assertEquals(2, NCOUtils.getReputationFromFame(25));
        assertEquals(2, NCOUtils.getReputationFromFame(26));
        assertEquals(2, NCOUtils.getReputationFromFame(27));
        assertEquals(10, NCOUtils.getReputationFromFame(335));
    }

    @Test
    void getFameFromReputation() {
        assertEquals(0, NCOUtils.getFameFromReputation(0));
        assertEquals(10, NCOUtils.getFameFromReputation(1));
        assertEquals(25, NCOUtils.getFameFromReputation(2));
        assertEquals(45, NCOUtils.getFameFromReputation(3));
        assertEquals(70, NCOUtils.getFameFromReputation(4));
        assertEquals(100, NCOUtils.getFameFromReputation(5));
        assertEquals(135,  NCOUtils.getFameFromReputation(6));
        assertEquals(175,  NCOUtils.getFameFromReputation(7));
        assertEquals(220,  NCOUtils.getFameFromReputation(8));
        assertEquals(270,  NCOUtils.getFameFromReputation(9));
        assertEquals(335, NCOUtils.getFameFromReputation(10));
        assertEquals(0, NCOUtils.getFameFromReputation(11));
    }


    @Test
    void parseAndFormatRole() {
        assertEquals("Nomad", NCOUtils.parseAndFormatRole("nomads"));
        assertEquals("lawmen", NCOUtils.parseAndFormatRole("lawmen"));
        assertEquals("Lawman", NCOUtils.parseAndFormatRole("excelawmannomad"));
        assertEquals("Rockerboy", NCOUtils.parseAndFormatRole("Rockerboy"));
        assertEquals("sockerboy", NCOUtils.parseAndFormatRole("sockerboy"));
        assertEquals("Netrunner", NCOUtils.parseAndFormatRole("Net-runner"));
        assertEquals("Netrunner", NCOUtils.parseAndFormatRole("Net runner"));
    }
}