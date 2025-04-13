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

import io.cdap.wrangler.api.*;
import io.cdap.wrangler.api.parser.*;
import io.cdap.wrangler.executor.ExecutorContext;
import io.cdap.wrangler.api.Executor;
import io.cdap.wrangler.api.Row;

import java.util.List;
import java.util.ArrayList;

@Plugin(type = Directive.TYPE)
@Name("aggregate-stats")
@Categories(categories = {"aggregate"})
@Description("Aggregates byte size and time duration columns into totals")
public class AggregateStats implements Directive, Executor {
    private String sizeColumn;
    private String timeColumn;
    private String outputSizeColumn;
    private String outputTimeColumn;
    private String outputSizeUnit = "MB";
    private String outputTimeUnit = "s";
    private String aggregationType = "total";

    private long totalBytes = 0;
    private long totalNanoseconds = 0;
    private int rowCount = 0;

    @Override
    public UsageDefinition define() {
        UsageDefinition.Builder builder = UsageDefinition.builder("aggregate-stats");
        builder.define("size-column", TokenType.COLUMN_NAME);
        builder.define("time-column", TokenType.COLUMN_NAME);
        builder.define("output-size-column", TokenType.COLUMN_NAME);
        builder.define("output-time-column", TokenType.COLUMN_NAME);
        builder.define("size-unit", TokenType.IDENTIFIER, true);
        builder.define("time-unit", TokenType.IDENTIFIER, true);
        builder.define("aggregation", TokenType.IDENTIFIER, true);
        return builder.build();
    }

    @Override
    public void initialize(Arguments args) throws DirectiveParseException {
        this.sizeColumn = ((ColumnName) args.value("size-column").value()).value();
        this.timeColumn = ((ColumnName) args.value("time-column").value()).value();
        this.outputSizeColumn = ((ColumnName) args.value("output-size-column").value()).value();
        this.outputTimeColumn = ((ColumnName) args.value("output-time-column").value()).value();

        if (args.contains("size-unit")) {
            this.outputSizeUnit = ((Identifier) args.value("size-unit").value()).value();
        }
        if (args.contains("time-unit")) {
            this.outputTimeUnit = ((Identifier) args.value("time-unit").value()).value();
        }
        if (args.contains("aggregation")) {
            this.aggregationType = ((Identifier) args.value("aggregation").value()).value();
        }
    }

    @Override
    public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
        for (Row row : rows) {
            int sizeIdx = row.find(sizeColumn);
            int timeIdx = row.find(timeColumn);

            if (sizeIdx == -1 || timeIdx == -1) {
                continue;
            }

            try {
                // Parse byte size
                String sizeStr = row.getValue(sizeIdx).toString();
                ByteSize byteSize = new ByteSize(sizeStr);
                totalBytes += byteSize.getBytes();

                // Parse time duration
                String timeStr = row.getValue(timeIdx).toString();
                TimeDuration timeDuration = new TimeDuration(timeStr);
                totalNanoseconds += timeDuration.getNanoseconds();

                rowCount++;
            } catch (Exception e) {
                throw new DirectiveExecutionException(
                    String.format("Error parsing values in row %d: %s", rowCount + 1, e.getMessage()), e);
            }
        }
        return rows;
    }

    @Override
    public List<Row> finalize() throws DirectiveExecutionException {
        Row result = new Row();
        
        // Calculate size in requested unit
        double outputSize;
        switch (outputSizeUnit.toLowerCase()) {
            case "b":
                outputSize = totalBytes;
                break;
            case "kb":
                outputSize = totalBytes / 1024.0;
                break;
            case "mb":
                outputSize = totalBytes / (1024.0 * 1024.0);
                break;
            case "gb":
                outputSize = totalBytes / (1024.0 * 1024.0 * 1024.0);
                break;
            case "tb":
                outputSize = totalBytes / (1024.0 * 1024.0 * 1024.0 * 1024.0);
                break;
            default:
                throw new DirectiveExecutionException("Unsupported size unit: " + outputSizeUnit);
        }

        // Calculate time in requested unit
        double outputTime;
        switch (outputTimeUnit.toLowerCase()) {
            case "ns":
                outputTime = totalNanoseconds;
                break;
            case "us":
                outputTime = totalNanoseconds / 1000.0;
                break;
            case "ms":
                outputTime = totalNanoseconds / 1000000.0;
                break;
            case "s":
            case "sec":
                outputTime = totalNanoseconds / 1000000000.0;
                break;
            case "m":
            case "min":
                outputTime = totalNanoseconds / (60.0 * 1000000000.0);
                break;
            case "h":
            case "hr":
                outputTime = totalNanoseconds / (60.0 * 60.0 * 1000000000.0);
                break;
            case "d":
            case "day":
                outputTime = totalNanoseconds / (24.0 * 60.0 * 60.0 * 1000000000.0);
                break;
            default:
                throw new DirectiveExecutionException("Unsupported time unit: " + outputTimeUnit);
        }

        // Apply aggregation type if needed
        if ("average".equalsIgnoreCase(aggregationType) && rowCount > 0) {
            outputSize /= rowCount;
            outputTime /= rowCount;
        }

        result.add(outputSizeColumn, outputSize);
        result.add(outputTimeColumn, outputTime);

        List<Row> results = new ArrayList<>();
        results.add(result);
        return results;
    }

    @Override
    public void destroy() {
        // Clean up if needed
    }
}