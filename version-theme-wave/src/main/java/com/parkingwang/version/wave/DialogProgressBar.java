package com.parkingwang.version.wave.supports;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.parkingwang.version.wave.R;

import java.text.DecimalFormat;


public class DialogProgressBar extends View {


    private int centerX;

    private int centerY;

    private Paint normalPaint;

    private Paint progressPaint;

    private Paint ballPaint;


    private Paint textPaint;

    private int textStrokeWidth = 3;

    private float strokeWidth = 100;


    /**
     * 扫过的度
     */
    private float sweepDegree = 360f;

    private float lineHeight;


    private int maxPercent = 100;

    private float targetValue;
    private int maxValue;

    private float lastPercent;
    private ValueAnimator animator;

    private float targetPercent;
    private float currPercent;

    private int totalDuration;
    private DecimalFormat decimalFormat;
    private boolean isComplete = false;
    private Context mContext;
    private Bitmap mBitmap;//减少绘制时间

    public DialogProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DialogProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DialogProgressBar);
        maxValue = a.getInteger(R.styleable.DialogProgressBar_max_value, 100);
        totalDuration = a.getInteger(R.styleable.DialogProgressBar_duration, 1000);
        strokeWidth =  a.getDimension(R.styleable.DialogProgressBar_line_stroke_width, dip(6));
        lineHeight = a.getDimension(R.styleable.DialogProgressBar_progress_line_height, dip(16));

        setNormalPaint(a);
        setProgressPaint(a);
        setBallPaint(a);
        setTextPaint(a);
        a.recycle();
        decimalFormat = new DecimalFormat("0.#");
        initAnimation();
        mBitmap = drawBitmap();
    }

    private void setBallPaint(TypedArray a) {
        ballPaint = simplePaint();
        ballPaint.setStyle(Paint.Style.FILL);
        int ballColor = a.getColor(R.styleable.DialogProgressBar_ball_color, progressPaint.getColor());
        ballPaint.setColor(ballColor);
    }


    private void setTextPaint(TypedArray a) {
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textStrokeWidth = a.getInteger(R.styleable.DialogProgressBar_text_stroke_width, textStrokeWidth);
        textPaint.setStrokeWidth(textStrokeWidth);
        textPaint.setTextSize(a.getDimension(R.styleable.DialogProgressBar_text_size, sp(22)));
        textPaint.setColor(a.getColor(R.styleable.DialogProgressBar_text_color, progressPaint.getColor()));
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);

    }

    private void setNormalPaint(TypedArray a) {
        normalPaint = simplePaint();
        int normalColor = a.getColor(R.styleable.DialogProgressBar_normal_color, Color.GRAY);
        normalPaint.setColor(normalColor);
    }

    private void setProgressPaint(TypedArray a) {
        progressPaint = simplePaint();
        int progressColor = a.getColor(R.styleable.DialogProgressBar_progress_color, Color.GREEN);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setColor(progressColor);
    }

    private void initAnimation() {
        animator = new ValueAnimator();
        animator.setDuration(totalDuration);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currPercent = (float) animation.getAnimatedValue();

                lastPercent = currPercent;

                invalidate();
            }
        });
    }


    private Paint simplePaint() {
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setDither(true);


        return mPaint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY) {
            widthSize = (int) dip(100);
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            heightSize = (int) dip(100);
        }

        int size = Math.min(widthSize, heightSize);
        setMeasuredDimension(size, size);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawNormalLines(canvas);
        drawPercentLines(canvas);
        // progress
        canvas.rotate(currPercent / maxPercent * sweepDegree);

        if (!isComplete) {
            drawCenterValue(canvas);
        } else {
            drawCenterLogo(canvas);
        }
    }

    private void drawCenterValue(Canvas canvas) {
        float value;
        if (currPercent == targetPercent) {
            value = targetValue;
        } else {
            value = (int) (maxValue * currPercent / maxPercent);
        }
        canvas.save();
        canvas.drawText(decimalFormat.format(value) + "%", centerX - textPaint.measureText(decimalFormat.format(value) + "%") / 2, centerY - (textPaint.descent() + textPaint.ascent()) / 2, textPaint);
        canvas.restore();
    }

    private void drawCenterLogo(Canvas canvas) {
        canvas.save();
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, getWidth() / 2 - mBitmap.getWidth() / 2, getHeight() / 2 - mBitmap.getHeight() / 2, null);
        }
        canvas.restore();
    }

    private void drawPercentLines(Canvas canvas) {
        canvas.save();
        canvas.rotate(90, centerX, centerY);
        for (float i = 0; i < currPercent / (10 / 3.6); i++) {
            canvas.drawLine(strokeWidth + getPaddingLeft(), centerY, lineHeight + getPaddingLeft(), centerY, progressPaint);
            canvas.rotate(10, centerX, centerY);
        }
        canvas.restore();
    }

    private void drawNormalLines(Canvas canvas) {
        canvas.save();
        canvas.rotate(90, centerX, centerY);
        for (int i = 0; i < maxPercent / (10 / 3.6); i++) { //画普通的
            canvas.drawLine(strokeWidth + getPaddingLeft(), centerY, lineHeight + getPaddingLeft(), centerY, normalPaint);
            canvas.rotate(10, centerX, centerY);
        }
        canvas.restore();
    }

    public void setValue(float value) {
        targetValue = value;
        if (value >= maxValue) {
            targetPercent = maxPercent;
        } else {
            targetPercent = (value / maxValue * maxPercent);
        }

        animator.setFloatValues(lastPercent, targetPercent);
        animator.setDuration(0);
        animator.start();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = centerY = w / 2;
    }

    private float dip(int dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getContext().getResources().getDisplayMetrics());
    }

    private float sp(int sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getContext().getResources().getDisplayMetrics());
    }


    public void setPiantComplete() {
        progressPaint.setColor(Color.parseColor("#6FDC77"));
        isComplete = true;
        invalidate();
    }

    public Bitmap drawBitmap() {
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_success);
        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 0.5f);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap;
    }
}
