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

import io.cdap.wrangler.api.parser.ByteSize;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ByteSizeTest {
    @Test
    public void testByteParsing() {
        ByteSize b1 = new ByteSize("1024B");
        assertEquals(1024L, b1.getBytes());
        
        ByteSize b2 = new ByteSize("1KB");
        assertEquals(1024L, b2.getBytes());
        
        ByteSize b3 = new ByteSize("1.5MB");
        assertEquals(1572864L, b3.getBytes());
        
        ByteSize b4 = new ByteSize("2GB");
        assertEquals(2147483648L, b4.getBytes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidByteFormat() {
        new ByteSize("10XB");
    }
}