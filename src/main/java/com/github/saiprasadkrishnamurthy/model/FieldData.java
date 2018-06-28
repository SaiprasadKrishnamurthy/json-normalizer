package com.github.saiprasadkrishnamurthy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(of = {"actualPath"})
@Data
@AllArgsConstructor
public class FieldData {
    private String canonicalPath;
    private String actualPath;
    private List<String> values = new ArrayList<>();
    private String targetField;
    private boolean toBeDeleted;
    private TargetFieldValueType targetFieldValueType;
}
