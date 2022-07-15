/*
 * MIT License
 *
 * Copyright (c) 2022 Luke Bemish
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.lukebemish.codecutils.api;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonGrammar;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Writer;

public class SmarterJanksonWriter {
    public static final SmarterJanksonWriter JSON5_2_SPACES = builder(JsonGrammar.JSON5)
            .indent("  ")
            .build();
    protected JsonGrammar grammar;
    protected String indent;

    public void write(JsonElement json, Writer writer, int depth) throws IOException {
        Writer wrappedWriter = new Writer() {
            @Override
            public void write(char @NotNull [] cbuf, int off, int len) throws IOException {
                writer.write(cbuf, off, len);
            }

            @Override
            public void flush() throws IOException {
                writer.flush();
            }

            @Override
            public void close() throws IOException {
                writer.close();
            }

            @Override
            public Writer append(CharSequence csq) throws IOException {
                return super.append(csq.toString().replace("\t",indent));
            }

            @Override
            public Writer append(char c) throws IOException {
                if (c=='\t') return super.append(indent);
                return super.append(c);
            }
        };
        json.toJson(wrappedWriter, grammar, depth);
    }

    public static Builder builder(JsonGrammar grammar) {
        return new Builder(grammar);
    }
    public static class Builder {
        private final SmarterJanksonWriter writer;

        public Builder(JsonGrammar grammar) {
            this.writer = new SmarterJanksonWriter();
            this.writer.grammar = grammar;
        }

        public Builder indent(String indent) {
            writer.indent = indent;
            return this;
        }

        public SmarterJanksonWriter build() {
            return writer;
        }
    }
}
