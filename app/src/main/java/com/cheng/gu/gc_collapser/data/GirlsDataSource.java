package com.cheng.gu.gc_collapser.data;

/**
 * Created by gc on 2016/7/16.
 */
public interface GirlsDataSource {

    interface LoadGirlsDataCallback {
        void onGirlsDataLoaded(GirlsBean girlsBean);

        void onDataNotAvailable();
    }

    void getGirls(int page, int size, LoadGirlsDataCallback callback);

    void getGirl(LoadGirlsDataCallback callback);

}
