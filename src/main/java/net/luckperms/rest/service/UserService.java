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

import net.luckperms.rest.model.CreateUserRequest;
import net.luckperms.rest.model.DemotionResult;
import net.luckperms.rest.model.Metadata;
import net.luckperms.rest.model.Node;
import net.luckperms.rest.model.NodeType;
import net.luckperms.rest.model.PermissionCheckRequest;
import net.luckperms.rest.model.PermissionCheckResult;
import net.luckperms.rest.model.PlayerSaveResult;
import net.luckperms.rest.model.PromotionResult;
import net.luckperms.rest.model.TemporaryNodeMergeStrategy;
import net.luckperms.rest.model.TrackRequest;
import net.luckperms.rest.model.UpdateUserRequest;
import net.luckperms.rest.model.User;
import net.luckperms.rest.model.UserLookupResult;
import net.luckperms.rest.model.UserSearchResult;
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
import java.util.UUID;

public interface UserService {

    @GET("/user")
    Call<Set<UUID>> list();

    @POST("/user")
    Call<PlayerSaveResult> create(@Body CreateUserRequest req);

    @GET("/user/lookup")
    Call<UserLookupResult> lookup(@Query("username") String username);

    @GET("/user/lookup")
    Call<UserLookupResult> lookup(@Query("uniqueId") UUID uniqueId);

    @GET("/user/search")
    Call<List<UserSearchResult>> searchNodesByKey(@Query("key") String key);

    @GET("/user/search")
    Call<List<UserSearchResult>> searchNodesByKeyStartsWith(@Query("keyStartsWith") String keyStartsWith);

    @GET("/user/search")
    Call<List<UserSearchResult>> searchNodesByMetaKey(@Query("metaKey") String metaKey);

    @GET("/user/search")
    Call<List<UserSearchResult>> searchNodesByType(@Query("type") NodeType type);

    @GET("/user/{uniqueId}")
    Call<User> get(@Path("uniqueId") UUID uniqueId);

    @PATCH("/user/{uniqueId}")
    Call<Void> update(@Path("uniqueId") UUID uniqueId, @Body UpdateUserRequest req);

    @DELETE("/user/{uniqueId}")
    Call<Void> delete(@Path("uniqueId") UUID uniqueId);

    @DELETE("/user/{uniqueId}")
    Call<Void> delete(@Path("uniqueId") UUID uniqueId, @Query("playerDataOnly") boolean playerDataOnly);

    @GET("/user/{uniqueId}/nodes")
    Call<List<Node>> nodes(@Path("uniqueId") UUID uniqueId);

    @POST("/user/{uniqueId}/nodes")
    Call<List<Node>> nodesAdd(@Path("uniqueId") UUID uniqueId, @Body Node node);

    @POST("/user/{uniqueId}/nodes")
    Call<List<Node>> nodesAdd(@Path("uniqueId") UUID uniqueId, @Body Node node, @Query("temporaryNodeMergeStrategy") TemporaryNodeMergeStrategy temporaryNodeMergeStrategy);

    @PATCH("/user/{uniqueId}/nodes")
    Call<List<Node>> nodesAdd(@Path("uniqueId") UUID uniqueId, @Body List<Node> nodes);

    @PATCH("/user/{uniqueId}/nodes")
    Call<List<Node>> nodesAdd(@Path("uniqueId") UUID uniqueId, @Body List<Node> nodes, @Query("temporaryNodeMergeStrategy") TemporaryNodeMergeStrategy temporaryNodeMergeStrategy);

    @PUT("/user/{uniqueId}/nodes")
    Call<Void> nodesSet(@Path("uniqueId") UUID uniqueId, @Body List<Node> nodes);

    @DELETE("/user/{uniqueId}/nodes")
    Call<Void> nodesDelete(@Path("uniqueId") UUID uniqueId);

    @HTTP(method = "DELETE", path = "/user/{uniqueId}/nodes", hasBody = true)
    Call<Void> nodesDelete(@Path("uniqueId") UUID uniqueId, @Body List<Node> nodes);

    @GET("/user/{uniqueId}/meta")
    Call<Metadata> metadata(@Path("uniqueId") UUID uniqueId);

    @GET("/user/{uniqueId}/permissionCheck")
    Call<PermissionCheckResult> permissionCheck(@Path("uniqueId") UUID uniqueId, @Query("permission") String permission);

    @POST("/user/{uniqueId}/permissionCheck")
    Call<PermissionCheckResult> permissionCheck(@Path("uniqueId") UUID uniqueId, @Body PermissionCheckRequest req);

    @POST("/user/{uniqueId}/promote")
    Call<PromotionResult> promote(@Path("uniqueId") UUID uniqueId, @Body TrackRequest req);

    @POST("/user/{uniqueId}/demote")
    Call<DemotionResult> demote(@Path("uniqueId") UUID uniqueId, @Body TrackRequest req);

}
