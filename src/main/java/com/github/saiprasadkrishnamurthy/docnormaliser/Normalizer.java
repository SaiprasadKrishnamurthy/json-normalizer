package com.github.saiprasadkrishnamurthy.docnormaliser;

import com.github.saiprasadkrishnamurthy.model.DocumentData;
import com.github.saiprasadkrishnamurthy.model.DocumentSettings;
import com.github.saiprasadkrishnamurthy.model.FieldData;
import com.github.saiprasadkrishnamurthy.model.TargetFieldValueType;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.mitchtalmadge.asciidata.graph.ASCIIGraph;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.saiprasadkrishnamurthy.model.DocumentData.ARRAY_ACCESSOR_PATTERN;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * @author saikris
 */
public final class Normalizer {

    private static final Logger LOG = LoggerFactory.getLogger(Normalizer.class);
    private static final String STATS_RECORD_SETTING = "json.normalizer.record.stats";
    private static final String STATS_RECORD_SIZE_SETTING = "json.normalizer.record.stats.size";
    private static final LRUMap<String, Double> STATS_MAP = new LRUMap<>(System.getProperty(STATS_RECORD_SIZE_SETTING) == null ? 300 : Integer.parseInt(System.getProperty(STATS_RECORD_SIZE_SETTING)));


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
                .forEach(fd -> {
                    if (fd.getActualPath().endsWith("]")) {
                        // Arrays should be deleted in the format '$['a']['b'][0]
                        String fields = Stream.of(fd.getCanonicalPath().split("\\."))
                                .map(f -> "['" + f + "']")
                                .collect(joining());
                        String arrayIndex = fd.getActualPath().substring(fd.getActualPath().lastIndexOf('['));
                        Integer n = Integer.parseInt(arrayIndex.replace("[", "").replace("]", ""));
                        for (int i = 0; i < n + 1; i++) {
                            String path = "$" + fields + "[" + i + "]";
                            try {
                                json.delete(path);
                            } catch (PathNotFoundException ex) {
                                LOG.debug(" Already deleted or path is invalid: {}", path);
                            }
                        }
                    } else {
                        json.delete(fd.getActualPath());
                    }
                });
        Map<String, Object> toBeDeletedJson = JsonFlattener.flattenAsMap(json.jsonString());
        logDebug(" Deleting all the empty objects deep down: {} ", toBeDeletedJson);
        finalCleanup(json, toBeDeletedJson);

        logDebug(" Final JSON: {} ", json::jsonString);
        if (Boolean.parseBoolean(System.getProperty(STATS_RECORD_SETTING)) || LOG.isDebugEnabled()) {
            logDebug(" Final JSON Stats: {} ", () -> {
                double originalJsonLength = originalJson.length();
                double newJsonLength = json.jsonString().length();
                double decrease = originalJsonLength - newJsonLength;
                double percent = (decrease / originalJsonLength) * 100D;
                STATS_MAP.putIfAbsent(UUID.randomUUID().toString(), percent);
                return String.format("[ORIGINAL]: %s [NORMALISED]: %s [%%DECREASE]: %s%%", originalJsonLength, newJsonLength, percent);
            });
        }

        return json.jsonString();
    }

    public static String plotNormalisedLengthReductionInPercentage() {
        if (Boolean.parseBoolean(System.getProperty(STATS_RECORD_SETTING)) && !STATS_MAP.isEmpty()) {
            String plot = ASCIIGraph
                    .fromSeries(ArrayUtils.toPrimitive(STATS_MAP.values().toArray(new Double[0])))
                    .withNumRows(10)
                    .plot();

            if (LOG.isDebugEnabled()) {
                LOG.debug("\n\n\n");
                LOG.debug(plot);
                LOG.debug("\n\n\n");
            }
            return plot;
        } else {
            return "";
        }
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
            } else if (v instanceof List && ((List) v).isEmpty()) {
                json.delete(k);
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
