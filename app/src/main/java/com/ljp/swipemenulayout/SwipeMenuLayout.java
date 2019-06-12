package com.ljp.swipemenulayout;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/*
 *@创建者       L_jp
 *@创建时间     2019/6/8 11:15.
 *@描述
 *
 * 使用方式:
 * 当前这个SwipeMenuLayout为parentView，
 * childView中第一个view为itemView，后面相继的view为menuView；
 * itemView的宽度不管是AT_MOST还是EXACTLY，都会指定为SwipeMenuLayout的宽度；
 * 如果SwipeMenuLayout的宽度为AT_MOST，会以它父view的宽度来测量
 *
 * 禁止侧滑功能 isEnableSwipe设置false
 * 默认左滑打开菜单，想要右滑打开菜单的话 isEnableLeftMenu设置true
 *
 *@更新者         $Author$
 *@更新时间         $Date$
 *@更新描述
 */
public class SwipeMenuLayout extends ViewGroup {
    private static final String TAG = "SwipeMenuLayout";
    private final Context mContext;
    private int mScaledTouchSlop;
    private int mScaledMaximumFlingVelocity;
    //内容view
    private View mContentView;
    //菜单内容的宽度,也是最大的宽度距离
    private int mMenuWidth;
    private float mLastRawX = 0;
    private float mFirstRawX = 0;
    private static SwipeMenuLayout mCacheView;
    private int mPointerId;
    //滑动速度
    private VelocityTracker mVelocityTracker;
    //多点触摸判断的变量
    private boolean isFingerTouch = false;
    //展开 关闭的动画
    private ValueAnimator mExpandAnim, mCloseAnim;
    //动画时间
    private int animDuration = 300;
    //阻塞拦截的一个控制变量
    private boolean chokeIntercept = false;
    /**
     * 是否开启阻塞效果 默认开启
     *  举个例子 比如你把item1的侧滑菜单划出来了，你继续滑动item2的，
     *  这是默认是开启阻塞效果的，在你滑动item2的时候 会先关闭item1的菜单，
     *  需要再次滑动item2才可以（qq是这样子的）
     *  如果关闭这个效果，你在滑动item2的同时会同时关闭item1
     */
    private boolean isOpenChoke = true;
    //是否启用侧滑 默认启用 默认左滑动 而且放置右侧
    private boolean isEnableSwipe = true;
    //是否启用右滑出现菜单 启用后是menu放置左侧
    private boolean isEnableLeftMenu = false;
    //是否开启点击菜单内容后自动关闭菜单  默认false
    private boolean isClickMenuAndClose = false;
    private SwipeMenuStateListener mSwipeMenuStateListener;

    public SwipeMenuLayout(Context context) {
        this(context, null);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SwipeMenuLayout, defStyleAttr, 0);
        isEnableSwipe = ta.getBoolean(R.styleable.SwipeMenuLayout_isEnableSwipe, true);
        isEnableLeftMenu = ta.getBoolean(R.styleable.SwipeMenuLayout_isEnableLeftMenu, false);
        isOpenChoke = ta.getBoolean(R.styleable.SwipeMenuLayout_isOpenChoke, true);
        isClickMenuAndClose = ta.getBoolean(R.styleable.SwipeMenuLayout_isClickMenuAndClose, false);
        ta.recycle();

