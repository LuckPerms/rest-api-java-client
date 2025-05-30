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

package net.luckperms.rest;

import net.luckperms.rest.service.ActionService;
import net.luckperms.rest.service.EventService;
import net.luckperms.rest.service.GroupService;
import net.luckperms.rest.service.MessagingService;
import net.luckperms.rest.service.MiscService;
import net.luckperms.rest.service.TrackService;
import net.luckperms.rest.service.UserService;
import okhttp3.OkHttpClient;

import java.util.function.Consumer;

/**
 * A Java client for the LuckPerms REST API.
 *
 * @see <a href="https://github.com/LuckPerms/rest-api">rest-api GitHub repo</a>
 */
public interface LuckPermsRestClient extends AutoCloseable {

    /**
     * Creates a new client builder.
     *
     * @return the new builder
     */
    static Builder builder() {
        return new LuckPermsRestClientImpl.BuilderImpl();
    }

    /**
     * Gets the user service.
     *
     * @return the user service
     */
    UserService users();

    /**
     * Gets the group service.
     *
     * @return the group service
     */
    GroupService groups();

    /**
     * Gets the track service.
     *
     * @return the track service
     */
    TrackService tracks();

    /**
     * Gets the action service.
     *
     * @return the action service
     */
    ActionService actions();

    /**
     * Gets the messaging service.
     *
     * @return the messaging service
     */
    MessagingService messaging();

    /**
     * Gets the event service.
     *
     * @return the event service
     */
    EventService events();

    /**
     * Gets the misc service.
     *
     * @return the misc service.
     */
    MiscService misc();

    /**
     * Close the underlying resources used by the client.
     */
    @Override
    void close();

    /**
     * A builder for {@link LuckPermsRestClient}
     */
    interface Builder {

        /**
         * Sets the API base URL.
         *
         * @param baseUrl the base url
         * @return this builder
         */
        Builder baseUrl(String baseUrl);

        /**
         * Sets the API key for authentication.
         *
         * @param apiKey the api key
         * @return this builder
         */
        Builder apiKey(String apiKey);

        /**
         * Sets a custom HTTP client configurer.
         *
         * @param clientConfigurer the client configurer
         * @return this builder
         */
        Builder httpClientConfigurer(Consumer<OkHttpClient.Builder> clientConfigurer);

        /**
         * Builds a client.
         *
         * @return a client
         */
        LuckPermsRestClient build();
    }
}
