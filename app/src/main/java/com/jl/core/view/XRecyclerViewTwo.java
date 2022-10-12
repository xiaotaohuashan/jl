package com.jl.core.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.jl.core.log.LogUtils;
import com.jl.core.view.XRecyclerView.AppBarStateChangeListener;
import com.jl.core.view.XRecyclerView.ArrowRefreshHeader;
import com.jl.core.view.XRecyclerView.LoadingMoreFooter;
import com.jl.core.view.XRecyclerView.ProgressStyle;

import java.util.ArrayList;



public class XRecyclerViewTwo extends RecyclerView {
    private int mRefreshProgressStyle = ProgressStyle.SquareSpin; //拉下刷新Progress样式
    private int mLoadingMoreProgressStyle = ProgressStyle.BallRotate; //加载更多Progress样式

    private boolean isLoadingMoreData = false; //是否在加载更多
    private boolean isNoMore = false; //是否到底了
    private boolean pullRefreshEnabled = false; //是否可以下拉刷新
    private boolean loadingMoreEnabled = false; //是否可以加载更多

    private ArrowRefreshHeader mRefreshHeader; //下拉刷新header
    private LoadingMoreFooter mFootView; //加载更多footer

    private ArrayList<View> mHeaderViews = new ArrayList<>(); //header集合
    private ArrayList<View> mFooterViews = new ArrayList<>(); //footer集合

    private WrapAdapter mWrapAdapter; //包装后的adapter
    private AdapterDataObserver mDataObserver = new MyDataObserver(); //adapter数据变化监听

    private LoadingListener mLoadingListener; //下拉刷新和上拉加载更多监听器
    private OnItemClickListener onItemClickListener; //item点击监听器

    //header和footer的itemType
    private static final int TYPE_REFRESH_HEADER = 10000;
    private static final int TYPE_LOAD_FOOTER = 20000;

    private AppBarStateChangeListener.State appbarState = AppBarStateChangeListener.State.EXPANDED; //兼容AppbarLayout
    private float mLastY = -1; //触摸位置Y
    private static final float DRAG_RATE = 3; //下拉刷新粘性


    public XRecyclerViewTwo(Context context) {
        this(context, null);
    }

    public XRecyclerViewTwo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XRecyclerViewTwo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mRefreshHeader = new ArrowRefreshHeader(getContext());
        mRefreshHeader.setProgressStyle(mRefreshProgressStyle);
        mFootView = new LoadingMoreFooter(getContext());
        mFootView.setProgressStyle(mLoadingMoreProgressStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mWrapAdapter = new WrapAdapter(adapter);
        super.setAdapter(mWrapAdapter);
        if (!adapter.hasObservers()) {
            adapter.registerAdapterDataObserver(mDataObserver);
        }
        mDataObserver.onChanged();
    }

    @Override
    public WrapAdapter getAdapter() {
        return mWrapAdapter;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        LogUtils.i("dy= " + dy + ";mFootView= " + mFootView.getParent() + ";isLoadingMoreData= " + !isLoadingMoreData + ";loadingMoreEnabled= " + loadingMoreEnabled + ";mLoadingListener= " + mLoadingListener);
        if (dy > 0 && mFootView.getParent() != null && !isLoadingMoreData && loadingMoreEnabled && mLoadingListener != null) {
            LayoutManager layoutManager = getLayoutManager();
            if (!isNoMore && layoutManager.getChildCount() > 0
                    && computeVerticalScrollRange() > computeVerticalScrollExtent()
                    && !canScrollVertically(mFootView.getHeight() + 5)
                    && mRefreshHeader.getState() == ArrowRefreshHeader.STATE_NORMAL) {
                isLoadingMoreData = true;
                mFootView.setState(LoadingMoreFooter.STATE_LOADING);
                mLoadingListener.onLoadMore();
            }
        }
    }

   /* @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE && !isLoadingMoreData && loadingMoreEnabled && mLoadingListener != null) {
            LayoutManager layoutManager = getLayoutManager();
            if (!isNoMore && layoutManager.getChildCount() > 0
                    && computeVerticalScrollRange() > computeVerticalScrollExtent()
                    && !canScrollVertically(mFootView.getHeight() + 5)
                    && mRefreshHeader.getState() == ArrowRefreshHeader.STATE_NORMAL) {
                isLoadingMoreData = true;
                mFootView.setState(LoadingMoreFooter.STATE_LOADING);
                mLoadingListener.onLoadMore();
            }
        }
    }*/

