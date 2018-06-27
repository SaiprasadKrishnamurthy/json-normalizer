package com.github.saiprasadkrishnamurthy.model;

import lombok.Data;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.javatuples.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Data
public class DocumentData {
    private static final String ARRAY_ACCESSOR_PATTERN = "\\[(.*?)]";
    private String settingsId;
    private DocumentSettings documentSettings;
    private List<FieldData> fieldData = new ArrayList<>();
    private Map<FieldData, List<FieldData>> targetToSourceMapping = new LinkedHashMap<>();

    public void addSourceFieldData(final String actualJsonPath, final Object value, final FieldsGroup fieldsGroup) {
        String canonicalPath = actualJsonPath.replaceAll(ARRAY_ACCESSOR_PATTERN, "");
        if (fieldsGroup.getSourceFields().contains(canonicalPath)) {
            List<String> values = new ArrayList<>();
            if (value instanceof List) {
                values = ((List<Object>) value).stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .flatMap(s -> Stream.of(s.split("\\W+")))
                        .collect(toList());
            } else if (value != null) {
                values.addAll(Arrays.asList(value.toString().split("\\W+")));
            }
            fieldData.add(new FieldData(canonicalPath, actualJsonPath, values, fieldsGroup.getTargetField(), false));
        }
    }

    public List<FieldData> fieldsToBeDeleted() {
        return targetToSourceMapping.entrySet().stream()
                .flatMap(e -> {
                    List<String> target = e.getKey().getValues();
                    return e.getValue().stream()
                            .map(fd -> new Pair<>(fd, ListUtils.subtract(fd.getValues(), ListUtils.subtract(target, fd.getValues()))))
                            .filter(pair -> pair.getValue1().isEmpty())
                            .map(Pair::getValue0);
                })
                .collect(Collectors.toList());
    }

    public void addTargetFieldData() {
        documentSettings.getFieldsGroup().forEach(fieldsGroup -> {
            String targetFieldName = fieldsGroup.getTargetField();
            List<FieldData> sourceFields = new ArrayList<>();
            List<String> sourceValues = fieldData.stream()
                    .filter(fd -> fd.getTargetField().equals(targetFieldName))
                    .flatMap(fd -> fd.getValues().stream())
                    .filter(Objects::nonNull)
                    .flatMap(s -> Stream.of(s.split("\\W+")))
                    .collect(toList());

            fieldData.stream().filter(fd -> fd.getTargetField().equals(targetFieldName))
                    .forEach(sourceFields::add);

            FieldData targetFieldData = new FieldData(targetFieldName, targetFieldName, sourceValues, null, false);
            targetToSourceMapping.put(targetFieldData, sourceFields);
        });
    }

    public Set<FieldData> fieldsToBeAdded() {
        return targetToSourceMapping.keySet();
    }

    public Optional<FieldsGroup> getFieldsGroup(final String jsonKey) {
        String canonicalPath = jsonKey.replaceAll(ARRAY_ACCESSOR_PATTERN, "");
        return documentSettings.getFieldsGroup().stream()
                .filter(fieldsGroup -> fieldsGroup.getSourceFields().contains(canonicalPath)).findFirst();
    }

    public List<FieldData> getFieldData() {
        return Collections.unmodifiableList(fieldData);
    }

    public List<FieldData> fieldsToBeUpdated() {
        return targetToSourceMapping.entrySet().stream()
                .flatMap(e -> {
                    List<String> target = e.getKey().getValues();
                    return e.getValue().stream()
                            .map(fd -> new Pair<>(fd, ListUtils.subtract(fd.getValues(), ListUtils.subtract(target, fd.getValues()))))
                            .filter(pair -> !pair.getValue1().isEmpty())
                            .peek(pair -> pair.getValue0().setValues(new ArrayList<>(pair.getValue1())))
                            .map(Pair::getValue0);
                })
                .collect(Collectors.toList());
    }
}
