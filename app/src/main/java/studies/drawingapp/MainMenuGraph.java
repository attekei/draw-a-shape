package studies.drawingapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainMenuGraph extends Activity {

    private ImageView imgOne;
    private ImageView imgTwo;
    private ImageView imgThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        imgOne = (ImageView) findViewById(R.id.imageButton);
        imgTwo = (ImageView) findViewById(R.id.imageButton2);
        imgThree = (ImageView) findViewById(R.id.imageButton3);

        bindEvents();
    }

    private void bindEvents() {
        imgOne.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuGraph.this, DrawingCanvas.class);
                intent.putExtra("model_slug", "apple");
                startActivity(intent);
            }
        });
        imgTwo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuGraph.this, DrawingCanvas.class);
                intent.putExtra("model_slug", "bear");
                startActivity(intent);
            }
        });
        imgThree.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuGraph.this, DrawingCanvas.class);
                intent.putExtra("model_slug", "stool");
                startActivity(intent);
            }
        });
    }
}
