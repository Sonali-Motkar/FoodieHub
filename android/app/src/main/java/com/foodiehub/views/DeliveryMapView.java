package com.foodiehub.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import androidx.annotation.Nullable;

/**
 * DeliveryMapView renders a smooth, high-performance real-time animated route map.
 * Simulates a delivery partner (e.g. Swiggy/Zomato) moving along city roads
 * with radar pulse waves, street grids, parks, and waypoint pins.
 */
public class DeliveryMapView extends View {

    private Paint roadPaint;
    private Paint streetGridPaint;
    private Paint routePaint;
    private Paint remainingRoutePaint;
    private Paint pulsePaint;
    private Paint pinPaint;
    private Paint textPaint;
    private Paint cardBgPaint;

    private Path routePath;
    private PathMeasure pathMeasure;
    private float routeLength;

    private float deliveryProgress = 0.35f; // 0.0 = Restaurant, 1.0 = Delivered
    private float pulseRadius = 15f;
    private int pulseAlpha = 220;
    private ValueAnimator pulseAnimator;

    public DeliveryMapView(Context context) {
        super(context);
        init();
    }

    public DeliveryMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DeliveryMapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Base street grid
        streetGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        streetGridPaint.setColor(Color.parseColor("#E2E8F0"));
        streetGridPaint.setStrokeWidth(12f);
        streetGridPaint.setStyle(Paint.Style.STROKE);

        // Main highway roads
        roadPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        roadPaint.setColor(Color.WHITE);
        roadPaint.setStrokeWidth(28f);
        roadPaint.setStyle(Paint.Style.STROKE);
        roadPaint.setStrokeCap(Paint.Cap.ROUND);

        // Traveled delivery route (Solid Orange)
        routePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        routePaint.setColor(Color.parseColor("#FF5722"));
        routePaint.setStrokeWidth(10f);
        routePaint.setStyle(Paint.Style.STROKE);
        routePaint.setStrokeCap(Paint.Cap.ROUND);

        // Remaining delivery route (Dashed Orange)
        remainingRoutePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        remainingRoutePaint.setColor(Color.parseColor("#FFAB91"));
        remainingRoutePaint.setStrokeWidth(8f);
        remainingRoutePaint.setStyle(Paint.Style.STROKE);
        remainingRoutePaint.setPathEffect(new DashPathEffect(new float[]{16, 12}, 0));

        // Radar pulse ring
        pulsePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pulsePaint.setColor(Color.parseColor("#FF5722"));
        pulsePaint.setStyle(Paint.Style.STROKE);
        pulsePaint.setStrokeWidth(4f);

        // Pins & badges
        pinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pinPaint.setStyle(Paint.Style.FILL);

