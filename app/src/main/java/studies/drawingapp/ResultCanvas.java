package studies.drawingapp;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by labso_000 on 27.10.2014.
 */
public class ResultCanvas extends Activity {
    private static final String TAG = "Result";
    Bundle extras;
    String newString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_canvas);

        setup();
    }
    private void setup(){
        final ImageView photo = (ImageView) findViewById(R.id.imageView);
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
            layers[1] = r.getDrawable(R.drawable.stool);

        }
        else if  (newString.equals("apple")){
            layers[1] = r.getDrawable(R.drawable.apple);

        }
        else if ( (newString.equals("bear"))){
            layers[1] = r.getDrawable(R.drawable.bear);

        }
        if(getIntent().hasExtra("img")) {

            Bitmap b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("img"), 0, getIntent().getByteArrayExtra("img").length);

            Drawable d = new BitmapDrawable(getResources(),b);
            layers[0] = d;

        }


        LayerDrawable layerDrawable = new LayerDrawable(layers);

        photo.setImageDrawable(layerDrawable);
    }
}
