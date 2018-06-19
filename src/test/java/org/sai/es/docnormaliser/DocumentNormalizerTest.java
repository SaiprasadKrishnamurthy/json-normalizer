package org.sai.es.docnormaliser;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.sai.es.model.DocumentNormalizationSettings;
import org.skyscreamer.jsonassert.JSONAssert;

import java.nio.charset.Charset;

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
}