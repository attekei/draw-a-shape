package studies.drawingapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class DrawingCanvas extends Activity {
    Bundle extras;
    String modelImageSlug;

    private static boolean eraserIsActive = false;
    private static boolean showModelPreview = false;

    private static final String TAG = "DrawingCanvas";

    private ImageView photo;
    private ImageView photoMid;
    private ImageView proceedButton;
    private ImageButton penEraserToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drawing_canvas);
        photo = (ImageView) findViewById(R.id.imageView);
        photoMid = (ImageView) findViewById(R.id.imageView2);
        proceedButton = (ImageView) findViewById(R.id.saveButton);
        penEraserToggle = (ImageButton) findViewById(R.id.penEraserToggle);
        extras = getIntent().getExtras();
        modelImageSlug = extras.getString("model_slug");
        setModelImage(modelImageSlug);

        bindEvents();
    }

    private void bindEvents() {
        photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toggleModelPreview();
            }
        });
        proceedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                archiveAndProceed();

            }
        });
        penEraserToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEraser();
            }
        });
    }

    private void archiveAndProceed() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean archiveDrawings = preferences.getBoolean("archive_drawings", false);

        DrawingBitmap bitmap = getDrawingBitmap();

        String bitmapPath;
        if (archiveDrawings) {
            String playerFullName = preferences.getString("player_name", "no_name_defined");
            String playerUserName = playerFullName.replaceAll(" ", "_").toLowerCase();
            bitmapPath = bitmap.saveAsArchivedPNG(playerUserName);
        } else {
            bitmapPath = bitmap.saveAsTemporaryPNG();
        }

        // TODO better bitmap memory management
        bitmap.getBitmap().recycle();

        boolean collectEstimates = preferences.getBoolean("collect_user_estimates", false);
        if (collectEstimates) {
            proceedToCollectEstimate(bitmapPath);
        }
        else {
            proceedToResults(bitmapPath);
        }
    }

    private void proceedToCollectEstimate(String bitmapPath) {
        Intent collectEstimatesIntent = new Intent(DrawingCanvas.this, UserEstimateCanvas.class);

        collectEstimatesIntent.putExtra("drawing_path", bitmapPath);
        collectEstimatesIntent.putExtra("model_slug", modelImageSlug);

        startActivity(collectEstimatesIntent);
    }

    private DrawingBitmap getDrawingBitmap() {
        View dc = findViewById(R.id.drawing_canvas);
        dc.buildDrawingCache();

        return DrawingBitmap.fromDrawingCache(dc.getDrawingCache(), dc.getWidth(), dc.getHeight())
                .removeTransparentMargins()
                .resizeImage(DrawingBitmap.PIXEL_COUNT_FOR_SAVE, false);
    }

    private void proceedToResults(String drawingBitmapPath) {
        Intent showResultsIntent = new Intent(DrawingCanvas.this, ResultCanvas.class);

        showResultsIntent.putExtra("drawing_path", drawingBitmapPath);
        showResultsIntent.putExtra("model_slug", modelImageSlug);

        startActivity(showResultsIntent);
    }

    private void toggleModelPreview() {
        showModelPreview = !showModelPreview;
        if (showModelPreview) {
            showModelPreview();
        }
        else{
            hideModelPreview();
        }
    }

    private void hideModelPreview() {
        photoMid.setVisibility(View.INVISIBLE);
        View root = findViewById(R.id.drawing_canvas);
        penEraserToggle.setVisibility(View.VISIBLE);
        proceedButton.setVisibility(View.VISIBLE);
        root.setVisibility(View.VISIBLE);
        DrawingCanvasView.setDraw(true);
        setModelImage(modelImageSlug);
    }

    private void showModelPreview() {
        photoMid.setVisibility(View.VISIBLE);
        View root = findViewById(R.id.drawing_canvas);
        penEraserToggle.setVisibility(View.INVISIBLE);
        proceedButton.setVisibility(View.INVISIBLE);
        root.setVisibility(View.INVISIBLE);
        photo.setImageResource(R.drawable.backquater);
        DrawingCanvasView.setDraw(false);
    }

    public void toggleEraser() {
        eraserIsActive = !eraserIsActive;
        DrawingCanvasView.setEraser(eraserIsActive);
        penEraserToggle.setSelected(eraserIsActive);
    }

    private void setModelImage(String modelSlug){
        final ImageView photo = (ImageView) findViewById(R.id.imageView);
        final ImageView photoMid = (ImageView) findViewById(R.id.imageView2);

        ModelImageProvider modelProvider = new ModelImageProvider(this);
        photo.setImageDrawable(modelProvider.getQuarterCircleDrawable(modelSlug));
        photoMid.setImageDrawable(modelProvider.getDrawable(modelSlug));
    }
}
