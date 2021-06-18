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