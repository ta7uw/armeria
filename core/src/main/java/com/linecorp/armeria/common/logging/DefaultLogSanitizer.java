/*
 * Copyright 2022 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.linecorp.armeria.common.logging;

import java.util.function.BiFunction;

import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.RequestContext;
import com.linecorp.armeria.common.annotation.Nullable;

/**
 *
 */
public class DefaultLogSanitizer implements LogSanitizer {

    private static <T> String sanitize(
            RequestContext ctx,
            BiFunction<? super RequestContext, ? super T, ? extends @Nullable Object> sanitizer,
            T input) {
        final Object sanitized = sanitizer.apply(ctx, input);
        return sanitized != null ? sanitized.toString() : "<sanitized>";
    }

    BiFunction<? super RequestContext, ? super HttpHeaders,
            ? extends @Nullable Object> headersSanitizer;
    BiFunction<? super RequestContext, Object,
            ? extends @Nullable Object> contentSanitizer;
    BiFunction<? super RequestContext, ? super HttpHeaders,
            ? extends @Nullable Object> trailersSanitizer;

    public DefaultLogSanitizer(
            BiFunction<? super RequestContext, ? super HttpHeaders,
                    ? extends @Nullable Object> headersSanitizer,
            BiFunction<? super RequestContext, Object,
                    ? extends @Nullable Object> contentSanitizer,
            BiFunction<? super RequestContext, ? super HttpHeaders,
                    ? extends @Nullable Object> trailersSanitizer
    ) {
        this.headersSanitizer = headersSanitizer;
        this.contentSanitizer = contentSanitizer;
        this.trailersSanitizer = trailersSanitizer;
    }

    @Override
    public String sanitizeHeaders(RequestContext ctx, HttpHeaders headers) {
        return sanitize(ctx, headersSanitizer, headers);
    }

    @Override
    public String sanitizeContent(RequestContext ctx, Object object) {
        return sanitize(ctx, contentSanitizer, object);
    }

    @Override
    public String sanitizeTrailers(RequestContext ctx, HttpHeaders trailers) {
        return sanitize(ctx, trailersSanitizer, trailers);
    }
}
