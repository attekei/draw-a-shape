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

        if (!isEraser) canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (draw) {
            if (isEraser) {
                reactToEraseEvent(event.getAction(), event.getX(), event.getY());
            }
            else {
                reactToPenEvent(event.getAction(), event.getX(), event.getY());
            }

        }
        redrawScreen();

        return true;

    }

    private void reactToEraseEvent(int action, float x, float y) {
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            drawCanvas.drawLine(x, y, x + 1, y + 1, eraserPaint);
        }
    }

    private void reactToPenEvent(int action, float touchX, float touchY) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setPathStartLocation(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                continuePathToLocation(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawPathAndStartNewPath(touchX, touchY);
                break;
        }
    }

    private void redrawScreen() {
        invalidate();
    }

    private void setPathStartLocation(float x, float y) {
        drawPath.moveTo(x, y);
    }

    private void drawPathAndStartNewPath(float x, float y) {
        drawPath.lineTo(x, y);
        drawCanvas.drawPath(drawPath, drawPaint);
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
