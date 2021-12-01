package edu.illinois.cs465.jukebox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class CustomSliderBar extends com.google.android.material.slider.Slider {
    private ArrayList<SliderProgressItem> mProgressItemsList;

    public CustomSliderBar(Context context) {
        super(context);
    }

    public CustomSliderBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSliderBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initData(ArrayList<SliderProgressItem> progressItemsList) {
        this.mProgressItemsList = progressItemsList;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        if (mProgressItemsList.size() > 0) {
            int progressBarWidth = getTrackWidth();
            int progressBarHeight = getTrackHeight() * 2;
            int lastProgressX = getTrackSidePadding();
            int progressItemWidth, progressItemRight;
            int center_vertical = getHeight() / 2;
            for (int i = 0; i < mProgressItemsList.size(); i++) {
                SliderProgressItem progressItem = mProgressItemsList.get(i);
                Paint progressPaint = new Paint();
                progressPaint.setColor(getResources().getColor(progressItem.color));

                progressItemWidth = (int) (progressItem.progressItemPercentage * progressBarWidth / 100);

                progressItemRight = lastProgressX + progressItemWidth;

                if (i == mProgressItemsList.size() - 1 && progressItemRight != progressBarWidth + getTrackSidePadding()) {
                    progressItemRight = progressBarWidth + getTrackSidePadding();
                }
                RectF progressRect = new RectF();
                progressRect.set(lastProgressX, center_vertical - (progressBarHeight/2), progressItemRight, center_vertical + (progressBarHeight/2));
                // canvas.drawRoundRect(progressRect, 50, 50, progressPaint);

                float radius = 40;
                float[] corners;
                if (i == 0 && i == mProgressItemsList.size() - 1) {
                    corners = new float[]{
                            radius, radius,        // Top left radius in px
                            radius, radius,        // Top right radius in px
                            radius, radius,        // Bottom right radius in px
                            radius, radius,        // Bottom left radius in px
                    };
                } else if (i == 0) {
                    corners = new float[]{
                            radius, radius,        // Top left radius in px
                            0, 0,        // Top right radius in px
                            0, 0,        // Bottom right radius in px
                            radius, radius,        // Bottom left radius in px
                    };
                } else if (i == mProgressItemsList.size() - 1) {
                    corners = new float[]{
                            0, 0,        // Top left radius in px
                            radius, radius,        // Top right radius in px
                            radius, radius,        // Bottom right radius in px
                            0, 0,        // Bottom left radius in px
                    };
                } else {
                    corners = new float[]{
                            0, 0,        // Top left radius in px
                            0, 0,        // Top right radius in px
                            0, 0,        // Bottom right radius in px
                            0, 0         // Bottom left radius in px
                    };
                }

                final Path path = new Path();
                path.addRoundRect(progressRect, corners, Path.Direction.CW);
                canvas.drawPath(path, progressPaint);

                lastProgressX = progressItemRight;
            }
            super.onDraw(canvas);
        }

    }
}
