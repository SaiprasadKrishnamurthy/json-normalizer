package com.github.saiprasadkrishnamurthy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

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
