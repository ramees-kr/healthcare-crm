package com.medicare.healthcarecrm.validation;

import jakarta.validation.groups.Default;

/**
 * Validation group marker for create operations.
 * Extends Default to ensure default validations also run if specified.
 */
public interface OnCreate extends Default {
}