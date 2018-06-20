package com.github.saiprasadkrishnamurthy.model;

import lombok.Data;

import java.util.List;

@Data
public class DocumentNormalizationSettings {
    private String documentType;
    private List<FieldSettings> fieldSettings;
}
