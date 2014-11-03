package studies.drawingapp;

import studies.algorithms.CMAESGuess;

/**
 * Created by atte on 3.11.14.
 */
abstract public class DrawingAnalyzer {
    abstract public double getDiffInPercents(int diffConstant);
    abstract public CMAESGuess getGuess();
}
