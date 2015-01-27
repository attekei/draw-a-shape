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
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;


public class ResultCanvas extends Activity {
    private static final String TAG = "Result";
    Bundle extras;
    String modelSlug;
    private Button restart;
    private Drawable modelDrawable;
    private Drawable drawingDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_canvas);

        restart = (Button) findViewById(R.id.restart);
        modelDrawable = getModelDrawable();
        drawingDrawable = Drawable.createFromPath(extras.getString("drawing_path"));

        showModelAndDrawing();
        bindEvents();
        runComparision();
    }

    private void runComparision() {
        DrawingBitmap drawingBitmap = DrawingBitmap.fromDrawable(drawingDrawable);
        DrawingBitmap downscaledBitmap = drawingBitmap.resizeImage(DrawingBitmap.PIXEL_COUNT_FOR_COMP, false);
        String pixels = downscaledBitmap.getBlackPixelListString();
    }

    private void bindEvents() {
        restart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                requestRestartWithDialog();
            }
        });
    }

    private void requestRestartWithDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Uudelleenaloitus");
        alert.setMessage("Haluatko aloittaa alusta?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showMainMenu();
            }
        });

        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    private void showMainMenu() {
        Intent intent = new Intent(this, MainMenuGraph.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showModelAndDrawing(){
        final ImageView drawingView = (ImageView) findViewById(R.id.drawingView);
        final FrameLayout frame = (FrameLayout) findViewById(R.id.frame);

        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{drawingDrawable, modelDrawable});
        drawingView.setImageDrawable(layerDrawable);
    }

    private Drawable getModelDrawable() {
        extras = getIntent().getExtras();
        modelSlug = extras.getString("model_slug");

        ModelImageProvider modelProvider = new ModelImageProvider(this);
        return modelProvider.getDrawable(modelSlug);
    }
}
