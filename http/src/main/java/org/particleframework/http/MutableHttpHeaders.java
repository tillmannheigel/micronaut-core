/*
 * Copyright 2017 original authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.particleframework.http;

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Extends {@link HttpHeaders} add methods for mutation of headers
 *
 * @author Graeme Rocher
 * @since 1.0
 */
public interface MutableHttpHeaders extends HttpHeaders {

    /**
     * Add a header for the given name and value
     *
     * @param header The head name
     * @param value The value
     * @return This headers object
     */
    MutableHttpHeaders add(CharSequence header, CharSequence value);

    /**
     * Set the allowed HTTP methods
     *
     * @param methods The methods to specify in the Allowed HTTP header
     * @return This HTTP headers
     */
    default MutableHttpHeaders allow(HttpMethod...methods) {
        return allow(Arrays.asList(methods));
    }

    /**
     * Set the allowed HTTP methods
     *
     * @param methods The methods to specify in the Allowed HTTP header
     * @return This HTTP headers
     */
    default MutableHttpHeaders allow(Collection<HttpMethod> methods) {
        String value = methods.stream().distinct().collect(Collectors.joining(","));
        return add(HttpHeaders.ALLOW, value);
    }

    /**
     * Sets the location header to the given URI
     *
     * @param uri The URI
     * @return This HTTP headers
     */
    default MutableHttpHeaders location(URI uri) {
        return add(HttpHeaders.LOCATION, uri.toString());
    }
    /**
     * Sets the {@link HttpHeaders#CONTENT_TYPE} header to the given media type
     *
     * @param mediaType The media type
     * @return This HTTP headers
     */
    default MutableHttpHeaders contentType(MediaType mediaType) {
        return add(HttpHeaders.CONTENT_TYPE, mediaType);
    }


    /**
     * Add a header for the given name and value
     *
     * @param header The head name
     * @param value The value
     * @return This headers object
     */
    default MutableHttpHeaders add(CharSequence header, ZonedDateTime value) {
        if(header != null && value != null) {
            add(header, value.format(DateTimeFormatter.RFC_1123_DATE_TIME));
        }
        return this;
    }

    /**
     * Add a header for the given name and value
     *
     * @param header The head name
     * @param value The value
     * @return This headers object
     */
    default MutableHttpHeaders add(CharSequence header, Integer value) {
        if(header != null && value != null) {
            return add(header, value.toString());
        }
        return this;
    }
}