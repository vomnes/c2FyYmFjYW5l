package com.extractor.csv.lib;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ValidatorTests {
    private StringMatcher m;

    @Before
    public void setup(){
        m = new Validator();
    }

    @Test
    public void testIsTrue() {
        assertTrue(m.isEmail("test@test.com"));
        assertFalse(m.isEmail("test@test"));
        assertFalse(m.isEmail("testest.com"));
        assertFalse(m.isEmail("testest"));
    }
}