    //为了兼容SwipeRefreshLayout
    @Override
    public boolean canScrollVertically(int direction) {
        if (getParent() instanceof SwipeRefreshLayout && isOnTop() && direction < 0) {
            return false;
        }
        return super.canScrollVertically(direction);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (pullRefreshEnabled && !(getParent() instanceof SwipeRefreshLayout)) {
            if (mLastY == -1) {
                mLastY = ev.getRawY();
            }
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastY = ev.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaY = ev.getRawY() - mLastY;
                    mLastY = ev.getRawY();
                    if (isOnTop() && appbarState == AppBarStateChangeListener.State.EXPANDED) {
                        mRefreshHeader.onMove(deltaY / DRAG_RATE);
                        if (mRefreshHeader.getVisibleHeight() > 0 && mRefreshHeader.getState() < ArrowRefreshHeader.STATE_REFRESHING) {
                            return false;
                        }
                    }
                    break;
                default:
                    mLastY = -1; // reset
                    if (isOnTop() && appbarState == AppBarStateChangeListener.State.EXPANDED) {
                        if (mRefreshHeader.releaseAction()) {
                            if (mLoadingListener != null) {
                                mLoadingListener.onRefresh();
                            }
                        }
                    }
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 判断mRefreshHeader有没有被加载到XRecyclerView里
     *
     * @return XRecyclerView是否滑动到了顶部
     */
    private boolean isOnTop() {
        if (mRefreshHeader != null && mRefreshHeader.getParent() != null) {
            return true;
        } else {
            return false;
        }
    }

    private class MyDataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            if (mWrapAdapter != null) {
                mWrapAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    }

    public class WrapAdapter extends Adapter<ViewHolder> {
        public Adapter adapter;

        public WrapAdapter(Adapter adapter) {
            this.adapter = adapter;
        }

        //根据position判断item是不是RefreshHeader
        public boolean isRefreshHeader(int position) {
            if (pullRefreshEnabled) {
                return position == 0;
            } else {
                return false;
            }
        }

        //根据position判断item是不是header（非RefreshHeader）
        public boolean isHeader(int position) {
            if (pullRefreshEnabled) {
                return position >= 1 && position < mHeaderViews.size() + 1;
            } else {
                return position >= 0 && position < mHeaderViews.size();
            }
        }

        //根据position判断item是不是LoadFooter
        public boolean isLoadFooter(int position) {
            if (loadingMoreEnabled) {
                return position == getItemCount() - 1;
            } else {
                return false;
            }
        }

        //根据position判断item是不是Footer（非LoadFooter）
        public boolean isFooter(int position) {
            if (loadingMoreEnabled) {
                return position >= getItemCount() - 1 - mFooterViews.size() && position < getItemCount() - 1;
            } else {
                return position >= getItemCount() - mFooterViews.size() && position < getItemCount();
            }
        }

        //根据type获取header view
        private View getHeaderViewByType(int itemType) {
            if (!isHeaderType(itemType)) {
                return null;
            }
            return mHeaderViews.get(itemType - TYPE_REFRESH_HEADER - 1);
        }

        //根据type获取footer view
        private View getFooterViewByType(int itemType) {
            if (!isFooterType(itemType)) {
                return null;
            }
            return mFooterViews.get(itemType - TYPE_LOAD_FOOTER - 1);
        }

        //判断一个type是否为HeaderType
        private boolean isHeaderType(int itemViewType) {
            return mHeaderViews.size() > 0
                    && itemViewType - TYPE_REFRESH_HEADER - 1 >= 0
                    && itemViewType - TYPE_REFRESH_HEADER - 1 < mHeaderViews.size();
        }

        //判断一个type是否为FooterType
        private boolean isFooterType(int itemViewType) {
            return mFooterViews.size() > 0
                    && itemViewType - TYPE_LOAD_FOOTER - 1 >= 0
                    && itemViewType - TYPE_LOAD_FOOTER - 1 < mFooterViews.size();
        }

        //判断type是否是header或者footer
        private boolean isReservedItemViewType(int itemViewType) {
            if (itemViewType == TYPE_REFRESH_HEADER || itemViewType == TYPE_LOAD_FOOTER
                    || isHeaderType(itemViewType) || isFooterType(itemViewType)) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_REFRESH_HEADER) {
                return new SimpleViewHolder(mRefreshHeader);
            } else if (isHeaderType(viewType)) {
                return new SimpleViewHolder(getHeaderViewByType(viewType));
            } else if (isFooterType(viewType)) {
                return new SimpleViewHolder(getFooterViewByType(viewType));
            } else if (viewType == TYPE_LOAD_FOOTER) {
                return new SimpleViewHolder(mFootView);
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (isHeader(position) || isRefreshHeader(position)) {
                return;
            }
            final int adjPosition = position - mHeaderViews.size() - (pullRefreshEnabled ? 1 : 0);
            if (adapter != null) {
                int adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    adapter.onBindViewHolder(holder, adjPosition);
                    if (onItemClickListener != null) {
                        holder.itemView.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onItemClickListener.onItemClick(adjPosition);
                            }
                        });
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            int otherCount = 0;
            if (pullRefreshEnabled) {
                otherCount++;
            }
            if (loadingMoreEnabled) {
                otherCount++;
            }
            if (adapter != null) {
                return mHeaderViews.size() + mFooterViews.size() + adapter.getItemCount() + otherCount;
            } else {
                return mHeaderViews.size() + mFooterViews.size() + otherCount;
            }
        }

        @Override
        public int getItemViewType(int position) {
            final int adjPosition = position - mHeaderViews.size() - (pullRefreshEnabled ? 1 : 0);
            if (isReservedItemViewType(adapter.getItemViewType(adjPosition))) {
                throw new IllegalStateException("XRecyclerView require itemViewType in adapter should be less than 10000 ");
            }
            if (isRefreshHeader(position)) {
                return TYPE_REFRESH_HEADER;
            }
            if (isHeader(position)) {
                position = position - (pullRefreshEnabled ? 1 : 0);
                return TYPE_REFRESH_HEADER + position + 1;
            }
            if (isFooter(position)) {
                position = position - (pullRefreshEnabled ? 1 : 0) - mHeaderViews.size() - adapter.getItemCount();
                return TYPE_LOAD_FOOTER + position + 1;
            }
            if (isLoadFooter(position)) {
                return TYPE_LOAD_FOOTER;
            }
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return adapter.getItemViewType(adjPosition);
                }
            }
            return 0;
        }

