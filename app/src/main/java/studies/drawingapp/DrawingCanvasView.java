package studies.drawingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class DrawingCanvasView extends View {
    private static boolean isEraser = false;
    private Path drawPath;
    private static final String TAG = "Piirto";
    private Paint canvasPaint;
    private Paint eraserPaint;
    private int drawStrokeWidth = 10;
    private int eraserStrokeWidth = 80;
    private static int paintColor = Color.BLACK;
    private static int backgroundColor = 0xFFFFFFFF;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private static Paint drawPaint;
    private static boolean draw = true;
    private Paint eraserPreviewPaint;

    public DrawingCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();

    }

    protected void setupDrawing() {
        this.setDrawingCacheEnabled(true);

        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(drawStrokeWidth);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        eraserPaint = new Paint();
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        eraserPaint.setColor(Color.TRANSPARENT);
        eraserPaint.setStrokeWidth(eraserStrokeWidth);
        eraserPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setStrokeJoin(Paint.Join.ROUND);
        eraserPaint.setStrokeCap(Paint.Cap.ROUND);

        eraserPreviewPaint = new Paint();
        eraserPreviewPaint.setColor(Color.rgb(241, 241, 241));
        eraserPreviewPaint.setStrokeWidth(eraserStrokeWidth);
        eraserPreviewPaint.setStyle(Paint.Style.STROKE);
        eraserPreviewPaint.setStrokeJoin(Paint.Join.ROUND);
        eraserPreviewPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, isEraser ? eraserPreviewPaint : drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (draw) {
            if (isEraser) {
                reactToEvent(event.getAction(), event.getX(), event.getY(), eraserPaint);
            }
            else {
                reactToEvent(event.getAction(), event.getX(), event.getY(), drawPaint);
            }

        }
        redrawScreen();

        return true;
    }

    private void reactToEvent(int action, float touchX, float touchY, Paint paint) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setPathStartLocation(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                continuePathToLocation(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawPathAndStartNewPath(touchX, touchY, paint);
                break;
        }
    }

    private void redrawScreen() {
        invalidate();
    }

    private void setPathStartLocation(float x, float y) {
        drawPath.moveTo(x, y);
    }

    private void drawPathAndStartNewPath(float x, float y, Paint paint) {
        drawPath.lineTo(x, y);
        drawCanvas.drawPath(drawPath, paint);
        drawPath.reset();
    }

    private void continuePathToLocation(float x, float y) {
        drawPath.lineTo(x, y);
    }

    public static void setEraser(boolean _isEraser) {
        isEraser = _isEraser;
    }

    public static void setDraw(boolean isDraw) {
        draw = isDraw;
    }

}
