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
import net.luckperms.rest.model.Action;
import net.luckperms.rest.model.ActionPage;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;
import retrofit2.Response;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActionServiceTest extends AbstractIntegrationTest {

    @Test
    public void testSubmit() throws IOException {
        LuckPermsRestClient client = createClient();

        Response<Void> resp = client.actions().submit(new Action(
                System.currentTimeMillis() / 1000L,
                new Action.Source(UUID.randomUUID(), randomName()),
                new Action.Target(UUID.randomUUID(), randomName(), Action.Target.Type.USER),
                "hello world"
        )).execute();
        assertTrue(resp.isSuccessful());
    }

    @Test
    public void testQuery() throws IOException {
        LuckPermsRestClient client = createClient();

        long time = System.currentTimeMillis() / 1000L;
        Action.Source source = new Action.Source(UUID.randomUUID(), randomName());

        Action testUserAction = new Action(time, source,
                new Action.Target(UUID.randomUUID(), randomName(), Action.Target.Type.USER),
                "test user action"
        );
        Action testGroupAction = new Action(time + 1, source,
                new Action.Target(null, randomName(), Action.Target.Type.GROUP),
                "test group action"
        );
        Action testTrackAction = new Action(time + 2, source,
                new Action.Target(null, randomName(), Action.Target.Type.TRACK),
                "test track action"
        );

        client.actions().submit(testUserAction).execute();
        client.actions().submit(testGroupAction).execute();
        client.actions().submit(testTrackAction).execute();

        // test simple query all
        Response<ActionPage> resp1 = client.actions().query().execute();
        assertTrue(resp1.isSuccessful());
        ActionPage body1 = resp1.body();
        assertNotNull(body1);
        assertEquals(3, body1.overallSize());
        assertEquals(ImmutableList.of(testTrackAction, testGroupAction, testUserAction), body1.entries());

        for (int i = 0; i < 20; i++) {
            Action testAction = new Action(
                    time + 100 - i,
                    new Action.Source(UUID.randomUUID(), randomName()),
                    new Action.Target(null, randomName(), Action.Target.Type.GROUP),
                    "test " + i
            );
            client.actions().submit(testAction).execute();
        }

        // test pagination
        Response<ActionPage> resp2 = client.actions().query(5, 2).execute();
        assertTrue(resp2.isSuccessful());
        ActionPage body2 = resp2.body();
        assertNotNull(body2);
        assertEquals(23, body2.overallSize());
        assertEquals(
                ImmutableList.of("test 5", "test 6", "test 7", "test 8", "test 9"),
                body2.entries().stream().map(Action::description).collect(Collectors.toList())
        );

        // test query by source
        Response<ActionPage> resp3 = client.actions().querySource(source.uniqueId(), 5, 1).execute();
        assertTrue(resp3.isSuccessful());
        ActionPage body3 = resp3.body();
        assertNotNull(body3);
        assertEquals(3, body3.overallSize());
    }

}