        // Text & Emojis
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);

        cardBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cardBgPaint.setColor(Color.WHITE);
        cardBgPaint.setStyle(Paint.Style.FILL);
        cardBgPaint.setShadowLayer(8f, 0, 4f, Color.parseColor("#40000000"));

        setLayerType(LAYER_TYPE_SOFTWARE, null);

        // Infinite radar pulse animation
        pulseAnimator = ValueAnimator.ofFloat(15f, 55f);
        pulseAnimator.setDuration(1600);
        pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimator.setInterpolator(new LinearInterpolator());
        pulseAnimator.addUpdateListener(animation -> {
            pulseRadius = (float) animation.getAnimatedValue();
            float fraction = animation.getAnimatedFraction();
            pulseAlpha = (int) (200 * (1f - fraction));
            invalidate();
        });
        pulseAnimator.start();
    }

    public void setProgress(float progress) {
        this.deliveryProgress = Math.max(0.0f, Math.min(1.0f, progress));
        invalidate();
    }

    public float getProgress() {
        return deliveryProgress;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        buildRoute(w, h);
    }

    private void buildRoute(int w, int h) {
        routePath = new Path();
        // Point A: Restaurant (top left quadrant)
        float startX = w * 0.18f;
        float startY = h * 0.22f;

        // Waypoint 1: Main road junction
        float midX1 = w * 0.45f;
        float midY1 = h * 0.28f;

        // Waypoint 2: City bridge curve
        float midX2 = w * 0.40f;
        float midY2 = h * 0.70f;

        // Point B: Customer Home (bottom right quadrant)
        float endX = w * 0.82f;
        float endY = h * 0.78f;

        routePath.moveTo(startX, startY);
        routePath.cubicTo(midX1, midY1, midX2, midY2, endX, endY);

        pathMeasure = new PathMeasure(routePath, false);
        routeLength = pathMeasure.getLength();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();

        // 1. Map Canvas Background (Light Map Slate)
        canvas.drawColor(Color.parseColor("#F1F5F9"));

        // 2. City Park Green Zone
        Paint parkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        parkPaint.setColor(Color.parseColor("#DCFCE7"));
        RectF parkRect = new RectF(w * 0.52f, h * 0.12f, w * 0.90f, h * 0.48f);
        canvas.drawRoundRect(parkRect, 24f, 24f, parkPaint);

        // Park Label
        Paint parkText = new Paint(Paint.ANTI_ALIAS_FLAG);
        parkText.setColor(Color.parseColor("#15803D"));
        parkText.setTextSize(26f);
        parkText.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("🌳 Green Park", parkRect.centerX(), parkRect.centerY(), parkText);

        // 3. City Street Grid Network
        canvas.drawLine(0, h * 0.35f, w, h * 0.35f, streetGridPaint);
        canvas.drawLine(0, h * 0.65f, w, h * 0.65f, streetGridPaint);
        canvas.drawLine(w * 0.30f, 0, w * 0.30f, h, streetGridPaint);
        canvas.drawLine(w * 0.70f, 0, w * 0.70f, h, streetGridPaint);

        // 4. Main Highway Underlay
        if (routePath != null) {
            canvas.drawPath(routePath, roadPaint);
        }

        // 5. Remaining Route (Dashed)
        if (routePath != null) {
            canvas.drawPath(routePath, remainingRoutePaint);
        }

        // 6. Traveled Route (Solid Orange)
        if (routePath != null && pathMeasure != null) {
            Path traveledPath = new Path();
            pathMeasure.getSegment(0, routeLength * deliveryProgress, traveledPath, true);
            canvas.drawPath(traveledPath, routePaint);
        }

        // 7. Calculate Live Position of Delivery Agent
        float[] currentPos = new float[2];
        float[] tangent = new float[2];
        if (pathMeasure != null) {
            pathMeasure.getPosTan(routeLength * deliveryProgress, currentPos, tangent);
        } else {
            currentPos[0] = w * 0.5f;
            currentPos[1] = h * 0.5f;
        }

        // 8. Start Pin: FoodieHub Kitchen
        float startX = w * 0.18f;
        float startY = h * 0.22f;
        drawLocationPin(canvas, startX, startY, "#16A34A", "🍔", "FoodieHub Kitchen");

        // 9. Destination Pin: Customer Home
        float endX = w * 0.82f;
        float endY = h * 0.78f;
        drawLocationPin(canvas, endX, endY, "#2563EB", "🏠", "Flat 402, Home");

        // 10. Live Delivery Partner Marker with Radar Pulse
        pulsePaint.setAlpha(pulseAlpha);
        canvas.drawCircle(currentPos[0], currentPos[1], pulseRadius, pulsePaint);
        canvas.drawCircle(currentPos[0], currentPos[1], pulseRadius * 0.65f, pulsePaint);

        // Scooter Icon Badge
        pinPaint.setColor(Color.WHITE);
        canvas.drawCircle(currentPos[0], currentPos[1], 24f, pinPaint);
        pinPaint.setColor(Color.parseColor("#FF5722"));
        pinPaint.setStyle(Paint.Style.STROKE);
        pinPaint.setStrokeWidth(3f);
        canvas.drawCircle(currentPos[0], currentPos[1], 24f, pinPaint);
        pinPaint.setStyle(Paint.Style.FILL);

        // Scooter Emoji
        textPaint.setTextSize(30f);
        canvas.drawText("🛵", currentPos[0], currentPos[1] + 10f, textPaint);

        // Partner Callout Pill
        drawCallout(canvas, currentPos[0], currentPos[1] - 34f, "Rahul Sharma (On the way)");
    }

    private void drawLocationPin(Canvas canvas, float x, float y, String colorHex, String emoji, String label) {
        // Pin shadow & white base
        pinPaint.setColor(Color.WHITE);
        canvas.drawCircle(x, y, 22f, pinPaint);

        // Pin border
        pinPaint.setColor(Color.parseColor(colorHex));
        pinPaint.setStyle(Paint.Style.STROKE);
        pinPaint.setStrokeWidth(3f);
        canvas.drawCircle(x, y, 22f, pinPaint);
        pinPaint.setStyle(Paint.Style.FILL);

        // Emoji
        textPaint.setTextSize(26f);
        canvas.drawText(emoji, x, y + 9f, textPaint);

        // Badge pill
        drawCallout(canvas, x, y + 36f, label);
    }

    private void drawCallout(Canvas canvas, float x, float y, String text) {
        Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setTextSize(22f);
        labelPaint.setColor(Color.parseColor("#1E293B"));
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setFakeBoldText(true);

        float textWidth = labelPaint.measureText(text);
        RectF bgRect = new RectF(x - textWidth / 2 - 12f, y - 18f, x + textWidth / 2 + 12f, y + 8f);

        canvas.drawRoundRect(bgRect, 8f, 8f, cardBgPaint);
        canvas.drawText(text, x, y, labelPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (pulseAnimator != null) {
            pulseAnimator.cancel();
        }
    }
}
