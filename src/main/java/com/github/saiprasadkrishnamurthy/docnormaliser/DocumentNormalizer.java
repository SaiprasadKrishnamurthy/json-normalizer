package com.github.saiprasadkrishnamurthy.docnormaliser;

import com.github.saiprasadkrishnamurthy.model.FieldSettings;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.apache.commons.lang3.StringUtils;
import com.github.saiprasadkrishnamurthy.model.DocumentNormalizationSettings;
import com.github.saiprasadkrishnamurthy.model.ExistenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * @author saikris
 */
public final class DocumentNormalizer {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentNormalizer.class);

    private DocumentNormalizer() {
    }

    public static String normalize(final DocumentNormalizationSettings documentNormalizationSettings, final String originalJson) {
        Map<String, Object> _flattenedDoc = null;
        Map<String, Object> _flattenedJson = JsonFlattener.flattenAsMap(originalJson);
        String pattern = "\\[(.*?)]";
        DocumentContext doc = null;
        DocumentContext _json = JsonPath.parse(originalJson);
        logDebug("Injecting the dynamic fields wherever applicable");

        long countOfDynamicFields = documentNormalizationSettings.getFieldSettings().stream()
                .filter(d -> d.isDynamicField())
                .peek(d -> {
                    if (d.getPrimaryField().contains(".")) {
                        throw new IllegalArgumentException("Dynamic fields are always only supported at the root level. Therefore it should not contain any accessor characters like dot '.'");
                    }
                    logDebug("Dynamic Fields found in the setting: {}", d.getPrimaryField());
                    _json.put("$", d.getPrimaryField(), null);
                }).count();

        if (countOfDynamicFields > 0) {
            doc = JsonPath.parse(_json.jsonString());
            _flattenedDoc = JsonFlattener.flattenAsMap(_json.jsonString());
        } else {
            doc = _json;
            _flattenedDoc = _flattenedJson;
            logDebug("No Dynamic Fields found in the setting");
        }

        Map<String, Object> flattenedJson = _flattenedDoc;
        logDebug("Json Flattened: {}", flattenedJson);

        DocumentContext json = doc;
        // Collect secondary field values
        Map<String, Set<Object>> primaryKeySecondaryValuesMap = new HashMap<>();
        Map<String, Set<Object>> primaryKeyValuesMap = new HashMap<>();
        logDebug("Collecting all primary and secondary values in one go");
        Set<String> unwantedFieldPaths = new HashSet<>();
        flattenedJson.forEach((actualJsonKey, actualJsonValue) -> {
            String canonicalizedJsonKey = actualJsonKey.replaceAll(pattern, "");
            documentNormalizationSettings.getFieldSettings()
                    .forEach(fieldSettings -> {
                        if (fieldSettings.combinedFields().contains(canonicalizedJsonKey)) {
                            String primaryField = fieldSettings.getPrimaryField();
                            if (canonicalizedJsonKey.equals(primaryField)) {
                                if (actualJsonValue != null && (StringUtils.isBlank(fieldSettings.getRejectValuesMatchingRegex()) || !Pattern.compile(fieldSettings.getRejectValuesMatchingRegex()).matcher(actualJsonValue.toString()).find())) {
                                    primaryKeyValuesMap.compute(canonicalizedJsonKey, (k, v) -> (v == null) ? new LinkedHashSet<>() : v)
                                            .addAll(Arrays.asList(actualJsonValue.toString().split(fieldSettings.getValuesDelimiter())));
                                }
                            } else {
                                for (String secondaryField : fieldSettings.getSecondaryFields()) {
                                    if (canonicalizedJsonKey.equals(secondaryField)) {
                                        Set<Object> secondaryValues = primaryKeySecondaryValuesMap.compute(primaryField, (k, v) -> (v == null) ? new TreeSet<>() : v);
                                        if (actualJsonValue != null && (StringUtils.isBlank(fieldSettings.getRejectValuesMatchingRegex()) || !Pattern.compile(fieldSettings.getRejectValuesMatchingRegex()).matcher(actualJsonValue.toString()).find())) {
                                            secondaryValues.add(actualJsonValue.toString());
                                        }
                                        // Remove the secondary value as per the settings.
                                        if (notDefinedAsPrimaryField(documentNormalizationSettings, canonicalizedJsonKey)) {
                                            if (fieldSettings.getExistenceType() == ExistenceType.ValuesOnlyInPrimary) {
                                                json.set(actualJsonKey, "");
                                            } else if (fieldSettings.getExistenceType() == ExistenceType.FieldOnlyInPrimary) {
                                                // in case of an array, we should delete the field rather than the value.
                                                if (actualJsonKey.endsWith("]")) {
                                                    json.delete(actualJsonKey.replaceAll(pattern, ""));
                                                } else {
                                                    json.delete(actualJsonKey);
                                                }
                                            }
                                        }
                                    }
                                }
                                if (fieldSettings.getUnwantedFields().stream().anyMatch(f -> f.equals(canonicalizedJsonKey))) {
                                    unwantedFieldPaths.add(actualJsonKey);
                                } else if (fieldSettings.getUnwantedFields().stream().anyMatch(f -> canonicalizedJsonKey.startsWith(f + ".") || canonicalizedJsonKey.contains("." + f + "."))) {
                                    unwantedFieldPaths.add(actualJsonKey);
                                }
                            }
                        }
                    });

        });
        logDebug("Collected all primary values successfully: {} ", primaryKeyValuesMap);
        logDebug("Collected all secondary values successfully: {} ", primaryKeySecondaryValuesMap);

        // Populate values
        Set<String> primaryFieldsAlreadyAdded = new HashSet<>();

        flattenedJson.forEach((actualJsonKey, actualJsonValue) -> {
            String canonicalizedJsonKey = actualJsonKey.replaceAll(pattern, "");
            if (primaryKeySecondaryValuesMap.containsKey(canonicalizedJsonKey)) {
                if (!primaryFieldsAlreadyAdded.contains(canonicalizedJsonKey)) {
                    // Look up the original FieldSynonymSetting
                    FieldSettings fieldSetting = documentNormalizationSettings.getFieldSettings().stream().filter(f -> f.getPrimaryField().equalsIgnoreCase(canonicalizedJsonKey)).findFirst().get();
                    // Populate the value
                    Stream<Object> secondaryValuesStream = primaryKeySecondaryValuesMap.get(canonicalizedJsonKey).stream();
                    Stream<Object> valuesStream = secondaryValuesStream;
                    if (primaryKeyValuesMap.get(canonicalizedJsonKey) != null) {
                        valuesStream = Stream.concat(primaryKeyValuesMap.get(canonicalizedJsonKey).stream(), secondaryValuesStream).collect(Collectors.toCollection(LinkedHashSet::new)).stream();
                    }
                    if (actualJsonKey.endsWith("]")) {
                        String normalizedArrayKey = "";
                        if (actualJsonKey.contains(".")) {
                            String allExceptLastField = actualJsonKey.substring(0, actualJsonKey.lastIndexOf("."));
                            String lastFieldDeArray = actualJsonKey.substring(actualJsonKey.lastIndexOf(".") + 1).replace(pattern, "");
                            normalizedArrayKey = allExceptLastField + "." + lastFieldDeArray;
                        } else {
                            String lastFieldDeArray = actualJsonKey.replaceAll(pattern, "");
                            normalizedArrayKey = lastFieldDeArray;
                        }
                        json.set(normalizedArrayKey, valuesStream.map(v -> v + "").collect(toList()));
                    } else {
                        json.set(actualJsonKey, valuesStream.map(v -> v + "").collect(joining(fieldSetting.getValuesDelimiter())));
                    }
                    primaryFieldsAlreadyAdded.add(canonicalizedJsonKey);
                } else {
                    if (!actualJsonKey.endsWith("]")) {
                        json.delete(actualJsonKey);
                    }
                }
            }
        });
        logDebug("Unwanted fields to be removed: {} ", unwantedFieldPaths);

        unwantedFieldPaths.forEach(path -> {
            try {
                json.delete(path);
            } catch (PathNotFoundException exception) {
                logDebug("A Tree Specified as Unwanted Field. Therefore attempting to delete recursively: {} ", path);
                List<String> tokens = Arrays.asList(path.split("\\."));
                Collections.reverse(tokens);
                tokens.forEach(token -> {
                    String replace = path.replace(token, "");
                    if (replace.endsWith(".")) {
                        replace = replace.substring(0, replace.length() - 1);
                    }
                    json.delete(replace);
                });
            }
        });

        logDebug("Original Doc Length: {} ", originalJson.length());
        logDebug("Normalized Doc Length: {} ", () -> json.jsonString().length());
        logDebug("% Reduction in size: {}% ", () -> ((originalJson.length() - json.jsonString().length()) / (double) originalJson.length()) * 100);
        logDebug("Output JSON: {} ", json::jsonString);

        return json.jsonString();
    }


    private static boolean notDefinedAsPrimaryField(final DocumentNormalizationSettings documentNormalizationSettings, final String canonicalizedJsonKey) {
        return documentNormalizationSettings.getFieldSettings().stream()
                .noneMatch(fs -> fs.getPrimaryField().equalsIgnoreCase(canonicalizedJsonKey));
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
