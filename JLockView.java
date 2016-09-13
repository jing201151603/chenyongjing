package com.dxhj.tianlang.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 陈永镜 on 2016/9/1.
 */
public class JLockView extends View {

    final static String ANDROIDXML = "http://schemas.android.com/apk/res/android";

    private int width, height;
    private int count = 3;
    private Context context;
    private int radiusBig = 70;
    private int radiusSmall = 20;
    private Point points[][] = new Point[count][count];
    private Paint smallCirclePaint;
    private Paint bigCirclePaint;
    private int bigCircleColor = Color.parseColor("#757C85");
    private int smallCircleColor = Color.GRAY;
    private int selectCircleColor = Color.RED;
    private int circleWidth = 2;
    private int backgroundColor = Color.parseColor("#C5A930");

    private Paint linePaint;
    private int lineColor = Color.BLUE;
    private int lineWidth = 5;
    private float startX;
    private float startY;
    private float currentX;
    private float currentY;
    private List<Point> pointList = new ArrayList<>();//存储所有line的点
    private List<Point> selectPointList = new ArrayList<>();//存储已选中的点
    private SharedPreferences sharedPreferences;
    private String length = "length";
    private String pointX = "pointX";
    private String pointY = "pointY";
    private int pwdLength = 0;//存储的密码长度
    private List<Point> pwdPoints = new ArrayList<>();//存放原始密码
    private int requestPwdLength = 4;//密码长度最低为4
    private PwdType pwdType = PwdType.setPwd;//默认为创建密码
    private boolean isEnable = true;//是否可用
    private boolean showLine = true;//是否显示线
    private WindowManager windowManager;


    private Context getJContext() {
        return context;
    }


    public JLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public JLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public JLockView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        backgroundColor = attrs.getAttributeIntValue(ANDROIDXML, "background", 0);
        warm(getClass().getName(), backgroundColor + "," + Color.WHITE);

        sharedPreferences = context.getSharedPreferences("JLockView", Context.MODE_PRIVATE);
        pwdLength = sharedPreferences.getInt(length, 0);
        if (pwdLength > 0) {
            pwdType = PwdType.inputPwd;
            for (int i = 0; i < pwdLength; i++) {
                Point point = new Point(sharedPreferences.getFloat(pointX + i, 0), sharedPreferences.getFloat(pointY + i, 0));
                pwdPoints.add(point);
            }
        }

        bigCirclePaint = new Paint();
        bigCirclePaint.setColor(bigCircleColor);
        bigCirclePaint.setStyle(Paint.Style.STROKE);
        bigCirclePaint.setAntiAlias(true);
        bigCirclePaint.setStrokeWidth(circleWidth);

        smallCirclePaint = new Paint();
        smallCirclePaint.setColor(smallCircleColor);
        smallCirclePaint.setStyle(Paint.Style.STROKE);
        smallCirclePaint.setAntiAlias(true);
        smallCirclePaint.setStrokeWidth(circleWidth);

        linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(lineWidth);

