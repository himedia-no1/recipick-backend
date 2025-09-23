package com.no1.recipick.user.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class UserApiApplicationTests {

    @Test
    void contextLoads() {
        // This test will pass if the application context loads successfully
    }

}