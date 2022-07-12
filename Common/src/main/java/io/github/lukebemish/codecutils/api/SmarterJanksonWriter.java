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
