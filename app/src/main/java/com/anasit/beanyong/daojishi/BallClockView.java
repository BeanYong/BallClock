package com.anasit.beanyong.daojishi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by BeanYong on 2015/12/6.
 */
public class BallClockView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    /**
     * 小球的颜色，在mColors中随机产生
     */
    private final static int[] mColors = {0xffff0000, 0xff00ff00, 0xff0000ff, 0xff60202f,
            0xff60202f, 0xffdc3a90, 0xff10d344, 0xffddddcd, 0xff7a9012, 0xff891208};

    private SurfaceHolder mHoldler;
    private Canvas mCanvas;
    /**
     * 绘制时间的画笔
     */
    private Paint mTimePaint = null;
    /**
     * 绘制彩色小球的画笔
     */
    private Paint mBallsPaint = null;
    /**
     * 小球的半径
     */
    private float mRadius;
    /**
     * 所有的已生成的小球对象
     */
    private List<BallEntity> mBalls = null;
    /**
     * 当前显示的分钟数十位
     */
    private int mCurrentMinuteNum_1 = 0;
    /**
     * 当前显示的分钟数个位
     */
    private int mCurrentMinuteNum_2 = 0;
    /**
     * 当前显示的小时数十位
     */
    private int mCurrentHourNum_1 = 0;
    /**
     * 当前显示的小时数个位
     */
    private int mCurrentHourNum_2 = 0;
    /**
     * 当前显示的秒数十位
     */
    private int mCurrentSecondNum_1 = 0;
    /**
     * 当前显示的秒数个位
     */
    private int mCurrentSecondNum_2 = 0;


    public BallClockView(Context context) {
        this(context, null);
    }

    public BallClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BallClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mHoldler = getHolder();
        mHoldler.addCallback(this);
        setKeepScreenOn(true);
        mTimePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimePaint.setColor(0xff0000ff);
        mBallsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBalls = new LinkedList<>();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        /*
         * 小球半径计算策略：
         * 每个数字占7个小球，冒号占4个，共占7*6+4*2 = 50
         * 左右各留边15个小球 共 50+15*2 = 80
         */
        mRadius = getMeasuredWidth() * 1.0f / 160;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void run() {
        while (true) {
            long startTime = System.currentTimeMillis();
            draw();
            long gapTime = System.currentTimeMillis() - startTime;
            if (gapTime < 50) {
                try {
                    Thread.sleep(50 - gapTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 绘图方法
     */
    private void draw() {
        mCanvas = mHoldler.lockCanvas();
        if (mCanvas != null) {
            int[] perNumOfTime = getTime();
            drawBg();
            drawNum(15, 15, perNumOfTime[0]);
            drawNum(23, 15, perNumOfTime[1]);
            drawNum(29, 15, 10);//冒号
            drawNum(38, 15, perNumOfTime[2]);
            drawNum(47, 15, perNumOfTime[3]);
            drawNum(53, 15, 10);//冒号
            drawNum(62, 15, perNumOfTime[4]);
            drawNum(71, 15, perNumOfTime[5]);

            if (mCurrentHourNum_1 != perNumOfTime[0]) {
                generateColorfulBalls(15, 15, perNumOfTime[0]);
                mCurrentHourNum_1 = perNumOfTime[0];
            }

            if (mCurrentHourNum_2 != perNumOfTime[1]) {
                generateColorfulBalls(23, 15, perNumOfTime[1]);
                mCurrentHourNum_2 = perNumOfTime[1];
            }

            if (mCurrentMinuteNum_1 != perNumOfTime[2]) {
                generateColorfulBalls(38, 15, perNumOfTime[2]);
                mCurrentMinuteNum_1 = perNumOfTime[2];
            }

            if (mCurrentMinuteNum_2 != perNumOfTime[3]) {
                generateColorfulBalls(47, 15, perNumOfTime[3]);
                mCurrentMinuteNum_2 = perNumOfTime[3];
            }

            if (mCurrentSecondNum_1 != perNumOfTime[4]) {
                generateColorfulBalls(62, 15, perNumOfTime[4]);
                mCurrentSecondNum_1 = perNumOfTime[4];
            }

            if (mCurrentSecondNum_2 != perNumOfTime[5]) {
                generateColorfulBalls(71, 15, perNumOfTime[5]);
                mCurrentSecondNum_2 = perNumOfTime[5];
            }

            drawColorfulBalls();
            updateColorfulBalls();
            mHoldler.unlockCanvasAndPost(mCanvas);
        }
    }

    /**
     * 以marginLeftBall倍的小球直径为左边距，marginTopBall倍的小球直径为上边距，生成彩色num
     *
     * @param marginLeftBall 左边距
     * @param marginTopBall  上边距
     * @param num            要绘制的数字
     */
    private void generateColorfulBalls(int marginLeftBall, int marginTopBall, int num) {
        for (int i = 0; i < DigitMetrix.digit[num].length; i++) {
            marginTopBall++;
            marginLeftBall -= DigitMetrix.digit[num][i].length;
            for (int j = 0; j < DigitMetrix.digit[num][i].length; j++) {
                marginLeftBall++;
                if (DigitMetrix.digit[num][i][j] == 1) {
                    BallEntity ball = new BallEntity(marginLeftBall * 2 * mRadius, marginTopBall * 2 * mRadius,
                            (float) (Math.random() * 10 * Math.pow(-1, j)), (float) (Math.random() * -5), mColors[(int) (Math.random() * 10)]);
                    mBalls.add(ball);
                }
            }
        }
    }

    /**
     * 更新彩色小球的位置信息
     */
    private void updateColorfulBalls() {
        List<BallEntity> temp = new ArrayList<>();//记录需要删除的ball
        for (BallEntity ball : mBalls) {
            if (ball.getX() < 0 || ball.getX() > getMeasuredWidth()) {//小球已经滚出屏幕，从LinkedList中删除
                temp.add(ball);
            }

            if (ball.getY() > getMeasuredHeight() - 2 * mRadius) {
                ball.setY(getMeasuredHeight() - 2 * mRadius);
                ball.setVy((float) (-ball.getVy() * 0.60));
            }

            ball.setX(ball.getX() + ball.getVx());
            ball.setY(ball.getY() + ball.getVy());
            ball.setVy(ball.getVy() + ball.getG());
        }

        mBalls.removeAll(temp);
    }

    /**
     * 绘制mBalls里的所有彩色小球
     */
    private void drawColorfulBalls() {
        for (BallEntity ball : mBalls) {
            mBallsPaint.setColor(ball.getColor());
            mCanvas.drawCircle(ball.getX(), ball.getY(), mRadius, mBallsPaint);
        }
    }

    /**
     * 以marginLeftBall倍的小球直径为左边距，marginTopBall倍的小球直径为上边距，绘制num
     *
     * @param marginLeftBall 左边距
     * @param marginTopBall  上边距
     * @param num            要绘制的数字
     */
    private void drawNum(int marginLeftBall, int marginTopBall, int num) {
        for (int i = 0; i < DigitMetrix.digit[num].length; i++) {
            marginTopBall++;
            marginLeftBall -= DigitMetrix.digit[num][i].length;
            for (int j = 0; j < DigitMetrix.digit[num][i].length; j++) {
                marginLeftBall++;
                if (DigitMetrix.digit[num][i][j] == 1) {
                    mCanvas.drawCircle(marginLeftBall * 2 * mRadius, marginTopBall * 2 * mRadius, mRadius, mTimePaint);
                }
            }
        }
    }

    /**
     * 获取当前的时分秒，并以数组的形式返回
     *
     * @return result 当前的时分秒的每一位数字以数组形式返回
     */
    private int[] getTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int[] result = {hour / 10, hour % 10, minute / 10, minute % 10, second / 10, second % 10};
        return result;
    }

    private void drawBg() {
        mCanvas.drawColor(0xffffffff);
    }
}
