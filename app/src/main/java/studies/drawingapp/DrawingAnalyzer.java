package studies.drawingapp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import studies.algorithms.CMAESGuess;
import studies.algorithms.PointCloud;

public class DrawingAnalyzer {
    private static final String TAG = "DrawingAnalyzer";

    final int emptyColor = Color.WHITE;
    final int pointColor = Color.BLACK;
    final int samples = 100;

    private CMAESGuess guess;
    private PointCloud modelCloud;
    private PointCloud resultCloud;

    public DrawingAnalyzer(Bitmap drawing, Bitmap model) {

        modelCloud = this.getCenteredCloud(model);

        Log.i(TAG, "Model cloud aligned");
        PointCloud alignedDrawingCloud =
                this.getCenteredCloud(drawing).alignByStandardDeviation(modelCloud);

        Log.i(TAG, "Drawing cloud aligned");

        guess = alignedDrawingCloud.downsample(samples).runCMAES(
                modelCloud.downsample(samples)
        );

        Log.i(TAG, "Clouds downsampled");

        resultCloud = alignedDrawingCloud.transformByCMAESGuess(guess);

        Log.i(TAG, "Transformation completed");
    }

    private PointCloud getCenteredCloud(Bitmap bitmap) {
        Bitmap scaledBitmap = scaleToFill(bitmap, 1000, 1000);

        int[] pixels = new int[scaledBitmap.getWidth() * scaledBitmap.getHeight()];
        scaledBitmap.getPixels(pixels, 0, scaledBitmap.getWidth(), 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());

        Log.i(TAG, "Getting centered cloud: " + scaledBitmap.getWidth());

        PointCloud cloud = PointCloud.fromImagePixelArray(pixels, scaledBitmap.getWidth(), pointColor);
        Log.i(TAG, "We've got a cloud! It's width is " + cloud.width());
        PointCloud centeredCloud = cloud.centerByMean();

        return centeredCloud;
    }

    private Bitmap scaleToFill(Bitmap b, int width, int height) {
        float factorH = height / (float) b.getWidth();
        float factorW = width / (float) b.getWidth();
        float factorToUse = (factorH > factorW) ? factorW : factorH;
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factorToUse), (int) (b.getHeight() * factorToUse), false);
    }


    public CMAESGuess getGuess() { return guess; }

    public Bitmap getModelResultBitmap() {
        return Bitmap.createBitmap(
                modelCloud.toImagePixelArray(pointColor, emptyColor),
                (int) modelCloud.width(),
                (int) modelCloud.height(),
                Bitmap.Config.ARGB_8888
        );
    }

    public Bitmap getDrawingResultBitmap() {
        return Bitmap.createBitmap(
                resultCloud.toImagePixelArray(pointColor, emptyColor),
                (int) resultCloud.width(),
                (int) resultCloud.height(),
                Bitmap.Config.ARGB_8888
        );
    }

    public double getDiffInPercents(int diffConstant) {
        return resultCloud.diffInPercentsTo(modelCloud, diffConstant);
    }
}
