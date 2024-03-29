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
import java.util.UUID;

public class PlayerSaveResult extends AbstractModel {
    private final Set<Outcome> outcomes;
    private final String previousUsername; // nullable
    private final Set<UUID> otherUniqueIds; // nullable

    public PlayerSaveResult(Set<Outcome> outcomes, String previousUsername, Set<UUID> otherUniqueIds) {
        this.outcomes = outcomes;
        this.previousUsername = previousUsername;
        this.otherUniqueIds = otherUniqueIds;
    }

    public Set<Outcome> outcomes() {
        return this.outcomes;
    }

    public String previousUsername() {
        return this.previousUsername;
    }

    public Set<UUID> otherUniqueIds() {
        return this.otherUniqueIds;
    }

    public enum Outcome {

        @SerializedName("clean_insert")
        CLEAN_INSERT,

        @SerializedName("no_change")
        NO_CHANGE,

        @SerializedName("username_updated")
        USERNAME_UPDATED,

        @SerializedName("other_unique_ids_present_for_username")
        OTHER_UNIQUE_IDS_PRESENT_FOR_USERNAME,
    }

}
