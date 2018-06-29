package com.github.saiprasadkrishnamurthy.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Data
public class DocumentData {
    public static final String ARRAY_ACCESSOR_PATTERN = "\\[(.*?)]";
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
                        .flatMap(s -> {
                            if (fieldsGroup.getValueTokenizerType() == ValueTokenizerType.STANDARD) {
                                return Stream.of(s.split("\\W+"));
                            } else if (fieldsGroup.getValueTokenizerType() == ValueTokenizerType.KEYWORD) {
                                return Stream.of(s);
                            } else {
                                return Stream.of(s);
                            }
                        })
                        .collect(toList());
            } else if (value != null) {
                if (fieldsGroup.getValueTokenizerType() == ValueTokenizerType.STANDARD) {
                    values.addAll(Arrays.asList(value.toString().split("\\W+")));
                } else if (fieldsGroup.getValueTokenizerType() == ValueTokenizerType.KEYWORD) {
                    values.add(value.toString());
                } else {
                    values.add(value.toString());
                }
            }
            fieldData.add(new FieldData(canonicalPath, actualJsonPath, values, fieldsGroup.getTargetField(), false, fieldsGroup.getTargetFieldValueType()));
        }
    }

    public void markFieldToBeDeleted(final String actualJsonPath) {
        String canonicalPath = actualJsonPath.replaceAll(ARRAY_ACCESSOR_PATTERN, "");
        documentSettings.getFieldsGroup().forEach(fieldsGroup -> {
            if (fieldsGroup.getFieldsToBeDeleted().contains(canonicalPath)) {
                fieldData.add(new FieldData(canonicalPath, actualJsonPath, null, null, true, fieldsGroup.getTargetFieldValueType()));
            } else {
                // Delete a tree.
                // Traverse to the Least Ancestor and check. If matched then mark the object deleted.
                fieldsGroup.getFieldsToBeDeleted().forEach(toBeDeleted -> {
                    String[] actualPathElements = actualJsonPath.split("\\.");
                    String[] canonicalPathElements = canonicalPath.split("\\.");
                    String[] toBeDeletedPathElements = toBeDeleted.split("\\.");
                    if (canonicalPath.contains(toBeDeleted)) {
                        int index = 0;
                        while (index < toBeDeletedPathElements.length && toBeDeletedPathElements[index].equals(canonicalPathElements[index])) {
                            index++;
                        }
                        String rootPathToBeDeleted = IntStream.range(0, index)
                                .mapToObj(i -> actualPathElements[i])
                                .collect(joining("."));

                        if (StringUtils.isNotBlank(rootPathToBeDeleted) &&
                                fieldData.stream().noneMatch(fd -> fd.getActualPath().equals(rootPathToBeDeleted) && fd.isToBeDeleted())) {
                            fieldData.add(new FieldData(toBeDeleted, rootPathToBeDeleted, null, null, true, fieldsGroup.getTargetFieldValueType()));
                        }
                    }
                });
            }
        });
    }

    public List<FieldData> fieldsToBeDeleted() {
        return targetToSourceMapping.entrySet().stream()
                .flatMap(e -> {
                    List<String> target = e.getKey().getValues();
                    return e.getValue().stream()
                            .map(fd -> {
                                List<String> small = fd.getValues();
                                List<String> big = target;
                                List<String> delta = new ArrayList<>();
                                for (String value : small) {
                                    if (big.stream().filter(v -> v.equals(value)).count() == 1) {
                                        delta.add(value);
                                    }
                                }
                                return new Pair<>(fd, delta);
                            })
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
                    .filter(fd -> fd.getTargetField() != null)
                    .filter(fd -> fd.getTargetField().equals(targetFieldName))
                    .flatMap(fd -> fd.getValues().stream())
                    .filter(Objects::nonNull)
                    .flatMap(s -> {
                        if (fieldsGroup.getValueTokenizerType() == ValueTokenizerType.STANDARD) {
                            return Stream.of(s.split("\\W+"));
                        } else if (fieldsGroup.getValueTokenizerType() == ValueTokenizerType.KEYWORD) {
                            return Stream.of(s);
                        } else {
                            return Stream.of(s);
                        }
                    })
                    .collect(toList());

            fieldData.stream()
                    .filter(fd -> fd.getTargetField() != null)
                    .filter(fd -> fd.getTargetField().equals(targetFieldName))
                    .forEach(sourceFields::add);

            FieldData targetFieldData = new FieldData(targetFieldName, targetFieldName, sourceValues, null, false, fieldsGroup.getTargetFieldValueType());
            targetToSourceMapping.put(targetFieldData, sourceFields);
        });
    }

    public Set<FieldData> fieldsToBeAdded() {
        return targetToSourceMapping.keySet();
    }

    public List<FieldData> fieldsToBeHardDeleted() {
        return fieldData.stream().filter(FieldData::isToBeDeleted).collect(toList());
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
                            .map(fd -> {
                                List<String> small = fd.getValues();
                                List<String> big = target;
                                List<String> delta = new ArrayList<>();
                                for (String value : small) {
                                    if (big.stream().filter(v -> v.equals(value)).count() == 1) {
                                        delta.add(value);
                                    }
                                }
                                return new Pair<>(fd, delta);
                            })
                            .filter(pair -> !pair.getValue1().isEmpty())
                            .peek(pair -> target.removeAll(pair.getValue1()))
                            .peek(pair -> pair.getValue0().setValues(new ArrayList<>(pair.getValue1())))
                            .map(Pair::getValue0);
                })
                .collect(Collectors.toList());
    }
}
