package com.zion.druid.web;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
public class WebRestController {

    private Logger logger = LoggerFactory.getLogger(WebRestController.class);

    /**
     * 0. API
     * @param parsingUrl
     * @param parsingType
     * @param outputBundle
     * @return
     * @throws IOException
     */
    @GetMapping("/druid")
    public ResponseEntity<?> druid(@RequestParam(value = "parsingUrl", required = false, defaultValue = "") String parsingUrl
                      , @RequestParam(value = "parsingType", required = false, defaultValue = "") String parsingType
                      , @RequestParam(value = "outputBundle", required = false, defaultValue = "") int outputBundle) throws IOException {

        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();

        String result = "";

        // 1. Jsoup parser를 이용한 웹 페이지 크롤링
        String crawling = getCrawling(parsingUrl, parsingType);

        // 2. 오름차순 영어, 숫자만  교차 출력
        result = getAscendingOnlyEnglishNumberCrossPrint(crawling);

        // 3. 출력묶음단위출력 ( 몫, 나머지 )
        int intLength = result.length();
        int share = intLength / outputBundle;

        String shareString = "";
        String restString = "";
        if (share != 0) {
            shareString = result.substring(0, share * outputBundle -1);
            restString = result.substring(share * outputBundle);
        } else {
            restString = result;
        }

        // 3-1. 결과값( 몫, 나머지 )
        resultMap.put("shareString", shareString);
        resultMap.put("restString", restString);

        return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }

    /**
     * 2. 오름차순 영어, 숫자만  교차 출력
     * @param crawling
     * @return
     */
    public String getAscendingOnlyEnglishNumberCrossPrint(String crawling) {
        String ascendingOnlyEnglishNumberCrossPrint = "";

        logger.debug("getAscendingOnlyEnglishNumberCrossPrint : crawling=" + crawling);

        // 2-1. 영어만 출력
        String englishPrint = getExtract(crawling, "[a-zA-Z]");

        // 2-1-1. 영어 대문자 후 소문자 식으로 정렬 (오름차순 정렬)
        String[] upperCaseLowerCaseSort = getUpperCaseLowerCaseSort(englishPrint);

        // 2-2 숫자만 출력 (오름차순 정렬)
        String[] numberPrint = getAscendingNumberPrint(crawling).split("");

        // 2-3. 교차출력
        int maxLength = 0;

        if (upperCaseLowerCaseSort.length > numberPrint.length) {
            maxLength = upperCaseLowerCaseSort.length;
        } else {
            maxLength = numberPrint.length;
        }

        for (int i = 0; i < maxLength; i++) {
            if (i < upperCaseLowerCaseSort.length) {
                ascendingOnlyEnglishNumberCrossPrint += upperCaseLowerCaseSort[i];
            }
            if (i < numberPrint.length) {
                ascendingOnlyEnglishNumberCrossPrint += numberPrint[i];
            }
        }

        logger.debug("getAscendingOnlyEnglishNumberCrossPrint : ascendingOnlyEnglishNumberCrossPrint=" + ascendingOnlyEnglishNumberCrossPrint);

        return ascendingOnlyEnglishNumberCrossPrint;
    }

    /**
     * 1. Jsoup parser를 이용한 웹 페이지 크롤링
     * @param parsingUrl
     * @param parsingType
     * @return
     * @throws IOException
     */
    private String getCrawling(@RequestParam(value = "parsingUrl", required = false, defaultValue = "") String parsingUrl
                             , @RequestParam(value = "parsingType", required = false, defaultValue = "") String parsingType) throws IOException {
        String result;

        // 1. Jsoup parser를 이용한 웹 페이지 크롤링
        logger.debug("getCrawling : parsingUrl=" + parsingUrl);

        Document doc = Jsoup.connect(parsingUrl).get();

        logger.debug("getCrawling : parsingType=" + parsingType);

        if ( parsingType.equals("html") ) {
            result = doc.html();
        } else {
            result = doc.text();
        }

        return result;
    }

    /**
     * 2. 영어, 숫자만 출력
     * @param result
     * @param regex
     * @return
     */
    private String getExtract(String result, String regex) {
        Pattern nonValidPattern = Pattern.compile(regex);
        Matcher matcher = nonValidPattern.matcher(result);

        String extract = "";

        while (matcher.find()) {
            extract += matcher.group();
        }

        return extract;
    }

    /**
     * 2-1-1. 영어 대문자 후 소문자 식으로 정렬
     * @param x
     * @return
     */
    public String[] getUpperCaseLowerCaseSort(String x) {
        String[] arr = new String[x.length()];
        for (int i = 0; i < x.length(); i++) {
            arr[i] = x.substring(i, i + 1);
        }

        Arrays.sort(arr, (o1, o2) -> {
            if (o1.equalsIgnoreCase(o2)) {
                if (o1.toUpperCase().equals(o1)) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                return o1.compareToIgnoreCase(o2);
            }
        });

//        for (String item : arr) {
//            System.out.print(item);
//        }

        return arr;
    }

    /**
     * 2-2 숫자만 출력(오름차순)
     * @param result
     * @return
     */
    private String getAscendingNumberPrint(String result) {
        String numberPrint = "";

        IntStream stream = result.chars();
        String intStr = stream.filter((ch)-> (48 <= ch && ch <= 57))
                .mapToObj(ch -> (char)ch)
                .map(Object::toString)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.joining());

        logger.debug("getAscendingNumberPrint : numberPrint=" + intStr);

        numberPrint = intStr;

        return numberPrint;
    }

    @GetMapping("/hello")
    public String hello() throws IOException {

        // 1. Jsoup parser를 이용한 웹 페이지 크롤링
        String URL = "https://m.blog.naver.com/PostView.nhn?blogId=sky930425&logNo=221562017606&proxyReferer=https:%2F%2Fwww.google.com%2F";
        Document doc = Jsoup.connect(URL).get();

        doc.text();
        doc.html();

        return doc.text();

    }

}
