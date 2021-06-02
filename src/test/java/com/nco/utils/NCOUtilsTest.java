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


}