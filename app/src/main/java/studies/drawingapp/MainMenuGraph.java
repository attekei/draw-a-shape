package studies.drawingapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by labso_000 on 18.11.2014.
 */
public class MainMenuGraph extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        final ImageView imgOne = (ImageView) findViewById(R.id.imageButton);
        final ImageView imgTwo = (ImageView) findViewById(R.id.imageButton2);
        final ImageView imgThree = (ImageView) findViewById(R.id.imageButton3);

        imgOne.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuGraph.this, DrawingCanvas.class);
                intent.putExtra("photo", "apple");
                startActivity(intent);
            }
        });
        imgTwo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuGraph.this, DrawingCanvas.class);
                intent.putExtra("photo", "bear");
                startActivity(intent);
            }
        });
        imgThree.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuGraph.this, DrawingCanvas.class);
                intent.putExtra("photo", "stool ");
                startActivity(intent);
            }
        });
    }
}
