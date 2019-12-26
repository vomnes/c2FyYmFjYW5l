package com.extractor.csv.lib;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ValidatorTests {
    private Validator m;

    @BeforeEach
    void init() {
        m = new Validator();
    }

    @Test
    public void testIsEmail() {
        Assertions.assertTrue(m.isEmail("test@test.com"));
        Assertions.assertFalse(m.isEmail("test@test"));
        Assertions.assertFalse(m.isEmail("testest.com"));
        Assertions.assertFalse(m.isEmail("testest"));
    }

    @Test
    public void testIsPhoneNumber() {
        Assertions.assertTrue(m.isPhoneNumber("02989999999"));
        Assertions.assertFalse(m.isPhoneNumber("029807abcd"));
        Assertions.assertFalse(m.isPhoneNumber("abc"));
    }
}