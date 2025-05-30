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
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

class LuckPermsRestClientImpl implements LuckPermsRestClient {
    private final OkHttpClient httpClient;
    private final EventCallAdapterFactory eventCallAdapterFactory;

    private final UserService userService;
    private final GroupService groupService;
    private final TrackService trackService;
    private final ActionService actionService;
    private final MessagingService messagingService;
    private final EventService eventService;
    private final MiscService miscService;

    LuckPermsRestClientImpl(String baseUrl, String apiKey, Consumer<OkHttpClient.Builder> clientConfigurer) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        if (apiKey != null && !apiKey.isEmpty()) {
            clientBuilder.addInterceptor(new AuthInterceptor(apiKey));
        }

        clientBuilder.readTimeout(60, TimeUnit.SECONDS);

        if (clientConfigurer != null) {
            clientConfigurer.accept(clientBuilder);
        }

        this.httpClient = clientBuilder.build();
        this.eventCallAdapterFactory = new EventCallAdapterFactory(this.httpClient);

        Retrofit retrofit = new Retrofit.Builder()
                .client(this.httpClient)
                .baseUrl(baseUrl)
                .addCallAdapterFactory(this.eventCallAdapterFactory)
                .addConverterFactory(GsonConverterFactory.create())
                .validateEagerly(true)
                .build();

        this.userService = retrofit.create(UserService.class);
        this.groupService = retrofit.create(GroupService.class);
        this.trackService = retrofit.create(TrackService.class);
        this.actionService = retrofit.create(ActionService.class);
        this.messagingService = retrofit.create(MessagingService.class);
        this.eventService = retrofit.create(EventService.class);
        this.miscService = retrofit.create(MiscService.class);
    }

    @Override
    public UserService users() {
        return this.userService;
    }

    @Override
    public GroupService groups() {
        return this.groupService;
    }

    public TrackService tracks() {
        return this.trackService;
    }

    @Override
    public ActionService actions() {
        return this.actionService;
    }

    @Override
    public MessagingService messaging() {
        return this.messagingService;
    }

    @Override
    public EventService events() {
        return this.eventService;
    }

    @Override
    public MiscService misc() {
        return this.miscService;
    }

    @Override
    public void close() {
        this.httpClient.dispatcher().executorService().shutdown();
        this.httpClient.connectionPool().evictAll();
        this.eventCallAdapterFactory.close();
    }

    static final class BuilderImpl implements Builder {
        private String baseUrl = null;
        private String apiKey = null;
        private Consumer<OkHttpClient.Builder> clientConfigurer = null;

        BuilderImpl() {

        }

        @Override
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        @Override
        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        @Override
        public Builder httpClientConfigurer(Consumer<OkHttpClient.Builder> clientConfigurer) {
            this.clientConfigurer = clientConfigurer;
            return this;
        }

        @Override
        public LuckPermsRestClient build() {
            Objects.requireNonNull(this.baseUrl, "baseUrl must be configured!");
            return new LuckPermsRestClientImpl(this.baseUrl, this.apiKey, this.clientConfigurer);
        }
    }

    static final class AuthInterceptor implements Interceptor {
        private final String key;

        AuthInterceptor(String key) {
            this.key = "Bearer " + key;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request().newBuilder().header("Authorization", this.key).build();
            return chain.proceed(request);
        }
    }
}
