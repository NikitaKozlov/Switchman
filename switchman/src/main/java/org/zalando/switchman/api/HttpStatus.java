/*
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// copied from package org.springframework.http
package org.zalando.switchman.api;

public enum HttpStatus {

    /**
     * {@code 200 OK}.
     *
     * @see  <a href="http://tools.ietf.org/html/rfc2616#section-10.2.1">HTTP/1.1</a>
     */
    OK(200),
    /**
     * {@code 201 Created}.
     *
     * @see  <a href="http://tools.ietf.org/html/rfc2616#section-10.2.2">HTTP/1.1</a>
     */
    CREATED(201),
    /**
     * {@code 202 Accepted}.
     *
     * @see  <a href="http://tools.ietf.org/html/rfc2616#section-10.2.3">HTTP/1.1</a>
     */
    ACCEPTED(202),
    /**
     * {@code 203 Non-Authoritative Information}.
     *
     * @see  <a href="http://tools.ietf.org/html/rfc2616#section-10.2.4">HTTP/1.1</a>
     */
    NON_AUTHORITATIVE_INFORMATION(203),
    /**
     * {@code 204 No Content}.
     *
     * @see  <a href="http://tools.ietf.org/html/rfc2616#section-10.2.5">HTTP/1.1</a>
     */
    NO_CONTENT(204),

    /**
     * {@code 400 Bad Request}.
     *
     * @see  <a href="http://tools.ietf.org/html/rfc2616#section-10.4.1">HTTP/1.1</a>
     */
    BAD_REQUEST(400),
    /**
     * {@code 404 Not Found}.
     *
     * @see  <a href="http://tools.ietf.org/html/rfc2616#section-10.4.5">HTTP/1.1</a>
     */
    NOT_FOUND(404),
    /**
     * {@code 409 Conflict}.
     *
     * @see  <a href="http://tools.ietf.org/html/rfc2616#section-10.4.10">HTTP/1.1</a>
     */
    CONFLICT(409),
    /**
     * {@code 410 Gone}.
     *
     * @see  <a href="http://tools.ietf.org/html/rfc2616#section-10.4.11">HTTP/1.1</a>
     */
    GONE(410);

    private final int value;

    HttpStatus(final int value) {
        this.value = value;
    }

    /**
     * Return the integer value of this status code.
     */
    public int value() {
        return this.value;
    }
}
