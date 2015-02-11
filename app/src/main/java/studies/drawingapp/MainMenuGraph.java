package studies.drawingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;

public class MainMenuGraph extends Activity {

    private ImageView imgOne;
    private ImageView imgTwo;
    private ImageView imgThree;
    private ImageButton settingsButton;
    private DialogFragment settingsDialogFragment;
    private ImageView imgFour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        imgOne = (ImageView) findViewById(R.id.imageButton);
        imgTwo = (ImageView) findViewById(R.id.imageButton2);
        imgThree = (ImageView) findViewById(R.id.imageButton3);
        imgFour = (ImageView) findViewById(R.id.imageButton4);
        settingsButton = (ImageButton) findViewById(R.id.settingsButton);

        bindEvents();
        initSettingsDialog();
    }

    private void initSettingsDialog() {
        settingsDialogFragment = new SettingsDialogFragment();
    }

    private void bindEvents() {
        imgOne.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startNewDrawing("apple");
            }
        });
        imgTwo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startNewDrawing("circle");
            }
        });
        imgThree.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startNewDrawing("stool");
            }
        });
        imgFour.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startNewDrawing("bear");
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                settingsDialogFragment.show(getFragmentManager(), "settings");
            }
        });

        MotionEventEffects effects = new MotionEventEffects();
        effects.bindImageViewMotionEffects(imgOne);
        effects.bindImageViewMotionEffects(imgTwo);
        effects.bindImageViewMotionEffects(imgThree);
    }

    private void startNewDrawing(String modelSlug) {
        Intent intent = new Intent(this, DrawingCanvas.class);
        intent.putExtra("model_slug", modelSlug);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}
