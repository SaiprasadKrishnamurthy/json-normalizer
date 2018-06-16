package org.sai.es.docnormaliser;

import com.github.wnameless.json.flattener.JsonFlattener;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.sai.es.model.DocumentNormalizationSettings;
import org.sai.es.model.ExistenceType;
import org.sai.es.model.FieldSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;

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
        Map<String, Object> flattenedJson = JsonFlattener.flattenAsMap(originalJson);
        logDebug("Json Flattened: {}", flattenedJson);
        String pattern = "\\[(.*?)]";
        DocumentContext json = JsonPath.parse(originalJson);

        // Collect secondary field values
        Map<String, Set<Object>> primaryKeySecondaryValuesMap = new HashMap<>();
        logDebug("Collecting all secondary values in one go");
        Set<String> unwantedFieldPaths = new HashSet<>();
        flattenedJson.forEach((actualJsonKey, actualJsonValue) -> {
            // For each actual Json key, check if it's present in any of the secondary fields, if so then dedupe and set it into the
            // primary field.
            String canonicalizedJsonKey = actualJsonKey.replaceAll(pattern, "");

            documentNormalizationSettings.getFieldSettings()
                    .forEach(fieldSettings -> {
                        String primaryField = fieldSettings.getPrimaryField();
                        for (String secondaryField : fieldSettings.getSecondaryFields()) {
                            if (canonicalizedJsonKey.equals(secondaryField)) {
                                Set<Object> secondaryValues = primaryKeySecondaryValuesMap.compute(primaryField, (k, v) -> (v == null) ? new TreeSet<>() : v);
                                if (actualJsonValue != null && (StringUtils.isBlank(fieldSettings.getRejectValuesMatchingRegex()) || !actualJsonValue.toString().matches(fieldSettings.getRejectValuesMatchingRegex()))) {
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
                        } else if (fieldSettings.getUnwantedFields().stream().anyMatch(f -> canonicalizedJsonKey.startsWith(f + ".") || canonicalizedJsonKey.contains("." + f + "."))){
                            unwantedFieldPaths.add(actualJsonKey);
                        }
                    });

        });
        logDebug("Collected all secondary values successfully: {} ", primaryKeySecondaryValuesMap);

        // Collect Primary Values.
        flattenedJson.forEach((actualJsonKey, actualJsonValue) -> {
            String canonicalizedJsonKey = actualJsonKey.replaceAll(pattern, "");
            if (primaryKeySecondaryValuesMap.containsKey(canonicalizedJsonKey)) {
                // Look up the original FieldSynonymSetting
                FieldSettings fieldSettings = documentNormalizationSettings.getFieldSettings().stream().filter(f -> f.getPrimaryField().equalsIgnoreCase(canonicalizedJsonKey)).findFirst().get();
                if (actualJsonValue != null && (StringUtils.isBlank(fieldSettings.getRejectValuesMatchingRegex()) || !actualJsonValue.toString().matches(fieldSettings.getRejectValuesMatchingRegex()))) {
                    primaryKeySecondaryValuesMap.get(canonicalizedJsonKey)
                            .addAll(Arrays.asList(actualJsonValue.toString().split(fieldSettings.getValuesDelimiter())));
                }
            }
        });

        logDebug("Collected all primary values successfully: {} ", primaryKeySecondaryValuesMap);


        // Populate values
        Set<String> primaryFieldsAlreadyAdded = new HashSet<>();

        flattenedJson.forEach((actualJsonKey, actualJsonValue) -> {
            String canonicalizedJsonKey = actualJsonKey.replaceAll(pattern, "");
            if (primaryKeySecondaryValuesMap.containsKey(canonicalizedJsonKey)) {
                if (!primaryFieldsAlreadyAdded.contains(canonicalizedJsonKey)) {
                    // Look up the original FieldSynonymSetting
                    FieldSettings fieldSetting = documentNormalizationSettings.getFieldSettings().stream().filter(f -> f.getPrimaryField().equalsIgnoreCase(canonicalizedJsonKey)).findFirst().get();
                    // Populate the value
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
                        json.set(normalizedArrayKey, primaryKeySecondaryValuesMap.get(canonicalizedJsonKey).stream().map(v -> v + "").collect(toList()));
                    } else {
                        json.set(actualJsonKey, primaryKeySecondaryValuesMap.get(canonicalizedJsonKey).stream().map(v -> v + "").collect(joining(fieldSetting.getValuesDelimiter())));
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
            }catch (PathNotFoundException exception) {
                logDebug("A Tree Specified as Unwanted Field. Therefore attempting to delete recursively: {} ", path);
                List<String> tokens = Arrays.asList(path.split("\\."));
                Collections.reverse(tokens);
                tokens.forEach(token -> {
                    String replace = path.replace(token, "");
                    if(replace.endsWith(".")) {
                        replace = replace.substring(0, replace.length()-1);
                    }
                    json.delete(replace);
                });
            }
        });

        logDebug("Output JSON: {} ", json::jsonString);
        return json.jsonString();
    }

    private static boolean notDefinedAsPrimaryField(final DocumentNormalizationSettings documentNormalizationSettings, final String canonicalizedJsonKey) {
        return documentNormalizationSettings.getFieldSettings().stream()
                .noneMatch(fs -> fs.getPrimaryField().equalsIgnoreCase(canonicalizedJsonKey));
    }

    private static void logDebug(final String message, Object... placeHolderValues) {
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
