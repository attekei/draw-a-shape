package studies.drawingapp;

public class ImageComparisonResult {
    public final double squareError;
    public final double systemEstimate;
    public final double usedDiffConstant;
    public final double[] drawingMean;
    public final double[] modelMean;
    public final double[] drawingStdDevScale;
    public final double[] cmaesTransformations;

    public ImageComparisonResult(double squareError, double systemEstimate, double usedDiffConstant,
                                 double[] drawingMean, double[] modelMean, double[] drawingStdDevScale,
                                 double[] cmaesTransformations) {
        this.squareError = squareError;
        this.systemEstimate = systemEstimate;
        this.usedDiffConstant = usedDiffConstant;
        this.drawingMean = drawingMean;
        this.modelMean = modelMean;
        this.drawingStdDevScale = drawingStdDevScale;
        this.cmaesTransformations = cmaesTransformations;
    }
}
