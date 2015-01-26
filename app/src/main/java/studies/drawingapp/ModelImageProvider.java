package studies.drawingapp;

import java.util.Properties;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class ModelImageProvider {
    private Context context;
    private Properties properties;

    public ModelImageProvider(Context context) {
        this.context = context;
    }

    public String[] getImageSlugs() {
        return new String[]{ "stool", "bear", "apple" };
    }

    public Drawable getDrawable(String imageSlug) {
        Resources res = context.getResources();
        int resID = res.getIdentifier("model-image-" + imageSlug, "drawable", context.getPackageName());
        return res.getDrawable(resID);
    }

    /**
     * Returns the quater circleimage used in UI corners
     */
    public Drawable getQuaterCircleDrawable(String imageSlug) {
        Resources res = context.getResources();
        int resID = res.getIdentifier("model-image-quater-" + imageSlug, "drawable", context.getPackageName());
        return res.getDrawable(resID);
    }

}