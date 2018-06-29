package com.github.saiprasadkrishnamurthy.docnormaliser;

import com.github.saiprasadkrishnamurthy.model.DocumentData;
import com.github.saiprasadkrishnamurthy.model.DocumentSettings;
import com.github.saiprasadkrishnamurthy.model.FieldData;
import com.github.saiprasadkrishnamurthy.model.TargetFieldValueType;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.github.saiprasadkrishnamurthy.model.DocumentData.ARRAY_ACCESSOR_PATTERN;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * @author saikris
 */
public final class Normalizer {

    private static final Logger LOG = LoggerFactory.getLogger(Normalizer.class);

    private Normalizer() {
    }

    public static String normalize(final DocumentSettings documentSettings, final String originalJson) {
        Map<String, Object> flattenedJson = JsonFlattener.flattenAsMap(originalJson);
        logDebug(" JSON Flattened: {} ", flattenedJson);
        DocumentContext json = JsonPath.parse(originalJson);
        logDebug(" JSON Parsed: {} ", json);
        DocumentData documentData = new DocumentData();
        documentData.setSettingsId(documentSettings.getId());
        documentData.setDocumentSettings(documentSettings);

        flattenedJson.forEach((jsonKey, jsonValue) -> {
            documentData.getFieldsGroup(jsonKey)
                    .ifPresent(fieldsGroup1 -> documentData.addSourceFieldData(jsonKey, jsonValue, fieldsGroup1));
            documentData.markFieldToBeDeleted(jsonKey);
        });

        documentData.addTargetFieldData();
        logDebug(" Fields data collected: {} ", documentData);

        // Populate
        logDebug("Target to source Mapping: {}", documentData.getTargetToSourceMapping());
        List<FieldData> fieldDataToBeDeleted = documentData.fieldsToBeDeleted();

        logDebug("Fields to be deleted: {}", () -> fieldDataToBeDeleted.stream().map(FieldData::getActualPath).collect(toList()));
        fieldDataToBeDeleted
                .forEach(fd -> {
                    if (fd.getActualPath().endsWith("]")) {
                        json.delete(fd.getActualPath().replaceAll(ARRAY_ACCESSOR_PATTERN, ""));
                    } else {
                        json.delete(fd.getActualPath());
                    }
                });

        // Fields to be updated.
        List<FieldData> fieldDataToBeUpdated = documentData.fieldsToBeUpdated();
        logDebug("Fields to be updated: {}", () -> fieldDataToBeUpdated.stream().map(FieldData::getActualPath).collect(toList()));
        fieldDataToBeUpdated.forEach(fd -> setValue(json, fd));

        // Add the target field.
        logDebug("Fields to be added: {}", () -> documentData.fieldsToBeAdded().stream().map(FieldData::getActualPath).collect(toList()));
        documentData.fieldsToBeAdded()
                .stream()
                .filter(fd -> fd.getActualPath() != null)
                .forEach(fd -> {
                    if (fd.getTargetFieldValueType() == TargetFieldValueType.ARRAY) {
                        json.put("$", fd.getActualPath(), new LinkedHashSet<>(fd.getValues()));
                    } else if (fd.getTargetFieldValueType() == TargetFieldValueType.COMMA_DELIMITED) {
                        json.put("$", fd.getActualPath(), fd.getValues().stream().distinct().collect(joining(",")));
                    } else if (fd.getTargetFieldValueType() == TargetFieldValueType.SPACE_DELIMITED) {
                        json.put("$", fd.getActualPath(), fd.getValues().stream().distinct().collect(joining(" ")));
                    }
                });

        logDebug("Fields to be hard deleted: {}", () -> documentData.fieldsToBeHardDeleted().stream().map(FieldData::getActualPath).collect(toList()));
        documentData.fieldsToBeHardDeleted()
                .forEach(fd -> json.delete(fd.getActualPath()));

        Map<String, Object> toBeDeletedJson = JsonFlattener.flattenAsMap(json.jsonString());
        logDebug(" Deleting all the empty objects deep down: {} ", toBeDeletedJson);
        finalCleanup(json, toBeDeletedJson);

        logDebug(" Final JSON: {} ", json.jsonString());

        return json.jsonString();
    }

    private static void finalCleanup(final DocumentContext json, final Map<String, Object> toBeDeletedJson) {
        toBeDeletedJson.forEach((k, v) -> {
            if (v instanceof Map && ((Map) v).isEmpty()) {
                if (k.endsWith("]")) {
                    json.delete(k.replaceAll(ARRAY_ACCESSOR_PATTERN, ""));
                } else {
                    json.delete(k);
                }
            } else if (v instanceof List && ((List) v).isEmpty()) {
                if (k.endsWith("]")) {
                    json.delete(k.replaceAll(ARRAY_ACCESSOR_PATTERN, ""));
                } else {
                    json.delete(k);
                }
            } else if (v == null) {
                if (k.endsWith("]")) {
                    json.delete(k.replaceAll(ARRAY_ACCESSOR_PATTERN, ""));
                } else {
                    json.delete(k);
                }
            }
        });
    }

    private static void setValue(DocumentContext json, FieldData fd) {
        if (fd.getActualPath().endsWith("]")) {
            String normalizedArrayKey = "";
            String allExceptLast = "";
            String last = "";
            if (fd.getActualPath().contains(".")) {
                String allExceptLastField = fd.getActualPath().substring(0, fd.getActualPath().lastIndexOf("."));
                String lastFieldDeArray = fd.getActualPath().substring(fd.getActualPath().lastIndexOf(".") + 1).replaceAll(ARRAY_ACCESSOR_PATTERN, "");
                normalizedArrayKey = allExceptLastField + "." + lastFieldDeArray;
                allExceptLast = normalizedArrayKey.substring(0, normalizedArrayKey.lastIndexOf('.'));
                last = normalizedArrayKey.substring(normalizedArrayKey.lastIndexOf('.') + 1);
                json.put("$." + allExceptLast, last, new LinkedHashSet<>(fd.getValues()));
            } else {
                String lastFieldDeArray = fd.getActualPath().replaceAll(ARRAY_ACCESSOR_PATTERN, "");
                normalizedArrayKey = lastFieldDeArray;
                json.put("$" + allExceptLast, normalizedArrayKey, new LinkedHashSet<>(fd.getValues()));
            }
        } else {
            if (fd.getActualPath().contains(".")) {
                String allExceptLastField = fd.getActualPath().substring(0, fd.getActualPath().lastIndexOf("."));
                String lastField = fd.getActualPath().substring(fd.getActualPath().lastIndexOf(".") + 1);

                json.put("$." + allExceptLastField, lastField, new LinkedHashSet<>(fd.getValues()).stream().collect(joining(" ")));
            } else {
                json.put("$", fd.getActualPath(), new LinkedHashSet<>(fd.getValues()).stream().collect(joining(" ")));
            }
        }
    }

    private static void logDebug(final String message, final Object... placeHolderValues) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(message, placeHolderValues);
        }
    }

    private static void logDebug(final String message, final Supplier<Object> supplier) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(message, supplier.get());
        }
    }
}
