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

import net.luckperms.rest.model.CreateGroupRequest;
import net.luckperms.rest.model.Group;
import net.luckperms.rest.model.GroupSearchResult;
import net.luckperms.rest.model.Metadata;
import net.luckperms.rest.model.Node;
import net.luckperms.rest.model.PermissionCheckRequest;
import net.luckperms.rest.model.PermissionCheckResult;
import net.luckperms.rest.model.TemporaryNodeMergeStrategy;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;
import java.util.Set;

public interface GroupService {

    @GET("/group")
    Call<Set<String>> list();

    @POST("/group")
    Call<Group> create(@Body CreateGroupRequest req);

    @GET("/group/search")
    Call<List<GroupSearchResult>> searchNodesByKey(@Query("key") String key);

    @GET("/group/search")
    Call<List<GroupSearchResult>> searchNodesByKeyStartsWith(@Query("keyStartsWith") String keyStartsWith);

    @GET("/group/search")
    Call<List<GroupSearchResult>> searchNodesByMetaKey(@Query("metaKey") String metaKey);

    @GET("/group/search")
    Call<List<GroupSearchResult>> searchNodesByType(@Query("type") String type);

    @GET("/group/{name}")
    Call<Group> get(@Path("name") String name);

    @DELETE("/group/{name}")
    Call<Void> delete(@Path("name") String name);

    @GET("/group/{name}/nodes")
    Call<List<Node>> nodes(@Path("name") String name);

    @POST("/group/{name}/nodes")
    Call<List<Node>> nodesAdd(@Path("name") String name, @Body Node node);

    @POST("/group/{name}/nodes")
    Call<List<Node>> nodesAdd(@Path("name") String name, @Body Node node, @Query("temporaryNodeMergeStrategy") TemporaryNodeMergeStrategy temporaryNodeMergeStrategy);

    @PATCH("/group/{name}/nodes")
    Call<List<Node>> nodesAdd(@Path("name") String name, @Body List<Node> nodes);

    @PATCH("/group/{name}/nodes")
    Call<List<Node>> nodesAdd(@Path("name") String name, @Body List<Node> nodes, @Query("temporaryNodeMergeStrategy") TemporaryNodeMergeStrategy temporaryNodeMergeStrategy);

    @PUT("/group/{name}/nodes")
    Call<Void> nodesSet(@Path("name") String name, @Body List<Node> nodes);

    @DELETE("/group/{name}/nodes")
    Call<Void> nodesDelete(@Path("name") String name);

    @HTTP(method = "DELETE", path = "/group/{name}/nodes", hasBody = true)
    Call<Void> nodesDelete(@Path("name") String name, @Body List<Node> nodes);

    @GET("/group/{name}/meta")
    Call<Metadata> metadata(@Path("name") String name);

    @GET("/group/{name}/permissionCheck")
    Call<PermissionCheckResult> permissionCheck(@Path("name") String name, @Query("permission") String permission);

    @POST("/group/{name}/permissionCheck")
    Call<PermissionCheckResult> permissionCheck(@Path("name") String name, @Body PermissionCheckRequest req);

}
