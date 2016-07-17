package com.cheng.gu.gc_collapser.girls;

import com.cheng.gu.gc_collapser.BasePresenter;
import com.cheng.gu.gc_collapser.BaseVeiw;
import com.cheng.gu.gc_collapser.data.GirlsBean;

import java.util.List;

/**
 * Created by gc on 2016/7/16.
 */
public class GirlsContract {

    interface View extends BaseVeiw {
        void showError();
        void showNormal();
        void loadData(int pos,List<GirlsBean.ResultsBean> data);
    }

    interface Presenter extends BasePresenter{
        void getGirls(int size,int page);
    }


}
