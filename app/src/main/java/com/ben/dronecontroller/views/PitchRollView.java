package com.ben.dronecontroller.views;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.ben.dronecontroller.Constants;
import com.ben.dronecontroller.R;

import java.util.ArrayList;


public class PitchRollView extends View {
    private Paint paint;

    private float x = 0;
    private float y = 0;

    private float newX;
    private float newY;

    private int horizontalLineColor = Color.WHITE;
    private int verticalLineColor = Color.WHITE;
    private int crosshairColor = Color.WHITE;

    private int targetAngle;

    private Constants.ChartDisplayMode displayMode = Constants.ChartDisplayMode.LINE_CHART;

    private final ArrayList<Float> pastValue_fusion = new ArrayList<>();
    private final ArrayList<Float> pastValue_acc = new ArrayList<>();

    public PitchRollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init(){
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);

        crosshairColor = ContextCompat.getColor(this.getContext(), R.color.crosshair_color);
        for(int i=0; i<50; i++){
            pastValue_fusion.add(0f);
            pastValue_acc.add(0f);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if(displayMode == Constants.ChartDisplayMode.CROSSHAIR_CHART) drawCrosshairChart(canvas);
        else if(displayMode == Constants.ChartDisplayMode.LINE_CHART) drawLineChart(canvas);
        else if(displayMode == Constants.ChartDisplayMode.TARGET_ANGLE_CHART) drawTargetAngleChart(canvas);

        //invalidate();
    }

    private void drawCrosshairChart(Canvas _canvas){
        int width = getWidth();
        int height = getHeight();

        float centerX = width/2f;
        float centerY = height/2f;

        newX += (x-newX)/7;
        newY += (y-newY)/7;

        // 十字線
        paint.setColor(horizontalLineColor);
        _canvas.drawLine(0, centerY, width, centerY, paint); // X軸

        paint.setColor(verticalLineColor);
        _canvas.drawLine(centerX, 0, centerX, height, paint);  // Y軸

        // 點
        paint.setColor(Color.RED);
        _canvas.drawCircle(newX + centerX, newY + centerY, 10, paint);
    }

    private void drawLineChart(Canvas _canvas){
        int width = getWidth();
        int height = getHeight();

        float centerX = width/2f;
        float centerY = height/2f;

        // 水平線 (t)
        paint.setColor(crosshairColor);
        _canvas.drawLine(0, centerY, width, centerY, paint);

        // 水平線 (degree)
        paint.setColor(crosshairColor);
        _canvas.drawLine(0, 0, 0, height, paint);

        // 線
        Path line_acc = new Path();
        Path line_fusion = new Path();

        line_acc.moveTo(0, pastValue_acc.get(0) + centerY);
        line_fusion.moveTo(0, pastValue_fusion.get(0) + centerY);

        for(int i=1; (i < pastValue_acc.size() && i < pastValue_fusion.size()); i++){

            line_acc.lineTo(
                    (float) (i * width) /pastValue_acc.size(),
                    pastValue_acc.get(i) + centerY
            );

            line_fusion.lineTo(
                    (float) (i * width) /pastValue_fusion.size(),
                    pastValue_fusion.get(i) + centerY
            );
        }

        paint.setColor(Color.WHITE);
        _canvas.drawPath(line_acc ,paint);

        paint.setColor(Color.RED);
        _canvas.drawPath(line_fusion ,paint);
    }

    private void drawTargetAngleChart(Canvas _canvas){
        int width = getWidth();
        int height = getHeight();

        float centerX = width/2f;
        float centerY = height/2f;

        // 水平線 (t)
        paint.setColor(crosshairColor);
        _canvas.drawLine(0, centerY, width, centerY, paint);

        // 水平線 (degree)
        paint.setColor(crosshairColor);
        _canvas.drawLine(0, 0, 0, height, paint);

        // 線
        Path line_fusion = new Path();
        line_fusion.moveTo(0, pastValue_fusion.get(0) + centerY);

        for(int i=1; i < pastValue_fusion.size(); i++){
            line_fusion.lineTo(
                    (float) (i * width) /pastValue_fusion.size(),
                    pastValue_fusion.get(i) + centerY
            );
        }

        paint.setColor(Color.WHITE);
        _canvas.drawLine(0, centerY+targetAngle, width, centerY+targetAngle, paint);

        paint.setColor(Color.RED);
        _canvas.drawPath(line_fusion ,paint);
    }


    public void updatePoint(float _x, float _y) {

        float sensitivity = 14;
        x = -_x * sensitivity * (float) getWidth()/1000.0f;
        y =  _y * sensitivity * (float) getWidth()/1000.0f;

        // 限制 x,y 大小
        // 防止紅點超出屏幕
        float scopeLimit = 400 * (float) getWidth()/1000.0f;
        x = Math.min(x, scopeLimit);
        y = Math.min(y, scopeLimit);
        x = Math.max(x, -scopeLimit);
        y = Math.max(y, -scopeLimit);

        // 改變十字線顏色
        float oScope = 8;
        boolean inOScope = _x< oScope && _x>-oScope && _y< oScope && _y>-oScope;
        boolean inXScope = _x< oScope && _x>-oScope;
        boolean inYScope = _y< oScope && _y>-oScope;

        horizontalLineColor = inOScope ? Color.RED : inYScope ? Color.YELLOW : crosshairColor;
        verticalLineColor   = inOScope ? Color.RED : inXScope ? Color.YELLOW : crosshairColor;

        invalidate();
    }

    public void updateValue(float _accX, float _accY, float _fusionX, float _fusionY) {

        float accX = -_accX * getHeight() / 180;
        float accY =  _accY * getHeight() / 180;
        float fusionX = -_fusionX * getHeight() / 180;
        float fusionY =  _fusionY * getHeight() / 180;

        pastValue_acc.add(accY);
        pastValue_acc.remove(0);

        pastValue_fusion.add(fusionY);
        pastValue_fusion.remove(0);

        targetAngle = (int) accX;

        invalidate();
    }

    public void setMode(Constants.ChartDisplayMode mode) {
        displayMode = mode;
    }
}