        init();
    }

    private void init() {
        //获取滑动的最小值，大于这个值就认为他是滑动  默认是8
        mScaledTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        // 获得允许执行fling （抛）的最大速度值 （惯性速度）
        mScaledMaximumFlingVelocity = ViewConfiguration.get(mContext).getScaledMaximumFlingVelocity();
    }

    public void setEnableRightSwipe(boolean enableRightSwipe) {
        isEnableLeftMenu = enableRightSwipe;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //让自己可点击 一定要设置
        setClickable(true);
        //获取测量模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //内容view的宽度
        int contentWidth = 0;
        int contentMaxHeight = 0;
        mMenuWidth = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.getVisibility() == View.GONE) {
                continue;
            }
            // 一定要设置
            childAt.setClickable(true);
            LayoutParams layoutParams = childAt.getLayoutParams();
            if (i == 0) {
                //让itemView的宽度为parentView的宽度
                layoutParams.width = getMeasuredWidth();
                mContentView = childAt;
            }
            //测量子view的宽高
            measureChild(childAt, widthMeasureSpec, heightMeasureSpec);
            //如果parentView测量模式不是精准的
            if (heightMode != MeasureSpec.EXACTLY) {
                contentMaxHeight = Math.max(contentMaxHeight, childAt.getMeasuredHeight());
            }
            //child测量结束后才能获取宽高
            if (i == 0) {
                contentWidth = childAt.getMeasuredWidth();
            } else {
                mMenuWidth += childAt.getMeasuredWidth();
            }
        }
        //取最大值 重新测量
        int height = Math.max(getMeasuredHeight(), contentMaxHeight);
        setMeasuredDimension(contentWidth, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int pLeft = getPaddingLeft();
        int pTop = getPaddingTop();
        int left = 0;
        int right = 0;

        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.getVisibility() == View.GONE) {
                continue;
            }
            if (i == 0) {
                childAt.layout(pLeft, pTop, pLeft + childAt.getMeasuredWidth(), pTop + childAt.getMeasuredHeight());
                left += pLeft + childAt.getMeasuredWidth();
            } else {
                //放置左侧
                if (isEnableLeftMenu) {
                    childAt.layout(right - childAt.getMeasuredWidth(), pTop, right, pTop + childAt.getMeasuredHeight());
                    right -= childAt.getMeasuredWidth();
                } else {
                    //放置右侧
                    childAt.layout(left, pTop, left + childAt.getMeasuredWidth(), pTop + childAt.getMeasuredHeight());
                    left += childAt.getMeasuredWidth();
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //如果关闭了侧滑 直接super
        if (!this.isEnableSwipe) {
            return super.dispatchTouchEvent(ev);
        }
        acquireVelocityTracker(ev);
        final VelocityTracker verTracker = mVelocityTracker;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                /*
                多跟手指的触摸处理  isTouch为true的话 表示之前已经有一个down事件了,
                再有其他的down事件 我返回false不处理了 (返回false时 只会执行一个down事件)
                 */
                if (isFingerTouch) {
                    return false;
                } else {
                    isFingerTouch = true;
                }
                //第一个触点的id， 此时可能有多个触点，但至少一个，计算滑动速率用
                mPointerId = ev.getPointerId(0);
                getParent().requestDisallowInterceptTouchEvent(false);
                mFirstRawX = ev.getRawX();
                mLastRawX = ev.getRawX();
                chokeIntercept = false;
                if (null != mCacheView) {
                    if (mCacheView != this) {
                        mCacheView.closeMenuAnim();
                        chokeIntercept = isOpenChoke;
                    }
                    //只要有一个侧滑菜单处于打开状态， 就不给外层布局上下滑动了
                    getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (chokeIntercept) {
                    break;
                }
                //计算移动的距离
                float gap = mLastRawX - ev.getRawX();
                //view滑动
                scrollBy((int) (gap), 0);
                if (Math.abs(gap) > mScaledTouchSlop || Math.abs(getScrollX()) > mScaledTouchSlop) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                //超过范围的话--->归位
                //目前是右滑的话 （菜单在左边）
                if (isEnableLeftMenu) {
                    if (getScrollX() < -mMenuWidth) {
                        scrollTo(-mMenuWidth, 0);
                    } else if (getScrollX() > 0) {
                        scrollTo(0, 0);
                    }
                } else {
                    if (getScrollX() < 0) {
                        scrollTo(0, 0);
                    } else if (getScrollX() > mMenuWidth) {
                        scrollTo(mMenuWidth, 0);
                    }
                }
                //重新赋值
                mLastRawX = ev.getRawX();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //多指触摸状态改变
                isFingerTouch = false;
                if (!chokeIntercept) {
                    //unitis值为1000（毫秒）时间单位内运动了多少个像素 正负最多为mScaledMaximumFlingVelocity
                    verTracker.computeCurrentVelocity(1000, mScaledMaximumFlingVelocity);
                    //获取x方向的运动速度
                    float velocityX = verTracker.getXVelocity(mPointerId);
                    //滑动速度超过1000  认为是快速滑动了
                    if (Math.abs(velocityX) > 1000) {
                        if (velocityX < -1000) {//左滑了
                            if (!isEnableLeftMenu) {
                                //展开Menu
                                expandMenuAnim();
                            } else {
                                //关闭Menu
                                closeMenuAnim();
                            }
                        } else {//右滑了
                            if (!isEnableLeftMenu) {
                                //关闭Menu
                                closeMenuAnim();
                            } else {
                                //展开Menu
                                expandMenuAnim();
                            }
                        }
                    } else {
                        //超过菜单布局的40% 就展开 反之关闭
                        if (Math.abs(getScrollX()) > mMenuWidth * 0.4) {//否则就判断滑动距离
                            //展开Menu
                            expandMenuAnim();
                        } else {
                            //关闭Menu
                            closeMenuAnim();
                        }
                    }
                }
                //释放VelocityTracker
                recycleVelocityTracker();
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!this.isEnableSwipe) {
            return super.onInterceptTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                //移动了就不能有长按事件
                mContentView.setLongClickable(false);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //抬手后可以长按
                if (getScrollX() == 0) {
                    mContentView.setLongClickable(true);
                }
                //大于系统给出的这个数值，就认为是滑动了 事件进行拦截
                if (Math.abs(ev.getRawX() - mFirstRawX) >= mScaledTouchSlop) {
                    return true;
                } else
                    //小于滑动值 就认为是点击行为
                    //如果已经侧滑出菜单，菜单范围内的点击事件不拦截 直接break掉
                    if (getScrollX() != 0) {
                        //没有开启的话 才走判断逻辑
                        if ((isEnableLeftMenu && ev.getX() <= mMenuWidth)
                                || (!isEnableLeftMenu && ev.getX() > getMeasuredWidth() - mMenuWidth)) {
                            if (isClickMenuAndClose) {
                                closeMenuAnim();
                            }
                            break;
                        }
                        //否则点击了 直接动画关闭
                        closeMenuAnim();
                        return true;
                    }
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * @param event 向VelocityTracker添加MotionEvent
     */
    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * * 释放VelocityTracker
     */
    private void recycleVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }


    public void expandMenuAnim() {
        //清除动画
        cleanAnim();
        //展开就赋值
        mCacheView = SwipeMenuLayout.this;
        //禁止长按事件
        if (null != mContentView) {
            mContentView.setLongClickable(false);
        }

        mExpandAnim = ValueAnimator.ofInt(getScrollX(), isEnableLeftMenu ? -mMenuWidth : mMenuWidth);
        mExpandAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scrollTo((Integer) animation.getAnimatedValue(), 0);
            }
        });
        mExpandAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mSwipeMenuStateListener != null) {
                    mSwipeMenuStateListener.menuIsOpen(true);
                }
            }
        });
        mExpandAnim.setInterpolator(new OvershootInterpolator());
        mExpandAnim.setDuration(animDuration).start();
    }

    /**
     * 平滑关闭
     */
    public void closeMenuAnim() {
        mCacheView = null;
        //清除动画
        cleanAnim();
        //解除长按事件
        if (null != mContentView) {
            mContentView.setLongClickable(true);
        }
        if (getScrollX()==0) {
            return;
        }
        mCloseAnim = ValueAnimator.ofInt(getScrollX(), 0);
        mCloseAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scrollTo((Integer) animation.getAnimatedValue(), 0);
            }
        });
        mCloseAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mSwipeMenuStateListener != null) {
                    mSwipeMenuStateListener.menuIsOpen(false);
                }
            }
        });
        mCloseAnim.setInterpolator(new AccelerateInterpolator());
        mCloseAnim.setDuration(animDuration).start();
    }

    //清除动画 防止上个动画没执行完 用户操作了另一个item
    private void cleanAnim() {
        if (mCloseAnim != null && mCloseAnim.isRunning()) {
            mCloseAnim.cancel();
        }
        if (mExpandAnim != null && mExpandAnim.isRunning()) {
            mExpandAnim.cancel();
        }
    }

    /*
        每次ViewDetach的时候
         1 mCacheView置为null 防止内存泄漏(mCacheView是一个静态变量)
         2 侧滑删除后自己后，这个View被Recycler回收；复用 下一个进入屏幕的View的状态应该是普通状态，而不是展开状态。
     */
    @Override
    protected void onDetachedFromWindow() {
        //避免多次调用
        if (getScrollX() != 0) {
            quickCloseMenu();
            mCacheView = null;
        }
        super.onDetachedFromWindow();
    }

    //快速关闭 没有动画时间
    public void quickCloseMenu() {
        if (getScrollX() != 0) {
            cleanAnim();
            scrollTo(0, 0);
            mCacheView = null;
        }
    }

    //展开时，禁止自身的长按
    @Override
    public boolean performLongClick() {
        if (getScrollX() != 0) {
            return true;
        }
        return super.performLongClick();
    }

    //获取上一个打开的view，用来关闭 上一个打开的。暂时应该用不到
    public SwipeMenuLayout getCacheView(){
        return mCacheView;
    }

    //当前是否展开
    public boolean isExpandMenu() {
        return Math.abs(getScaleX()) >= mMenuWidth;
    }

    //获取是否打开阻塞
    public boolean isOpenChoke() {
        return isOpenChoke;
    }

    //设置是否打开阻塞
    public void setOpenChoke(boolean openChoke) {
        isOpenChoke = openChoke;
    }

    //获取是否打开了侧滑菜单功能
    public boolean isEnableSwipe() {
        return isEnableSwipe;
    }

    //设置是否开启侧滑菜单
    public void setEnableSwipe(boolean enableSwipe) {
        isEnableSwipe = enableSwipe;
    }

    //获取是否打开了 菜单在左侧功能
    public boolean isEnableLeftMenu() {
        return isEnableLeftMenu;
    }

    //设置菜单是否在左侧
    public void setEnableLeftMenu(boolean enableLeftMenu) {
        isEnableLeftMenu = enableLeftMenu;
    }

    //获取点击菜单后是否直接关闭菜单
    public boolean isClickMenuAndClose() {
        return isClickMenuAndClose;
    }

    //设置 点击菜单后是否直接关闭菜单
    public void setClickMenuAndClose(boolean clickMenuAndClose) {
        isClickMenuAndClose = clickMenuAndClose;
    }

    public void setSwipeMenuStateListener(SwipeMenuStateListener listener){
        this.mSwipeMenuStateListener = listener;
    }
}
