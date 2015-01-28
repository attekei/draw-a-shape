package studies.drawingapp;

import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MotionEventEffects {

    private final PorterDuff.Mode filterMode = PorterDuff.Mode.SRC_ATOP;

    public void bindImageViewMotionEffects(final ImageView view) {
        view.setColorFilter(Color.argb(80, 255,255,255), filterMode);
        view.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_HOVER_ENTER){
                    view.setColorFilter(Color.argb(50, 0, 0, 0), filterMode);
                }
                if(event.getAction() == MotionEvent.ACTION_HOVER_EXIT){
                    view.setColorFilter(Color.argb(80, 255,255,255), filterMode);
                }
                if(event.getAction() == MotionEvent.ACTION_HOVER_MOVE){
                    view.setColorFilter(Color.argb(50, 0, 0, 0), filterMode);
                }
                return false;
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            private Rect rect;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    view.setColorFilter(Color.argb(255, 0, 0, 0), filterMode);
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    view.setColorFilter(Color.argb(80, 255,255,255), filterMode);
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())){
                        view.setColorFilter(Color.argb(80, 255,255,255), filterMode);
                    }
                }
                return false;
            }
        });
    }
}
