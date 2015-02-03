package studies.drawingapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;


public class UserEstimateCanvas extends Activity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_estimate_canvas);

        extras = getIntent().getExtras();
        modelSlug = extras.getString("model_slug");

        proceed = (ImageButton) findViewById(R.id.restart);
        modelDrawable = getModelDrawable();
        ratingText = (TextView) findViewById(R.id.ratingText);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        drawingDrawable = Drawable.createFromPath(extras.getString("drawing_path"));

        showModelAndDrawing();

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
        DrawingBitmap drawingBitmap = DrawingBitmap.fromDrawable(drawingDrawable);
        comparison = new ImageComparison(this, modelSlug);

        comparison.run(drawingBitmap, new Response.Listener<ImageComparisonResult>() {
            @Override
            public void onResponse(ImageComparisonResult result) {
                comparisonResult = result;
                comparisonDone = true;

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

    private void showModelAndDrawing(){
        final ImageView drawingView = (ImageView) findViewById(R.id.drawingView);
        final FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
        modelDrawable.setAlpha(80);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{modelDrawable, drawingDrawable});
        drawingView.setImageDrawable(layerDrawable);
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
        if (ratingBar.getRating() == 0f) return;

        if (comparisonDone) {
            if (progressDialog != null) progressDialog.dismiss();
            // Currently only fire and forget the new estimate
            comparison.addUserEstimate(ratingBar.getRating() / 5.0, comparisonResult.squareError, null, null);

            Intent showResultsIntent = new Intent(UserEstimateCanvas.this, ResultCanvas.class);

            showResultsIntent.putExtra("drawing_path", extras.getString("drawing_path"));
            showResultsIntent.putExtra("model_slug", modelSlug);
            showResultsIntent.putExtra("comparison_result", comparisonResult);

            startActivity(showResultsIntent);
        }
        else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Loading...");
            progressDialog.show();
            wantingToProceed = true;
        }
    }


}
