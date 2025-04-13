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

package io.cdap.wrangler.parse;

import io.cdap.wrangler.api.parser.Token;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.parser.Column;
import io.cdap.wrangler.api.parser.StringToken;
import io.cdap.wrangler.api.parser.IntegerToken;
import io.cdap.wrangler.api.parser.BooleanToken;
import io.cdap.wrangler.grammar.DirectivesBaseVisitor;
import io.cdap.wrangler.grammar.DirectivesParser;

public class RecipeVisitor extends DirectivesBaseVisitor<Token> {

    @Override
    public Token visitByteSizeArg(DirectivesParser.ByteSizeArgContext ctx) {
        return new ByteSize(ctx.getText());
    }

    @Override
    public Token visitTimeDurationArg(DirectivesParser.TimeDurationArgContext ctx) {
        return new TimeDuration(ctx.getText());
    }

    @Override
    public Token visitValue(DirectivesParser.ValueContext ctx) {
        if (ctx.BYTE_SIZE() != null) {
            return new ByteSize(ctx.getText());
        } else if (ctx.TIME_DURATION() != null) {
            return new TimeDuration(ctx.getText());
        } else if (ctx.COLUMN_NAME() != null) {
            return new Column(ctx.getText());
        } else if (ctx.STRING() != null) {
            return new StringToken(ctx.getText());
        } else if (ctx.INT() != null) {
            return new IntegerToken(ctx.getText());
        } else if (ctx.BOOL() != null) {
            return new BooleanToken(ctx.getText());
        }
        return super.visitValue(ctx);  // Fallback
    }
    @Override
    public Token visitBytesizeExpr(DirectivesParser.BytesizeExprContext ctx) {
    return new ByteSize(ctx.BYTESIZE().getText());
    }

    @Override
    public Token visitTimedurationExpr(DirectivesParser.TimedurationExprContext ctx) {
    return new TimeDuration(ctx.TIMEDURATION().getText());
    }

}
