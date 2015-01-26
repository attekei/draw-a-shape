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
import android.widget.Button;
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
    private Button button;
    private Button penEraserToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drawing_canvas);
        photo = (ImageView) findViewById(R.id.imageView);
        photoMid = (ImageView) findViewById(R.id.imageView2);
        button = (Button) findViewById(R.id.saveButton);
        penEraserToggle = (Button) findViewById(R.id.penEraserToggle);

        extras = getIntent().getExtras();
        modelImageSlug = extras.getString("photo");
        setModelImage(modelImageSlug);

        bindEvents();
    }

    private void bindEvents() {
        photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toggleModelPreview();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showSaveDialog();

            }
        });
        penEraserToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEraser();
            }
        });
    }

    private void showSaveDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(DrawingCanvas.this);
        alert.setTitle("Archive drawing");
        alert.setMessage("Do you want to archive the drawing? The drawing is archived as an PNG image.");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Bitmap bitmap = getDrawingBitmap();
                saveBitmap(bitmap);
                showResults(bitmap);
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Bitmap bitmap = getDrawingBitmap();
                showResults(bitmap);
            }
        });

        alert.show();
    }

    private Bitmap getDrawingBitmap() {
        View dc = findViewById(R.id.drawing_canvas);
        dc.buildDrawingCache();

        return Bitmap.createBitmap(
                dc.getDrawingCache(), 0, 0, dc.getWidth(), dc.getHeight()
        );
    }

    private void showResults(Bitmap drawingBitmap) {
        Intent showResultsIntent = new Intent(DrawingCanvas.this, ResultCanvas.class);
        // TODO downscale image
        //showResultsIntent.putExtra("drawing_bitmap", drawingBitmap);
        showResultsIntent.putExtra("drawing_slug", modelImageSlug);

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
        root.setBackgroundColor(Color.WHITE);
        penEraserToggle.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
        root.setVisibility(View.VISIBLE);
        DrawingCanvasView.setDraw(true);
        setModelImage(modelImageSlug);
    }

    private void showModelPreview() {
        photoMid.setVisibility(View.VISIBLE);
        View root = findViewById(R.id.drawing_canvas);
        root.setBackgroundColor(Color.BLACK);
        penEraserToggle.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);
        root.setVisibility(View.INVISIBLE);
        photo.setImageResource(R.drawable.backquater);
        DrawingCanvasView.setDraw(false);
    }

    public void toggleEraser() {
        eraserIsActive = !eraserIsActive;
        DrawingCanvasView.setEraser(eraserIsActive);
        penEraserToggle.setSelected(eraserIsActive);
    }

    public  void  saveBitmap (Bitmap savePic)  {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/drawing-app/" + "testikuva.png";

        File file = new File(filePath);
        File path = new File(file.getParent());

        if (savePic != null) {
            try {
                // build directory
                if (file.getParent() != null && !path.isDirectory()) {
                    boolean directoryCreated =  path.mkdirs();
                    if (directoryCreated) Log.i(TAG, "Image directory created.");
                }

                FileOutputStream fos = new FileOutputStream(filePath);
                savePic.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.close();
                Log.i(TAG, "Saving the image was successful." + filePath);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Log.v(TAG, "savePicture image parsing error");
        }

    }

    private void setModelImage(String newString){
        final ImageView photo = (ImageView) findViewById(R.id.imageView);
        final ImageView photoMid = (ImageView) findViewById(R.id.imageView2);
        if (newString.equals( "stool")){
            photo.setImageResource(R.drawable.stoolquater);
            photoMid.setImageResource(R.drawable.stool);
        }
        else if  (newString.equals("apple")){
            photo.setImageResource(R.drawable.applequater);
            photoMid.setImageResource(R.drawable.apple);
        }
        if ( (newString.equals("bear"))){
            photo.setImageResource(R.drawable.bearquater);
            photoMid.setImageResource(R.drawable.bear);
        }
    }
}
