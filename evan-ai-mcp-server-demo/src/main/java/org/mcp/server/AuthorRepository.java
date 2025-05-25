package org.mcp.server;

import org.springframework.ai.tool.annotation.Tool;

import java.util.List;

class AuthorRepository {

    @Tool(description = "Retrieve author details by article title")
    Author getAuthorByArticleTitle(String articleTitle) {
        return new Author("Sample Author", "author@example.com");
    }

    @Tool(description = "Get a list of top sample authors")
    List<Author> getTopAuthors() {
        return List.of(
                new Author("Sample Author", "author@example.com"),
                new Author("Another Author", "another@example.com")
        );
    }

    record Author(String name, String email) {
    }

}