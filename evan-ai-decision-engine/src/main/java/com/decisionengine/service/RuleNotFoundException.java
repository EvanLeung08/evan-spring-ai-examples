package com.decisionengine.service;

public class RuleNotFoundException extends RuntimeException {
    public RuleNotFoundException(String msg) { super(msg); }
}