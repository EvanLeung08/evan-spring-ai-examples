package com.decisionengine.service;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.springframework.stereotype.Service;

@Service
public class ScriptToolExecutor {
    public Object executeScript(String script, String request, String config) {
        try {
            Binding binding = new Binding();
            binding.setVariable("request", request);
            binding.setVariable("config", config);
            GroovyShell shell = new GroovyShell(binding);
            Object scriptObj = shell.evaluate(script);
            Object result = scriptObj.getClass().getMethod("call", Object.class, Object.class)
                    .invoke(scriptObj, request, config);
            return result;
        } catch (Exception e) {
            return "Script execution failed: " + e.getMessage();
        }
    }
}