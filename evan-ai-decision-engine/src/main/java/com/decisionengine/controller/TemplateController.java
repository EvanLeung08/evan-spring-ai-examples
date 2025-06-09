package com.decisionengine.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api/templates")
public class TemplateController {

    private final ResourceLoader resourceLoader;
    private final ResourcePatternResolver resolver;

    public TemplateController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.resolver = new PathMatchingResourcePatternResolver(resourceLoader);
    }

    @GetMapping("/{toolType}/{category}")
    @ResponseBody
    public List<String> listTemplates(
            @PathVariable String toolType,
            @PathVariable String category) {

        List<String> templateNames = new ArrayList<>();
        try {
            // Format: classpath:templates/{toolType}/{category}/*
            String pattern = String.format("classpath:data/%s/%s/*",
                    toolType.toLowerCase(), category.toLowerCase());

            Resource[] resources = resolver.getResources(pattern);
            for (Resource res : resources) {
                String filename = res.getFilename();
                if (filename != null) {
                    templateNames.add(filename);
                }
            }
            return templateNames;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error reading templates: " + e.getMessage());
        }
    }

    @GetMapping("/{toolType}/{category}/{filename}")
    @ResponseBody
    public String getTemplateContent(
            @PathVariable String toolType,
            @PathVariable String category,
            @PathVariable String filename) {

        try {
            // Format: classpath:templates/{toolType}/{category}/{filename}
            String path = String.format("data/%s/%s/%s",
                    toolType.toLowerCase(), category.toLowerCase(), filename);

            Resource resource = resourceLoader.getResource("classpath:" + path);

            if (!resource.exists()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Template not found: " + path);
            }

            try (Reader reader = new InputStreamReader(
                    resource.getInputStream(), StandardCharsets.UTF_8)) {
                return FileCopyUtils.copyToString(reader);
            }

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error reading template: " + e.getMessage());
        }
    }
}