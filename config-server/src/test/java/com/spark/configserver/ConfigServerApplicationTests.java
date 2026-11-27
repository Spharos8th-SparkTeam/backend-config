package com.spark.configserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.cloud.config.server.git.uri=https://github.com/Spharos8th-SparkTeam/backend-config.git",
        "spring.cloud.config.server.git.clone-on-start=false",
        "spring.cloud.config.server.git.default-label=develop"
})
class ConfigServerApplicationTests {

    @Test
    void contextLoads() {
    }
}
