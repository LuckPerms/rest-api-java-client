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

package net.luckperms.rest.service;

import net.luckperms.rest.event.EventCall;
import net.luckperms.rest.model.LogBroadcastEvent;
import net.luckperms.rest.model.PostNetworkSyncEvent;
import net.luckperms.rest.model.PostSyncEvent;
import net.luckperms.rest.model.PreNetworkSyncEvent;
import net.luckperms.rest.model.PreSyncEvent;
import retrofit2.http.GET;

public interface EventService {

    @GET("/event/log-broadcast")
    EventCall<LogBroadcastEvent> logBroadcast();

    @GET("/event/post-network-sync")
    EventCall<PostNetworkSyncEvent> postNetworkSync();

    @GET("/event/post-sync")
    EventCall<PostSyncEvent> postSync();

    @GET("/event/pre-network-sync")
    EventCall<PreNetworkSyncEvent> preNetworkSync();

    @GET("/event/pre-sync")
    EventCall<PreSyncEvent> preSync();

}
