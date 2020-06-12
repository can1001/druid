package com.zion.druid.web;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class WebRestControllerTest {

    private Logger logger = LoggerFactory.getLogger(WebRestControllerTest.class);
    private WebRestController restController;

    @Before
    public void create() {
        restController = new WebRestController();
    }

    @Test
    public void druid() throws IOException {
        String parsingUrl   = "https://m.blog.naver.com/PostView.nhn?blogId=sky930425&logNo=221562017606&proxyReferer=https:%2F%2Fwww.google.com%2F";
        String parsingType  = "text";
        int outputBundle    = 100;

        ResponseEntity<Map<String, Object>> actualResult = (ResponseEntity<Map<String, Object>>) restController.druid(parsingUrl, parsingType, outputBundle);
        logger.debug("druid : shareString=" + String.valueOf(actualResult.getBody().get("shareString")));
        logger.debug("druid : restString=" + String.valueOf(actualResult.getBody().get("restString")));
        assertThat(actualResult.getBody().get("restString"), equalTo(
                "uuuuuuuVVVVVVVVVVVVvvvvvvvvvvwwwwwwwwxxxxYyyyyyyyyyyyyyzzzzzzzzz"));
    }

    @Test
    public void matchAscendingOnlyEnglishNumberCrossPrint() {
        testAscendingOnlyEnglishNumberCrossPrint("@#01A1Aab2A?", "A0A1A1a2b");
    }

    private void testAscendingOnlyEnglishNumberCrossPrint(String input, String match) {
        logger.debug("testAscendingOnlyEnglishNumberCrossPrint : input=" + input);
        logger.debug("testAscendingOnlyEnglishNumberCrossPrint : match=" + match);
        String ascendingOnlyEnglishNumberCrossPrint = restController.getAscendingOnlyEnglishNumberCrossPrint(input);
        assertThat(ascendingOnlyEnglishNumberCrossPrint, equalTo(match));
    }

    @Test
    public void matchUpperCaseLowerCaseSort() throws IOException {
        testUpperCaseLowerCaseSort("AAAaaaBb", "AAAaaaBb");
        testUpperCaseLowerCaseSort("AAAaaabB", "AAAaaaBb");
        testUpperCaseLowerCaseSort("aaaBbAAA", "AAAaaaBb");
        testUpperCaseLowerCaseSort("aaabBAAA", "AAAaaaBb");
        testUpperCaseLowerCaseSort("AaAaAaBb", "AAAaaaBb");
    }

    private void testUpperCaseLowerCaseSort(String input, String match) {
        logger.debug("testUpperCaseLowerCaseSort : input=" + input);
        logger.debug("testUpperCaseLowerCaseSort : match=" + match);
        String[] upperCaseLowerCaseSort = restController.getUpperCaseLowerCaseSort(input);
        assertEquals(String.join("", upperCaseLowerCaseSort), match);
    }

}