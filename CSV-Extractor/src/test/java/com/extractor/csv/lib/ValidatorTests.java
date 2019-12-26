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
        Assertions.assertTrue(m.isPhoneNumberFR("0299999999"));
        Assertions.assertTrue(m.isPhoneNumberFR("0699999999"));
        Assertions.assertTrue(m.isPhoneNumberFR("06 99 99 99 99"));
        Assertions.assertTrue(m.isPhoneNumberFR("06  99  99  99  99"));
        Assertions.assertTrue(m.isPhoneNumberFR("06.99.99.99.99"));
        Assertions.assertTrue(m.isPhoneNumberFR("+33699999999"));
        Assertions.assertFalse(m.isPhoneNumberFR("+3369999999945"));
        Assertions.assertFalse(m.isPhoneNumberFR("029807abcd"));
        Assertions.assertFalse(m.isPhoneNumberFR("abc"));
    }
}