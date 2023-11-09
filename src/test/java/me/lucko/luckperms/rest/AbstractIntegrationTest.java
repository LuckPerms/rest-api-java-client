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
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class AbstractIntegrationTest {

    @Container
    private static final GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse("ghcr.io/luckperms/rest-api"))
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(AbstractIntegrationTest.class)))
            .withExposedPorts(8080)
            .waitingFor(new WaitAllStrategy()
                    .withStrategy(Wait.forListeningPort())
                    .withStrategy(Wait.forLogMessage(".*Successfully enabled.*", 1))
            );

    protected LuckPermsRestClient createClient() {
        assertTrue(container.isRunning());

        String host = container.getHost();
        Integer port = container.getFirstMappedPort();
        String baseUrl = "http://" + host + ":" + port + "/";

        return LuckPermsRestClient.builder().baseUrl(baseUrl).build();
    }

    protected static String randomName() {
        return "random" + ThreadLocalRandom.current().nextInt(9999999);
    }

}
