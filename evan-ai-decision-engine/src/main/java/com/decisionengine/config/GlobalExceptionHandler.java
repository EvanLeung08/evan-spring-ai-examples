package com.decisionengine.config;

import com.decisionengine.service.RuleNotFoundException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuleNotFoundException.class)
    public ResponseEntity<String> handleRuleNotFound(RuleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}