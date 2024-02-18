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

import com.google.gson.Gson;
import com.launchdarkly.eventsource.ConnectStrategy;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.FaultEvent;
import com.launchdarkly.eventsource.MessageEvent;
import com.launchdarkly.eventsource.StreamEvent;
import net.luckperms.rest.event.EventCall;
import net.luckperms.rest.event.EventProducer;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.CallAdapter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

class EventCallAdapter implements CallAdapter<Object, EventCall<?>> {
    private final Type eventType;
    private final OkHttpClient client;
    private final ExecutorService executorService;

    EventCallAdapter(Type eventType, OkHttpClient client, ExecutorService executorService) {
        this.eventType = eventType;
        this.client = client;
        this.executorService = executorService;
    }

    @Override
    public Type responseType() {
        return Object.class;
    }

    @Override
    public EventCall<Object> adapt(Call<Object> call) {
        return new EventCallImpl<>(call.request().url(), this.eventType, this.client, this.executorService);
    }

    private static final class EventCallImpl<E> implements EventCall<E> {
        private final HttpUrl url;
        private final Type eventType;
        private final OkHttpClient client;
        private final Executor executor;

        EventCallImpl(HttpUrl url, Type eventType, OkHttpClient client, Executor executor) {
            this.url = url;
            this.eventType = eventType;
            this.client = client;
            this.executor = executor;
        }

        @Override
        public EventProducer<E> subscribe() throws Exception {
            EventSource eventSource = new EventSource.Builder(ConnectStrategy.http(this.url).httpClient(this.client)).build();
            eventSource.start();

            return new EventProducerImpl<>(eventSource, this.eventType, this.executor);
        }
    }

    private static final class EventProducerImpl<E> implements EventProducer<E> {
        private static final Gson GSON = new Gson();

        private final EventSource eventSource;
        private final Type eventType;

        private final List<Consumer<E>> handlers;
        private final List<Consumer<Exception>> errorHandlers;

        private EventProducerImpl(EventSource eventSource, Type eventType, Executor executor) {
            this.eventSource = eventSource;
            this.eventType = eventType;
            this.handlers = new CopyOnWriteArrayList<>();
            this.errorHandlers = new CopyOnWriteArrayList<>();

            executor.execute(this::pollForEvents);
        }

        private void pollForEvents() {
            try {
                for (StreamEvent event : this.eventSource.anyEvents()) {
                    if (event instanceof MessageEvent) {
                        handleMessage((MessageEvent) event);
                    } else if (event instanceof FaultEvent) {
                        handleError(((FaultEvent) event).getCause());
                    }
                }
            } catch (Exception e) {
                handleError(e);
            }
        }

        private void handleMessage(MessageEvent e) {
            String eventName = e.getEventName();
            if (!eventName.equals("message")) {
                return;
            }

            E parsedEvent;
            try {
                parsedEvent = GSON.fromJson(e.getData(), this.eventType);
            } catch (Exception ex) {
                handleError(ex);
                return;
            }

            for (Consumer<E> handler : this.handlers) {
                try {
                    handler.accept(parsedEvent);
                } catch (Exception ex) {
                    handleError(ex);
                }
            }
        }

        private void handleError(Exception e) {
            for (Consumer<Exception> errorHandler : this.errorHandlers) {
                try {
                    errorHandler.accept(e);
                } catch (Exception ex) {
                    // ignore
                }
            }
        }

        @Override
        public void subscribe(Consumer<E> consumer) {
            this.handlers.add(consumer);
        }

        @Override
        public void errorHandler(Consumer<Exception> errorHandler) {
            this.errorHandlers.add(errorHandler);
        }

        @Override
        public void close() {
            this.eventSource.close();
        }
    }

}
