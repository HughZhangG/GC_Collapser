package com.cheng.gu.gc_collapser.girls;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheng.gu.gc_collapser.ImageActivity;
import com.cheng.gu.gc_collapser.R;
import com.cheng.gu.gc_collapser.adapter.ExpandableRecyclerViewAdapterPlus;
import com.cheng.gu.gc_collapser.animateor.ScaleInLeftAnimator;
import com.cheng.gu.gc_collapser.base.ViewHolder;
import com.cheng.gu.gc_collapser.data.GirlsBean;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by gc on 2016/7/16.
 */
public class GirlsFragment extends Fragment implements GirlsContract.View, ExpandableRecyclerViewAdapterPlus.OnChildClickListener {

    private static final String TAG = GirlsFragment.class.getSimpleName();
    public static final String BUNDLE_KEY = "BUNDLE_KEY";
    public static final String BUNDLE_KEY_POS = "BUNDLE_KEY_POS";
    @BindView(R.id.id_swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.id_recycler_view)
    RecyclerView mRecyclerView;

    private Context mContext;
    private Unbinder unbinder;
    private GirlsPresenter mPresenter;

    private List<String> mGroup;

    private List<List<GirlsBean.ResultsBean>> mChild;
    private ExpandableRecyclerViewAdapterPlus mAdapterPlus;

    int page = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_girls, container, false);

        initView(inflate, savedInstanceState);
        return inflate;
    }

    private void initView(View view, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
        mPresenter = new GirlsPresenter(this);
        initRecyclerView();
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue
                .COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG,"-----------onRefresh---------");
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        swipeRefreshLayout.setRefreshing(false);
//                    }
//                },100);
                mPresenter.loadMore(page);
            }
        });
        mPresenter.start();
    }

    private void initRecyclerView() {

    }

    @Override
    public void showError() {

    }

    @Override
    public void showNormal() {

    }

    @Override
    public synchronized void loadData(int i, List<GirlsBean.ResultsBean> data) {
        Log.d(TAG, "loadData: " + i);
        page++;
        String groupContent = "第   " + (page) + "  组";
        if (mGroup == null) {
            mGroup = new ArrayList<String>();
        }
        if (mChild == null) {
            mChild = new ArrayList<List<GirlsBean.ResultsBean>>();
        }
        mGroup.add(groupContent);
        mChild.add(data);

        if (mGroup.size() == GirlsPresenter.PAGE_COUNT) {
            swipeRefreshLayout.setRefreshing(false);
            if (mAdapterPlus == null) {
                mAdapterPlus = new ExpandableRecyclerViewAdapterPlus
                        (mContext, mGroup, mChild, R.layout.item_group_plus, R.layout.item_child_plus);
                mAdapterPlus.setDataBindAndFaceModeManager(new ExpandableRecyclerViewAdapterPlus
                        .DataBindAndFaceModeManager() {

                    @Override
                    public void showDefaultChildFace(ViewHolder holder) {

                    }

                    @Override
                    public void showSelectedChildFace(ViewHolder holder) {

                    }

                    @Override
                    public void bindChildData(ViewHolder holder, int groupPos, int childPos) {
                        ImageView imageView = holder.getView(R.id.id_iv_item_child);

                        String url = mChild.get(groupPos).get(childPos).getUrl();
                        Picasso.with(mContext).load(url).into(imageView);
                    }

                    @Override
                    public void bindGroupData(ViewHolder holder, int groupPos) {
                        TextView textView = holder.getView(R.id.id_tv_item_group_title);
                        textView.setText(mGroup.get(groupPos));
                    }

                    @Override
                    public void showCollapsedGroupFace(ViewHolder holder) {

                    }

                    @Override
                    public void showExpandedGroupFace(ViewHolder holder) {

                    }
                });
                mAdapterPlus.setOnChildClickListener(this);
                mRecyclerView.setAdapter(mAdapterPlus);
                mAdapterPlus.setActualSelectedPosition(-1, -1);
                mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager
                        .VERTICAL));
//            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,
// false));
                mRecyclerView.setItemAnimator(new ScaleInLeftAnimator());
            }
        }

    }

    @Override
    public synchronized void loadMore(List<GirlsBean.ResultsBean> results) {
        if (mAdapterPlus != null) {
            page++;
            String title = "第   " + page + "  组";
            mGroup.add(title);
            mChild.add(results);
            mAdapterPlus.loadMore(title,results);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onChildClick(int groupPos, int childPos, Object obj) {
        if (! (obj instanceof GirlsBean.ResultsBean))
            return;
        Intent intent = new Intent(mContext, ImageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_POS,childPos);
        bundle.putSerializable(BUNDLE_KEY, (Serializable) mChild.get(groupPos));
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
