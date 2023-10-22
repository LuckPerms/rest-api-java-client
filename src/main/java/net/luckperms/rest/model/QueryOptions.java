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

import java.util.Set;

public class QueryOptions extends AbstractModel {
    private final Mode queryMode; // nullable
    private final Set<Flag> flags; // nullable
    private final Set<Context> contexts; // nullable

    public QueryOptions(Mode queryMode, Set<Flag> flags, Set<Context> contexts) {
        this.queryMode = queryMode;
        this.flags = flags;
        this.contexts = contexts;
    }

    public Mode queryMode() {
        return this.queryMode;
    }

    public Set<Flag> flags() {
        return this.flags;
    }

    public Set<Context> contexts() {
        return this.contexts;
    }

    public enum Mode {

        @SerializedName("contextual")
        CONTEXTUAL,

        @SerializedName("non_contextual")
        NON_CONTEXTUAL
    }

    public enum Flag {

        @SerializedName("resolve_inheritance")
        RESOLVE_INHERITANCE,

        @SerializedName("include_nodes_without_server_context")
        INCLUDE_NODES_WITHOUT_SERVER_CONTEXT,

        @SerializedName("include_nodes_without_world_context")
        INCLUDE_NODES_WITHOUT_WORLD_CONTEXT,

        @SerializedName("apply_inheritance_nodes_without_server_context")
        APPLY_INHERITANCE_NODES_WITHOUT_SERVER_CONTEXT,

        @SerializedName("apply_inheritance_nodes_without_world_context")
        APPLY_INHERITANCE_NODES_WITHOUT_WORLD_CONTEXT
    }
}
