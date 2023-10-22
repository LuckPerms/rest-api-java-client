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

import com.google.gson.Gson;
import com.google.gson.JsonElement;

abstract class AbstractModel {
    private static final Gson GSON = new Gson();

    private transient String jsonString;
    private transient JsonElement jsonTree;

    private String asJsonString() {
        String json = this.jsonString;
        if (json == null) {
            json = GSON.toJson(this);
            this.jsonString = json;
        }
        return json;
    }

    private JsonElement asJsonTree() {
        JsonElement json = this.jsonTree;
        if (json == null) {
            json = GSON.toJsonTree(this);
            this.jsonTree = json;
        }
        return json;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + asJsonString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof AbstractModel && asJsonTree().equals(((AbstractModel) obj).asJsonTree()));
    }

    @Override
    public int hashCode() {
        return asJsonTree().hashCode();
    }
}
