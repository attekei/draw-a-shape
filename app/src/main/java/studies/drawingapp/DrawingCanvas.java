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

import studies.drawingapp.mainmenu.MainMenu;


public class DrawingCanvas extends Activity {
    Bundle extras;
    String newString;
    private static boolean erase = true;
    private static boolean backgroundBool = true;
    private static final String TAG = "DrawingCanvas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_canvas);
        final ImageView photo = (ImageView) findViewById(R.id.imageView);
        final ImageView photoMid = (ImageView) findViewById(R.id.imageView2);
        final Button button = (Button) findViewById(R.id.saveButton);
        final Button eraser = (Button) findViewById(R.id.penEraserToggler);
       // photo.setImageResource(R.drawable.applequater);
        extras = getIntent().getExtras();
        if(extras == null) {
            newString = null;
        }
        else {
            newString = extras.getString("photo");
        }
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


        final Button pen = (Button) findViewById(R.id.penEraserToggler);
        photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (backgroundBool) {
                    backgroundBool = !backgroundBool;
                    photoMid.setVisibility(View.VISIBLE);
                    // Find the root view

                    View root = findViewById(R.id.drawing_canvas);
                    root.setBackgroundColor(Color.BLACK);
                    eraser.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.INVISIBLE);
                    root.setVisibility(View.INVISIBLE);

                    DrawingCanvasView.setDraw(false);
                }
                else{
                    backgroundBool = !backgroundBool;
                    photoMid.setVisibility(View.INVISIBLE);
                    // Find the root view

                    View root = findViewById(R.id.drawing_canvas);
                    root.setBackgroundColor(Color.WHITE);
                    eraser.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
                    root.setVisibility(View.VISIBLE);
                    DrawingCanvasView.setDraw(true);
                }
            }
        });


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
    public void changePhoto (String name){
        View photo = findViewById(R.id.imageView);
    }
}

