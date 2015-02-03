package studies.drawingapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DrawingBitmap {
    private Bitmap bitmap = null;

    private static final String TAG = "DrawingBitmap";

    /**
     * Recommended pixel count for saving the drawing.
     */
    public static final int PIXEL_COUNT_FOR_SAVE  = 1000 * 1000;

    /**
     * Recommended pixel count for image comparision
     * (for resizeImage before getBlackPixelListString)
     */
    public static final int PIXEL_COUNT_FOR_COMP  = 10 * 1000;

    DrawingBitmap(Bitmap originalBitmap) {
        bitmap = originalBitmap;
    }

    public static DrawingBitmap fromDrawingCache(Bitmap drawingCache, Integer width, Integer height) {
        Bitmap fixedBitmap = Bitmap.createBitmap(drawingCache, 0, 0, width, height);
        return new DrawingBitmap(fixedBitmap);
    }

    public static DrawingBitmap fromDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return new DrawingBitmap(((BitmapDrawable)drawable).getBitmap());
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return new DrawingBitmap(bitmap);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * Removes transparent margins from image.
     * @return New cropped image or null if the whole image is transparent.
     */
    public DrawingBitmap removeTransparentMargins() {
        int width = bitmap.getWidth(), height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int minX = getMinNonTransparentX(pixels, width, height);
        if (minX == -1) return null; // No any non-transparent pixel found

        int maxX = getMaxNonTransparentX(pixels, width, height);
        int minY = getMinNonTransparentY(pixels, width, height);
        int maxY = getMaxNonTransparentY(pixels, width, height);

        return new DrawingBitmap(Bitmap.createBitmap(bitmap, minX, minY, maxX - minX, maxY - minY));
    }

    private int getMinNonTransparentX(int[] pixels, int width, int height) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (pixels[ y * width + x] != 0) return x;
            }
        }
        return -1;
    }

    private int getMaxNonTransparentX(int[] pixels, int width, int height) {
        for (int x = width - 1; x >= 0; x--) {
            for (int y = 0; y < height; y++) {
                if (pixels[ y * width + x] != 0) return x;
            }
        }
        return -1;
    }

    private int getMinNonTransparentY(int[] pixels, int width, int height) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (pixels[ y * width + x] != 0) return y;
            }
        }
        return -1;
    }

    private int getMaxNonTransparentY(int[] pixels, int width, int height) {
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                if (pixels[ y * width + x] != 0) return y;
            }
        }
        return -1;
    }

    /**
     * Creates a permanent PNG in format "username-timestamp.png".
     * @return Path to the created file or null if creation failed
     */
    public String saveAsArchivedPNG(String username) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        return saveAsPNG(username + "-" + sdf.format(new Date()));
    }

    /**
     * Creates a temporary PNG to "tmp.png" file. Old file gets overwritten.
     * @return Path to the created file or null if creation failed
     */
    public String saveAsTemporaryPNG() {
        return saveAsPNG("tmp");
    }


    /**
     * Creates a PNG file with given filename to "/drawing-app/" directory.
     * @param filename Name of the file (without PNG extension)
     * @return Path to the created file or null if creation failed
     */
    public String saveAsPNG(String filename) {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/drawing-app/" + filename + ".png";

        File file = new File(filePath);
        File path = new File(file.getParent());

        try {
            if (file.getParent() != null && !path.isDirectory()) {
                boolean directoryCreated =  path.mkdirs();
                if (directoryCreated) Log.i(TAG, "Image directory created.");
            }

            FileOutputStream fos = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();

            Log.i(TAG, "Saving the image was successful." + filePath);

            return filePath;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public DrawingBitmap resizeImage(int maxPixelCount, boolean enableUpscaling) {
        double scale = getResizeScale(maxPixelCount, enableUpscaling);
        return new DrawingBitmap(
                Bitmap.createScaledBitmap(bitmap, (int)(scale * bitmap.getWidth()), (int)(scale * bitmap.getHeight()), true)
        );
    }

    public double getResizeScale(double maxPixelCount, boolean enableUpscaling) {
        double nonLimitedScale = Math.sqrt(maxPixelCount / (bitmap.getWidth() * bitmap.getHeight()));
        return enableUpscaling ? nonLimitedScale : (nonLimitedScale < 1 ? nonLimitedScale : 1);
    }

    public ArrayList<int[]> getBlackPixelPositions(double scaleMultiplier) {
        int width = bitmap.getWidth(), height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        ArrayList<int[]> blackPixels = new ArrayList<int[]>();

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        String pixelString = "";
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (pixels[ y * width + x] != 0) {
                    blackPixels.add(new int[]{(int)Math.round(scaleMultiplier * x), (int)Math.round(scaleMultiplier * y)});
                }
            }
        }

        return blackPixels;
    }
}
