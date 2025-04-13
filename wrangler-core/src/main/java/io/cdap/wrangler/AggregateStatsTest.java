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
package io.cdap.wrangler.directives.aggregates;

import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.executor.ExecutorContext;
import io.cdap.wrangler.parser.GrammarBasedParser;
import io.cdap.wrangler.parser.RecipeParser;
import io.cdap.wrangler.parser.TokenGroup;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class AggregateStatsTest {
    @Test
    public void testAggregateStats() throws Exception {
        // Create sample data
        List<Row> rows = Arrays.asList(
            new Row("file", "file1.txt").add("size", "10KB").add("time", "100ms"),
            new Row("file", "file2.txt").add("size", "1.5MB").add("time", "2.5s"),
            new Row("file", "file3.txt").add("size", "500B").add("time", "50ms")
        );

        // Define recipe
        String[] recipe = {
            "aggregate-stats :size :time total_size_mb total_time_sec"
        };

        // Execute
        RecipeParser parser = new GrammarBasedParser(Arrays.asList(recipe));
        List<Row> results = parser.parse().execute(rows, new ExecutorContext());

        // Verify
        Assert.assertEquals(1, results.size());
        Row result = results.get(0);
        
        // 10KB = 10240B, 1.5MB = 1572864B, 500B = 500B → Total = 1583604B = 1.5103MB
        Assert.assertEquals(1.5103, (double) result.getValue("total_size_mb"), 0.0001);
        
        // 100ms = 0.1s, 2.5s = 2.5s, 50ms = 0.05s → Total = 2.65s
        Assert.assertEquals(2.65, (double) result.getValue("total_time_sec"), 0.0001);
    }

    @Test
    public void testAggregateStatsWithAverage() throws Exception {
        // Create sample data
        List<Row> rows = Arrays.asList(
            new Row("file", "file1.txt").add("size", "10KB").add("time", "100ms"),
            new Row("file", "file2.txt").add("size", "1.5MB").add("time", "2.5s"),
            new Row("file", "file3.txt").add("size", "500B").add("time", "50ms")
        );

        // Define recipe
        String[] recipe = {
            "aggregate-stats :size :time avg_size_kb avg_time_ms average"
        };

        // Execute
        RecipeParser parser = new GrammarBasedParser(Arrays.asList(recipe));
        List<Row> results = parser.parse().execute(rows, new ExecutorContext());

        // Verify
        Assert.assertEquals(1, results.size());
        Row result = results.get(0);
        
        // Total bytes = 1583604B / 3 = 527868B = 515.496KB
        Assert.assertEquals(515.496, (double) result.getValue("avg_size_kb"), 0.001);
        
        // Total time = 2.65s = 2650ms / 3 = 883.333ms
        Assert.assertEquals(883.333, (double) result.getValue("avg_time_ms"), 0.001);
    }
}