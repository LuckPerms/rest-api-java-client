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

import net.luckperms.rest.model.Action;
import net.luckperms.rest.model.ActionPage;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.UUID;

public interface ActionService {

    @GET("/action")
    Call<ActionPage> query();

    @GET("/action")
    Call<ActionPage> query(@Query("pageSize") int pageSize, @Query("pageNumber") int pageNumber);

    @GET("/action")
    Call<ActionPage> querySource(@Query("source") UUID source);

    @GET("/action")
    Call<ActionPage> querySource(@Query("source") UUID source, @Query("pageSize") int pageSize, @Query("pageNumber") int pageNumber);

    @GET("/action")
    Call<ActionPage> queryTargetUser(@Query("user") UUID user);

    @GET("/action")
    Call<ActionPage> queryTargetUser(@Query("user") UUID user, @Query("pageSize") int pageSize, @Query("pageNumber") int pageNumber);

    @GET("/action")
    Call<ActionPage> queryTargetGroup(@Query("group") String group);

    @GET("/action")
    Call<ActionPage> queryTargetGroup(@Query("group") String group, @Query("pageSize") int pageSize, @Query("pageNumber") int pageNumber);

    @GET("/action")
    Call<ActionPage> queryTargetTrack(@Query("track") String track);

    @GET("/action")
    Call<ActionPage> queryTargetTrack(@Query("track") String track, @Query("pageSize") int pageSize, @Query("pageNumber") int pageNumber);

    @GET("/action")
    Call<ActionPage> querySearch(@Query("search") String search);

    @GET("/action")
    Call<ActionPage> querySearch(@Query("search") String search, @Query("pageSize") int pageSize, @Query("pageNumber") int pageNumber);

    @POST("/action")
    Call<Void> submit(@Body Action action);

}
