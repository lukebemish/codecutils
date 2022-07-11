/*


Parts of this class are adapted from the "databuddy" tool, licensed under the following:

The MIT License (MIT)
Copyright (c) 2020 Joseph Bettendorff aka "Commoble"
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
// DataFixerUpper is Copyright (c) Microsoft Corporation. All rights reserved. Licensed under the MIT license.
 */

package io.github.lukebemish.codecutils.impl;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.NullObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.github.lukebemish.codecutils.api.CommentingOps;

import java.util.*;
import java.util.stream.Stream;

public abstract class NightConfigOps implements CommentingOps<Object> {

    @Override
    public Object empty() {
        return NullObject.NULL_OBJECT;
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, Object input) {
        if (input instanceof Config)
            return this.convertMap(outOps, input);
        if (input instanceof Collection)
            return this.convertList(outOps, input);
        if (input == null || input instanceof NullObject)
            return outOps.empty();
        if (input instanceof Enum<?> enumValue)
            return outOps.createString(enumValue.name());
        if (input instanceof Number number)
            return outOps.createNumeric(number);
        if (input instanceof String string)
            return outOps.createString(string);
        if (input instanceof Boolean bool)
            return outOps.createBoolean(bool);
        throw new UnsupportedOperationException("NightConfigOps was unable to convert a value: " + input);
    }

    @Override
    public DataResult<Number> getNumberValue(Object i) {
        return i instanceof Number n
                ? DataResult.success(n)
                : DataResult.error("Not a number: " + i);
    }

    @Override
    public Object createNumeric(Number i) {
        return i;
    }

    @Override
    public DataResult<String> getStringValue(Object input) {
        return (input instanceof Config || input instanceof Collection) ?
                DataResult.error("Not a string: " + input) :
                DataResult.success(String.valueOf(input));
    }

    @Override
    public Object createString(String value) {
        return value;
    }

    @Override
    public DataResult<Object> mergeToList(Object list, Object value) {
        if (!(list instanceof Collection) && list != this.empty()) {
            return DataResult.error("mergeToList called with not a list: " + list, list);
        }
        final Collection<Object> result = new ArrayList<>();
        if (list != this.empty()) {
            @SuppressWarnings("unchecked")
            Collection<Object> listAsCollection = (Collection<Object>)list;
            result.addAll(listAsCollection);
        }
        result.add(value);
        return DataResult.success(result);
    }

    @Override
    public DataResult<Object> mergeToMap(Object map, Object key, Object value) {
        if (!(map instanceof Config) && map != this.empty()) {
            return DataResult.error("mergeToMap called with not a map: " + map, map);
        }
        DataResult<String> stringResult = this.getStringValue(key);
        Optional<DataResult.PartialResult<String>> badResult = stringResult.error();
        if (badResult.isPresent()) {
            return DataResult.error("key is not a string: " + key, map);
        }
        return stringResult.flatMap(s ->{

            final Config output = newConfig();
            if (map != this.empty())
            {
                Config oldConfig = (Config)map;
                output.addAll(oldConfig);
            }
            output.add(s, value);
            return DataResult.success(output);
        });
    }

    @Override
    public DataResult<Stream<Pair<Object, Object>>> getMapValues(Object input) {
        if (!(input instanceof Config)) {
            return DataResult.error("Not a Config: " + input);
        }
        final Config config = (Config)input;
        return DataResult.success(config.entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue())));
    }

    @Override
    public Object createMap(Stream<Pair<Object, Object>> map) {
        final Config result = newConfig();
        map.forEach(p -> result.add(this.getStringValue(p.getFirst()).getOrThrow(false, s -> {}), p.getSecond()));
        return result;
    }

    @Override
    public DataResult<Stream<Object>> getStream(Object input) {
        if (input instanceof Collection)
        {
            @SuppressWarnings("unchecked")
            Collection<Object> collection = (Collection<Object>)input;
            return DataResult.success(collection.stream());
        }
        return DataResult.error("Not a collection: " + input);
    }

    @Override
    public Object createList(Stream<Object> input) {
        return input.toList();
    }

    @Override
    public Object remove(Object input, String key) {
        if (input instanceof Config oldConfig)
        {
            final Config result = newConfig();
            oldConfig.entrySet().stream()
                    .filter(entry -> !Objects.equals(entry.getKey(), key))
                    .forEach(entry -> result.add(entry.getKey(), entry.getValue()));
            return result;
        }
        return input;
    }

    protected abstract Config newConfig();

    @Override
    public void setComment(Object input, List<String> path, String comment) {
        if (input instanceof CommentedConfig config) {
            config.setComment(path, comment);
        }
    }
}
