package com.github.saiprasadkrishnamurthy.docnormaliser;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saiprasadkrishnamurthy.model.DocumentSettings;
import org.apache.commons.io.IOUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.skyscreamer.jsonassert.JSONAssert;

import java.nio.charset.Charset;

import static org.junit.Assert.assertNotNull;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NormalizerTest {

    @Test
    public void normalize_scenario1() throws Exception {
        String scenarioPrefix = "scenario1";
        String settingsJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "documentsettings.json"), Charset.defaultCharset());
        String inputJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "input.json"), Charset.defaultCharset());
        String expectedJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "expected.json"), Charset.defaultCharset());
        DocumentSettings documentNormalizationSettings = new ObjectMapper().readValue(settingsJson, DocumentSettings.class);
        String actualJson = Normalizer.normalize(documentNormalizationSettings, inputJson);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void normalize_scenario2() throws Exception {
        String scenarioPrefix = "scenario2";
        String settingsJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "documentsettings.json"), Charset.defaultCharset());
        String inputJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "input.json"), Charset.defaultCharset());
        String expectedJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "expected.json"), Charset.defaultCharset());
        DocumentSettings documentNormalizationSettings = new ObjectMapper().readValue(settingsJson, DocumentSettings.class);
        String actualJson = Normalizer.normalize(documentNormalizationSettings, inputJson);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void normalize_scenario3() throws Exception {
        String scenarioPrefix = "scenario3";
        String settingsJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "documentsettings.json"), Charset.defaultCharset());
        String inputJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "input.json"), Charset.defaultCharset());
        String expectedJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "expected.json"), Charset.defaultCharset());
        DocumentSettings documentNormalizationSettings = new ObjectMapper().readValue(settingsJson, DocumentSettings.class);
        String actualJson = Normalizer.normalize(documentNormalizationSettings, inputJson);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void normalize_scenario4() throws Exception {
        String scenarioPrefix = "scenario4";
        String settingsJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "documentsettings.json"), Charset.defaultCharset());
        String inputJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "input.json"), Charset.defaultCharset());
        String expectedJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "expected.json"), Charset.defaultCharset());
        DocumentSettings documentNormalizationSettings = new ObjectMapper().readValue(settingsJson, DocumentSettings.class);
        String actualJson = Normalizer.normalize(documentNormalizationSettings, inputJson);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void normalize_scenario5() throws Exception {
        String scenarioPrefix = "scenario5";
        String settingsJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "documentsettings.json"), Charset.defaultCharset());
        String inputJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "input.json"), Charset.defaultCharset());
        String expectedJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "expected.json"), Charset.defaultCharset());
        DocumentSettings documentNormalizationSettings = new ObjectMapper().readValue(settingsJson, DocumentSettings.class);
        String actualJson = Normalizer.normalize(documentNormalizationSettings, inputJson);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void normalize_scenario6() throws Exception {
        String scenarioPrefix = "scenario6";
        String settingsJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "documentsettings.json"), Charset.defaultCharset());
        String inputJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "input.json"), Charset.defaultCharset());
        String expectedJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "expected.json"), Charset.defaultCharset());
        DocumentSettings documentNormalizationSettings = new ObjectMapper().readValue(settingsJson, DocumentSettings.class);
        String actualJson = Normalizer.normalize(documentNormalizationSettings, inputJson);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void normalize_scenario7() throws Exception {
        String scenarioPrefix = "scenario7";
        String settingsJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "documentsettings.json"), Charset.defaultCharset());
        String inputJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "input.json"), Charset.defaultCharset());
        String expectedJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "expected.json"), Charset.defaultCharset());
        DocumentSettings documentNormalizationSettings = new ObjectMapper().readValue(settingsJson, DocumentSettings.class);
        String actualJson = Normalizer.normalize(documentNormalizationSettings, inputJson);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void normalize_scenario8() throws Exception {
        String scenarioPrefix = "scenario8";
        String settingsJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "documentsettings.json"), Charset.defaultCharset());
        String inputJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "input.json"), Charset.defaultCharset());
        String expectedJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "expected.json"), Charset.defaultCharset());
        DocumentSettings documentNormalizationSettings = new ObjectMapper().readValue(settingsJson, DocumentSettings.class);
        String actualJson = Normalizer.normalize(documentNormalizationSettings, inputJson);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void normalize_scenario9() throws Exception {
        String scenarioPrefix = "scenario9";
        String settingsJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "documentsettings.json"), Charset.defaultCharset());
        String inputJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "input.json"), Charset.defaultCharset());
        String expectedJson = IOUtils.toString(NormalizerTest.class.getClassLoader().getResourceAsStream(scenarioPrefix + "/" + "expected.json"), Charset.defaultCharset());
        DocumentSettings documentNormalizationSettings = new ObjectMapper().readValue(settingsJson, DocumentSettings.class);
        String actualJson = Normalizer.normalize(documentNormalizationSettings, inputJson);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void plotNormalisedLengthReductionInPercentage() throws Exception {
        System.setProperty("json.normalizer.record.stats", "true");
        assertNotNull(Normalizer.plotNormalisedLengthReductionInPercentage());
    }
}