package com.cheng.gu.gc_collapser.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cheng.gu.gc_collapser.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gc on 2016/7/15.
 */
public class ExpandableRecyclerViewAdapterPlus<T, H> extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = ExpandableRecyclerViewAdapterPlus.class.getSimpleName();
    private static final int TYPE_GROUP = 0;
    private static final int TYPE_CHILD = 1;
    private List<T> mGroupDataList;
    private List<List<H>> mChildDataList;
    private LayoutInflater mInflater;
    private int mSelectedGroupPos = -1;
    private int mSelectedChildPos = -1;
    private int mLastSelectedGroup = -1;
    private List<Object> mData;//显示的数据

    private Context mContext;

    private int mGroupLayoutId = -1;
    private int mChildLayoutId = -1;
    /**
     * 真实选中的子项位置
     */
    private int mActualSelectedGroupPos = -1;
    private int mActualSelectedChildPos = -1;


    private OnChildClickListener onChildClickListener;

    public void loadMore(T title, List<H> results) {
        mGroupDataList.add(title);
        mChildDataList.add(results);
        mData.add(title);
        notifyItemInserted(mData.size()-1);
    }

    public interface OnChildClickListener {
        void onChildClick(int groupPos, int ChildPos, Object obj);
    }

    public void setOnChildClickListener(OnChildClickListener listener) {
        onChildClickListener = listener;
    }


    private OnItemSelectedChangeListener onItemSelectedChangeListener;

    public interface OnItemSelectedChangeListener {
        void selectedChang(int groupPos, int childPos);
    }

    public void setOnItemSelectedChangeListener(OnItemSelectedChangeListener listener) {
        onItemSelectedChangeListener = listener;
    }


    private DataBindAndFaceModeManager dataBindAndFaceModeManager;

    public interface DataBindAndFaceModeManager {
        void showDefaultChildFace(ViewHolder holder);

        void showSelectedChildFace(ViewHolder holder);

        void bindChildData(ViewHolder holder, int groupPos, int childPos);

        void bindGroupData(ViewHolder holder, int groupPos);

//        void showSelectedGroupFace_WithChild(ViewHolder holder);
//        void showSelectedGroupFace_WithoutChild(ViewHolder holder);

        void showCollapsedGroupFace(ViewHolder holder);

        void showExpandedGroupFace(ViewHolder holder);

    }

    public void setDataBindAndFaceModeManager(DataBindAndFaceModeManager manager) {
        dataBindAndFaceModeManager = manager;
    }


    public ExpandableRecyclerViewAdapterPlus(Context context, List<T> groupList, List<List<H>> childList,
                                             @LayoutRes int groupLayoutId, @LayoutRes int childLayoutId) {

        mGroupDataList = new ArrayList<T>();
        mChildDataList = new ArrayList<List<H>>();

        if (groupList != null) {
            mGroupDataList.addAll(groupList);
            mChildDataList.addAll(childList);
        }
//        mGroupDataList = groupList == null ? new ArrayList<T>() : groupList;
//        mChildDataList = childList == null ? new ArrayList<List<H>>() : childList;

        if (getGroupCount() != mChildDataList.size()) {
            throw new IllegalArgumentException(
                    "The number of group data source must equals the child size!");
        }

        mInflater = LayoutInflater.from(context);
        mContext = context;
        mData = new ArrayList<Object>();
        mData.addAll(mGroupDataList);
        this.mGroupLayoutId = groupLayoutId;
        this.mChildLayoutId = childLayoutId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = null;
        switch (viewType) {
            case TYPE_GROUP:
                itemView = mInflater.inflate(mGroupLayoutId, parent, false);
                break;
            case TYPE_CHILD:
                itemView = mInflater.inflate(mChildLayoutId, parent, false);
                break;
        }

        ViewHolder holder = ViewHolder.createViewHolder(mContext, itemView);

        return holder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (dataBindAndFaceModeManager != null) {
            if (isChild(position)) {
                dataBindAndFaceModeManager.showDefaultChildFace(holder);
            } else {
                dataBindAndFaceModeManager.showCollapsedGroupFace(holder);
            }

            if (mSelectedGroupPos == -1) {
                dataBindAndFaceModeManager.bindGroupData(holder, position);
//                dataBindAndFaceModeManager.showCollapsedGroupFace(holder);
            } else {
                if (position <= mSelectedGroupPos) {
                    dataBindAndFaceModeManager.bindGroupData(holder, position);

//                    if (!hasChild(position)) {
//                        if (mActualSelectedGroupPos == position) {
//                            dataBindAndFaceModeManager.showSelectedGroupFace_WithoutChild(holder);
//                        }
//                    } else {
//                        if (mActualSelectedGroupPos == position) {
//                            dataBindAndFaceModeManager.showSelectedGroupFace_WithChild(holder);
//                        }
//                    }
                    if (mActualSelectedGroupPos == position) {
                        dataBindAndFaceModeManager.showExpandedGroupFace(holder);
                    }

                } else if (isChild(position)) {
                    int childPos = position - mSelectedGroupPos - 1;
                    dataBindAndFaceModeManager.bindChildData(holder, mSelectedGroupPos, childPos);

                    /**
                     * 只有展开父条目 然后点击子条目才会生效颜色变化
                     *  二次触发才算更改选中项
                     */
                    if (mActualSelectedGroupPos == mSelectedGroupPos) {
                        if (childPos == mActualSelectedChildPos) {
                            dataBindAndFaceModeManager.showSelectedChildFace(holder);
                        }
                    }

                } else {

                    int groupPos = position - getChildCount(mSelectedGroupPos);
                    dataBindAndFaceModeManager.bindGroupData(holder, groupPos);

//                    if (!hasChild(groupPos)) {
//                        if (mActualSelectedGroupPos == groupPos) {
//                            dataBindAndFaceModeManager.showSelectedGroupFace_WithoutChild(holder);
//                        }
//                    } else {
//                        if (mActualSelectedGroupPos == groupPos) {
//                            dataBindAndFaceModeManager.showSelectedGroupFace_WithChild(holder);
//                        }
//                    }
                    if (mActualSelectedGroupPos == position) {
                        dataBindAndFaceModeManager.showExpandedGroupFace(holder);
                    }

                }
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int layoutPosition = holder.getLayoutPosition();

                if (mSelectedGroupPos == -1) {//初始展开一项
                    setSelectedPosition(layoutPosition, -1);
                    if (!hasChild(mSelectedGroupPos)) {

                        if (mActualSelectedGroupPos == mSelectedGroupPos /*&& mActualSelectedChildPos ==
                        layoutPosition - mSelectedGroupPos - 1*/) {
                            //没有变化，不注册事件
                            return;
                        }

                        mActualSelectedGroupPos = mSelectedGroupPos;

                        if (onItemSelectedChangeListener != null) {
                            onItemSelectedChangeListener.selectedChang(mActualSelectedGroupPos, -1);
                        }

//                        notifyDataSetChanged();

//                        Toast.makeText(v.getContext(), mActualSelectedGroupPos + "--a--", Toast
//                                .LENGTH_SHORT).show();
                        Log.d(TAG, "onClick--1: groupPos:"+mActualSelectedGroupPos);
                    }

                } else {
                    if (isChild(layoutPosition)) {

                        int childPos = layoutPosition - mSelectedGroupPos - 1;

                        setSelectedPosition(mSelectedGroupPos, childPos);

                        if (onChildClickListener != null) {
                            onChildClickListener.onChildClick(mSelectedGroupPos, childPos, mData.get
                                    (layoutPosition));
                        }
//                        Toast.makeText(mContext, "-->onChildClick:   groupPos:" + mSelectedGroupPos + "" +
//                                " childPos:" + childPos, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onClick--2: "+"-->onChildClick:   groupPos:" + mSelectedGroupPos + "" +
                                " childPos:" + childPos);

                        /**
                         * 点击子项,保存状态
                         */

                        if (mActualSelectedGroupPos == mSelectedGroupPos && mActualSelectedChildPos ==
                                childPos) {
                            //没有变化，不注册事件
                            return;
                        }


                        //当在一个组内选择另一个选项时
                        /*if (mActualSelectedGroupPos == mSelectedGroupPos && mActualSelectedChildPos !=
                        childPos){
                            *//**
                         * 更新上一次选中项
                         *//*
                            notifyItemChanged(mActualSelectedGroupPos + mActualSelectedChildPos + 1);
                            notifyItemChanged(layoutPosition);
                        }else {
                            notifyItemChanged(layoutPosition);
                        }*/

                        mActualSelectedGroupPos = mSelectedGroupPos;
                        mActualSelectedChildPos = childPos;

                        if (onItemSelectedChangeListener != null) {
                            onItemSelectedChangeListener.selectedChang(mActualSelectedGroupPos,
                                    mActualSelectedChildPos);
                        }

//                        notifyDataSetChanged();

//                        Toast.makeText(v.getContext(), mActualSelectedGroupPos + "--b--" +
//                                mActualSelectedChildPos, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onClick--3: groupPos:"+mActualSelectedGroupPos);
                    } else {

                        if (layoutPosition <= mSelectedGroupPos) {
                            setSelectedPosition(layoutPosition, -1);

                            /**
                             * 如果没有子项，那么就注册父项的选中事件
                             */

                            if (!hasChild(layoutPosition)) {
                                if (mActualSelectedGroupPos == mSelectedGroupPos /*&&
                                mActualSelectedChildPos == layoutPosition - mSelectedGroupPos - 1*/) {
                                    //没有变化，不注册事件
                                    return;
                                }

                                /**
                                 * 需要考虑上一次选中的是子项还是父项
                                 * 如果是子项，这次只需要刷新本次点击项
                                 * 如果是父项，需要刷新上次和本次点击项
                                 */
                                /*if (mActualSelectedChildPos != -1){
                                    notifyItemChanged(layoutPosition);
                                }else{
                                    if (mActualSelectedGroupPos != -1){
                                        notifyItemChanged(mActualSelectedGroupPos);
                                        notifyItemChanged(layoutPosition);
                                    }
                                }*/

                                mActualSelectedGroupPos = mSelectedGroupPos;
//                                mActualSelectedChildPos = mSelectedChildPos;

                                if (onItemSelectedChangeListener != null) {
                                    onItemSelectedChangeListener.selectedChang(mActualSelectedGroupPos,
                                            -1/*mActualSelectedChildPos*/);
                                }

//                                Toast.makeText(v.getContext(), mActualSelectedGroupPos + "--c--", Toast
//                                        .LENGTH_SHORT).show();
                                Log.d(TAG, "onClick--4: groupPos:"+mActualSelectedGroupPos);

//                                notifyDataSetChanged();

                                /**
                                 * 此处存在一个BUG
                                 * 当有子项的父项展开式 直接点击另一个没有子项的父项，那么子项的关闭动画还没执行，就直接调用了notify
                                 * 所以动画效果看不到
                                 * 但是如果写成notifyItemRangeChanged(mActualSelectedGroupPos);
                                 * 那么 该无子项的父项会先于动画出现在错乱的位置。
                                 * 效果不理想，还不如这种情况不显示动画。
                                 * 可以考虑在这个notify代码执行前加个 延迟。
                                 */
//                                notifyItemRangeChanged(mActualSelectedGroupPos);

                            }


                        } else {
                            int groupPos = layoutPosition - getChildCount(mSelectedGroupPos);

                            setSelectedPosition(groupPos, -1);

                            if (!hasChild(groupPos)) {

                                if (mActualSelectedGroupPos == groupPos /*&& mActualSelectedChildPos ==
                                layoutPosition - mSelectedGroupPos - 1*/) {
                                    //没有变化，不注册事件
                                    return;
                                }

                                /**
                                 * 需要考虑上一次选中的是子项还是父项
                                 * 如果是子项，这次只需要刷新本次点击项
                                 * 如果是父项，需要刷新上次和本次点击项
                                 */
                                /*if (mActualSelectedChildPos != -1) {
                                    notifyItemChanged(layoutPosition);
                                }else{
                                    if (mActualSelectedGroupPos != -1){
                                        notifyItemChanged(mActualSelectedGroupPos);
                                        notifyItemChanged(layoutPosition);
                                    }
                                }*/

                                mActualSelectedGroupPos = groupPos;

                                if (onItemSelectedChangeListener != null) {
                                    onItemSelectedChangeListener.selectedChang(mActualSelectedGroupPos,
                                            -1/*mActualSelectedChildPos*/);
                                }

//                                notifyDataSetChanged();
                                /**
                                 * 此处存在一个BUG
                                 * 当有子项的父项展开式 直接点击另一个没有子项的父项，那么子项的关闭动画还没执行，就直接调用了notify
                                 * 所以动画效果看不到
                                 * 但是如果写成notifyItemRangeChanged(mActualSelectedGroupPos);
                                 * 那么 该无子项的父项会先于动画出现在错乱的位置。
                                 * 效果不理想，还不如这种情况不显示动画。
                                 * 可以考虑在这个notify代码执行前加个 延迟。
                                 */
//                                notifyItemRangeChanged(mActualSelectedGroupPos);

//                                Toast.makeText(v.getContext(), mActualSelectedGroupPos + "--d--", Toast
//                                        .LENGTH_SHORT).show();
                                Log.d(TAG, "onClick--5: groupPos:"+mActualSelectedGroupPos);
                            }

                        }
                    }
                }
            }
        });

    }


    public void setActualSelectedPosition(int groupPosition, int childPosition) {
        if (groupPosition != -1) {
            this.mActualSelectedGroupPos = groupPosition;
            this.mActualSelectedChildPos = childPosition;
        }
        setSelectedPosition(groupPosition, childPosition);
    }


    /**
     * set selected item
     *
     * @param groupPosition
     * @param childPosition
     */
    private void setSelectedPosition(int groupPosition, int childPosition) {

        Log.d(TAG, "setSelectedPosition: " + groupPosition);

        mLastSelectedGroup = mSelectedGroupPos;

        if (mLastSelectedGroup == groupPosition && childPosition != -1) {
            //click child
            mSelectedChildPos = childPosition;
            return;
        }

        if (mLastSelectedGroup != -1) {
//            for (int i = 0; i < getChildCount(mLastSelectedGroup); i++) {
//                mData.remove(mLastSelectedGroup + 1);//去掉3个值
//                notifyItemRemoved(mLastSelectedGroup + 1);
//            }
//
            mData.removeAll(mChildDataList.get(mLastSelectedGroup));
            notifyItemRangeRemoved(mLastSelectedGroup+1,getChildCount(mLastSelectedGroup));
            if (mLastSelectedGroup == groupPosition && hasChild(groupPosition)) {
                mSelectedGroupPos = -1;
                return;
            }
        }

        this.mSelectedGroupPos = groupPosition;
        this.mSelectedChildPos = childPosition;


        if (mSelectedGroupPos != -1 && mSelectedGroupPos != mLastSelectedGroup) {
            if (hasChild(mSelectedGroupPos)) {
//                for (int i = 0; i < getChildCount(mSelectedGroupPos); i++) {
//                    mData.add(mSelectedGroupPos + 1, mChildDataList.get(mSelectedGroupPos).get
//                            (getChildCount(mSelectedGroupPos) - 1 - i));
//                    notifyItemInserted(mSelectedGroupPos + 1);
//                }
                mData.addAll(mSelectedGroupPos + 1,mChildDataList.get(mSelectedGroupPos));
                notifyItemRangeInserted(mSelectedGroupPos + 1,getChildCount(mSelectedGroupPos));
            }
        }


    }


    /**
     * judge is selected child or not
     *
     * @param position
     * @return
     */
    public boolean isSelectedChild(int position) {
        return (mActualSelectedGroupPos + mActualSelectedChildPos + 1) == position;
    }


    /**
     * @param position
     * @return
     */
    public boolean isSelectedGroup(int position) {
        return mActualSelectedGroupPos == position;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * has child or not
     *
     * @param groupPos
     * @return
     */
    public boolean hasChild(int groupPos) {
        return mChildDataList.get(groupPos) == null ? false : mChildDataList.get(groupPos).size() != 0;
    }

    /**
     * is child or not
     *
     * @param position
     * @return
     */
    public boolean isChild(int position) {
        if (mSelectedGroupPos != -1) {
            return position > mSelectedGroupPos && position < mSelectedGroupPos +
                    getChildCount(mSelectedGroupPos) + 1;
        }
        return false;
    }

    /**
     * get the child count by group pos
     *
     * @param groupPosition
     * @return
     */
    public int getChildCount(int groupPosition) {
        if (groupPosition < mGroupDataList.size()) {
            return mChildDataList.get(groupPosition) == null ? 0 : mChildDataList.get(groupPosition).size();
        }
        return 0;
    }

    /**
     * group count
     *
     * @return
     */
    public int getGroupCount() {
        int count = mGroupDataList.size();
        return count;
    }

    @Override
    public int getItemViewType(int position) {

        if (isChild(position)) {
            return TYPE_CHILD;
        }

        return TYPE_GROUP;
    }

}
