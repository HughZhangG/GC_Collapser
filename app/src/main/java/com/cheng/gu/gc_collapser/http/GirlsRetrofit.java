package com.cheng.gu.gc_collapser.http;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gc on 2016/7/16.
 */
public class GirlsRetrofit {
    private static Retrofit retrofit;
    private static final String BATH_URL = "http://gank.io/";

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            synchronized (GirlsRetrofit.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BATH_URL)
                            .addConverterFactory(GsonConverterFactory.create())
//                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                            .client(MyApplication.defaultOkHttpClient())
                            .build();
                }
            }
        }
        return retrofit;
    }
}
