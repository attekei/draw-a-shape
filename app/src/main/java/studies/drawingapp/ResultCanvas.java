package studies.drawingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.text.MessageFormat;


public class ResultCanvas extends Activity {
    private static final String TAG = "Result";
    Bundle extras;
    String modelSlug;
    private ImageButton restart;
    private Drawable modelDrawable;
    private Drawable drawingDrawable;
    private ImageComparisonResult comparisonResult = null;
    private ImageComparison comparison;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_canvas);

        extras = getIntent().getExtras();
        modelSlug = extras.getString("model_slug");

        restart = (ImageButton) findViewById(R.id.restart);
        modelDrawable = getModelDrawable();
        statusText = (TextView) findViewById(R.id.statusText);

        drawingDrawable = Drawable.createFromPath(extras.getString("drawing_path"));

        showModelAndDrawing();
        bindEvents();
        runComparison();
    }

    private void runComparison() {
        ImageComparisonResult alreadyCalculatedResult = (ImageComparisonResult) extras.get("comparison_result");
        if (alreadyCalculatedResult != null) {
            comparisonResult = alreadyCalculatedResult;
            showResults();
        }
        else {
            DrawingBitmap drawingBitmap = DrawingBitmap.fromDrawable(drawingDrawable);
            comparison = new ImageComparison(this, modelSlug);

            comparison.run(drawingBitmap, new Response.Listener<ImageComparisonResult>() {
                @Override
                public void onResponse(ImageComparisonResult result) {
                    comparisonResult = result;
                    showResults();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    statusText.setText("Calculation failed, probably API problem :(");
                }
            });
        }
    }

    private void showErrorDialog(String message, Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(message);
        alert.setNeutralButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alert.show();
    }

    private void askEstimateFromUser() {
        statusText.setText("             Waiting user input...");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your estimate of the match in percents");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Double fraction = Double.parseDouble(input.getText().toString()) / 100;

                // Currently only fire and forget the new estimate
                comparison.addUserEstimate(fraction, comparisonResult.squareError, null, null);

                showResults();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                showResults();
            }
        });

        AlertDialog dialog = builder.create();
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        wmlp.x = 50;   //x position
        wmlp.y = 50;   //y position

        dialog.show();
    }

    private void showResults() {
        String percents = MessageFormat.format("{0,number,#.###}", comparisonResult.systemEstimate * 100);
        statusText.setText("Match: " + percents + " %");
    }

    private void bindEvents() {
        restart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                requestRestartWithDialog();
            }
        });
    }

    private void requestRestartWithDialog() {
        showMainMenu();
    }

    private void showMainMenu() {
        Intent intent = new Intent(this, MainMenuGraph.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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
}
