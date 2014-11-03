package studies.drawingapp;

import java.util.Random;

import studies.algorithms.CMAESGuess;

/**
 * Created by atte on 3.11.14.
 */
public class DummyDrawingAnalyzer extends DrawingAnalyzer {
    public double getDiffInPercents(int diffConstant) {
        return new Random().nextDouble() * 100;
    }

    public CMAESGuess getGuess() {
        return CMAESGuess.fromDoubleArray(new double[]{1,1,1,1,1});
    }
}
