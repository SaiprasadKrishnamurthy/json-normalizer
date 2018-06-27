package com.github.saiprasadkrishnamurthy.docnormaliser;

import com.github.saiprasadkrishnamurthy.model.DocumentData;
import com.github.saiprasadkrishnamurthy.model.DocumentSettings;
import com.github.saiprasadkrishnamurthy.model.FieldData;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

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
        });

        documentData.addTargetFieldData();
        logDebug(" Fields data collected: {} ", documentData);

        // Populate
        logDebug("Target to source Mapping: {}", documentData.getTargetToSourceMapping());
        List<FieldData> fieldDataToBeDeleted = documentData.fieldsToBeDeleted();

        logDebug("Fields to be deleted: {}", () -> fieldDataToBeDeleted.stream().map(FieldData::getActualPath).collect(toList()));
        fieldDataToBeDeleted
                .forEach(fd -> json.delete(fd.getActualPath()));

        // Fields to be updated.
        List<FieldData> fieldDataToBeUpdated = documentData.fieldsToBeUpdated();
        logDebug("Fields to be updated: {}", () -> fieldDataToBeUpdated.stream().map(FieldData::getActualPath).collect(toList()));
        fieldDataToBeUpdated.forEach(fd -> json.set(fd.getActualPath(), fd.getValues()));

        // Add the target field.
        logDebug("Fields to be added: {}", () -> documentData.fieldsToBeAdded().stream().map(FieldData::getActualPath).collect(toList()));
        documentData.fieldsToBeAdded()
                .forEach(fd -> json.put("$", fd.getActualPath(), new LinkedHashSet<>(fd.getValues())));

        logDebug(" Final JSON: {} ", json.jsonString());
        return json.jsonString();
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
