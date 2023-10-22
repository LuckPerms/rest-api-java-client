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

import java.util.List;
import java.util.UUID;

public class User extends AbstractModel {
    private final UUID uniqueId;
    private final String username;
    private final List<String> parentGroups;
    private final List<Node> nodes;
    private final Metadata metadata;

    public User(UUID uniqueId, String username, List<String> parentGroups, List<Node> nodes, Metadata metadata) {
        this.uniqueId = uniqueId;
        this.username = username;
        this.parentGroups = parentGroups;
        this.nodes = nodes;
        this.metadata = metadata;
    }

    public UUID uniqueId() {
        return this.uniqueId;
    }

    public String username() {
        return this.username;
    }

    public List<String> parentGroups() {
        return this.parentGroups;
    }

    public List<Node> nodes() {
        return this.nodes;
    }

    public Metadata metadata() {
        return this.metadata;
    }
}
