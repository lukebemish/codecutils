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

ModsDotGroovy.make {
    modLoader = 'javafml'
    loaderVersion = '>=40'

    license = 'LGPL-3.0-or-later'
    issueTrackerUrl = 'https://github.com/lukebemish/codecutils/issues'

    mod {
        modId = "${this.buildProperties.mod_id}"
        version = "${this.version}"
        authors = ['Luke Bemish']
        description = 'Various utilities for using codecs in modding.'
        displayName = "${this.buildProperties.mod_name}"
        displayUrl = 'https://github.com/lukebemish/codecutils'

        dependencies {
            minecraft = "~${this.minecraftVersion}"
            forge {
                versionRange = ">=${this.forgeVersion}"
            }
            quiltLoader {
                versionRange = ">=${this.quiltLoaderVersion}"
            }
            onQuilt {
                mod {
                    modId = 'quilt_base'
                    versionRange = ">=${this.buildProperties.quilt_stdlib_version}"
                }
            }
        }

        entrypoints {
            init = 'io.github.lukebemish.codecutils.quilt.CodecUtilsQuilt'
        }

        onQuilt {
            group = "${this.group}"
            intermediate_mappings = 'net.fabricmc:intermediary'
        }
    }
}