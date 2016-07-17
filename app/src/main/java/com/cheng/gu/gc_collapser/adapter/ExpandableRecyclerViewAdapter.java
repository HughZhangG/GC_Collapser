package com.cheng.gu.gc_collapser.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cheng.gu.gc_collapser.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guangcheng.Zhang on 2016/7/13.
 * email:zhang411988153@163.com
 *       gucheng.zhang0901@gmail.com
 *
 *
 *
 *
 *  There are two different classes of data change events,
 *  item changes and structural changes.
 *  Item changes are when a single item has its data updated
 *  but no positional changes have occurred. Structural changes
 *  are when items are inserted, removed or moved within the data set.
 *
 *  If you are writing an adapter it will always be more efficient
 *  to use the more specific change events if you can.
 *  Rely on notifyDataSetChanged() as a last resort.
 *
 *
 * notifyDataSetChanged() does:
 *
 * This event does not specify what about the data set has changed,
 * forcing any observers to assume that all existing items
 * and structure may no longer be valid.
 * LayoutManagers will be forced to fully rebind and relayout all visible views.
 *
 * my opinion:
 * notifyDataSetChanged(): can refresh data set and data structure and show mode;
 * notifyItemChange(): can not change the data set or data structure, on  can refresh
 *          the display mode which you set;
 *          so , if you have change the data source. and you would change the show mode
 *          on the base of the changed data source , i think you should notifyDataSetChange()
 *          before your display;
 *
 */
