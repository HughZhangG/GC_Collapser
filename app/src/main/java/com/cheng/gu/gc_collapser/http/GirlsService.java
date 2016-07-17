package com.cheng.gu.gc_collapser.http;

import com.cheng.gu.gc_collapser.data.GirlsBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by gc on 2016/7/16.
 */
public interface GirlsService {
    @GET("api/data/{type}/{count}/{page}")
    Call<GirlsBean> getGirls(@Path("type") String type,
                    @Path("count") int count,
                    @Path("page") int page);
}
