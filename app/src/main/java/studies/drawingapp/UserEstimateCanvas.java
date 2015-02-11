package studies.drawingapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;


public class UserEstimateCanvas extends Activity {
    // TODO Refactor async logic using e.g. RXJava
    // (currently async logic is hard to reason due to use of mutable variables)

    private Bundle extras;
    private String modelSlug;
    private ImageButton proceed;
    private Drawable modelDrawable;
    private TextView ratingText;
    private Drawable drawingDrawable;
    private ImageComparisonResult comparisonResult;
    private ImageComparison comparison;
    private boolean comparisonDone = false;
    private boolean wantingToProceed = false;
    private ProgressDialog progressDialog = null;
    private RatingBar ratingBar;
    private ImageView drawingView;
    private DrawingBitmap drawingBitmap;
    private TextView loadingTextView;
    private boolean cancelling = false;
    private ImageButton cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_estimate_canvas);

        extras = getIntent().getExtras();
        modelSlug = extras.getString("model_slug");

        proceed = (ImageButton) findViewById(R.id.restart);
        cancel = (ImageButton) findViewById(R.id.cancel);
        modelDrawable = getModelDrawable();
        ratingText = (TextView) findViewById(R.id.ratingText);
        drawingView = (ImageView) findViewById(R.id.drawingView);
        loadingTextView = (TextView) findViewById(R.id.loadingTextView);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        drawingDrawable = Drawable.createFromPath(extras.getString("drawing_path"));

        runComparison();
        bindEvents();
        disableProceedButton();
    }

    private void disableProceedButton() {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        proceed.setColorFilter(filter);
    }

    private void runComparison() {
        drawingBitmap = DrawingBitmap.fromDrawable(drawingDrawable);

        comparison = new ImageComparison(this, modelSlug);

        comparison.run(drawingBitmap, new Response.Listener<ImageComparisonResult>() {
            @Override
            public void onResponse(ImageComparisonResult result) {
                comparisonResult = result;
                comparisonDone = true;

                showImagesByComparisonResults();
                if (wantingToProceed) proceedToResults();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                comparisonDone = true;

                if (wantingToProceed) proceedToResults();
            }
        });
    }

    private void showImagesByComparisonResults() {
        int modelMargin = 150;
        Bitmap modelBitmap = DrawingBitmap.fromDrawable(modelDrawable).getBitmap();

        int combinedWidth = modelBitmap.getWidth() + modelMargin;
        int combinedHeight = modelBitmap.getHeight() + modelMargin;
        double density = getResources().getDisplayMetrics().density;

        Matrix marginMatrix = new Matrix();
        marginMatrix.setTranslate(modelMargin / 2, modelMargin / 2);

        Matrix drawingMatrix = getDrawingTransformationMatrix();

        Bitmap combinedBitmap = Bitmap.createBitmap(combinedWidth, combinedHeight, Bitmap.Config.ARGB_8888);
        Canvas combinedCanvas = new Canvas(combinedBitmap);

        combinedCanvas.setMatrix(marginMatrix);
        combinedCanvas.drawBitmap(modelBitmap, 0, 0, null);

        drawingMatrix.postScale((float)density, (float)density, 0, 0);
        drawingMatrix.postConcat(marginMatrix);
        combinedCanvas.setMatrix(drawingMatrix);

        combinedCanvas.drawBitmap(drawingBitmap.getBitmap(), 0, 0, null);

        drawingView.setImageBitmap(combinedBitmap);

        loadingTextView.setVisibility(View.GONE);
    }

    private Matrix getDrawingTransformationMatrix() {
        double[] dMean = comparisonResult.drawingMean, mMean = comparisonResult.modelMean;
        double[] stdDevScale = comparisonResult.drawingStdDevScale;
        double[] transf = comparisonResult.cmaesTransformations;

        Matrix drawingMatrix = new Matrix();

        // Model and drawing mean to same coordinates
        drawingMatrix.postTranslate((float) (mMean[0] - dMean[0]), (float) (mMean[1] - dMean[1]));

        // Standard deviation scaling
        drawingMatrix.postScale((float) stdDevScale[0], (float) stdDevScale[1], (float) mMean[0], (float) mMean[1]);

        // Transformations by evolution algorithm
        drawingMatrix.postScale((float)transf[2], (float)transf[2], (float)mMean[0], (float)mMean[1]);
        drawingMatrix.postRotate((float)Math.toDegrees(transf[3]), (float)mMean[0], (float)mMean[1]);
        drawingMatrix.postTranslate((float)transf[0], (float)transf[1]);

        return drawingMatrix;
    }

    private Drawable getModelDrawable() {
        ModelImageProvider modelProvider = new ModelImageProvider(this);
        return modelProvider.getDrawable(modelSlug);
    }

    private void bindEvents() {
        proceed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                proceedToResults();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelling = true;
                proceedToResults();
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating < 0.5) {
                    rating = 0.5f;
                    ratingBar.setRating(rating);
                }

                String status = "";
                if (rating <= 1) {
                    status = "Not even close";
                }
                else if (rating <= 2) {
                    status = "Not totally hopeless";
                }
                else if (rating <= 3) {
                    status = "Not good or bad";
                }
                else if (rating <= 4) {
                    status = "Pretty similar as original";
                }
                else if (rating <= 5) {
                    status = "Really good match";
                }

                ratingText.setTypeface(null, Typeface.ITALIC);
                ratingText.setText(status);

                proceed.setColorFilter(null);
            }
        });
    }

    private void proceedToResults() {
        if (!cancelling && ratingBar.getRating() == 0f) return;

        if (comparisonDone) {
            if (progressDialog != null) progressDialog.dismiss();
            // Currently only fire and forget the new estimate

            if (!cancelling) {
                comparison.addUserEstimate(ratingBar.getRating() / 5.0, comparisonResult.squareError, null, null);
            }

            /**
            Intent showResultsIntent = new Intent(UserEstimateCanvas.this, ResultCanvas.class);

            showResultsIntent.putExtra("drawing_path", extras.getString("drawing_path"));
            showResultsIntent.putExtra("model_slug", modelSlug);
            showResultsIntent.putExtra("comparison_result", comparisonResult);

            startActivity(showResultsIntent);
             */

            Intent intent = new Intent(getApplicationContext(), MainMenuGraph.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Loading...");
            progressDialog.show();
            wantingToProceed = true;
        }
    }


}
