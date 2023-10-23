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

import net.luckperms.rest.LuckPermsClient;
import net.luckperms.rest.model.Context;
import net.luckperms.rest.model.CreateGroupRequest;
import net.luckperms.rest.model.CreateTrackRequest;
import net.luckperms.rest.model.CreateUserRequest;
import net.luckperms.rest.model.DemotionResult;
import net.luckperms.rest.model.Group;
import net.luckperms.rest.model.Metadata;
import net.luckperms.rest.model.Node;
import net.luckperms.rest.model.PermissionCheckRequest;
import net.luckperms.rest.model.PermissionCheckResult;
import net.luckperms.rest.model.PlayerSaveResult;
import net.luckperms.rest.model.PromotionResult;
import net.luckperms.rest.model.QueryOptions;
import net.luckperms.rest.model.TemporaryNodeMergeStrategy;
import net.luckperms.rest.model.Track;
import net.luckperms.rest.model.TrackRequest;
import net.luckperms.rest.model.UpdateTrackRequest;
import net.luckperms.rest.model.UpdateUserRequest;
import net.luckperms.rest.model.User;
import net.luckperms.rest.model.UserLookupResult;
import net.luckperms.rest.model.UserSearchResult;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserServiceTest extends AbstractIntegrationTest {

    @Test
    public void testUserCrud() throws IOException {
        LuckPermsClient client = createClient();

        UUID uuid = UUID.randomUUID();
        String username = randomName();

        // create
        Response<PlayerSaveResult> createResp = client.users().create(new CreateUserRequest(uuid, username)).execute();
        assertTrue(createResp.isSuccessful());
        assertEquals(201, createResp.code());
        PlayerSaveResult result = createResp.body();
        assertNotNull(result);

        // read
        Response<User> readResp = client.users().get(uuid).execute();
        assertTrue(readResp.isSuccessful());
        User user = readResp.body();
        assertNotNull(user);
        assertEquals(uuid, user.uniqueId());
        assertEquals(username, user.username());

        // update
        Response<Void> updateResp = client.users().update(uuid, new UpdateUserRequest(randomName())).execute();
        assertTrue(updateResp.isSuccessful());

        // delete
        Response<Void> deleteResp = client.users().delete(uuid).execute();
        assertTrue(deleteResp.isSuccessful());
    }

    @Test
    public void testUserCreate() throws IOException {
        LuckPermsClient client = createClient();

        UUID uuid = UUID.randomUUID();
        String username = randomName();

        // create - clean insert
        Response<PlayerSaveResult> createResp1 = client.users().create(new CreateUserRequest(uuid, username)).execute();
        assertTrue(createResp1.isSuccessful());
        assertEquals(201, createResp1.code());
        PlayerSaveResult result1 = createResp1.body();
        assertNotNull(result1);
        assertEquals(ImmutableSet.of(PlayerSaveResult.Outcome.CLEAN_INSERT), result1.outcomes());
        assertNull(result1.previousUsername());
        assertNull(result1.otherUniqueIds());

        // create - no change
        Response<PlayerSaveResult> createResp2 = client.users().create(new CreateUserRequest(uuid, username)).execute();
        assertTrue(createResp2.isSuccessful());
        assertEquals(200, createResp2.code());
        PlayerSaveResult result2 = createResp2.body();
        assertNotNull(result2);
        assertEquals(ImmutableSet.of(PlayerSaveResult.Outcome.NO_CHANGE), result2.outcomes());
        assertNull(result2.previousUsername());
        assertNull(result2.otherUniqueIds());

        // create - changed username
        String otherUsername = randomName();
        Response<PlayerSaveResult> createResp3 = client.users().create(new CreateUserRequest(uuid, otherUsername)).execute();
        assertTrue(createResp3.isSuccessful());
        assertEquals(200, createResp3.code());
        PlayerSaveResult result3 = createResp3.body();
        assertNotNull(result3);
        assertEquals(ImmutableSet.of(PlayerSaveResult.Outcome.USERNAME_UPDATED), result3.outcomes());
        assertEquals(username, result3.previousUsername());
        assertNull(result3.otherUniqueIds());

        // create - changed uuid
        UUID otherUuid = UUID.randomUUID();
        Response<PlayerSaveResult> createResp4 = client.users().create(new CreateUserRequest(otherUuid, otherUsername)).execute();
        assertTrue(createResp4.isSuccessful());
        assertEquals(201, createResp4.code());
        PlayerSaveResult result4 = createResp4.body();
        assertNotNull(result4);
        assertEquals(ImmutableSet.of(PlayerSaveResult.Outcome.CLEAN_INSERT, PlayerSaveResult.Outcome.OTHER_UNIQUE_IDS_PRESENT_FOR_USERNAME), result4.outcomes());
        assertNull(result4.previousUsername());
        assertEquals(ImmutableSet.of(uuid), result4.otherUniqueIds());
    }

    @Test
    public void testUserList() throws IOException {
        LuckPermsClient client = createClient();

        UUID uuid = UUID.randomUUID();
        String username = randomName();

        // create a user & give it a permission
        assertTrue(client.users().create(new CreateUserRequest(uuid, username)).execute().isSuccessful());
        assertTrue(client.users().nodesAdd(uuid, new Node("test.node", true, Collections.emptySet(), null)).execute().isSuccessful());

        Response<Set<UUID>> resp = client.users().list().execute();
        assertTrue(resp.isSuccessful());
        assertNotNull(resp.body());
        assertTrue(resp.body().contains(uuid));
    }

    @Test
    public void testUserLookup() throws IOException {
        LuckPermsClient client = createClient();

        UUID uuid = UUID.randomUUID();
        String username = randomName();

        // create a user
        assertTrue(client.users().create(new CreateUserRequest(uuid, username)).execute().isSuccessful());

        // uuid to username
        Response<UserLookupResult> uuidToUsername = client.users().lookup(uuid).execute();
        assertTrue(uuidToUsername.isSuccessful());
        assertNotNull(uuidToUsername.body());
        assertEquals(username, uuidToUsername.body().username());

        // username to uuid
        Response<UserLookupResult> usernameToUuid = client.users().lookup(username).execute();
        assertTrue(usernameToUuid.isSuccessful());
        assertNotNull(usernameToUuid.body());
        assertEquals(uuid, uuidToUsername.body().uniqueId());

        // not found
        assertEquals(404, client.users().lookup(UUID.randomUUID()).execute().code());
        assertEquals(404, client.users().lookup(randomName()).execute().code());
    }

    @Test
    public void testUserNodes() throws IOException {
        LuckPermsClient client = createClient();

        UUID uuid = UUID.randomUUID();
        String username = randomName();

        // create a user
        assertTrue(client.users().create(new CreateUserRequest(uuid, username)).execute().isSuccessful());

        // get user nodes and validate they are as expected
        List<Node> nodes = client.users().nodes(uuid).execute().body();
        assertNotNull(nodes);
        assertEquals(ImmutableList.of(
                new Node("group.default", true, Collections.emptySet(), null)
        ), nodes);

        long expiryTime = (System.currentTimeMillis() / 1000L) + 60;

        // add a node
        assertTrue(client.users().nodesAdd(uuid, new Node("test.node.one", true, Collections.emptySet(), null)).execute().isSuccessful());

        // add multiple nodes
        assertTrue(client.users().nodesAdd(uuid, ImmutableList.of(
                new Node("test.node.two", false, Collections.emptySet(), null),
                new Node("test.node.three", true, Collections.singleton(new Context("server", "test")), null),
                new Node("test.node.four", false, Collections.emptySet(), expiryTime)
        )).execute().isSuccessful());

        // get user nodes and validate they are as expected
        nodes = client.users().nodes(uuid).execute().body();
        assertNotNull(nodes);
        assertEquals(ImmutableSet.of(
                new Node("group.default", true, Collections.emptySet(), null),
                new Node("test.node.one", true, Collections.emptySet(), null),
                new Node("test.node.two", false, Collections.emptySet(), null),
                new Node("test.node.three", true, Collections.singleton(new Context("server", "test")), null),
                new Node("test.node.four", false, Collections.emptySet(), expiryTime)
        ), ImmutableSet.copyOf(nodes));

        // delete nodes
        assertTrue(client.users().nodesDelete(uuid, ImmutableList.of(
                new Node("test.node.one", true, Collections.emptySet(), null),
                new Node("test.node.two", false, Collections.emptySet(), null)
        )).execute().isSuccessful());

        // get user nodes and validate they are as expected
        nodes = client.users().nodes(uuid).execute().body();
        assertNotNull(nodes);
        assertEquals(ImmutableSet.of(
                new Node("group.default", true, Collections.emptySet(), null),
                new Node("test.node.three", true, Collections.singleton(new Context("server", "test")), null),
                new Node("test.node.four", false, Collections.emptySet(), expiryTime)
        ), ImmutableSet.copyOf(nodes));

        // add a duplicate node with a later expiry time
        long laterExpiryTime = expiryTime + 60;
        assertTrue(client.users().nodesAdd(uuid, new Node("test.node.four", false, Collections.emptySet(), laterExpiryTime), TemporaryNodeMergeStrategy.REPLACE_EXISTING_IF_DURATION_LONGER).execute().isSuccessful());

        // get user nodes and validate they are as expected
        nodes = client.users().nodes(uuid).execute().body();
        assertNotNull(nodes);
        assertEquals(ImmutableSet.of(
                new Node("group.default", true, Collections.emptySet(), null),
                new Node("test.node.three", true, Collections.singleton(new Context("server", "test")), null),
                new Node("test.node.four", false, Collections.emptySet(), laterExpiryTime)
        ), ImmutableSet.copyOf(nodes));

        long evenLaterExpiryTime = expiryTime + 60;

        // add multiple nodes
        assertTrue(client.users().nodesAdd(uuid, ImmutableList.of(
                new Node("test.node.two", false, Collections.emptySet(), null),
                new Node("test.node.three", true, Collections.singleton(new Context("server", "test")), null),
                new Node("test.node.four", false, Collections.emptySet(), evenLaterExpiryTime)
        ), TemporaryNodeMergeStrategy.REPLACE_EXISTING_IF_DURATION_LONGER).execute().isSuccessful());

        // get user nodes and validate they are as expected
        nodes = client.users().nodes(uuid).execute().body();
        assertNotNull(nodes);
        assertEquals(ImmutableSet.of(
                new Node("group.default", true, Collections.emptySet(), null),
                new Node("test.node.two", false, Collections.emptySet(), null),
                new Node("test.node.three", true, Collections.singleton(new Context("server", "test")), null),
                new Node("test.node.four", false, Collections.emptySet(), evenLaterExpiryTime)
        ), ImmutableSet.copyOf(nodes));

        // set nodes
        assertTrue(client.users().nodesSet(uuid, ImmutableList.of(
                new Node("group.default", true, Collections.emptySet(), null),
                new Node("test.node.five", false, Collections.emptySet(), null),
                new Node("test.node.six", true, Collections.singleton(new Context("server", "test")), null),
                new Node("test.node.seven", false, Collections.emptySet(), evenLaterExpiryTime)
        )).execute().isSuccessful());

        // get user nodes and validate they are as expected
        nodes = client.users().nodes(uuid).execute().body();
        assertNotNull(nodes);
        assertEquals(ImmutableSet.of(
                new Node("group.default", true, Collections.emptySet(), null),
                new Node("test.node.five", false, Collections.emptySet(), null),
                new Node("test.node.six", true, Collections.singleton(new Context("server", "test")), null),
                new Node("test.node.seven", false, Collections.emptySet(), evenLaterExpiryTime)
        ), ImmutableSet.copyOf(nodes));

        // delete all nodes
        assertTrue(client.users().nodesDelete(uuid).execute().isSuccessful());

        // get user nodes and validate they are as expected
        nodes = client.users().nodes(uuid).execute().body();
        assertNotNull(nodes);
        assertEquals(ImmutableSet.of(
                new Node("group.default", true, Collections.emptySet(), null)
        ), ImmutableSet.copyOf(nodes));
    }

    @Test
    public void testUserMetadata() throws IOException {
        LuckPermsClient client = createClient();

        UUID uuid = UUID.randomUUID();
        String username = randomName();

        // create a user
        assertTrue(client.users().create(new CreateUserRequest(uuid, username)).execute().isSuccessful());

        // set some permissions
        assertTrue(client.users().nodesAdd(uuid, ImmutableList.of(
                new Node("prefix.100.&c[Admin] ", true, Collections.emptySet(), null),
                new Node("suffix.100. test", true, Collections.emptySet(), null),
                new Node("meta.hello.world", true, Collections.emptySet(), null)
        )).execute().isSuccessful());

        // assert metadata
        Response<Metadata> resp = client.users().metadata(uuid).execute();
        assertTrue(resp.isSuccessful());

        Metadata metadata = resp.body();
        assertNotNull(metadata);

        assertEquals("&c[Admin] ", metadata.prefix());
        assertEquals(" test", metadata.suffix());
        assertEquals("default", metadata.primaryGroup());

        Map<String, String> metaMap = metadata.meta();
        assertEquals("world", metaMap.get("hello"));
    }

    @Test
    public void testUserSearch() throws IOException {
        LuckPermsClient client = createClient();

        // clear existing users
        Set<UUID> existingUsers = client.users().list().execute().body();
        if (existingUsers != null) {
            for (UUID u : existingUsers) {
                client.users().delete(u).execute();
            }
        }

        UUID uuid = UUID.randomUUID();
        String username = randomName();

        // create a user
        assertTrue(client.users().create(new CreateUserRequest(uuid, username)).execute().isSuccessful());

        // set some permissions
        long expiryTime = (System.currentTimeMillis() / 1000L) + 60;
        assertTrue(client.users().nodesAdd(uuid, ImmutableList.of(
                new Node("test.node.one", true, Collections.emptySet(), null),
                new Node("test.node.two", false, Collections.emptySet(), null),
                new Node("test.node.three", true, Collections.singleton(new Context("server", "test")), null),
                new Node("test.node.four", false, Collections.emptySet(), expiryTime),
                new Node("prefix.100.&c[Admin] ", true, Collections.emptySet(), null),
                new Node("suffix.100. test", true, Collections.emptySet(), null),
                new Node("meta.hello.world", true, Collections.emptySet(), null)
        )).execute().isSuccessful());

        // searchNodesByKey
        Response<List<UserSearchResult>> resp1 = client.users().searchNodesByKey("test.node.one").execute();
        assertTrue(resp1.isSuccessful());
        assertNotNull(resp1.body());
        assertEquals(ImmutableList.of(
                new UserSearchResult(uuid, ImmutableList.of(new Node("test.node.one", true, Collections.emptySet(), null)))
        ), resp1.body());

        // searchNodesByKeyStartsWith
        Response<List<UserSearchResult>> resp2 = client.users().searchNodesByKeyStartsWith("test.node").execute();
        assertTrue(resp2.isSuccessful());
        assertNotNull(resp2.body());
        assertEquals(ImmutableList.of(
                new UserSearchResult(uuid, ImmutableList.of(
                        new Node("test.node.one", true, Collections.emptySet(), null),
                        new Node("test.node.two", false, Collections.emptySet(), null),
                        new Node("test.node.three", true, Collections.singleton(new Context("server", "test")), null),
                        new Node("test.node.four", false, Collections.emptySet(), expiryTime)
                ))
        ), resp2.body());

        // searchNodesByMetaKey
        Response<List<UserSearchResult>> resp3 = client.users().searchNodesByMetaKey("hello").execute();
        assertTrue(resp3.isSuccessful());
        assertNotNull(resp3.body());
        assertEquals(ImmutableList.of(
                new UserSearchResult(uuid, ImmutableList.of(new Node("meta.hello.world", true, Collections.emptySet(), null)))
        ), resp3.body());

        // searchNodesByType
        Response<List<UserSearchResult>> resp4 = client.users().searchNodesByType("prefix").execute();
        assertTrue(resp4.isSuccessful());
        assertNotNull(resp4.body());
        assertEquals(ImmutableList.of(
                new UserSearchResult(uuid, ImmutableList.of(new Node("prefix.100.&c[Admin] ", true, Collections.emptySet(), null)))
        ), resp4.body());
    }

    @Test
    public void testUserPermissionCheck() throws IOException {
        LuckPermsClient client = createClient();

        UUID uuid = UUID.randomUUID();
        String username = randomName();

        // create a user
        assertTrue(client.users().create(new CreateUserRequest(uuid, username)).execute().isSuccessful());

        // set some permissions
        assertTrue(client.users().nodesAdd(uuid, ImmutableList.of(
                new Node("test.node.one", true, Collections.emptySet(), null),
                new Node("test.node.two", false, Collections.emptySet(), null),
                new Node("test.node.three", true, Collections.singleton(new Context("server", "test")), null)
        )).execute().isSuccessful());

        Response<PermissionCheckResult> resp0 = client.users().permissionCheck(uuid, "test.node.zero").execute();
        assertTrue(resp0.isSuccessful());
        assertNotNull(resp0.body());
        assertEquals(PermissionCheckResult.Tristate.UNDEFINED, resp0.body().result());
        assertNull(resp0.body().node());

        Response<PermissionCheckResult> resp1 = client.users().permissionCheck(uuid, "test.node.one").execute();
        assertTrue(resp1.isSuccessful());
        assertNotNull(resp1.body());
        assertEquals(PermissionCheckResult.Tristate.TRUE, resp1.body().result());
        assertEquals(new Node("test.node.one", true, Collections.emptySet(), null), resp1.body().node());

        Response<PermissionCheckResult> resp2 = client.users().permissionCheck(uuid, "test.node.two").execute();
        assertTrue(resp2.isSuccessful());
        assertNotNull(resp2.body());
        assertEquals(PermissionCheckResult.Tristate.FALSE, resp2.body().result());
        assertEquals(new Node("test.node.two", false, Collections.emptySet(), null), resp2.body().node());

        Response<PermissionCheckResult> resp3 = client.users().permissionCheck(uuid, "test.node.three").execute();
        assertTrue(resp3.isSuccessful());
        assertNotNull(resp3.body());
        assertEquals(PermissionCheckResult.Tristate.UNDEFINED, resp3.body().result());
        assertNull(resp3.body().node());

        Response<PermissionCheckResult> resp4 = client.users().permissionCheck(uuid, new PermissionCheckRequest(
                "test.node.three",
                new QueryOptions(null, null, ImmutableSet.of(new Context("server", "test"), new Context("world", "aaa")))
        )).execute();
        assertTrue(resp4.isSuccessful());
        assertNotNull(resp4.body());
        assertEquals(PermissionCheckResult.Tristate.TRUE, resp4.body().result());
        assertEquals(new Node("test.node.three", true, Collections.singleton(new Context("server", "test")), null), resp4.body().node());
    }

    @Test
    public void testUserPromoteDemote() throws IOException {
        LuckPermsClient client = createClient();

        // create a user
        UUID uuid = UUID.randomUUID();
        String username = randomName();
        assertTrue(client.users().create(new CreateUserRequest(uuid, username)).execute().isSuccessful());

        // create a track
        String trackName = randomName();
        assertTrue(client.tracks().create(new CreateTrackRequest(trackName)).execute().isSuccessful());

        // create some groups
        Group group1 = Objects.requireNonNull(client.groups().create(new CreateGroupRequest(randomName())).execute().body());
        Group group2 = Objects.requireNonNull(client.groups().create(new CreateGroupRequest(randomName())).execute().body());
        Group group3 = Objects.requireNonNull(client.groups().create(new CreateGroupRequest(randomName())).execute().body());
        ImmutableList<String> groupNames = ImmutableList.of(group1.name(), group2.name(), group3.name());

        // update the track
        assertTrue(client.tracks().update(trackName, new UpdateTrackRequest(groupNames)).execute().isSuccessful());

        // promote the user along the track
        Response<PromotionResult> promoteResp = client.users().promote(uuid, new TrackRequest(trackName, ImmutableSet.of())).execute();
        assertTrue(promoteResp.isSuccessful());
        PromotionResult promoteResult = promoteResp.body();
        assertNotNull(promoteResult);
        assertTrue(promoteResult.success());
        assertEquals(PromotionResult.Status.ADDED_TO_FIRST_GROUP, promoteResult.status());
        assertNull(promoteResult.groupFrom());
        assertEquals(group1.name(), promoteResult.groupTo());

        // promote the user along the track (again)
        promoteResp = client.users().promote(uuid, new TrackRequest(trackName, ImmutableSet.of())).execute();
        assertTrue(promoteResp.isSuccessful());
        promoteResult = promoteResp.body();
        assertNotNull(promoteResult);
        assertTrue(promoteResult.success());
        assertEquals(PromotionResult.Status.SUCCESS, promoteResult.status());
        assertEquals(group1.name(), promoteResult.groupFrom());
        assertEquals(group2.name(), promoteResult.groupTo());

        // demote the user along the track
        Response<DemotionResult> demoteResp = client.users().demote(uuid, new TrackRequest(trackName, ImmutableSet.of())).execute();
        assertTrue(demoteResp.isSuccessful());
        DemotionResult demoteResult = demoteResp.body();
        assertNotNull(demoteResult);
        assertTrue(demoteResult.success());
        assertEquals(DemotionResult.Status.SUCCESS, demoteResult.status());
        assertEquals(group2.name(), demoteResult.groupFrom());
        assertEquals(group1.name(), demoteResult.groupTo());

        // demote the user along the track (again)
        demoteResp = client.users().demote(uuid, new TrackRequest(trackName, ImmutableSet.of())).execute();
        assertTrue(demoteResp.isSuccessful());
        demoteResult = demoteResp.body();
        assertNotNull(demoteResult);
        assertTrue(demoteResult.success());
        assertEquals(DemotionResult.Status.REMOVED_FROM_FIRST_GROUP, demoteResult.status());
        assertEquals(group1.name(), demoteResult.groupFrom());
        assertNull(demoteResult.groupTo());
    }
}
