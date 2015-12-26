package com.anasit.beanyong.daojishi;

/**
 * Created by BeanYong on 2015/12/6.
 */
public class BallEntity {
    /**
     * 小球圆心的横坐标
     */
    private float x;
    /**
     * 小球圆心的纵坐标
     */
    private float y;
    /**
     * 小球水平方向的速度
     */
    private float Vx;
    /**
     * 小球竖直方向的速度
     */
    private float Vy;
    /**
     * 小球竖直方向上的加速度
     */
    private float g = 3;
    /**
     * 小球的颜色
     */
    private int color;

    public BallEntity(float x, float y, float vx, float vy, int color) {
        this.x = x;
        this.y = y;
        Vx = vx;
        Vy = vy;
        this.color = color;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getVx() {
        return Vx;
    }

    public void setVx(float vx) {
        Vx = vx;
    }

    public float getVy() {
        return Vy;
    }

    public void setVy(float vy) {
        Vy = vy;
    }

    public float getG() {
        return g;
    }

    public void setG(float g) {
        this.g = g;
    }

    public int getColor() {
        return color;
    }
}
