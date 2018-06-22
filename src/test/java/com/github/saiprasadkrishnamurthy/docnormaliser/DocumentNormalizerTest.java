package com.github.saiprasadkrishnamurthy.docnormaliser;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saiprasadkrishnamurthy.model.DocumentNormalizationSettings;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static junit.framework.TestCase.assertTrue;

public class DocumentNormalizerTest {

    @Test
    public void normalize_scenario1() throws Exception {
        String scenarioPrefix = "scenario1";
        String settingsJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "normalization.json"), Charset.defaultCharset());
        String inputJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "input.json"), Charset.defaultCharset());
        String expectedJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "expected.json"), Charset.defaultCharset());
        DocumentNormalizationSettings documentNormalizationSettings = new ObjectMapper().readValue(settingsJson, DocumentNormalizationSettings.class);
        String actualJson = DocumentNormalizer.normalize(documentNormalizationSettings, inputJson);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void normalize_scenario2() throws Exception {
        String scenarioPrefix = "scenario2";
        String settingsJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "normalization.json"), Charset.defaultCharset());
        String inputJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "input.json"), Charset.defaultCharset());
        String expectedJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "expected.json"), Charset.defaultCharset());
        DocumentNormalizationSettings documentNormalizationSettings = new ObjectMapper().readValue(settingsJson, DocumentNormalizationSettings.class);
        String actualJson = DocumentNormalizer.normalize(documentNormalizationSettings, inputJson);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void normalize_scenario3() throws Exception {
        String scenarioPrefix = "scenario3";
        String settingsJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "normalization.json"), Charset.defaultCharset());
        String inputJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "input.json"), Charset.defaultCharset());
        String expectedJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "expected.json"), Charset.defaultCharset());
        DocumentNormalizationSettings documentNormalizationSettings = new ObjectMapper().readValue(settingsJson, DocumentNormalizationSettings.class);
        String actualJson = DocumentNormalizer.normalize(documentNormalizationSettings, inputJson);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void normalize_scenario4() throws Exception {
        String scenarioPrefix = "scenario4";
        String settingsJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "normalization.json"), Charset.defaultCharset());
        String inputJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "input.json"), Charset.defaultCharset());
        String expectedJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "expected.json"), Charset.defaultCharset());
        DocumentNormalizationSettings documentNormalizationSettings = new ObjectMapper().readValue(settingsJson, DocumentNormalizationSettings.class);
        String actualJson = DocumentNormalizer.normalize(documentNormalizationSettings, inputJson);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void normalize_scenario5() throws Exception {
        String scenarioPrefix = "scenario5";
        String settingsJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "normalization.json"), Charset.defaultCharset());
        String inputJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "input.json"), Charset.defaultCharset());
        String expectedJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "expected.json"), Charset.defaultCharset());
        DocumentNormalizationSettings documentNormalizationSettings = new ObjectMapper().readValue(settingsJson, DocumentNormalizationSettings.class);
        String actualJson = DocumentNormalizer.normalize(documentNormalizationSettings, inputJson);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void normalize_scenario6() throws Exception {
        String scenarioPrefix = "scenario6";
        String settingsJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "normalization.json"), Charset.defaultCharset());
        String inputJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "input.json"), Charset.defaultCharset());
        String expectedJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "expected.json"), Charset.defaultCharset());
        DocumentNormalizationSettings documentNormalizationSettings = new ObjectMapper().readValue(settingsJson, DocumentNormalizationSettings.class);
        String actualJson = DocumentNormalizer.normalize(documentNormalizationSettings, inputJson);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void normalize_scenario7() throws Exception {
        String scenarioPrefix = "scenario7";
        String settingsJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "normalization.json"), Charset.defaultCharset());
        String inputJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "input.json"), Charset.defaultCharset());
        String expectedJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "expected.json"), Charset.defaultCharset());
        DocumentNormalizationSettings documentNormalizationSettings = new ObjectMapper().readValue(settingsJson, DocumentNormalizationSettings.class);
        String actualJson = DocumentNormalizer.normalize(documentNormalizationSettings, inputJson);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void normalize_scenario8() throws Exception {
        String scenarioPrefix = "scenario8";
        String settingsJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "normalization.json"), Charset.defaultCharset());
        String inputJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "input.json"), Charset.defaultCharset());
        String expectedJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "expected.json"), Charset.defaultCharset());
        DocumentNormalizationSettings documentNormalizationSettings = new ObjectMapper().readValue(settingsJson, DocumentNormalizationSettings.class);
        String actualJson = DocumentNormalizer.normalize(documentNormalizationSettings, inputJson);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void normalize_scenario9() throws Exception {
        String scenarioPrefix = "scenario9";
        String settingsJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "normalization.json"), Charset.defaultCharset());
        String inputJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "input.json"), Charset.defaultCharset());
        String expectedJson = IOUtils.toString(DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "expected.json"), Charset.defaultCharset());
        DocumentNormalizationSettings documentNormalizationSettings = new ObjectMapper().readValue(settingsJson, DocumentNormalizationSettings.class);
        String actualJson = DocumentNormalizer.normalize(documentNormalizationSettings, inputJson);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void normalizedFieldsFor() throws Exception {
        String scenarioPrefix = "scenario8";
        InputStream settingsJson = DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "normalization.json");
        assertTrue(CollectionUtils.isEqualCollection(new HashSet<>(Arrays.asList("contacts.homePhone", "_phoneNumbers")),
                DocumentNormalizer.normalizedFieldsFor("contacts.homePhone", settingsJson)));

        settingsJson = DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "normalization.json");
        assertTrue(CollectionUtils.isEqualCollection(new HashSet<>(Arrays.asList("contacts.officePhone", "_phoneNumbers")),
                DocumentNormalizer.normalizedFieldsFor("contacts.officePhone", settingsJson)));

        settingsJson = DocumentNormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "normalization.json");
        assertTrue(CollectionUtils.isEqualCollection(Collections.emptySet(),
                DocumentNormalizer.normalizedFieldsFor("non_Existing", settingsJson)));

    }
}