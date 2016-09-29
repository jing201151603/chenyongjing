package com.dxhj.tianlang.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

/**
 * Created by chenyongjing on 2016/2/13.
 *  
 */
public class JCircleImageView extends ImageView {

    //外圆的宽度
    private float outCircleWidth = dpToPx(2, getResources());

    //外圆的颜色
    private int outCircleColor = Color.WHITE;

    //画笔
    private Paint paint;

    //view的宽度和高度
    private float viewWidth;

    private float viewHeigth;

    private Bitmap bitmap;

    private Context context;

    public JCircleImageView(Context context) {
        this(context, null);
    }


    public JCircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public JCircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context, attrs, defStyleAttr);
    }


    public Context getJContext() {
        return context;
    }

    /**
     * 初始化资源文件
     *
     * @param context
     * @param attrs
     * @param defStyleAttr      
     */
    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        this.context = context;
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(outCircleWidth);
        paint.setColor(outCircleColor);//颜色
        paint.setAntiAlias(true);//设置抗锯齿
    }

    /**
     * view的测量
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec      
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float width = measureWith(widthMeasureSpec);
        float height = measureWith(heightMeasureSpec);
        if (width <= 0) width = dpToPx(50, getResources());
        if (height <= 0) height = dpToPx(50, getResources());
        viewWidth = width - outCircleWidth * 2;
        viewHeigth = height - outCircleWidth * 2;

        //调用该方法将测量后的宽和高设置进去，完成测量工作，
        setMeasuredDimension((int) width, (int) height);
    }

    /**
     * 测量宽和高
     *
     * @param widthMeasureSpec
     * @return      
     */
    private float measureWith(int widthMeasureSpec) {
        float result = 0;
        //从MeasureSpec对象中提取出来具体的测量模式和大小
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            //测量的模式，精确
            result = size;
        } else {
            result = viewWidth;
        }
        return result;
    }

    /**
     * 绘制
     *
     * @param canvas      
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //加载图片
        loadImage();

        if (bitmap != null) {
            //拿到最小的值(这里我们要去到最小的)
            float min = Math.min(viewWidth, viewHeigth);
            min = Math.abs(min);
            float circleCenter = min / 2;
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) min, (int) min, false);

            //画图像
            canvas.drawBitmap(createCircleBitmap(bitmap, (int) min), outCircleWidth, outCircleWidth, null);

            //画圆
            canvas.drawCircle(circleCenter + outCircleWidth, circleCenter + outCircleWidth, circleCenter, paint);
        }

    }


    /**
     * 创建一个圆形的bitmap
     *
     * @param bitmap  传入的bitmap
     * @param
     * @return      
     */

    private Bitmap createCircleBitmap(Bitmap bitmap, int min) {
        Bitmap image = null;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        image = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);

        //画一个和图片大小相等的画布
        canvas.drawCircle(min / 2, min / 2, min / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return image;
    }


    /**
     * 加载bitmap
     *      
     */
    private void loadImage() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) this.getDrawable();

        if (bitmapDrawable != null) {
            bitmap = bitmapDrawable.getBitmap();
        }
    }


    /**
     * 对外提供的可以设置外圆的颜色的方法
     *
     * @param outCircleColor      
     */
    public void setOutCircleColor(int outCircleColor) {
        if (null != paint) {
            paint.setColor(outCircleColor);
        }
        this.invalidate();
    }


    /**
     * 对外提供给的设置外圆的宽度大小的方法
     *
     * @param outCircleWidth      
     */
    public void setOutCircleWidth(float outCircleWidth) {
        this.outCircleWidth = outCircleWidth;
        paint.setStrokeWidth(this.outCircleWidth);
        this.invalidate();
    }

    /**
     * Convert Dp to Pixel
     */
    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }
}