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
import net.luckperms.rest.model.CreateGroupRequest;
import net.luckperms.rest.model.CreateTrackRequest;
import net.luckperms.rest.model.Group;
import net.luckperms.rest.model.Track;
import net.luckperms.rest.model.UpdateTrackRequest;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;
import retrofit2.Response;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrackServiceTest extends AbstractIntegrationTest {

    @Test
    public void testTrackCrud() throws IOException {
        LuckPermsClient client = createClient();

        String name = randomName();

        // create
        Response<Track> createResp = client.tracks().create(new CreateTrackRequest(name)).execute();
        assertTrue(createResp.isSuccessful());
        Track track = createResp.body();
        assertNotNull(track);
        assertEquals(name, track.name());
        assertEquals(ImmutableList.of(), track.groups());

        // create - already exists
        assertEquals(409, client.tracks().create(new CreateTrackRequest(name)).execute().code());

        // read
        Response<Track> readResp = client.tracks().get(name).execute();
        assertTrue(readResp.isSuccessful());
        track = readResp.body();
        assertNotNull(track);
        assertEquals(name, track.name());
        assertEquals(ImmutableList.of(), track.groups());

        // update
        Group group1 = Objects.requireNonNull(client.groups().create(new CreateGroupRequest(randomName())).execute().body());
        Group group2 = Objects.requireNonNull(client.groups().create(new CreateGroupRequest(randomName())).execute().body());
        Group group3 = Objects.requireNonNull(client.groups().create(new CreateGroupRequest(randomName())).execute().body());
        ImmutableList<String> groupNames = ImmutableList.of(group1.name(), group2.name(), group3.name());

        Response<Void> updateResp = client.tracks().update(name, new UpdateTrackRequest(groupNames)).execute();
        assertTrue(updateResp.isSuccessful());

        // read - again
        readResp = client.tracks().get(name).execute();
        assertTrue(readResp.isSuccessful());
        track = readResp.body();
        assertNotNull(track);
        assertEquals(name, track.name());
        assertEquals(groupNames, track.groups());

        // delete
        Response<Void> deleteResp = client.tracks().delete(name).execute();
        assertTrue(deleteResp.isSuccessful());

        // not found
        assertEquals(404, client.tracks().get(name).execute().code());
        assertEquals(404, client.tracks().delete(name).execute().code());
    }

    @Test
    public void testTrackList() throws IOException {
        LuckPermsClient client = createClient();

        String name = randomName();

        // create a track
        assertTrue(client.tracks().create(new CreateTrackRequest(name)).execute().isSuccessful());

        Response<Set<String>> resp = client.tracks().list().execute();
        assertTrue(resp.isSuccessful());
        assertNotNull(resp.body());
        assertTrue(resp.body().contains(name));
    }

}