public class ExpandableRecyclerViewAdapter extends RecyclerView
        .Adapter<ExpandableRecyclerViewAdapter.VH> {

    private static final String TAG = ExpandableRecyclerViewAdapter.class.getSimpleName();
    private static final int TYPE_GROUP = 0;
    private static final int TYPE_CHILD = 1;
    private List<Object> mGroupDataList;
    private List<List<Object>> mChildDataList;
    private LayoutInflater mInflater;
    private int mSelectedGroupPos = -1;
    private int mSelectedChildPos = -1;
    private int mLastSelectedGroup = -1;
    private List<Object> mData;//显示的数据

    private Context mContext;

    private static int DEFAULT_GROUP_TEXT_COLOR = Color.BLACK;
    private static int SELECTED_GROUP_TEXT_COLOR = Color.BLUE;

    private static int DEFAULT_CHILD_TEXT_COLOR = Color.WHITE;
    private static int SELECTED_CHILD_TEXT_COLOR = Color.GREEN;


    /**
     * 真实选中的子项位置
     */
    private int mActualSelectedGroupPos = -1;
    private int mActualSelectedChildPos = -1;

    private OnItemSelectedChangeListener onItemSelectedChangeListener;
    public interface OnItemSelectedChangeListener{
        void selectedChang(int groupPos, int childPos);
    }
    public void setOnItemSelectedChangeListener(OnItemSelectedChangeListener listener){
        onItemSelectedChangeListener = listener;
    }


    public ExpandableRecyclerViewAdapter(Context context, List<Object> groupList, List<List<Object>>
            childList) {
        mGroupDataList = groupList == null ? new ArrayList<Object>() : groupList;
        mChildDataList = childList == null ? new ArrayList<List<Object>>() : childList;
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mData = new ArrayList<Object>();
        mData.addAll(mGroupDataList);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = null;
        switch (viewType) {
            case TYPE_GROUP:
                itemView = mInflater.inflate(R.layout.item_group, parent, false);
                break;
            case TYPE_CHILD:
                itemView = mInflater.inflate(R.layout.item_child, parent, false);
                break;
        }

        VH holder = new VH(itemView);

        return holder;
    }



    @Override
    public void onBindViewHolder(final VH holder, final int position) {

        if (isChild(position)){
            holder.textView.setTextColor(DEFAULT_CHILD_TEXT_COLOR);
        }else{
            holder.textView.setTextColor(DEFAULT_GROUP_TEXT_COLOR);
        }


        if (mSelectedGroupPos == -1) {
            holder.textView.setText(mGroupDataList.get(position).toString());
        } else {
            if (position <= mSelectedGroupPos) {
                holder.textView.setText(mGroupDataList.get(position).toString());

                if (!hasChild(position)) {
                    if (mActualSelectedGroupPos == position) {
                        holder.textView.setTextColor(SELECTED_GROUP_TEXT_COLOR);
                    }
                }

            } else if (isChild(position)) {
                holder.textView.setText(mChildDataList.get(mSelectedGroupPos).get(position -
                        mSelectedGroupPos - 1).toString());

                /**
                 * 只有展开父条目 然后点击子条目才会生效颜色变化
                 */
                if (mActualSelectedGroupPos == mSelectedGroupPos) {
                    if (position - mSelectedGroupPos - 1 == mActualSelectedChildPos) {
                        holder.textView.setTextColor(SELECTED_CHILD_TEXT_COLOR);
                    }
                }

            } else {

                int groupPos = position - getChildCount(mSelectedGroupPos);
                holder.textView.setText(mGroupDataList.get(groupPos).toString());

                if (!hasChild(groupPos)) {
                    if (mActualSelectedGroupPos == groupPos) {
                        holder.textView.setTextColor(SELECTED_GROUP_TEXT_COLOR);
                    }
                }

            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int layoutPosition = holder.getLayoutPosition();

                Log.d(TAG, "onClick: " + layoutPosition + "-----");

                if (mSelectedGroupPos == -1) {//初始展开一项
                    setSelectedPosition(layoutPosition, -1);
                    if (!hasChild(mSelectedGroupPos)) {

                        if (mActualSelectedGroupPos == mSelectedGroupPos /*&& mActualSelectedChildPos == layoutPosition - mSelectedGroupPos - 1*/){
                            //没有变化，不注册事件
                            return;
                        }

                        mActualSelectedGroupPos = mSelectedGroupPos;

                        if (onItemSelectedChangeListener != null){
                            onItemSelectedChangeListener.selectedChang(mActualSelectedGroupPos,-1);
                        }

                        notifyDataSetChanged();

                        Toast.makeText(v.getContext(), mActualSelectedGroupPos +"--4--",Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (isChild(layoutPosition)) {

                        int childPos = layoutPosition - mSelectedGroupPos - 1;

                        setSelectedPosition(mSelectedGroupPos, childPos);

                        /**
                         * 点击子项,保存状态
                         */

                        if (mActualSelectedGroupPos == mSelectedGroupPos && mActualSelectedChildPos == childPos){
                            //没有变化，不注册事件
                            return;
                        }


                        //当在一个组内选择另一个选项时
                        /*if (mActualSelectedGroupPos == mSelectedGroupPos && mActualSelectedChildPos != childPos){
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

                        if (onItemSelectedChangeListener != null){
                            onItemSelectedChangeListener.selectedChang(mActualSelectedGroupPos, mActualSelectedChildPos);
                        }

                        notifyDataSetChanged();

                        Toast.makeText(v.getContext(), mActualSelectedGroupPos +"--3--"+ mActualSelectedChildPos,Toast.LENGTH_SHORT).show();
                    } else {

                        if (layoutPosition <= mSelectedGroupPos){
                            setSelectedPosition(layoutPosition,-1);

                            /**
                             * 如果没有子项，那么就注册父项的选中事件
                             */

                            if (!hasChild(layoutPosition)){
                                if (mActualSelectedGroupPos == mSelectedGroupPos /*&& mActualSelectedChildPos == layoutPosition - mSelectedGroupPos - 1*/){
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

                                if (onItemSelectedChangeListener != null){
                                    onItemSelectedChangeListener.selectedChang(mActualSelectedGroupPos,-1/*mActualSelectedChildPos*/);
                                }

                                Toast.makeText(v.getContext(), mActualSelectedGroupPos +"--1--",Toast.LENGTH_SHORT).show();

//                                notifyDataSetChanged();
                                notifyItemRangeChanged(0,mData.size());

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


                        }else {
                            int groupPos = layoutPosition  - getChildCount(mSelectedGroupPos);

                            setSelectedPosition( groupPos, -1);

                            if (!hasChild(groupPos)) {

                                if (mActualSelectedGroupPos == groupPos /*&& mActualSelectedChildPos == layoutPosition - mSelectedGroupPos - 1*/){
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

                                if (onItemSelectedChangeListener != null){
                                    onItemSelectedChangeListener.selectedChang(mActualSelectedGroupPos,-1/*mActualSelectedChildPos*/);
                                }

//                                notifyDataSetChanged();
                                notifyItemRangeChanged(0,mData.size());
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

                                Toast.makeText(v.getContext(), mActualSelectedGroupPos +"--2--",Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }
            }
        });

    }


    public void setActualSelectedPosition(int groupPosition, int childPosition){
        if (groupPosition != -1) {
            this.mActualSelectedGroupPos = groupPosition;
            this.mActualSelectedChildPos = childPosition;
        }
        setSelectedPosition(groupPosition,childPosition);
    }


    /**
     *
     * @param groupPosition
     * @param childPosition
     */
    private void setSelectedPosition(int groupPosition, int childPosition) {

        Log.d(TAG, "setSelectedPosition: " + groupPosition);

        mLastSelectedGroup = mSelectedGroupPos;

        if (mLastSelectedGroup == groupPosition && childPosition != -1){
            //click child
            mSelectedChildPos = childPosition;
            return;
        }

        if (mLastSelectedGroup != -1) {
            for (int i = 0; i < getChildCount(mLastSelectedGroup); i++) {
                notifyItemRemoved(mLastSelectedGroup + 1);
                mData.remove(mLastSelectedGroup + 1);//去掉3个值
            }
            if (mLastSelectedGroup == groupPosition && hasChild(groupPosition)){
                mSelectedGroupPos = -1;
                return;
            }
        }

        this.mSelectedGroupPos = groupPosition;
        this.mSelectedChildPos = childPosition;


        if (mSelectedGroupPos != -1 && mSelectedGroupPos != mLastSelectedGroup) {
            if (hasChild(mSelectedGroupPos)) {
                for (int i = 0; i < getChildCount(mSelectedGroupPos); i++) {
                    mData.add(mSelectedGroupPos + 1, mChildDataList.get(mSelectedGroupPos).get
                            (getChildCount(mSelectedGroupPos) - 1 - i));
                    notifyItemInserted(mSelectedGroupPos + 1);
                }
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
        return mGroupDataList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (isChild(position)) {
            return TYPE_CHILD;
        }

        return TYPE_GROUP;
    }

    public class VH extends RecyclerView.ViewHolder {

        private TextView textView;

        public VH(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.id_tv);
        }
    }

}

