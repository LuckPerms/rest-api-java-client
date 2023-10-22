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

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class Action extends AbstractModel {
    private final Long timestamp;
    private final Source source;
    private final Target target;
    private final String description;

    public Long timestamp() {
        return this.timestamp;
    }

    public Source source() {
        return this.source;
    }

    public Target target() {
        return this.target;
    }

    public String description() {
        return this.description;
    }

    public Action(Long timestamp, Source source, Target target, String description) {
        this.timestamp = timestamp;
        this.source = source;
        this.target = target;
        this.description = description;
    }

    public static class Source extends AbstractModel {
        private final UUID uniqueId;
        private final String name;

        public Source(UUID uniqueId, String name) {
            this.uniqueId = uniqueId;
            this.name = name;
        }

        public UUID uniqueId() {
            return this.uniqueId;
        }

        public String name() {
            return this.name;
        }
    }

    public static class Target extends AbstractModel {
        private final UUID uniqueId; // nullable
        private final String name;
        private final Type type;

        public Target(UUID uniqueId, String name, Type type) {
            this.uniqueId = uniqueId;
            this.name = name;
            this.type = type;
        }

        public Target(String name, Type type) {
            this.uniqueId = null;
            this.name = name;
            this.type = type;
        }

        public UUID uniqueId() {
            return this.uniqueId;
        }

        public String name() {
            return this.name;
        }

        public Type type() {
            return this.type;
        }

        public enum Type {

            @SerializedName("user")
            USER,

            @SerializedName("group")
            GROUP,

            @SerializedName("track")
            TRACK
        }
    }
}
