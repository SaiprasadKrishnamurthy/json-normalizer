package org.sai.es.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

import static org.sai.es.model.ExistenceType.FieldOnlyInPrimary;

@Data
@EqualsAndHashCode(of = {"primaryField"})
public class FieldSettings {
    private String primaryField;
    private String[] secondaryFields;
    private List<String> unwantedFields = new ArrayList<>();
    private ExistenceType existenceType = FieldOnlyInPrimary;
    private String valuesDelimiter = " ";
    private String rejectValuesMatchingRegex;
}