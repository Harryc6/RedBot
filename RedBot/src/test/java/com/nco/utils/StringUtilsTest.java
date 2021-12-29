package com.nco.utils;

import com.nco.enums.Skills;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void camelToSnakeCase() {
        assertEquals("hello_world", StringUtils.camelToSnakeCase("helloWorld"));
        assertEquals("hello_world", StringUtils.camelToSnakeCase("HelloWorld"));
        assertEquals("hello_world", StringUtils.camelToSnakeCase(" HelloWorld"));
    }

    @Test
    void capitalSnakeToCamelCase() {
        assertEquals("HelloWorld", StringUtils.capitalSnakeToCamelCase("Hello_world"));
        assertEquals("HelloWorld", StringUtils.capitalSnakeToCamelCase("hello_world"));
        assertEquals("AVTech", StringUtils.capitalSnakeToCamelCase(Skills.A_V_TECH.toString()));
    }

    @Test
    void snakeToCamelCase() {
        assertEquals("helloWorld", StringUtils.snakeToCamelCase("Hello_world"));
        assertEquals("helloWorld", StringUtils.snakeToCamelCase("hello_world"));
    }

    @Test
    void camelToFormal() {
        assertEquals("Hello World", StringUtils.camelToFormal("helloWorld"));
        assertEquals("Hello World", StringUtils.camelToFormal("HelloWorld"));
        assertEquals("Hello World", StringUtils.camelToFormal(" HelloWorld"));
        assertEquals("Hello world", StringUtils.camelToFormal("Hello world"));
    }

    @Test
    void capitalizeWords() {
        assertEquals("Hello World", StringUtils.capitalizeWords("hello world"));
        assertEquals("Helloworld", StringUtils.capitalizeWords("helloWorld"));
        assertEquals("Helloworld", StringUtils.capitalizeWords("HelloWorld"));
    }

    @Test
    void formalToCapitalizedCamelCase() {
        Object[] expected = Arrays.stream(Skills.values()).map(skill -> StringUtils.snakeToCamelCase(skill.toString())).toArray();
        Object[] actual = Arrays.stream(Skills.values()).map(skill -> StringUtils.formalToCapitalizedCamelCase(StringUtils.snakeToCapitalizedWords(skill.toString()))).toArray();
        assertArrayEquals(expected, actual);
    }
}