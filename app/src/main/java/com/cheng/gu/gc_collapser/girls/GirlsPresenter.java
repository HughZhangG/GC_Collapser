package com.cheng.gu.gc_collapser.girls;

import com.cheng.gu.gc_collapser.BasePresenter;
import com.cheng.gu.gc_collapser.data.GirlsBean;
import com.cheng.gu.gc_collapser.data.GirlsDataSource;
import com.cheng.gu.gc_collapser.data.GirlsRepository;

/**
 * Created by gc on 2016/7/16.
 */
public class GirlsPresenter implements BasePresenter {

    public static final int PAGE_COUNT = 10;
    public static final int PAGE_SIZE = 10;

    private GirlsContract.View mView;
    private GirlsRepository mRepository;

    public GirlsPresenter(GirlsContract.View view) {
        mView = view;
        mRepository = new GirlsRepository();
    }

    @Override
    public void start() {
        for (int i = 0; i < PAGE_COUNT; i++) {
            final int finalI = i;
            mRepository.getGirls(PAGE_SIZE, i, new GirlsDataSource.LoadGirlsDataCallback() {
                @Override
                public void onGirlsDataLoaded(GirlsBean girlsBean) {
                    mView.loadData(finalI,girlsBean.getResults());
                }

                @Override
                public void onDataNotAvailable() {

                }
            });

        }

    }
}
