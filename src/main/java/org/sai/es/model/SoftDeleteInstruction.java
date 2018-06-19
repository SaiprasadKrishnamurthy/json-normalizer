package org.sai.es.model;

import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class SoftDeleteInstruction {
    private String primaryFieldPath;
    private String secondaryFieldPath;
    private Set<Object> primaryFieldValues = new LinkedHashSet<>();
    private Set<Object> secondaryFieldValues = new LinkedHashSet<>();
}
