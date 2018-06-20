package com.github.saiprasadkrishnamurthy.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static com.github.saiprasadkrishnamurthy.model.ExistenceType.FieldOnlyInPrimary;

@Data
@EqualsAndHashCode(of = {"primaryField"})
public class FieldSettings {
    private String primaryField;
    private boolean dynamicField;
    private String[] secondaryFields;
    private List<String> unwantedFields = new ArrayList<>();
    private ExistenceType existenceType = FieldOnlyInPrimary;
    private String valuesDelimiter = " ";
    private String rejectValuesMatchingRegex;
    private Set<String> combinedFields = new HashSet<>();

    public Set<String> combinedFields() {
        if (combinedFields.isEmpty()) {
            combinedFields = Stream.concat(Stream.concat(Stream.of(primaryField), Stream.of(secondaryFields)), unwantedFields.stream()).collect(toSet());
        }
        return combinedFields;
    }
}