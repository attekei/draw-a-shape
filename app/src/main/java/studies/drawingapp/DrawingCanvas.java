package studies.drawingapp;


import android.app.AlertDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import java.io.File;
import java.io.FileOutputStream;

import studies.drawingapp.mainmenu.MainMenu;


public class DrawingCanvas extends Activity {
    private static boolean erase = true;
    private static final String TAG = "DrawingCanvas";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drawing_canvas);
        final Button pen = (Button) findViewById(R.id.penEraserToggler);

        final Button button = (Button) findViewById(R.id.saveButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(DrawingCanvas.this);
                alert.setTitle("Varmistus");
                alert.setMessage("Haluatko varmasti tallentaa");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        View drawingCanvas = findViewById(R.id.drawing_canvas);
                        drawingCanvas.buildDrawingCache();

                        Bitmap bitmap = Bitmap.createBitmap(
                                drawingCanvas.getDrawingCache(),
                                0,
                                0,
                                drawingCanvas.getWidth(),
                                drawingCanvas.getHeight()
                        );

                        analyzeImage(bitmap, bitmap);
                        saveBitmap(bitmap);
                        Intent intent = new Intent(DrawingCanvas.this, MainMenu.class);
                        startActivity(intent);

                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
                alert.show();

            }
        });
        final Button eraser = (Button) findViewById(R.id.penEraserToggler);
        eraser.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.v(TAG, "OnClick");
                DrawingCanvasView.setEraser(erase);
                eraser.setSelected(erase);
                erase = !erase;
            }

        });
    }

    public double analyzeImage(Bitmap drawing, Bitmap model) {
        //DrawingAnalyzer analyzer = new CMAESDrawingAnalyzer(drawing, model);
        DrawingAnalyzer analyzer = new DummyDrawingAnalyzer();

        Double diff = analyzer.getDiffInPercents(100);
        Log.i(TAG, "diff: " + diff.toString() + " guess: " + analyzer.getGuess().toString());
        return diff;
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
                // output image to file
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
}

