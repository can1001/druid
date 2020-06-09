package com.zion.druid.web;

import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class WebRestControllerTest {

    @Test
    public void druid() throws IOException {

        WebRestController rest = new WebRestController();
        ResponseEntity<Map<String, Object>> actualResult = (ResponseEntity<Map<String, Object>>) rest.druid("text");
        System.out.println(actualResult.getBody().get("shareString"));
        System.out.println(actualResult.getBody().get("restString"));
        assertThat(actualResult.getBody().get("restString"), equalTo(
                "uuuuuuuVVVVVVVVVVVVvvvvvvvvvvwwwwwwwwxxxxYyyyyyyyyyyyyyzzzzzzzzz"));

    }

}