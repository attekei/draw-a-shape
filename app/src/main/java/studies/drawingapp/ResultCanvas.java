package studies.drawingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;


public class ResultCanvas extends Activity {
    private static final String TAG = "Result";
    Bundle extras;
    String newString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_canvas);
        setup();
        final Button restart= (Button) findViewById(R.id.restart);
        restart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ResultCanvas.this);
                alert.setTitle("Uudelleen aloitus");
                alert.setMessage("Haluatko aloittaa alusta");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent  = new Intent(ResultCanvas.this, MainMenuGraph.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    }
    private void setup(){
        final ImageView photo = (ImageView) findViewById(R.id.imageView);
        final FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
        Resources r = getResources();
        Drawable[] layers = new Drawable[2];
        extras = getIntent().getExtras();
        if(extras == null) {
            newString = null;
        }
        else {
            newString = extras.getString("photo");
        }
        if (newString.equals( "stool")){
            layers[0] = r.getDrawable(R.drawable.stool);

        }
        else if  (newString.equals("apple")){
            layers[0] = r.getDrawable(R.drawable.apple);

        }
        else if ( (newString.equals("bear"))){
            layers[0] = r.getDrawable(R.drawable.bear);

        }
        if(getIntent().hasExtra("img")) {

            Bitmap b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("img"), 0, getIntent().getByteArrayExtra("img").length);

            Drawable d = new BitmapDrawable(getResources(),b);
            frame.setBackground(d);

        }

        photo.setImageDrawable(layers[0]);
    }
}
