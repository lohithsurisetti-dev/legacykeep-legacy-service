package com.legacykeep.legacy.exception;

import org.springframework.http.HttpStatus;
import java.util.UUID;

/**
 * Exception thrown when an inheritance rule is not found.
 */
public class InheritanceRuleNotFoundException extends LegacyException {
    
    public InheritanceRuleNotFoundException(UUID ruleId) {
        super("INHERITANCE_RULE_NOT_FOUND", "Inheritance rule not found with ID: " + ruleId, HttpStatus.NOT_FOUND);
    }
    
    public InheritanceRuleNotFoundException(String message) {
        super("INHERITANCE_RULE_NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }
}