        @Override
        public long getItemId(int position) {
            if (adapter != null && position >= mHeaderViews.size() + (pullRefreshEnabled ? 1 : 0)) {
                int adjPosition = position - mHeaderViews.size() - (pullRefreshEnabled ? 1 : 0);
                if (adjPosition < adapter.getItemCount()) {
                    return adapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (isHeader(position) || isFooter(position) || isLoadFooter(position)
                                || isRefreshHeader(position)) ? gridManager.getSpanCount() : 1;
                    }
                });
            }
            adapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && (isHeader(holder.getLayoutPosition()) || isRefreshHeader(holder.getLayoutPosition())
                    || isFooter(holder.getLayoutPosition()) || isLoadFooter(holder.getLayoutPosition()))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
            adapter.onViewAttachedToWindow(holder);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            adapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            adapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            adapter.onViewRecycled(holder);
        }

        @Override
        public boolean onFailedToRecycleView(ViewHolder holder) {
            return adapter.onFailedToRecycleView(holder);
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            adapter.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            adapter.registerAdapterDataObserver(observer);
        }

        private class SimpleViewHolder extends ViewHolder {
            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewParent p = getParent();
        // 解决和CollapsingToolbarLayout冲突的问题
        while (p != null) {
            if (p instanceof CoordinatorLayout) {
                break;
            }
            p = p.getParent();
        }
        if (p != null) {
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) p;
            int childCount = coordinatorLayout.getChildCount();
            AppBarLayout appBarLayout = null;
            for (int i = childCount - 1; i >= 0; i--) {
                final View child = coordinatorLayout.getChildAt(i);
                if (child instanceof AppBarLayout) {
                    appBarLayout = (AppBarLayout) child;
                    break;
                }
            }
            if (appBarLayout != null) {
                appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
                    @Override
                    public void onStateChanged(AppBarLayout appBarLayout, State state) {
                        appbarState = state;
                    }
                });
            }
        }
    }

    public interface LoadingListener {
        void onRefresh();

        void onLoadMore();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    /**
     * 设置刷新、加载监听器
     *
     * @param listener 监听器
     */
    public void setLoadingListener(LoadingListener listener) {
        mLoadingListener = listener;
    }

    /**
     * item点击监听
     *
     * @param listener 监听器
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    /**
     * 添加headerview
     *
     * @param view View
     */
    public void addHeaderView(View view) {
        mHeaderViews.add(view);
        if (mWrapAdapter != null) {
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    public void removeHeadView(View view) {
        mHeaderViews.remove(view);
        if (mWrapAdapter != null) {
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 添加footerview
     *
     * @param view View
     */
    public void addFooterView(View view) {
        mFooterViews.add(view);
        if (mWrapAdapter != null) {
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    public void removeFooterView(View view) {
        mFooterViews.remove(view);
        if (mWrapAdapter != null) {
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 代码主动刷新
     */
    public void refresh() {
        if (pullRefreshEnabled && mLoadingListener != null) {
            if (mRefreshHeader != null) {
                mRefreshHeader.setState(ArrowRefreshHeader.STATE_REFRESHING);
                mRefreshHeader.onMove(mRefreshHeader.getMeasuredHeight());
            }
            mLoadingListener.onRefresh();
        }
    }

    /**
     * 重置下拉刷新、加载更多状态
     */
    public void reset() {
        setNoMore(false);
        setLoadMoreComplete();
        setRefreshComplete();
    }

    /**
     * 设置下拉刷新完成
     */
    public void setRefreshComplete() {
        if (mRefreshHeader != null) {
            mRefreshHeader.refreshComplete();
        }
        setNoMore(false);
    }

    /**
     * 设置加载更多完成
     */
    public void setLoadMoreComplete() {
        isLoadingMoreData = false;
        if (!isNoMore && mFootView != null) {
            mFootView.setState(LoadingMoreFooter.STATE_COMPLETE);
        }
    }

    /**
     * 设置没有更多数据了
     *
     * @param noMore true没有了,false还有
     */
    public void setNoMore(boolean noMore) {
        isLoadingMoreData = false;
        isNoMore = noMore;
        if (mFootView != null) {
            mFootView.setState(isNoMore ? LoadingMoreFooter.STATE_NOMORE : LoadingMoreFooter.STATE_COMPLETE);
        }
    }

    /**
     * 设置加载失败的展示
     */
    public void setLoadError() {
        isLoadingMoreData = false;
        if (mFootView != null) {
            mFootView.setState(LoadingMoreFooter.STATE_ERROR);
            mFootView.setClickable(true);
            mFootView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFootView.setClickable(false);
                    mFootView.setState(LoadingMoreFooter.STATE_LOADING);
                    isLoadingMoreData = true;
                    mLoadingListener.onLoadMore();
                }
            });
        }
    }

    /**
     * 设置是否可以下拉刷新
     *
     * @param enabled true可以,false不可以
     */
    public void setPullRefreshEnabled(boolean enabled) {
        pullRefreshEnabled = enabled;
    }

    /**
     * 设置是否可以加载更多
     *
     * @param enabled true可以,false不可以
     */
    public void setLoadingMoreEnabled(boolean enabled) {
        loadingMoreEnabled = enabled;
        if (!enabled && mFootView != null) {
            mFootView.setState(LoadingMoreFooter.STATE_NOMORE);
        }
    }

    /**
     * 设置下拉刷新进度条样式
     *
     * @param style 样式
     */
    public void setRefreshProgressStyle(int style) {
        mRefreshProgressStyle = style;
        if (mRefreshHeader != null) {
            mRefreshHeader.setProgressStyle(style);
        }
    }

    /**
     * 设置加载更多进度条样式
     *
     * @param style 样式
     */
    public void setLoadingMoreProgressStyle(int style) {
        mLoadingMoreProgressStyle = style;
        mFootView.setProgressStyle(style);
    }

    /**
     * 设置下拉刷新旋转图标
     *
     * @param resId 资源id
     */
    public void setArrowImageView(int resId) {
        if (mRefreshHeader != null) {
            mRefreshHeader.setArrowImageView(resId);
        }
    }

    /**
     * 设置下拉刷新文字提示
     *
     * @param arr {下拉提示,松开提示,正在刷新提示,刷新完成提示}
     */
    public void setRefreshTextView(String[] arr) {
        if (mRefreshHeader != null) {
            mRefreshHeader.setRefreshTip(arr);
        }
    }

    /**
     * 设置是否需要下拉旋转动画
     *
     * @param need true需要 false不需要
     */
    public void setNeedPullAnim(boolean need) {
        if (mRefreshHeader != null) {
            mRefreshHeader.setNeedAnim(need);
        }
    }
}