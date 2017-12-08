package com.reedandrew.proxy;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public abstract class RequestParser {

    private static final Pattern FIRST_LINE_PATTERN = Pattern.compile("(GET) http://(.*) (HTTP/1.0)");

    public static ParsedRequest parseHeader(List<String> lines) {
        if (lines.size() <= 0) {
            throw new IllegalArgumentException("Header was empty!");
        }
        StringBuilder finalHeader = new StringBuilder();

        String domain = "";

        String firstLine = lines.get(0);
        Matcher m = FIRST_LINE_PATTERN.matcher(firstLine);
        if (m.matches()) {
            String method = m.group(1);
            String url = m.group(2);
            String version = m.group(3);
            String path;

            String[] urlParts = url.split("/", 2);
            if (urlParts.length == 1) {
                domain = urlParts[0];
                path = "/";
            } else if (urlParts.length == 2) {
                domain = urlParts[0];
                path = "/" + urlParts[1];
            } else {
                log.error(firstLine);
                throw new IllegalArgumentException("Header not formatted correctly!");
            }

            finalHeader.append(method);
            finalHeader.append(" ");
            if (StringUtils.isEmpty(path)) {
                finalHeader.append("/");
            } else {
                finalHeader.append(path);
            }
            finalHeader.append(" ");
            finalHeader.append(version);
            finalHeader.append("\r\n");

            finalHeader.append("Host: ");
            finalHeader.append(domain);
            finalHeader.append("\r\n");

        } else {
            throw new IllegalArgumentException("Header not formatted correctly!");
        }

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.toLowerCase().startsWith("connection") && !line.toLowerCase().startsWith("host")) {
                finalHeader.append(line);
                finalHeader.append("\r\n");
            }
        }

        finalHeader.append("Connection: close");
        finalHeader.append("\r\n");
        finalHeader.append("\r\n");

        return new ParsedRequest(finalHeader.toString(), domain);
    }
}
