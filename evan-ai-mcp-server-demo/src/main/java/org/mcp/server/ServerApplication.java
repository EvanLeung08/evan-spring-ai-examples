package org.mcp.server;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Excluding the below auto-configurations to avoid start up
 * failure. Their corresponding starters are present on the classpath but are
 * only needed by other articles in the shared codebase.
 */
@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}