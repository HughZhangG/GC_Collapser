package com.cheng.gu.gc_collapser.data;

import com.cheng.gu.gc_collapser.data.remote.RemoteGirlsDataSource;

/**
 * Created by gc on 2016/7/16.
 */
public class GirlsRepository implements GirlsDataSource{

    private RemoteGirlsDataSource mRemoteGirlsDataSource;

    public GirlsRepository() {
        this.mRemoteGirlsDataSource = new RemoteGirlsDataSource();
    }

    @Override
    public void getGirls(int page, int size, LoadGirlsDataCallback callback) {
        mRemoteGirlsDataSource.getGirls(page,size,callback);
    }

    @Override
    public void getGirl(LoadGirlsDataCallback callback) {
        mRemoteGirlsDataSource.getGirl(callback);
    }
}
