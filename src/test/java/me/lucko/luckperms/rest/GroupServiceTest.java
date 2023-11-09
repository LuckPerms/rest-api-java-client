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
import net.luckperms.rest.model.Context;
import net.luckperms.rest.model.CreateGroupRequest;
import net.luckperms.rest.model.Group;
import net.luckperms.rest.model.GroupSearchResult;
import net.luckperms.rest.model.Metadata;
import net.luckperms.rest.model.Node;
import net.luckperms.rest.model.NodeType;
import net.luckperms.rest.model.PermissionCheckRequest;
import net.luckperms.rest.model.PermissionCheckResult;
import net.luckperms.rest.model.QueryOptions;
import net.luckperms.rest.model.TemporaryNodeMergeStrategy;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;
import org.testcontainers.shaded.com.google.common.collect.ImmutableSet;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GroupServiceTest extends AbstractIntegrationTest {

    @Test
    public void testGroupCrud() throws IOException {
        LuckPermsRestClient client = createClient();

        String name = randomName();

        // create
        Response<Group> createResp = client.groups().create(new CreateGroupRequest(name)).execute();
        assertTrue(createResp.isSuccessful());
        Group group = createResp.body();
        assertNotNull(group);
        assertEquals(name, group.name());
        assertNull(group.displayName());
        assertEquals(0, group.weight());

        // create - already exists
        assertEquals(409, client.groups().create(new CreateGroupRequest(name)).execute().code());

        // read
        Response<Group> readResp = client.groups().get(name).execute();
        assertTrue(readResp.isSuccessful());
        group = readResp.body();
        assertNotNull(group);
        assertEquals(name, group.name());

        // delete
        Response<Void> deleteResp = client.groups().delete(name).execute();
        assertTrue(deleteResp.isSuccessful());

        // not found
        assertEquals(404, client.groups().get(name).execute().code());
        assertEquals(404, client.groups().delete(name).execute().code());
    }

    @Test
    public void testGroupList() throws IOException {
        LuckPermsRestClient client = createClient();

        String name = randomName();

        // create a group
        assertTrue(client.groups().create(new CreateGroupRequest(name)).execute().isSuccessful());

        Response<Set<String>> resp = client.groups().list().execute();
        assertTrue(resp.isSuccessful());
        assertNotNull(resp.body());
        assertTrue(resp.body().contains(name));
    }

    @Test
    public void testGroupNodes() throws IOException {
        LuckPermsRestClient client = createClient();

        String name = randomName();

        // create a group
        assertTrue(client.groups().create(new CreateGroupRequest(name)).execute().isSuccessful());

        // get group nodes and validate they are as expected
        List<Node> nodes = client.groups().nodes(name).execute().body();
        assertNotNull(nodes);
        assertEquals(0, nodes.size());

        long expiryTime = (System.currentTimeMillis() / 1000L) + 60;

        // add a node
        assertTrue(client.groups().nodesAdd(name, new Node("test.node.one", true, Collections.emptySet(), null)).execute().isSuccessful());

        // add multiple nodes
        assertTrue(client.groups().nodesAdd(name, ImmutableList.of(
                new Node("test.node.two", false, Collections.emptySet(), null),
                new Node("test.node.three", true, Collections.singleton(new Context("server", "test")), null),
                new Node("test.node.four", false, Collections.emptySet(), expiryTime)
        )).execute().isSuccessful());

        // get group nodes and validate they are as expected
        nodes = client.groups().nodes(name).execute().body();
        assertNotNull(nodes);
        assertEquals(ImmutableSet.of(
                new Node("test.node.one", true, Collections.emptySet(), null),
                new Node("test.node.two", false, Collections.emptySet(), null),
                new Node("test.node.three", true, Collections.singleton(new Context("server", "test")), null),
                new Node("test.node.four", false, Collections.emptySet(), expiryTime)
        ), ImmutableSet.copyOf(nodes));

        // delete nodes
        assertTrue(client.groups().nodesDelete(name, ImmutableList.of(
                new Node("test.node.one", true, Collections.emptySet(), null),
                new Node("test.node.two", false, Collections.emptySet(), null)
        )).execute().isSuccessful());

        // get group nodes and validate they are as expected
        nodes = client.groups().nodes(name).execute().body();
        assertNotNull(nodes);
        assertEquals(ImmutableSet.of(
                new Node("test.node.three", true, Collections.singleton(new Context("server", "test")), null),
                new Node("test.node.four", false, Collections.emptySet(), expiryTime)
        ), ImmutableSet.copyOf(nodes));

        // add a duplicate node with a later expiry time
        long laterExpiryTime = expiryTime + 60;
        assertTrue(client.groups().nodesAdd(name, new Node("test.node.four", false, Collections.emptySet(), laterExpiryTime), TemporaryNodeMergeStrategy.REPLACE_EXISTING_IF_DURATION_LONGER).execute().isSuccessful());

        // get group nodes and validate they are as expected
        nodes = client.groups().nodes(name).execute().body();
        assertNotNull(nodes);
        assertEquals(ImmutableSet.of(
                new Node("test.node.three", true, Collections.singleton(new Context("server", "test")), null),
                new Node("test.node.four", false, Collections.emptySet(), laterExpiryTime)
        ), ImmutableSet.copyOf(nodes));

        long evenLaterExpiryTime = expiryTime + 60;

        // add multiple nodes
        assertTrue(client.groups().nodesAdd(name, ImmutableList.of(
                new Node("test.node.two", false, Collections.emptySet(), null),
                new Node("test.node.three", true, Collections.singleton(new Context("server", "test")), null),
                new Node("test.node.four", false, Collections.emptySet(), evenLaterExpiryTime)
        ), TemporaryNodeMergeStrategy.REPLACE_EXISTING_IF_DURATION_LONGER).execute().isSuccessful());

        // get group nodes and validate they are as expected
        nodes = client.groups().nodes(name).execute().body();
        assertNotNull(nodes);
        assertEquals(ImmutableSet.of(
                new Node("test.node.two", false, Collections.emptySet(), null),
                new Node("test.node.three", true, Collections.singleton(new Context("server", "test")), null),
                new Node("test.node.four", false, Collections.emptySet(), evenLaterExpiryTime)
        ), ImmutableSet.copyOf(nodes));

        // set nodes
        assertTrue(client.groups().nodesSet(name, ImmutableList.of(
                new Node("test.node.five", false, Collections.emptySet(), null),
                new Node("test.node.six", true, Collections.singleton(new Context("server", "test")), null),
                new Node("test.node.seven", false, Collections.emptySet(), evenLaterExpiryTime)
        )).execute().isSuccessful());

        // get group nodes and validate they are as expected
        nodes = client.groups().nodes(name).execute().body();
        assertNotNull(nodes);
        assertEquals(ImmutableSet.of(
                new Node("test.node.five", false, Collections.emptySet(), null),
                new Node("test.node.six", true, Collections.singleton(new Context("server", "test")), null),
                new Node("test.node.seven", false, Collections.emptySet(), evenLaterExpiryTime)
        ), ImmutableSet.copyOf(nodes));

        // delete all nodes
        assertTrue(client.groups().nodesDelete(name).execute().isSuccessful());

        // get group nodes and validate they are as expected
        nodes = client.groups().nodes(name).execute().body();
        assertNotNull(nodes);
        assertEquals(0, nodes.size());
    }

    @Test
    public void testGroupMetadata() throws IOException {
        LuckPermsRestClient client = createClient();

        String name = randomName();

        // create a group
        assertTrue(client.groups().create(new CreateGroupRequest(name)).execute().isSuccessful());

        // set some permissions
        assertTrue(client.groups().nodesAdd(name, ImmutableList.of(
                new Node("prefix.100.&c[Admin] ", true, Collections.emptySet(), null),
                new Node("suffix.100. test", true, Collections.emptySet(), null),
                new Node("meta.hello.world", true, Collections.emptySet(), null)
        )).execute().isSuccessful());

        // assert metadata
        Response<Metadata> resp = client.groups().metadata(name).execute();
        assertTrue(resp.isSuccessful());

        Metadata metadata = resp.body();
        assertNotNull(metadata);

        assertEquals("&c[Admin] ", metadata.prefix());
        assertEquals(" test", metadata.suffix());
        assertNull(metadata.primaryGroup());

        Map<String, String> metaMap = metadata.meta();
        assertEquals("world", metaMap.get("hello"));
    }

    @Test
    public void testGroupSearch() throws IOException {
        LuckPermsRestClient client = createClient();

        // clear existing groups
        for (String g : Objects.requireNonNull(client.groups().list().execute().body())) {
            if (!g.equals("default")) {
                client.groups().delete(g).execute();
            }
        }

        String name = randomName();

        // create a group
        assertTrue(client.groups().create(new CreateGroupRequest(name)).execute().isSuccessful());

        // set some permissions
        long expiryTime = (System.currentTimeMillis() / 1000L) + 60;
        assertTrue(client.groups().nodesAdd(name, ImmutableList.of(
                new Node("test.node.one", true, Collections.emptySet(), null),
                new Node("test.node.two", false, Collections.emptySet(), null),
                new Node("test.node.three", true, Collections.singleton(new Context("server", "test")), null),
                new Node("test.node.four", false, Collections.emptySet(), expiryTime),
                new Node("prefix.100.&c[Admin] ", true, Collections.emptySet(), null),
                new Node("suffix.100. test", true, Collections.emptySet(), null),
                new Node("meta.hello.world", true, Collections.emptySet(), null)
        )).execute().isSuccessful());

        // searchNodesByKey
        Response<List<GroupSearchResult>> resp1 = client.groups().searchNodesByKey("test.node.one").execute();
        assertTrue(resp1.isSuccessful());
        assertNotNull(resp1.body());
        assertEquals(ImmutableList.of(
                new GroupSearchResult(name, ImmutableList.of(new Node("test.node.one", true, Collections.emptySet(), null)))
        ), resp1.body());

        // searchNodesByKeyStartsWith
        Response<List<GroupSearchResult>> resp2 = client.groups().searchNodesByKeyStartsWith("test.node").execute();
        assertTrue(resp2.isSuccessful());
        assertNotNull(resp2.body());
        assertEquals(ImmutableList.of(
                new GroupSearchResult(name, ImmutableList.of(
                        new Node("test.node.one", true, Collections.emptySet(), null),
                        new Node("test.node.two", false, Collections.emptySet(), null),
                        new Node("test.node.three", true, Collections.singleton(new Context("server", "test")), null),
                        new Node("test.node.four", false, Collections.emptySet(), expiryTime)
                ))
        ), resp2.body());

        // searchNodesByMetaKey
        Response<List<GroupSearchResult>> resp3 = client.groups().searchNodesByMetaKey("hello").execute();
        assertTrue(resp3.isSuccessful());
        assertNotNull(resp3.body());
        assertEquals(ImmutableList.of(
                new GroupSearchResult(name, ImmutableList.of(new Node("meta.hello.world", true, Collections.emptySet(), null)))
        ), resp3.body());

        // searchNodesByType
        Response<List<GroupSearchResult>> resp4 = client.groups().searchNodesByType(NodeType.PREFIX).execute();
        assertTrue(resp4.isSuccessful());
        assertNotNull(resp4.body());
        assertEquals(ImmutableList.of(
                new GroupSearchResult(name, ImmutableList.of(new Node("prefix.100.&c[Admin] ", true, Collections.emptySet(), null)))
        ), resp4.body());
    }

    @Test
    public void testGroupPermissionCheck() throws IOException {
        LuckPermsRestClient client = createClient();

        String name = randomName();

        // create a group
        assertTrue(client.groups().create(new CreateGroupRequest(name)).execute().isSuccessful());

        // set some permissions
        assertTrue(client.groups().nodesAdd(name, ImmutableList.of(
                new Node("test.node.one", true, Collections.emptySet(), null),
                new Node("test.node.two", false, Collections.emptySet(), null),
                new Node("test.node.three", true, Collections.singleton(new Context("server", "test")), null)
        )).execute().isSuccessful());

        Response<PermissionCheckResult> resp0 = client.groups().permissionCheck(name, "test.node.zero").execute();
        assertTrue(resp0.isSuccessful());
        assertNotNull(resp0.body());
        assertEquals(PermissionCheckResult.Tristate.UNDEFINED, resp0.body().result());
        assertNull(resp0.body().node());

        Response<PermissionCheckResult> resp1 = client.groups().permissionCheck(name, "test.node.one").execute();
        assertTrue(resp1.isSuccessful());
        assertNotNull(resp1.body());
        assertEquals(PermissionCheckResult.Tristate.TRUE, resp1.body().result());
        assertEquals(new Node("test.node.one", true, Collections.emptySet(), null), resp1.body().node());

        Response<PermissionCheckResult> resp2 = client.groups().permissionCheck(name, "test.node.two").execute();
        assertTrue(resp2.isSuccessful());
        assertNotNull(resp2.body());
        assertEquals(PermissionCheckResult.Tristate.FALSE, resp2.body().result());
        assertEquals(new Node("test.node.two", false, Collections.emptySet(), null), resp2.body().node());

        Response<PermissionCheckResult> resp3 = client.groups().permissionCheck(name, "test.node.three").execute();
        assertTrue(resp3.isSuccessful());
        assertNotNull(resp3.body());
        assertEquals(PermissionCheckResult.Tristate.UNDEFINED, resp3.body().result());
        assertNull(resp3.body().node());

        Response<PermissionCheckResult> resp4 = client.groups().permissionCheck(name, new PermissionCheckRequest(
                "test.node.three",
                new QueryOptions(null, null, ImmutableSet.of(new Context("server", "test"), new Context("world", "aaa")))
        )).execute();
        assertTrue(resp4.isSuccessful());
        assertNotNull(resp4.body());
        assertEquals(PermissionCheckResult.Tristate.TRUE, resp4.body().result());
        assertEquals(new Node("test.node.three", true, Collections.singleton(new Context("server", "test")), null), resp4.body().node());
    }

}
