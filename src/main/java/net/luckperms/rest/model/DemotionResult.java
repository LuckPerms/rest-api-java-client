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

public class DemotionResult extends AbstractModel {
    private final boolean success;
    private final Status status;
    private final String groupFrom; // nullable
    private final String groupTo; // nullable

    public DemotionResult(boolean success, Status status, String groupFrom, String groupTo) {
        this.success = success;
        this.status = status;
        this.groupFrom = groupFrom;
        this.groupTo = groupTo;
    }

    public boolean success() {
        return this.success;
    }

    public Status status() {
        return this.status;
    }

    public String groupFrom() {
        return this.groupFrom;
    }

    public String groupTo() {
        return this.groupTo;
    }

    public enum Status {

        @SerializedName("success")
        SUCCESS,

        @SerializedName("removed_from_first_group")
        REMOVED_FROM_FIRST_GROUP,

        @SerializedName("malformed_track")
        MALFORMED_TRACK,

        @SerializedName("not_on_track")
        NOT_ON_TRACK,

        @SerializedName("ambiguous_call")
        AMBIGUOUS_CALL,

        @SerializedName("undefined_failure")
        UNDEFINED_FAILURE
    }
}
