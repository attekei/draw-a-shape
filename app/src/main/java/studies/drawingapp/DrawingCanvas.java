package studies.drawingapp;


import android.app.AlertDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;


public class DrawingCanvas extends Activity {
   // RequestQueue queue = Volley.newRequestQueue(this);  // this = context
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
        extras = getIntent().getExtras();
        if(extras == null) {
            newString = null;
        }
        else {
            newString = extras.getString("photo");
            changeImage(newString);
        }



        final Button pen = (Button) findViewById(R.id.penEraserToggler);
        photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (backgroundBool) {
                    backgroundBool = !backgroundBool;
                    photoMid.setVisibility(View.VISIBLE);
                    // Find the root view
                    // Pimennys
                    View root = findViewById(R.id.drawing_canvas);
                    root.setBackgroundColor(Color.BLACK);
                    eraser.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.INVISIBLE);
                    root.setVisibility(View.INVISIBLE);
                    photo.setImageResource(R.drawable.backquater);
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
                    changeImage(newString);
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

                       // analyzeImage(bitmap, bitmap);
                       // saveBitmap(bitmap);
                        //
                        //String pixels = changeBitmap(bitmap);
                       // post(pixels, newString);
                        Intent intent = new Intent(DrawingCanvas.this, ResultCanvas.class);
                        ByteArrayOutputStream bs = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                        intent.putExtra("img", bs.toByteArray());
                        intent.putExtra("photo", newString);
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
      //  Log.i(TAG, "diff: " + diff.toString() + " guess: " + analyzer.getGuess().toString());
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
    //TODO: Liian raskas, pitää kehittää parempi tapa!

    private static String changeBitmap(Bitmap myBitmap){
        String pointString = "";
        int width = myBitmap.getWidth();
        for (int x = 0; x < myBitmap.getWidth(); x++) {
            for (int y = 0; y < myBitmap.getHeight(); y++) {

                if (myBitmap.getPixel(x, y) == Color.BLACK) {

                    pointString += " " + x + " " + y;
                }
            }
        }
        return pointString;
    }
    // Miten tämä implementoidaan???
    private void post (final String img, final String name){

        String url = "https://drawing-app-algorithm.herokuapp.com/compare";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put(img, name);

                return params;
            }
        };
      //  queue.add(postRequest);
    }
    private void changeImage(String newString){
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
