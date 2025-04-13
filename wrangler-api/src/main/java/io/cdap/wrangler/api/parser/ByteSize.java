/*
 * Copyright © 2025 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.cdap.wrangler.api.parser;

import io.cdap.wrangler.api.annotations.PublicEvolving;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PublicEvolving
public class ByteSize extends Token {
    private static final Pattern BYTE_PATTERN = Pattern.compile("^(\\d+(?:\\.\\d+)?)\\s*([KMGTP]?B)$", Pattern.CASE_INSENSITIVE);
    private final long bytes;

    public ByteSize(String value) {
        super(TokenType.BYTE_SIZE, value);
        Matcher matcher = BYTE_PATTERN.matcher(value.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("Invalid byte size format '%s'. Expected format like '10KB', '5.5MB'.", value));
        }

        double size = Double.parseDouble(matcher.group(1));
        String unit = matcher.group(2).toUpperCase();

        switch (unit) {
            case "B":
                bytes = (long) size;
                break;
            case "KB":
                bytes = (long) (size * 1024);
                break;
            case "MB":
                bytes = (long) (size * 1024 * 1024);
                break;
            case "GB":
                bytes = (long) (size * 1024 * 1024 * 1024);
                break;
            case "TB":
                bytes = (long) (size * 1024 * 1024 * 1024 * 1024);
                break;
            default:
                throw new IllegalArgumentException("Unsupported byte unit: " + unit);
        }
    }

    public long getBytes() {
        return bytes;
    }

    public double getKB() {
        return bytes / 1024.0;
    }

    public double getMB() {
        return bytes / (1024.0 * 1024.0);
    }

    public double getGB() {
        return bytes / (1024.0 * 1024.0 * 1024.0);
    }

    public double getTB() {
        return bytes / (1024.0 * 1024.0 * 1024.0 * 1024.0);
    }
}