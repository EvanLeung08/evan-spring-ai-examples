package org.mcp.client;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Excluding the below auto-configurations to avoid start up
 * failure. Their corresponding starters are present on the classpath but are
 * only needed by other articles in the shared codebase.
 */
@SpringBootApplication
class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

}