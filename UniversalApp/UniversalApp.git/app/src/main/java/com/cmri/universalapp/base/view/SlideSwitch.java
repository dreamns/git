package com.cmri.universalapp.base.view;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.cmri.universalapp.R;


/**
 *
 */
public class SlideSwitch extends View {

    public static final int SHAPERECT = 1;
    public static final int SHAPECIRCLE = 2;
    private static int RIMSIZE = 4;
    private static final int DEFAULTCOLORTHEME = 0xff00DB6A;
    // 3 attributes
    private int colorTheme;
    private boolean isOpen;
    private int shape;
    // varials of drawing
    private Paint paint;
    private Rect backRect;
    private Rect frontRect;
    private RectF frontCircleRect;
    private RectF backCircleRect;
    private int alpha;
    private int maxLeft;
    private int minLeft;
    private int frontrectLeft;
    private int frontRectLeftBegin = RIMSIZE;
    private int eventStartX;
    private int eventLastX;
    private int diffX = 0;
    private boolean slideable = true;
    private SlideListener listener;

    /**
     *
     */
    public interface SlideListener {
        /**
         *
         * @param v
         */
        public void open(SlideSwitch v);

        /**
         *
         * @param v
         */
        public void close(SlideSwitch v);
    }

    public SlideSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        listener = null;
        paint = new Paint();
        paint.setAntiAlias(true);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.slideswitch);
        colorTheme = a.getColor(R.styleable.slideswitch_themeColor,
                DEFAULTCOLORTHEME);
        isOpen = a.getBoolean(R.styleable.slideswitch_isOpen, false);
        shape = a.getInt(R.styleable.slideswitch_shape, SHAPECIRCLE);
        RIMSIZE = a.getInt(R.styleable.slideswitch_rim_size, 4);
        a.recycle();
    }

    public SlideSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideSwitch(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureDimension(280, widthMeasureSpec);
        int height = measureDimension(140, heightMeasureSpec);
        if (shape == SHAPECIRCLE) {
            if (width < height){
                width = height * 2;
            }
        }
        setMeasuredDimension(width, height);
        initDrawingVal();
    }

    /**
     *
     */
    public void initDrawingVal() {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        backCircleRect = new RectF();
        frontCircleRect = new RectF();
        frontRect = new Rect();
        backRect = new Rect(0, 0, width, height);
        minLeft = RIMSIZE;
        if (shape == SHAPERECT){
            maxLeft = width / 2;
        }else{
            maxLeft = width - (height - 2 * RIMSIZE) - RIMSIZE;
        }
        if (isOpen) {
            frontrectLeft = maxLeft;
            alpha = 255;
        } else {
            frontrectLeft = RIMSIZE;
            alpha = 0;
        }
        frontRectLeftBegin = frontrectLeft;
    }

    /**
     *
     * @param defaultSize
     * @param measureSpec
     * @return
     */
    public int measureDimension(int defaultSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize; // UNSPECIFIED
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (shape == SHAPERECT) {
            paint.setColor(Color.GRAY);
            canvas.drawRect(backRect, paint);
            paint.setColor(colorTheme);
            paint.setAlpha(alpha);
            canvas.drawRect(backRect, paint);
            frontRect.set(frontrectLeft, RIMSIZE, frontrectLeft
                    + getMeasuredWidth() / 2 - RIMSIZE, getMeasuredHeight()
                    - RIMSIZE);
            paint.setColor(Color.WHITE);
            canvas.drawRect(frontRect, paint);
        } else {
            // draw circle
            int radius;
            radius = backRect.height() / 2;
            paint.setColor(Color.LTGRAY);
            backCircleRect.set(backRect);
            canvas.drawRoundRect(backCircleRect, radius, radius, paint);
            paint.setColor(colorTheme);
            paint.setAlpha(alpha);
            canvas.drawRoundRect(backCircleRect, radius, radius, paint);
            frontRect.set(frontrectLeft, RIMSIZE, frontrectLeft
                    + backRect.height() - 2 * RIMSIZE, backRect.height()
                    - RIMSIZE);
            frontCircleRect.set(frontRect);
            paint.setColor(Color.WHITE);
            canvas.drawRoundRect(frontCircleRect, radius, radius, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!slideable){
            return super.onTouchEvent(event);
        }
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                eventStartX = (int) event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                eventLastX = (int) event.getRawX();
                diffX = eventLastX - eventStartX;
                int tempX = diffX + frontRectLeftBegin;
                tempX = (tempX > maxLeft ? maxLeft : tempX);
                tempX = (tempX < minLeft ? minLeft : tempX);
                if (tempX >= minLeft && tempX <= maxLeft) {
                    frontrectLeft = tempX;
                    alpha = (int) (255 * (float) (tempX - RIMSIZE) / (float) (maxLeft - RIMSIZE));
                    invalidateView();
                }
                break;
            case MotionEvent.ACTION_UP:
                int wholeX = (int) (event.getRawX() - eventStartX);
                frontRectLeftBegin = frontrectLeft;
                boolean toRight;
                toRight = (frontRectLeftBegin > maxLeft / 2 ? true : false);
                if (Math.abs(wholeX) < 3) {
                    toRight = !toRight;
                }
                moveToDest(toRight);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * draw again
     */
    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    public void setSlideListener(SlideListener listener) {
        this.listener = listener;
    }

    /**
     *
     * @param toRight
     */
    public void moveToDest(final boolean toRight) {
        ValueAnimator toDestAnim = ValueAnimator.ofInt(frontrectLeft,
                toRight ? maxLeft : minLeft);
        toDestAnim.setDuration(300);
        toDestAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        toDestAnim.start();
        toDestAnim.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                frontrectLeft = (Integer) animation.getAnimatedValue();
                alpha = (int) (255 * (float) (frontrectLeft - RIMSIZE) / (float) (maxLeft - RIMSIZE));
                invalidateView();
            }
        });
        toDestAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (toRight) {
                    isOpen = true;
                    if (listener != null){
                        listener.open(SlideSwitch.this);
                    }
                    frontRectLeftBegin = maxLeft;
                } else {
                    isOpen = false;
                    if (listener != null){
                        listener.close(SlideSwitch.this);
                    }
                    frontRectLeftBegin = minLeft;
                }
            }
        });
    }

    public void setState(boolean isOpen) {
        this.isOpen = isOpen;
        initDrawingVal();
        invalidateView();
//		if (listener != null)
//			if (isOpen == true) {
//				listener.open(SlideSwitch.this);
//			} else {
//				listener.close(SlideSwitch.this);
//			}
    }

    public void setShapeType(int shapeType) {
        this.shape = shapeType;
    }

    public void setSlideable(boolean slideable) {
        this.slideable = slideable;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.isOpen = bundle.getBoolean("isOpen");
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putBoolean("isOpen", this.isOpen);
        return bundle;
    }
}
