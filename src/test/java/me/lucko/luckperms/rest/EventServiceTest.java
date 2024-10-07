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

package me.lucko.luckperms.rest;

import net.luckperms.rest.LuckPermsRestClient;
import net.luckperms.rest.event.EventCall;
import net.luckperms.rest.event.EventProducer;
import net.luckperms.rest.model.Action;
import net.luckperms.rest.model.CustomMessage;
import net.luckperms.rest.model.CustomMessageReceiveEvent;
import net.luckperms.rest.model.LogBroadcastEvent;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class EventServiceTest extends AbstractIntegrationTest {

    @Test
    public void testLogBroadcastEvent() throws Exception {
        LuckPermsRestClient client = createClient();

        Action exampleAction = new Action(
                System.currentTimeMillis() / 1000L,
                new Action.Source(UUID.randomUUID(), randomName()),
                new Action.Target(UUID.randomUUID(), randomName(), Action.Target.Type.USER),
                "hello world"
        );

        EventCall<LogBroadcastEvent> call = client.events().logBroadcast();
        LogBroadcastEvent event = testEvent(call, 5, () -> {
            try {
                client.actions().submit(exampleAction).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        assertEquals(LogBroadcastEvent.Origin.LOCAL_API, event.origin());
        assertEquals(exampleAction, event.entry());
        assertNotSame(event.entry(), exampleAction);
    }

    @Test
    public void testCustomMessageReceiveEvent() throws Exception {
        try (Network network = Network.newNetwork(); GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis"))) {
            redis.withNetwork(network).withNetworkAliases("redis").start();

            Supplier<GenericContainer<?>> restSupplier = () -> createContainer()
                    .withNetwork(network)
                    .withEnv("LUCKPERMS_MESSAGING_SERVICE", "redis")
                    .withEnv("LUCKPERMS_REDIS_ENABLED", "true")
                    .withEnv("LUCKPERMS_REDIS_ADDRESS", "redis:6379");

            try (GenericContainer<?> restA = restSupplier.get(); GenericContainer<?> restB = restSupplier.get()) {
                restA.start();
                restB.start();

                LuckPermsRestClient clientA = createClient(restA);
                LuckPermsRestClient clientB = createClient(restB);

                EventCall<CustomMessageReceiveEvent> call = clientA.events().customMessageReceive();
                CustomMessageReceiveEvent event = testEvent(call, 5, () -> {
                    try {
                        clientB.messaging().sendCustomMessage(new CustomMessage("custom:test", "aaabbbccc")).execute();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                assertEquals("custom:test", event.channelId());
                assertEquals("aaabbbccc", event.payload());
            }
        }
    }

    @Test
    @Disabled("takes too long to run")
    public void testLogBroadcastEventLongWait() throws Exception {
        LuckPermsRestClient client = createClient();

        Action exampleAction = new Action(
                System.currentTimeMillis() / 1000L,
                new Action.Source(UUID.randomUUID(), randomName()),
                new Action.Target(UUID.randomUUID(), randomName(), Action.Target.Type.USER),
                "hello world"
        );

        EventCall<LogBroadcastEvent> call = client.events().logBroadcast();
        LogBroadcastEvent event = testEvent(call, 110, () -> {
            try {
                Thread.sleep(100_000);
                client.actions().submit(exampleAction).execute();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        assertEquals(LogBroadcastEvent.Origin.LOCAL_API, event.origin());
        assertEquals(exampleAction, event.entry());
        assertNotSame(event.entry(), exampleAction);
    }

    private static <E> E testEvent(EventCall<E> call, int waitSeconds, Runnable generateEventAction) throws Exception {
        // subscribe to the event
        EventProducer<E> subscription = call.subscribe();
        AwaitingConsumer<E> consumer = new AwaitingConsumer<>();
        AwaitingConsumer<Exception> errorConsumer = new AwaitingConsumer<>();
        subscription.subscribe(consumer);
        subscription.errorHandler(errorConsumer);

        // cause the event to be fired
        CompletableFuture.runAsync(generateEventAction);

        // wait for the event to be generated and received
        consumer.await(waitSeconds, TimeUnit.SECONDS);

        // validate a single event was returned
        List<E> events = consumer.getResults();
        assertEquals(1, events.size());
        E event = events.get(0);

        // close subscription and assert that there were no errors
        subscription.close();
        assertEquals(0, errorConsumer.getResults().size());

        return event;
    }

    private static final class AwaitingConsumer<T> implements Consumer<T> {
        private final List<T> results = new ArrayList<>();
        private final CountDownLatch latch = new CountDownLatch(1);

        @Override
        public void accept(T obj) {
            this.results.add(obj);
            this.latch.countDown();
        }

        public List<T> getResults() {
            return this.results;
        }

        public void await(long timeout, TimeUnit unit) throws InterruptedException {
            this.latch.await(timeout, unit);
        }
    }

}
