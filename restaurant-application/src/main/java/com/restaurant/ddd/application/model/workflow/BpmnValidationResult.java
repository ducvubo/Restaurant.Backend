package com.restaurant.ddd.application.model.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Result của BPMN Validation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BpmnValidationResult {
    private boolean valid;
    private List<String> errors;

    public BpmnValidationResult(boolean valid) {
        this.valid = valid;
        this.errors = new ArrayList<>();
    }

    public void addError(String error) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(error);
        this.valid = false;
    }

    public void addErrors(List<String> errorList) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        if (errorList != null) {
            this.errors.addAll(errorList);
            this.valid = false;
        }
    }
}
