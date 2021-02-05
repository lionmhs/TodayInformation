package today.news.refresh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class GodRefreshLayout extends LinearLayout {

    private BaseRefreshManager mRefreshManager;
    private Context mContext;
    private View headerView;
    private int headerViewHeight;
    private int minHeaderHeight;
    private int maxHeaderHeight;
    private int downY;
    private RefreshListener mRefreshListener;  //回调接口
    private RecyclerView mRecyClerView;
    private ScrollView mScrollView;
    private  int interceptX;
    private  int interceptY;

    public GodRefreshLayout(Context context) {
        super(context);
        initView(context);

    }


    public GodRefreshLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);

    }

    public GodRefreshLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
    }
    private void initHeaderView() {
        setOrientation(VERTICAL);
        headerView = mRefreshManager.getHeaderView();
        headerView.measure(0, 0);

        headerViewHeight = headerView.getMeasuredHeight();//getHeight 未测量为0
        minHeaderHeight = -headerViewHeight;
        maxHeaderHeight = (int) (headerViewHeight * 0.3f);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerViewHeight);
        params.topMargin = minHeaderHeight;
        headerView.setLayoutParams(params);
        addView(headerView, 0);    //添加View

    }
    //回调接口
    public void setRefreshListener(RefreshListener refreshListener) {
        this.mRefreshListener = refreshListener;

    }
    public interface RefreshListener {
        void onRefreshing();
    }

    public void refreshOver() {
        hideHeadlerView(getHeadViewLayoutParams());
    }





    /*
    开启下拉刷新  默认下拉刷新效果
     */
    public void setRefreshManager() {
        mRefreshManager = new DefaultRefreshManager(mContext);
        initHeaderView();
    }

    /**
     * 开启下拉刷新  使用用户自定义的下拉刷新效果
     *
     * @param manager
     */
    public void setRefreshManager(BaseRefreshManager manager) {
        mRefreshManager = manager;
        initHeaderView();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View child = getChildAt(0);
        //获取RecyclerView
        if (child instanceof RecyclerView) {
            mRecyClerView = (RecyclerView) child;
        }
        if (child instanceof ScrollView) {
            mScrollView = (ScrollView) child;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (downY == 0) {
                    downY = interceptY;
                }

                int moveY = (int) event.getY();
                int dy = moveY - downY;
                if (dy > 0) {
                    LayoutParams layoutParams = getHeadViewLayoutParams();
                    int topMargin = (int) Math.min(dy / 1.8f + minHeaderHeight, maxHeaderHeight);
                    //回调不断缩放方法
                    if (topMargin <= 0) {
                        // 0 - 1 之间
                        float percent = ((topMargin - minHeaderHeight) * 1.0f) / (-minHeaderHeight);
                        mRefreshManager.downRefreshPercent(percent);
                    }
                    if (topMargin < 0 && mCurrentRefreshState != RefreshState.DOWNREFRESH) {
                        //当前不是下拉状态，将它设置成下拉状态然后处理这个状态
                        mCurrentRefreshState = RefreshState.DOWNREFRESH;
                        //提示下拉刷新
                        handleRefreshState(mCurrentRefreshState);
                    } else if (topMargin >= 0 && mCurrentRefreshState != RefreshState.RELEASEREFRESH) {
                        mCurrentRefreshState = RefreshState.RELEASEREFRESH;
                        //提示释放刷新
                        handleRefreshState(mCurrentRefreshState);
                    }
                    //阻尼效果
                    layoutParams.topMargin = topMargin;
                    headerView.setLayoutParams(layoutParams);
                }
                return true;

            case MotionEvent.ACTION_UP:
                if (handleEventUp(event)) {
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                interceptX = (int) ev.getX();
                interceptY = (int) ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:

                int dx = (int) (ev.getX() - interceptX);
                int dy = (int) (ev.getY() - interceptY);
                if (Math.abs(dy) > Math.abs(dx) && dy > 0 ) {
                    if (handleChildIsTop()) {
                        return true;
                    }


                }

                break;

        }
        return super.onInterceptTouchEvent(ev);
    }

    //判断子View 是否在顶端
    private boolean handleChildIsTop() {
        if (mRecyClerView != null) {
            return RefreshScrollingUtil.isRecyclerViewToTop(mRecyClerView);
        }
        if (mScrollView != null) {
            return RefreshScrollingUtil.isScrollViewOrWebViewToTop(mScrollView);
        }
        return false;
    }
    //这个方法回调时，可以获取当前ViewGroup的子View


    private boolean handleEventUp(MotionEvent event) {
        final LayoutParams layoutParams = getHeadViewLayoutParams();
        if (mCurrentRefreshState == RefreshState.DOWNREFRESH) {
            hideHeadlerView(layoutParams);
        } else if (mCurrentRefreshState == RefreshState.RELEASEREFRESH) {
            layoutParams.topMargin = 0;
            headerView.setLayoutParams(layoutParams);

            mCurrentRefreshState = RefreshState.REFRESHING;
            handleRefreshState(mCurrentRefreshState);
            if (mRefreshListener != null) {
                //不为空回调这个方法
                mRefreshListener.onRefreshing();
            }
        }
        return layoutParams.topMargin > minHeaderHeight;
    }

    private void hideHeadlerView(final LayoutParams layoutParams) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(layoutParams.topMargin, minHeaderHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                layoutParams.topMargin = animatedValue;
                headerView.setLayoutParams(layoutParams);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentRefreshState = RefreshState.IDDLE;
                handleRefreshState(mCurrentRefreshState);
            }
        });
        valueAnimator.setDuration(500);
        valueAnimator.start();
    }

    private LayoutParams getHeadViewLayoutParams() {
        return (LayoutParams) headerView.getLayoutParams();
    }

    private void handleRefreshState(RefreshState mCurrentRefreshState) {
        switch (mCurrentRefreshState) {
            case DOWNREFRESH:
                mRefreshManager.downRefresh();
                break;
            case RELEASEREFRESH:
                mRefreshManager.releaseRefresh();
                break;
            case IDDLE:
                mRefreshManager.iddleRefresh();
            case REFRESHING:
                mRefreshManager.ingRefresh();
        }
    }

    private RefreshState mCurrentRefreshState = RefreshState.IDDLE;

    // 依次为，静止  下拉刷新   释放刷新  正在刷新  刷新完成
    private enum RefreshState {
        IDDLE, DOWNREFRESH, RELEASEREFRESH, REFRESHING, REFRESHOVER;
    }
}
