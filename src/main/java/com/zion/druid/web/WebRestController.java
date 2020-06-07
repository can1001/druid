package com.zion.druid.web;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

    @GetMapping("/hello")
    public String hello() throws IOException {

        // 1. Jsoup parser를 이용한 웹 페이지 크롤링
        String URL = "https://m.blog.naver.com/PostView.nhn?blogId=sky930425&logNo=221562017606&proxyReferer=https:%2F%2Fwww.google.com%2F";
        Document doc = Jsoup.connect(URL).get();

        doc.text();
        doc.html();

        return doc.text();
//        return "HelloWorld";
    }

    /**
     * 00
     * @param parsingUrl
     * @param parsingType
     * @param outputBundle
     * @return
     * @throws IOException
     */
    @GetMapping("/druid")
    public Object druid(@RequestParam(value = "type", required = false, defaultValue = "") String type) throws IOException {
        JSONPObject responseJson = null;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();

        String result = "";

        // 1. Jsoup parser를 이용한 웹 페이지 크롤링
        String crawling = getCrawling(type);

        // 2. 영어, 숫자만 출력
        // 2-1. 영어만 출력
        String englishPrint = getExtract(crawling, "[a-zA-Z]");

        // 2-1-1. 영어 대문자 후 소문자 식으로 정렬
        String[] upperCaseLowerCaseSort = getUpperCaseLowerCaseSort(englishPrint);

        // 2-2 숫자만 출력
        String[] numberPrint = getAscendingNumberPrint(crawling).split("");

        // 4. 교차출력
        int maxLength = 0;

        if (upperCaseLowerCaseSort.length > numberPrint.length) {
            maxLength = upperCaseLowerCaseSort.length;
        } else {
            maxLength = numberPrint.length;
        }

        for (int i = 0; i < maxLength; i++) {
            if (i < upperCaseLowerCaseSort.length) {
                result += upperCaseLowerCaseSort[i];
            }
            if (i < numberPrint.length) {
                result += numberPrint[i];
            }
        }

        // 5. 출력묶음단위출력
        int intLength = result.length();
        int outputBundle = 100;
        int share = intLength / outputBundle;
        int rest  = intLength % outputBundle;

        String shareString = "";
        String restString = "";
        if (share != 0) {
            shareString = result.substring(0, share * outputBundle -1);
            restString = result.substring(share * outputBundle);
        } else {
            restString = result;
        }

        // 결과값( 몫, 나머지 )
        resultMap.put("shareString", shareString);
        resultMap.put("restString", restString);

        return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }

    private String[] getUpperCaseLowerCaseSort(String x) {
        x = "AAAaaaBb";

        String[] arr = new String[x.length()];
        // String[] arr2 = new String[x.length()];



        for (int i = 0; i < x.length(); i++) {
            arr[i] = x.substring(i, i + 1);
            // arr2[i] = x.substring(i, i + 1);
        }

        char[] ch = x.toCharArray();
        // System.out.print(ch);

        // Arrays.sort(arr, (o1, o2) -> o1.compareToIgnoreCase(o2));

        final boolean[] blnCase = {true};
        Arrays.sort(arr, (o1, o2) -> {
            if (o1.equalsIgnoreCase(o2)) {
                if (blnCase[0]) {
                    if (o1.toUpperCase().equals(o1)) {
                        blnCase[0] = true;
                        return -1;
                    } else {
                        blnCase[0] = false;
                        return 1;
                    }
                } else {
                    if (o1.toUpperCase().equals(o1)) {
                        blnCase[0] = false;
                        return 1;
                    } else {
                        blnCase[0] = true;
                        return -1;
                    }
                }
            } else {
                return o1.compareToIgnoreCase(o2);
            }
        });

        for (String item : arr) {
            System.out.print(item);
        }


        return arr;
    }

    /**
     * 1. Jsoup parser를 이용한 웹 페이지 크롤링
     * @param parsingUrl
     * @param parsingType
     * @param outputBundle
     * @return
     * @throws IOException
     */
    private String getCrawling(@RequestParam(value = "parsingType", required = false, defaultValue = "") String parsingType) throws IOException {
        String result;

        // 1. Jsoup parser를 이용한 웹 페이지 크롤링
        String URL = "https://m.blog.naver.com/PostView.nhn?blogId=sky930425&logNo=221562017606&proxyReferer=https:%2F%2Fwww.google.com%2F";
        Document doc = Jsoup.connect(URL).get();

        // System.out.println(type);

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
     * 2-2 숫자만 출력
     * @param result
     * @return
     */
    private String getNumberPrint_(String result) {
        String numberPrint = "";

        IntStream stream = result.chars();
        String intStr = stream.filter((ch)-> (48 <= ch && ch <= 57))
                .mapToObj(ch -> (char)ch)
                .map(Object::toString)
                .collect(Collectors.joining())
                // .compareTo()
        ;

        System.out.println(intStr);

        numberPrint = intStr;

        return numberPrint;
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

        // System.out.println(intStr);

        numberPrint = intStr;

        return numberPrint;
    }

}
