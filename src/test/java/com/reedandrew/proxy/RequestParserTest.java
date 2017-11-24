package com.reedandrew.proxy;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RequestParserTest {

    @Test
    public void testGetMethodWithNoPathHttp10() {
        String expected = "GET / HTTP/1.0\r\nHost: www.google.com\r\nConnection: close\r\n\r\n";
        List<String> headerLines = new ArrayList<>();
        headerLines.add("GET http://www.google.com HTTP/1.0");
        Assert.assertEquals(expected, RequestParser.parseHeader(headerLines).fullRequest());
    }

    @Test
    public void testGetMethodWithPathHttp10() {
        String expected = "GET /index.html HTTP/1.0\r\nHost: www.google.com\r\nConnection: close\r\n\r\n";
        List<String> headerLines = new ArrayList<>();
        headerLines.add("GET http://www.google.com/index.html HTTP/1.0");
        Assert.assertEquals(expected, RequestParser.parseHeader(headerLines).fullRequest());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonGetMethodThrowsException() {
        List<String> headerLines = new ArrayList<>();
        headerLines.add("POST http://www.google.com/index.html HTTP/1.0");
        RequestParser.parseHeader(headerLines);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotHttp10ThrowsException() {
        List<String> headerLines = new ArrayList<>();
        headerLines.add("GET http://www.google.com/index.html HTTP/1.1");
        RequestParser.parseHeader(headerLines);
    }

    @Test
    public void testGetMethodWithNoPathHttp10WithPort() {
        String expected = "GET / HTTP/1.0\r\nHost: www.google.com:8080\r\nConnection: close\r\n\r\n";
        List<String> headerLines = new ArrayList<>();
        headerLines.add("GET http://www.google.com:8080 HTTP/1.0");
        Assert.assertEquals(expected, RequestParser.parseHeader(headerLines).fullRequest());
    }

    @Test
    public void testGetMethodWithPathHttp10WithPort() {
        String expected = "GET /index.html HTTP/1.0\r\nHost: www.google.com:8080\r\nConnection: close\r\n\r\n";
        List<String> headerLines = new ArrayList<>();
        headerLines.add("GET http://www.google.com:8080/index.html HTTP/1.0");
        Assert.assertEquals(expected, RequestParser.parseHeader(headerLines).fullRequest());
    }

    @Test
    public void testConnectionHeaderOverwritten() {
        String expected = "GET /index.html HTTP/1.0\r\nHost: www.google.com:8080\r\nConnection: close\r\n\r\n";
        List<String> headerLines = new ArrayList<>();
        headerLines.add("GET http://www.google.com:8080/index.html HTTP/1.0");
        headerLines.add("Connection: keep-alive");
        Assert.assertEquals(expected, RequestParser.parseHeader(headerLines).fullRequest());
    }

    @Test
    public void testNonConnectionHeadersRetained() {
        String expected = "GET /index.html HTTP/1.0\r\nHost: www.google.com:8080\r\nHeader1: test\r\nHeader2: test2\r\nConnection: close\r\n\r\n";
        List<String> headerLines = new ArrayList<>();
        headerLines.add("GET http://www.google.com:8080/index.html HTTP/1.0");
        headerLines.add("Connection: keep-alive");
        headerLines.add("Header1: test");
        headerLines.add("Header2: test2");
        Assert.assertEquals(expected, RequestParser.parseHeader(headerLines).fullRequest());
    }

    @Test
    public void testWashingtonEdu() {
        List<String> headerLines = new ArrayList<>();
        headerLines.add("GET http://www.washington.edu/ HTTP/1.0");
        headerLines.add("Host: www.washington.edu");
        headerLines.add("Connection: keep-alive");
        String expected = "GET / HTTP/1.0\r\nHost: www.washington.edu\r\nConnection: close\r\n\r\n";
        Assert.assertEquals(expected, RequestParser.parseHeader(headerLines).fullRequest());
    }

    @Test
    public void testLongPath() {
        List<String> headerLines = new ArrayList<>();
        headerLines.add("GET http://www.washington.edu/static/home/wp-content/themes/uw-2014/js/libraries/jquery.min.js?ec3099f HTTP/1.0");
        String expected = "GET /static/home/wp-content/themes/uw-2014/js/libraries/jquery.min.js?ec3099f HTTP/1.0\r\nHost: www.washington.edu\r\nConnection: close\r\n\r\n";
        Assert.assertEquals(expected, RequestParser.parseHeader(headerLines).fullRequest());
    }

}
