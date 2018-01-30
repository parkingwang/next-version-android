package com.parkingwang.version.check.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.parkingwang.version.check.R;
/**
 * @author 占迎辉 (zhanyinghui@parkingwang.com)
 * @version 2017/10/19
 */

public class DrawHookView extends View {
    private final int SHADOW_COLOR = Color.parseColor("#b1ffda");
    private final int IN_CIRCLE_COLOR = Color.parseColor("#ffffff");
    private final int OUT_CIRCLE_COLOR = Color.parseColor("#88ffffff");
    private Paint paint = new Paint();
    private Paint lightPaint = new Paint();
    private Bitmap mBitmap;
    private Paint pathPaint = new Paint();
    //绘制圆弧的进度值
    private long progress = 0;
    //线1的x轴
    private int line1_x = 0;
    //线1的y轴
    private int line1_y = 0;
    //线2的x轴
    private int line2_x = 0;
    //线2的y轴
    private int line2_y = 0;
    private boolean isComplete = false;
    private boolean isOver = false;
    private int mCenter;
    private int mCenter1;
    private int mRadius;
    private OnCompleteListener mOnCompleteListener;

    public DrawHookView(Context context) {
        super(context);
    }

    public DrawHookView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawHookView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init() {
        initBitmap();

        paint.setColor(OUT_CIRCLE_COLOR);
        paint.setAlpha(80);
        //设置圆弧的宽度
        paint.setStrokeWidth(dip2px(getContext(), 3));
        //设置圆弧为空心
        paint.setStyle(Paint.Style.STROKE);
        //消除锯齿
        paint.setAntiAlias(true);
        paint.setStrokeWidth(8);

        //设置画笔颜色
        setLayerType(View.LAYER_TYPE_SOFTWARE, lightPaint);
        lightPaint.setColor(IN_CIRCLE_COLOR);
        lightPaint.setAlpha(255);
        //设置圆弧的宽度
        lightPaint.setStrokeWidth(8);
        //设置圆弧为空心
        lightPaint.setStyle(Paint.Style.STROKE);
        //消除锯齿
        lightPaint.setShadowLayer(12, 0, 0, SHADOW_COLOR);
        lightPaint.setAntiAlias(true);

        setLayerType(View.LAYER_TYPE_SOFTWARE, pathPaint);
        pathPaint.setColor(IN_CIRCLE_COLOR);
        pathPaint.setAlpha(255);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setAntiAlias(true);
        pathPaint.setShadowLayer(12, 0, 0, SHADOW_COLOR);
    }

    //绘制
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isComplete) {
            drawBitmap(canvas);
        }
        if (progress < 100) {
            //获取圆心的x坐标
            mCenter = getWidth() / 2;
            mCenter1 = mCenter - getWidth() / 5;
            //圆弧半径
            mRadius = getWidth() / 2 - 20;
            canvas.drawCircle(mCenter, mCenter, mRadius, paint);

            //定义的圆弧的形状和大小的界限
            RectF rectF = new RectF(mCenter - mRadius, mCenter - mRadius, mCenter + mRadius, mCenter + mRadius);
            //根据进度画
            // 圆弧
            canvas.drawArc(rectF, -90, 360 * progress / 100, false, lightPaint);
        }
        /**
         * 绘制对勾
         */
        //先等圆弧画完，才话对勾
        if (progress == 100) {
            //定义的圆弧的形状和大小的界限
            RectF rectF = new RectF(mCenter - mRadius, mCenter - mRadius, mCenter + mRadius, mCenter + mRadius);
            //根据进度画
            // 圆弧
            canvas.drawArc(rectF, -90, 360 * progress / 100, false, lightPaint);
            setComplete();
            if (line1_x < mRadius / 3) {
                line1_x++;
                line1_y++;
            }
            pathPaint.setStrokeWidth(16);
            //画第一根线
            canvas.drawLine(mCenter1, mCenter, mCenter1 + line1_x + 4, mCenter + line1_y + 4, pathPaint);

            if (line1_x == mRadius / 3) {
                line2_x = line1_x;
                line2_y = line1_y;
                line1_x++;
                line1_y++;
            }
            if (line1_x >= mRadius / 3 && line2_x <= mRadius) {
                line2_x++;
                line2_y--;
            }
            //画第二根线
            canvas.drawLine(mCenter1 + line1_x - 1, mCenter + line1_y, mCenter1 + line2_x, mCenter + line2_y, pathPaint);
            canvas.save();
            if (line2_x == mRadius) {
                mOnCompleteListener.onDrawComplete();
            }
        }


    }

    public boolean drawComplete() {
        return isOver;
    }

    private void initBitmap() {
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.arrow_download);
        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 0.5f);
        mBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

    }

    private void drawBitmap(Canvas canvas) {
        if (mBitmap != null) {
            canvas.save();
            canvas.drawBitmap(mBitmap, getWidth() / 2 - mBitmap.getWidth() / 2, getHeight() / 2 - mBitmap.getHeight() / 2, new Paint());
        }
    }

    public static int dip2px(Context context, int dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    public void setValue(long percent) {
        if (percent > 100) {
            throw new IllegalArgumentException("percent must less than 100!");
        }
        setCurPercent(percent);
    }

    private void setCurPercent(long percent) {
        this.progress = percent;
        invalidate();
    }

    public void setComplete() {
        if (!isComplete) {
            isComplete = true;
            Paint clearPaint = new Paint();
            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        }
        invalidate();
    }

    public void addOnCompleteListener(OnCompleteListener onComPleteListener) {
        mOnCompleteListener = onComPleteListener;
    }

    public interface OnCompleteListener {
        void onDrawComplete();
    }
}
