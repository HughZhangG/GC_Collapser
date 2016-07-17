package com.cheng.gu.gc_collapser.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by gc on 2016/7/11.
 */
public class ZoomImageView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener, View
        .OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {

    private boolean mOnce = false;

    /**
     * 初始化缩放值
     */
    private float mInitScale;
    /**
     * 双击缩放值
     */
    private float mMidScale;
    /**
     * 缩放最大值
     */
    private float mMaxScale;

    private Matrix mScaleMatrix;

    /**
     * 捕获用户多指缩放比例
     */
    private ScaleGestureDetector mScaleGestureDetector;


    //自由移动-------------
    /**
     * 记录上一次触点数目
     */
    private int mLastPointerCount;
    private float mLastX;
    private float mLastY;
    private boolean isCanDrag = false;
    private int mScaledTouchSlop;//最小移动值

    private boolean needCheckLeftAndRight, needCheckTopAndBottom;

    //------------双击放大缩小
    private GestureDetector mGestureDetector;
    private boolean isAutoScale;


    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mScaleMatrix = new Matrix();

        setScaleType(ScaleType.MATRIX);

        mScaleGestureDetector = new ScaleGestureDetector(context, this);

        setOnTouchListener(this);

        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {

                if (isAutoScale)
                    return true;

                float x = e.getX();
                float y = e.getY();

                if (getScale() < mMidScale) {
                    postDelayed(new AutoScaleRunnable(mMidScale,x,y),16);
                    isAutoScale = true;
                }else{
                    postDelayed(new AutoScaleRunnable(mInitScale,x,y),16);
                    isAutoScale = true;
                }


                return true;
            }
        });
    }


    /**
     * 自动缩放
     */
    private class AutoScaleRunnable implements Runnable {
        //缩放目标值
        private float mTargetScale;
        //中心点
        private float x;
        private float y;
        //缩放梯度因子
        private final float BIGGER = 1.07f;
        private final float SMALLER = 0.97f;

        private float mTempScale;


        public AutoScaleRunnable(float mTargetScale, float x, float y) {
            this.mTargetScale = mTargetScale;
            this.x = x;
            this.y = y;

            if (getScale() < mTargetScale)
                mTempScale = BIGGER;
            if (getScale() > mTargetScale)
                mTempScale = SMALLER;

        }

        @Override
        public void run() {

            //进行缩放
            mScaleMatrix.postScale(mTempScale, mTempScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);

            float currentScale = getScale();

            if ((mTempScale > 1.0f && currentScale < mTargetScale) ||
                    (mTempScale < 1.0f && currentScale > mTargetScale)) {
                    postDelayed(this,16);
            }else{//设定为我们的目标值
                float scale = mTargetScale / currentScale;
                mScaleMatrix.postScale(scale,scale,x,y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }

        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }

    /**
     * 缩放图片填充控件
     */
    @Override
    public void onGlobalLayout() {

        if (!mOnce) {
            int width = getWidth();
            int height = getHeight();

            Drawable drawable = getDrawable();
            if (drawable == null)
                return;

            int dw = drawable.getIntrinsicWidth();
            int dh = drawable.getIntrinsicHeight();

            /**
             * 缩放
             */
            float scale = 1.0f;

            if (dw > width && dh < height) {
                scale = width * 1.0f / dw;
            } else if (dw < width && dh > height) {
                scale = height * 1.0f / dh;
            } else if ((dw < width && dh < height) || (dw > width && dh > height)) {
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
            }

            /**
             * 得到初始化缩放比例
             */
            mInitScale = scale;
            mMaxScale = scale * 4;
            mMidScale = scale * 2;

            /**
             * 将图片居中控件
             */
            int dx = width / 2 - dw / 2;
            int dy = height / 2 - dh / 2;

            mScaleMatrix.postTranslate(dx, dy);

            mScaleMatrix.postScale(mInitScale, mInitScale, width / 2, height / 2);

            setImageMatrix(mScaleMatrix);

            mOnce = true;
        }


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        /**
         * 双击缩放功能
         */
        if (mGestureDetector.onTouchEvent(event))
            return true;

        /**
         * 缩放功能
         */
        mScaleGestureDetector.onTouchEvent(event);


        /**
         * 移动功能
         */
        int x = 0;
        int y = 0;

        int pointerCount = event.getPointerCount();

        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        //中心点
        x /= pointerCount;
        y /= pointerCount;

        if (mLastPointerCount != pointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }

        mLastPointerCount = pointerCount;

        RectF rectf = getMatrixRectF();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if (rectf.width() > getWidth() + 0.01 || rectf.height() > getHeight()+0.01){
                    if (getParent() instanceof ViewPager){//阻止父view拦截事件
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }

                break;

            case MotionEvent.ACTION_MOVE:

                if (rectf.width() > getWidth() + 0.01 || rectf.height() > getHeight()+0.01){
                    if (getParent() instanceof ViewPager){//阻止父view拦截事件
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }


                float dx = x - mLastX;
                float dy = y - mLastY;
                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dx, dy);
                }
                if (isCanDrag) {
                    RectF matrixRectF = rectf;
                    if (getDrawable() != null) {
                        needCheckLeftAndRight = needCheckTopAndBottom = true;
                        //图片宽度比容器宽度小，禁止左右移动
                        if (matrixRectF.width() < getWidth()) {
                            needCheckLeftAndRight = false;
                            dx = 0;
                        }
                        //图片高度比容器高度小，禁止上下移动
                        if (matrixRectF.height() < getHeight()) {
                            needCheckTopAndBottom = false;
                            dy = 0;
                        }

                        mScaleMatrix.postTranslate(dx, dy);

                        checkBorderWhenTranslate();

                        setImageMatrix(mScaleMatrix);
                    }
                }
                mLastX = x;
                mLastY = y;

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount = 0;
                break;
        }


        return true;
    }

    /**
     * 在移动时判断边界，进行边界检查
     */
    private void checkBorderWhenTranslate() {

        RectF rectF = getMatrixRectF();

        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rectF.top > 0 && needCheckTopAndBottom) {
            deltaY = -rectF.top;
        }
        if (rectF.bottom < height && needCheckTopAndBottom) {
            deltaY = height - rectF.bottom;
        }
        if (rectF.left > 0 && needCheckLeftAndRight) {
            deltaX = -rectF.left;
        }
        if (rectF.right < width && needCheckLeftAndRight) {
            deltaX = width - rectF.right;
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);


    }

    /**
     * 判断移动距离是否大于系统最小规定距离
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt(dx * dx + dy * dy) > mScaledTouchSlop;
    }

    /**
     * 获取当前缩放比例
     *
     * @return
     */
    public float getScale() {
        float[] scals = new float[9];
        mScaleMatrix.getValues(scals);
        return scals[Matrix.MSCALE_X];//由于X Y 方向缩放比例一致
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();

        float scale = getScale();

        if (getDrawable() == null)
            return true;

        //缩放范围控制
        if ((scale < mMaxScale && scaleFactor > 1.0f) || (scale > mInitScale && scaleFactor < 1.0f)) {
            if (scale * scaleFactor > mMaxScale)
                scaleFactor = mMaxScale / scale;
            if (scale * scaleFactor < mInitScale)
                scaleFactor = mInitScale / scale;

            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();

            mScaleMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);

            checkBorderAndCenterWhenScale();

            setImageMatrix(mScaleMatrix);
        }

        return true;
    }

    /**
     * 在缩放的时候进行边界控制以及位置控制
     * 缩放边界检测，防止出现白边
     */
    private void checkBorderAndCenterWhenScale() {
        RectF matrixRectF = getMatrixRectF();

        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        /**
         * 图片宽度大于容器宽度
         */
        if (matrixRectF.width() >= width) {
            if (matrixRectF.left > 0) {
                deltaX = -matrixRectF.left;
            }
            if (matrixRectF.right < width) {
                deltaX = width - matrixRectF.right;
            }
        }
        /**
         * 图片高度大于容器高度
         */
        if (matrixRectF.height() >= height) {
            if (matrixRectF.top > 0) {
                deltaY = -matrixRectF.top;
            }
            if (matrixRectF.bottom < height) {
                deltaY = height - matrixRectF.bottom;
            }
        }

        /**
         * 如果图片宽度或者高度小于容器宽高，图片居中
         */
        if (matrixRectF.width() < width) {
            deltaX = width / 2f - matrixRectF.right + matrixRectF.width() / 2f;
        }

        if (matrixRectF.height() < height) {
            deltaY = height / 2f - matrixRectF.bottom + matrixRectF.height() / 2f;
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);

    }

    /**
     * 得到图片缩放后的宽高以及l,t,r,b
     *
     * @return
     */
    private RectF getMatrixRectF() {
        RectF rectF = new RectF();
        Matrix matrix = mScaleMatrix;

        Drawable d = getDrawable();

        if (d != null) {
            rectF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rectF);
        }

        return rectF;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }
}
