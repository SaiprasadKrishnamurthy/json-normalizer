package com.github.saiprasadkrishnamurthy.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.github.saiprasadkrishnamurthy.model.ExistenceType.FieldOnlyInPrimary;
import static java.util.stream.Collectors.toSet;

@Data
@EqualsAndHashCode(of = {"primaryField"})
public class FieldSettings {
    private String description;
    private String primaryField;
    private boolean dynamicField;
    private List<String> secondaryFields;
    private List<String> unwantedFields = new ArrayList<>();
    private ExistenceType existenceType = FieldOnlyInPrimary;
    private String valuesDelimiter = " ";
    private String rejectValuesMatchingRegex;
    private Set<String> combinedFields = new HashSet<>();

    public boolean isRemoveOnly() {
        return primaryField == null && secondaryFields == null && !unwantedFields.isEmpty();
    }

    public Set<String> combinedFields() {
        if (combinedFields.isEmpty()) {
            combinedFields = Stream.concat(Stream.concat(Stream.of(primaryField), secondaryFields.stream()), unwantedFields.stream()).collect(toSet());
        }
        return combinedFields;
    }
}