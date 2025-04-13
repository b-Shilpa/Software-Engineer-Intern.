/*
 * Copyright © 2025 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.cdap.wrangler.api.parser;

import io.cdap.wrangler.api.annotations.PublicEvolving;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PublicEvolving
public class TimeDuration extends Token {
    private static final Pattern DURATION_PATTERN = 
        Pattern.compile("^(\\d+(?:\\.\\d+)?)\\s*(ns|us|ms|s|sec|m|min|h|hr|d|day|days)$", Pattern.CASE_INSENSITIVE);
    private final long nanoseconds;

    public TimeDuration(String value) {
        super(TokenType.TIME_DURATION, value);
        Matcher matcher = DURATION_PATTERN.matcher(value.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format(
                "Invalid time duration format '%s'. Expected format like '10ms', '5.5s'.", value));
        }

        double amount = Double.parseDouble(matcher.group(1));
        String unit = matcher.group(2).toLowerCase();

        switch (unit) {
            case "ns":
                nanoseconds = (long) amount;
                break;
            case "us":
                nanoseconds = (long) (amount * 1000);
                break;
            case "ms":
                nanoseconds = (long) (amount * 1000_000);
                break;
            case "s":
            case "sec":
                nanoseconds = (long) (amount * 1000_000_000);
                break;
            case "m":
            case "min":
                nanoseconds = (long) (amount * 60 * 1000_000_000L);
                break;
            case "h":
            case "hr":
                nanoseconds = (long) (amount * 60 * 60 * 1000_000_000L);
                break;
            case "d":
            case "day":
            case "days":
                nanoseconds = (long) (amount * 24 * 60 * 60 * 1000_000_000L);
                break;
            default:
                throw new IllegalArgumentException("Unsupported time unit: " + unit);
        }
    }

    public long getNanoseconds() {
        return nanoseconds;
    }

    public double getMilliseconds() {
        return nanoseconds / 1_000_000.0;
    }

    public double getSeconds() {
        return nanoseconds / 1_000_000_000.0;
    }

    public double getMinutes() {
        return nanoseconds / (60.0 * 1_000_000_000.0);
    }

    public double getHours() {
        return nanoseconds / (60.0 * 60.0 * 1_000_000_000.0);
    }

    public double getDays() {
        return nanoseconds / (24.0 * 60.0 * 60.0 * 1_000_000_000.0);
    }
}