        setBackgroundColor(backgroundColor);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        warm(getClass().getName(), "onDraw");
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                Point point = points[i][j];
                canvas.drawCircle(point.getPointX(), point.getPointY(), radiusBig, bigCirclePaint);
                if (point.isSelect) {//选中情况下
                    smallCirclePaint.setColor(selectCircleColor);
                    canvas.drawCircle(point.getPointX(), point.getPointY(), radiusSmall, smallCirclePaint);
                } else {//没选中情况下
                    smallCirclePaint.setColor(smallCircleColor);
                    canvas.drawCircle(point.getPointX(), point.getPointY(), radiusSmall, smallCirclePaint);
                }
            }
        }

        int sise = pointList.size();
        if (sise > 0 && showLine) {
            Point pointNow = pointList.get(sise - 1);
            canvas.drawLine(startX, startY, pointNow.getPointX(), pointNow.getPointY(), linePaint);

            float lastX = 0, lastY = 0;
            if (selectPointList.size() > 0) {
                for (int i = 0; i < selectPointList.size(); i++) {
                    Point point = selectPointList.get(i);
                    if (i == 0) {
                        canvas.drawLine(point.getPointX(), point.getPointY(), point.getPointX(), point.getPointY(), linePaint);
                    } else {
                        point.setLastPointX(lastX);
                        point.setLastPointY(lastY);
                        canvas.drawLine(point.getLastPointX(), point.getLastPointY(), point.getPointX(), point.getPointY(), linePaint);
                    }
                    lastX = point.getPointX();
                    lastY = point.getPointY();
                    startX = point.getPointX();
                    startY = point.getPointY();
                }
            }

        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /*height = View.MeasureSpec.getSize(heightMeasureSpec);
        width = View.MeasureSpec.getSize(widthMeasureSpec);*/
        width = windowManager.getDefaultDisplay().getWidth();
        int centerX = width / 2;
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                points[i][j] = new Point(centerX + (dip2px(getJContext(), 100) * i - dip2px(getJContext(), 100)), dip2px(getJContext(), 100) + dip2px(getJContext(), 100) * j);
            }
        }
        warm(getClass().getName(), "onMeasure,width=" + width + ",height=" + height);
        setMeasuredDimension(width, width);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        warm(getClass().getName(), "onLayout");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnable) return true;
        currentX = event.getX();
        currentY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                pointList.add(new Point(currentX, currentY));
                for (int i = 0; i < count; i++) {
                    for (int j = 0; j < count; j++) {
                        Point point = points[i][j];
                        if (isIncluding(currentX, currentY, point) && !point.isSelect) {
                            point.setSelect(true);
                            selectPointList.add(point);
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                clear();
                break;
            case MotionEvent.ACTION_CANCEL:
                clear();
                break;
        }
        postInvalidate();
        return true;
    }

    private void clear() {
        if (onJLockListener == null) {
            clearLines();
            return;
        }
        new Handler() {
        }.postDelayed(new Runnable() {
                          @Override
                          public void run() {
                              if (selectPointList.size() < requestPwdLength) {//密码长度不足
                                  onJLockListener.onShort();
                                  if (pwdType != PwdType.inputPwd)
                                      pwdType = PwdType.setPwd;
                              } else {


                                  if (pwdType == PwdType.setPwd) {//设置密码
                                      if (pwdPoints.size() > 0) pwdPoints.clear();
                                      pwdPoints.addAll(selectPointList);
                                      pwdType = PwdType.rePwd;
                                      onJLockListener.onReset();

                                  } else if (pwdType == PwdType.rePwd) {//确认密码
                                      if (pwdPoints.size() != selectPointList.size()) {//两次输入的长度不一致
                                          inputIncorrect();
                                      } else {//两次输入密码的长度一致
                                          if (isSame(pwdPoints, selectPointList)) {//密码一致
                                              onJLockListener.onCreateSucceed();//密码创建成功
                                              samePwd(pwdPoints);//储存密码
                                          } else {
                                              inputIncorrect();
                                          }
                                      }

                                  } else if (pwdType == PwdType.inputPwd) {//输入手势密码
                                      if (pwdPoints.size() == selectPointList.size() && isSame(pwdPoints, selectPointList)) {//长度一致并且任意一集合包含另一集合时，密码正确
                                          onJLockListener.onInputPwd(true);
                                      } else {
                                          onJLockListener.onInputPwd(false);
                                      }
                                  }

                              }
                              clearLines();
                          }
                      }

                , 300);

    }

    private void inputIncorrect() {
        onJLockListener.onDifferent();//两次密码输入不一致
        pwdType = PwdType.setPwd;
        pwdPoints.clear();
    }

    private void samePwd(List<Point> pwdPoints) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(length, pwdPoints.size());
        for (int i = 0; i < pwdPoints.size(); i++) {
            Point point = pwdPoints.get(i);
            editor.putFloat(pointX + i, point.getPointX());
            editor.putFloat(pointY + i, point.getPointY());
        }
        editor.commit();
        pwdType = PwdType.inputPwd;
    }

    /**
     * 显示线
     *
     * @param
     */
    public void showLine() {
        showLine = true;
    }

    /**
     * 隐藏线
     */
    public void hideLine() {
        showLine = false;
    }

    private void clearLines() {
        pointList.clear();
        selectPointList.clear();
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                points[i][j].setSelect(false);
            }
        }
        postInvalidate();
    }

    public void createPwd() {
        if (recoverPwdList.size() > 0) recoverPwdList.clear();
        getPwds(recoverPwdList);//清除前保存在缓存中，方便事件回滚

        sharedPreferences.edit().clear().commit();
        pwdPoints.clear();
        selectPointList.clear();
        pointList.clear();
        pwdType = PwdType.setPwd;
    }

    private void getPwds(List<Point> pwdPoints) {
        for (int i = 0; i < getLength(); i++) {
            Point point = new Point(sharedPreferences.getFloat(pointX + i, 0), sharedPreferences.getFloat(pointY + i, 0));
            pwdPoints.add(point);
        }
    }

    public int getLength() {
        return sharedPreferences.getInt(length, 0);
    }

    private List<Point> recoverPwdList = new ArrayList<>();

    public void recover() {
        samePwd(recoverPwdList);
    }

    public PwdType getPwdType() {
        return pwdType;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    private boolean isSame(List<Point> points1, List<Point> points2) {
        for (int i = 0; i < points1.size(); i++) {
            if (points1.get(i).getPointY() != points2.get(i).getPointY() || points1.get(i).getPointX() != points2.get(i).getPointX())
                return false;
        }
        return true;
    }

    public interface OnJLockListener {
        void onShort();//密码长度不足

        void onCreateSucceed();//密码设置成功

        void onDifferent();//两次密码不一致

        void onInputPwd(boolean result);//输入密码

        void onReset();//再次输入手势密码
    }

    private OnJLockListener onJLockListener;

    public void setOnJLockListener(OnJLockListener onJLockListener) {
        this.onJLockListener = onJLockListener;
    }

    public enum PwdType {
        setPwd, rePwd, inputPwd
    }

    private boolean isIncluding(float currentX, float currentY, Point point) {
        float distanceX = Math.abs(currentX - point.getPointX());
        float distanceY = Math.abs(currentY - point.getPointY());
        int distanceZ = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
        if (distanceZ <= radiusBig) {//在圆内
            return true;
        }
        return false;
    }

    private boolean isIncluding(float currentX, float currentY) {
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                Point point = points[i][j];
                float distanceX = Math.abs(currentX - point.getPointX());
                float distanceY = Math.abs(currentY - point.getPointY());
                int distanceZ = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
                if (distanceZ <= radiusBig) {//你在圆内
                    return true;
                }
            }

        }

        return false;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void warm(String tag, String msg) {
        Log.w(tag, msg);
    }


    class Point {
        float pointX, lastPointX = 0;
        float pointY, lastPointY = 0;
        boolean isSelect = false;

        public Point(float pointX, float pointY) {
            this.pointX = pointX;
            this.pointY = pointY;
        }

        public float getPointX() {
            return pointX;
        }

        public void setPointX(float pointX) {
            this.pointX = pointX;
        }

        public float getPointY() {
            return pointY;
        }

        public void setPointY(float pointY) {
            this.pointY = pointY;
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }

        public float getLastPointX() {
            return lastPointX;
        }

        public void setLastPointX(float lastPointX) {
            this.lastPointX = lastPointX;
        }

        public float getLastPointY() {
            return lastPointY;
        }

        public void setLastPointY(float lastPointY) {
            this.lastPointY = lastPointY;
        }
    }

}
