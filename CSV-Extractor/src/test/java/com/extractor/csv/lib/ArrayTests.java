package com.extractor.csv.lib;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ArrayTests {
    private Array m;

    @BeforeEach
    void init() {
        m = new Array();
    }

    @Test
    public void testContainsItemToCheckIsString() {
        String[] items = {"a", "b", "c", "d"};
        Assertions.assertTrue(m.Contains(items, "a"));
        Assertions.assertTrue(m.Contains(items, "d"));
        Assertions.assertFalse(m.Contains(items, "y"));
        Assertions.assertFalse(m.Contains(items, "z"));
    }

    @Test
    public void testContainsItemToCheckIsStringArray() {
        String[] items = {"a", "b", "c", "d"};
        Assertions.assertTrue(m.Contains(items, new String[]{"a"}));
        Assertions.assertTrue(m.Contains(items, new String[]{"a", "d"}));
        Assertions.assertFalse(m.Contains(items, new String[]{"z", "y"}));
        Assertions.assertTrue(m.Contains(items, new String[]{"d", "a"}));
        Assertions.assertFalse(m.Contains(items, new String[]{"z"}));
        Assertions.assertFalse(m.Contains(items, new String[]{""}));
    }
}