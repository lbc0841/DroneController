package com.ben.dronecontroller.views;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.ben.dronecontroller.R;


public class VirtualJoystickView extends View {
    private Paint paint, boldPaint, boldPaint2, fillPaint, glowPaint;

    private float width, height;
    private float centerX, centerY;

    private float x, y;
    private float newX, newY;

    private final float radius = 450;

    private long delayTime = 0;

    private int horizontalLineColor = Color.WHITE;
    private int verticalLineColor = Color.WHITE;
    private int crosshairColor = Color.WHITE;
    private int directionArrowColor = Color.WHITE;


    private final float scopeLimit = 350;
    private final float oScope = 3;
    private final float sensitivity = 16;


    public VirtualJoystickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init(){
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(80);

        boldPaint = new Paint();
        boldPaint.setColor(Color.BLACK);
        boldPaint.setStrokeWidth(18f);
//        boldPaint.setStrokeCap(Paint.Cap.ROUND);
        boldPaint.setStyle(Paint.Style.STROKE);
        boldPaint.setTextSize(80);

        boldPaint2 = new Paint();
        boldPaint2.setColor(Color.BLACK);
        boldPaint2.setStrokeWidth(30f);
        boldPaint2.setStyle(Paint.Style.STROKE);
        boldPaint2.setTextSize(80);

        fillPaint = new Paint();
        fillPaint.setColor(Color.BLACK);
        fillPaint.setStrokeWidth(5f);
        fillPaint.setStrokeCap(Paint.Cap.ROUND);
        fillPaint.setTextSize(86);

        glowPaint = new Paint();
        glowPaint.setColor(Color.BLACK);
        glowPaint.setStrokeWidth(20f);
        glowPaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL));
        glowPaint.setTextSize(80);


        crosshairColor = ContextCompat.getColor(this.getContext(), R.color.crosshair_color);
        directionArrowColor = ContextCompat.getColor(this.getContext(), R.color.direction_arrow_color);

        horizontalLineColor = crosshairColor;
        verticalLineColor = crosshairColor;
    }


    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        width = getWidth();
        height = getHeight();

        centerX = width/2f;
        centerY = height/2f +40f;

        newX += (x-newX)/7;
        newY += (y-newY)/7;

        drawCrosshair(canvas);
        drawCircles(canvas);
        drawDirectionArrow(canvas);
        drawPoint(canvas);
        drawBattery(canvas);
        drawSignal(canvas);
        drawDashboard(canvas);

        invalidate();

    }


    private void drawCrosshair(Canvas _canvas){
        paint.setColor(horizontalLineColor);
        _canvas.drawLine(centerX+radius, centerY, centerX-radius, centerY, paint); // X軸

        paint.setColor(verticalLineColor);
        _canvas.drawLine(centerX, centerY+radius, centerX, centerY-radius, paint);  // Y軸
    }

    private void drawCircles(Canvas _canvas){

        paint.setColor(crosshairColor);
        boldPaint.setColor(crosshairColor);
        _canvas.drawCircle(centerX, centerY, radius, boldPaint);
        _canvas.drawCircle(centerX, centerY, radius/5*3, paint);
    }

    private void drawDirectionArrow(Canvas _canvas){
        boldPaint.setColor(directionArrowColor);

        Path directionArrowPath = new Path();

        if(newX*newX + newY*newY > 2500){

            float direction = (float) Math.atan(newY/newX);
            if(newX < 0) { direction += (float) Math.toRadians(180); }

            for(int i=-5; i<=5; i++){
                float cos = (float) (Math.cos( direction + Math.toRadians(4*i) ));
                float sin = (float) (Math.sin( direction + Math.toRadians(4*i) ));

                if(i == -5){
                    directionArrowPath.moveTo(
                            cos*radius + centerX,
                            sin*radius + centerY
                    );
                }
                else if(i == 0){
                    directionArrowPath.lineTo(
                            cos*(radius+20) + centerX,
                            sin*(radius+20) + centerY
                    );
                }
                else {
                    directionArrowPath.lineTo(
                            cos*radius + centerX,
                            sin*radius + centerY
                    );
                }
            }
            _canvas.drawPath(directionArrowPath, boldPaint);
        }

    }

    private void drawPoint(Canvas _canvas){
        glowPaint.setColor(crosshairColor);
        _canvas.drawCircle(newX + centerX, newY + centerY, 30, glowPaint);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void drawBattery(Canvas _canvas){
        int scaleX = 200;
        int scaleY = 180;
        int startPointX = -40;
        int startPointY = (int) centerY + 300;

        Drawable batteryIcon = getResources().getDrawable(R.drawable.ic_battery_full, null);
        batteryIcon.setBounds(startPointX, startPointY, startPointX+scaleX, startPointY+scaleY);
        batteryIcon.draw(_canvas);

        paint.setColor(Color.GRAY);

        if(delayTime == 0){
            _canvas.drawText("- %", startPointX + (float) scaleX /2, startPointY+scaleY, paint);
        }
        else{
            _canvas.drawText("100%", startPointX + (float) scaleX /2, startPointY+scaleY, paint);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void drawSignal(Canvas _canvas){
        int scaleX = 200;
        int scaleY = 200;
        int startPointX = (int) width - scaleX;
        int startPointY = (int) centerY + 300;

        Drawable signalIcon = getResources().getDrawable(R.drawable.ic_signal_full, null);
        signalIcon.setBounds(startPointX, startPointY, startPointX+scaleX, startPointY+scaleY);
        signalIcon.draw(_canvas);

        paint.setColor(Color.GRAY);
        if(delayTime == 0){
            _canvas.drawText("- ms", startPointX - (float) scaleX /2, startPointY+scaleY, paint);
        }
        else{
            _canvas.drawText(delayTime + "ms", startPointX - (float) scaleX /2, startPointY+scaleY, paint);
        }
    }

    @SuppressLint("DefaultLocale")
    private void drawDashboard(Canvas _canvas){
        paint.setColor(Color.GRAY);
        boldPaint.setColor(Color.GRAY);
        boldPaint2.setColor(Color.WHITE);
        fillPaint.setColor(Color.WHITE);

        Path dashboardPath = new Path();
//        Path velocityPath = new Path();
        float r = radius+180;

        int iScope = 20;
        for(int i=-iScope; i<=iScope; i++){
            float cos = (float) (Math.cos( Math.toRadians(2.4f*i -90) ));
            float sin = (float) (Math.sin( Math.toRadians(2.4f*i -90) ));

            if(i == -iScope){
                dashboardPath.moveTo(
                        cos*r + centerX,
                        sin*r + centerY
                );
//                velocityPath.moveTo(
//                        cos*(r-10) + centerX,
//                        sin*(r-10) + centerY
//                );
            }
            else if (i == iScope) {
//                String v = String.format("%03d", getVelocity()[0]);
                String v = String.format("%03d", 0);
                _canvas.drawText(v, width-180, sin*r + centerY+60, fillPaint);
            }
            else {
                dashboardPath.lineTo(
                        cos*r + centerX,
                        sin*r + centerY
                );
            }

            if(i < iScope-1){
                if(i%4 == 0){
                    _canvas.drawLine(
                            cos*(r+6) + centerX,
                            sin*(r+6) + centerY,
                            cos*(r-50) + centerX,
                            sin*(r-50) + centerY,
                            paint
                    );
                }
                else {
                    _canvas.drawLine(
                            cos*r + centerX,
                            sin*r + centerY,
                            cos*(r-20) + centerX,
                            sin*(r-20) + centerY,
                            paint
                    );
                }
            }

//            if( i < getVelocity()[0]*iScope/50 - iScope){
//                velocityPath.lineTo(
//                        cos*(r-10) + centerX,
//                        sin*(r-10) + centerY
//                );
//            }
        }
        _canvas.drawPath(dashboardPath, boldPaint);
//        _canvas.drawPath(velocityPath, boldPaint2);
    }





    public void updatePoint(float _x, float _y) {

        x = -_x * sensitivity;
        y =  _y * sensitivity;

        if( x*x + y*y > scopeLimit*scopeLimit ){

            x = (float) (x*scopeLimit / Math.sqrt(x*x + y*y));
            y = (float) (y*scopeLimit / Math.sqrt(x*x + y*y));
        }

//        updateCrosshairColor(_x, _y);
    }

    public void updateDelayTime(long delayTime_ms) {

        delayTime = delayTime_ms;
    }

    private void updateCrosshairColor(float _x, float _y){

        if(_x<oScope && _x>-oScope && _y<oScope && _y>-oScope) {

            horizontalLineColor = Color.RED;
            verticalLineColor = Color.RED;
        }
        else if(_x<oScope && _x>-oScope){

            verticalLineColor = Color.YELLOW;
            horizontalLineColor = crosshairColor;
        }
        else if(_y<oScope && _y>-oScope){

            horizontalLineColor = Color.YELLOW;
            verticalLineColor = crosshairColor;
        }
        else {
            horizontalLineColor = crosshairColor;
            verticalLineColor = crosshairColor;
        }
    }

    public int[] getVelocity(){

        int velocity;
        int velocityX;
        int velocityY;

        if(Math.abs(newX) < oScope*sensitivity){
            velocityX = 0;
        }
        else {
            velocityX = (int) (newX / scopeLimit * 100);
        }

        if(Math.abs(newY) < oScope*sensitivity){
            velocityY = 0;
        }
        else {
            velocityY = (int) (-newY / scopeLimit * 100);
        }

        velocity = (int) Math.sqrt(velocityX*velocityX + velocityY*velocityY);

        if(velocity > 100){
            velocity = 100;
        }

        return new int[] {
                velocity,
                velocityX,
                velocityY
        };
    }


}
