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
        int resID = res.getIdentifier("model_image_" + imageSlug, "drawable", context.getPackageName());
        return resID != 0 ? res.getDrawable(resID) : null;
    }

    /**
     * Returns the quarter circle of image used in UI corners
     */
    public Drawable getQuarterCircleDrawable(String imageSlug) {
        Resources res = context.getResources();
        int resID = res.getIdentifier("model_image_quater_" + imageSlug, "drawable", context.getPackageName());
        return res.getDrawable(resID);
    }

}