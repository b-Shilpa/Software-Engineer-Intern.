/*
 * Copyright © 2025 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.wrangler.parser;

import io.cdap.wrangler.api.parser.TimeDuration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TimeDurationTest {
    @Test
    public void testTimeParsing() {
        TimeDuration t1 = new TimeDuration("1000ns");
        assertEquals(1000L, t1.getNanoseconds());
        
        TimeDuration t2 = new TimeDuration("500us");
        assertEquals(500_000L, t2.getNanoseconds());
        
        TimeDuration t3 = new TimeDuration("100ms");
        assertEquals(100_000_000L, t3.getNanoseconds());
        
        TimeDuration t4 = new TimeDuration("5s");
        assertEquals(5_000_000_000L, t4.getNanoseconds());
        
        TimeDuration t5 = new TimeDuration("2m");
        assertEquals(120_000_000_000L, t5.getNanoseconds());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTimeFormat() {
        new TimeDuration("10xs");
    }
}