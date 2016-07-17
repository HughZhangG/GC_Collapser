package com.cheng.gu.gc_collapser.data.remote;

import com.cheng.gu.gc_collapser.data.GirlsBean;
import com.cheng.gu.gc_collapser.data.GirlsDataSource;
import com.cheng.gu.gc_collapser.http.GirlsRetrofit;
import com.cheng.gu.gc_collapser.http.GirlsService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gc on 2016/7/16.
 */
public class RemoteGirlsDataSource implements GirlsDataSource {

    @Override
    public void getGirls(int page, int size, final LoadGirlsDataCallback callback) {
        Call<GirlsBean> call = GirlsRetrofit.getRetrofit()
                .create(GirlsService.class)
                .getGirls("福利", page, size);
        call.enqueue(new Callback<GirlsBean>() {
           @Override
           public void onResponse(Call<GirlsBean> call, Response<GirlsBean> response) {
               callback.onGirlsDataLoaded(response.body());
           }

           @Override
           public void onFailure(Call<GirlsBean> call, Throwable t) {
               callback.onDataNotAvailable();
           }
        });

    }

    @Override
    public void getGirl(LoadGirlsDataCallback callback) {
        getGirls(1,1,callback);
    }
}
