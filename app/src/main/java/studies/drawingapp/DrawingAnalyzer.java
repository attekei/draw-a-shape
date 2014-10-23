package studies.drawingapp;

import android.graphics.Bitmap;
import android.graphics.Color;

import studies.algorithms.CMAESGuess;
import studies.algorithms.PointCloud;

public class DrawingAnalyzer {
    final int emptyColor = Color.WHITE;
    final int pointColor = Color.BLACK;
    final int samples = 100;

    private CMAESGuess guess;
    private PointCloud modelCloud;
    private PointCloud resultCloud;

    public DrawingAnalyzer(Bitmap drawing, Bitmap model) {
        modelCloud = this.getCenteredCloud(model);
        PointCloud alignedDrawingCloud =
                this.getCenteredCloud(drawing).alignByStandardDeviation(modelCloud);

        guess = alignedDrawingCloud.downsample(samples).runCMAES(
                modelCloud.downsample(samples)
        );

        resultCloud = alignedDrawingCloud.transformByCMAESGuess(guess);
    }

    private PointCloud getCenteredCloud(Bitmap bitmap) {
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        PointCloud cloud = PointCloud.fromImagePixelArray(pixels, bitmap.getWidth(), pointColor);
        PointCloud centeredCloud = cloud.centerByMean();

        return centeredCloud;
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
