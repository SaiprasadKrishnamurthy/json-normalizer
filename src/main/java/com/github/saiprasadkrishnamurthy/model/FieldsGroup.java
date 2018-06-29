package com.github.saiprasadkrishnamurthy.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FieldsGroup {
    private String description;
    private List<String> sourceFields = new ArrayList<>();
    private String targetField;
    private TargetFieldValueType targetFieldValueType = TargetFieldValueType.SPACE_DELIMITED;
    private String ignoreValueMatchingRegex;
    private ValueTokenizerType valueTokenizerType = ValueTokenizerType.STANDARD;
    private List<String> fieldsToBeDeleted = new ArrayList<>();
}
