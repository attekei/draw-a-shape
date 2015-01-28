package studies.drawingapp;

import android.app.AlertDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;


import java.io.File;
import java.io.FileOutputStream;

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
                showArchiveDialog();

            }
        });
        penEraserToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEraser();
            }
        });
    }

    private void showArchiveDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(DrawingCanvas.this);
        alert.setTitle("Archive drawing");
        alert.setMessage("Do you want to archive the drawing for later use?");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DrawingBitmap bitmap = getDrawingBitmap();
                String bitmapPath = bitmap.saveAsArchivedPNG("testuser");
                showResults(bitmapPath);
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DrawingBitmap bitmap = getDrawingBitmap();
                String bitmapPath = bitmap.saveAsTemporaryPNG();
                showResults(bitmapPath);
            }
        });

        alert.show();
    }

    private DrawingBitmap getDrawingBitmap() {
        View dc = findViewById(R.id.drawing_canvas);
        dc.buildDrawingCache();

        return DrawingBitmap.fromDrawingCache(dc.getDrawingCache(), dc.getWidth(), dc.getHeight())
                .removeTransparentMargins()
                .resizeImage(DrawingBitmap.PIXEL_COUNT_FOR_SAVE, false);
    }

    private void showResults(String drawingBitmapPath) {
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
