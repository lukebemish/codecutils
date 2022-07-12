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

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.*;

public class CommentedCodec<A> implements Codec<A> {
    public static <T> CommentedCodec<T> of(Codec<T> codec) {
        return new CommentedCodec<>(codec);
    }

    private final Map<List<String>, String> comments;
    private final Codec<A> codec;

    protected CommentedCodec(Codec<A> wrapped) {
        this.codec = wrapped;
        this.comments = new HashMap<>();
    }

    @Override
    public <T1> DataResult<Pair<A, T1>> decode(DynamicOps<T1> ops, T1 input) {
        return codec.decode(ops, input);
    }

    @Override
    public <T1> DataResult<T1> encode(A input, DynamicOps<T1> ops, T1 prefix) {
        DataResult<T1> data = codec.encode(input, ops, prefix);
        if (ops instanceof CommentingOps<T1> commentingOps) {
            Optional<T1> result = data.result();
            if (result.isPresent()) {
                T1 object = result.get();
                comments.forEach((key, value) -> commentingOps.setComment(object, key, value));
                return DataResult.success(object);
            }
        }
        return data;
    }

    public CommentedCodec<A> comment(String comment, String... path) {
        comments.put(Arrays.stream(path).toList(), comment);
        return this;
    }

    public CommentedCodec<A> comment(String comment, List<String> path) {
        comments.put(path, comment);
        return this;
    }

    public CommentedCodec<A> comment(Map<List<String>, String> comments) {
        this.comments.putAll(comments);
        return this;
    }
}
