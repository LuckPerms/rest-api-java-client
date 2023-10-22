/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.luckperms.rest.model;

import java.util.Map;

public class Metadata extends AbstractModel {
    private final Map<String, String> meta;
    private final String prefix; // nullable
    private final String suffix; // nullable
    private final String primaryGroup; // nullable

    public Metadata(Map<String, String> meta, String prefix, String suffix, String primaryGroup) {
        this.meta = meta;
        this.prefix = prefix;
        this.suffix = suffix;
        this.primaryGroup = primaryGroup;
    }

    public Map<String, String> meta() {
        return this.meta;
    }

    public String prefix() {
        return this.prefix;
    }

    public String suffix() {
        return this.suffix;
    }

    public String primaryGroup() {
        return this.primaryGroup;
    }
}
