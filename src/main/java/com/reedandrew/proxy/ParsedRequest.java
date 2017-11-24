package com.reedandrew.proxy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author areed.
 */
@AllArgsConstructor
public class ParsedRequest {

    @Getter
    @Accessors(fluent = true)
    private String fullRequest;

    @Getter
    @Accessors(fluent = true)
    private String url;
}
