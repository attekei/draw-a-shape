package studies.drawingapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

public class SettingsDialogFragment extends DialogFragment{

    private SharedPreferences preferences;
    private boolean archiveDrawings;
    private boolean collectEstimates;
    private EditText playerNameEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        archiveDrawings = preferences.getBoolean("archive_drawings", false);
        collectEstimates = preferences.getBoolean("collect_user_estimates", false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View settingsDialogView = inflater.inflate(R.layout.settings_dialog_view, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle("Game settings");
        builder.setView(settingsDialogView)
                // Add action buttons
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        updateSettings();
                    }
                });

        CheckBox archiveDrawingsCheckbox = (CheckBox) settingsDialogView.findViewById(R.id.archive_drawings_checkbox);
        archiveDrawingsCheckbox.setChecked(archiveDrawings);
        archiveDrawingsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                archiveDrawings = isChecked;
            }
        });

        CheckBox collectEstimatesCheckbox = (CheckBox) settingsDialogView.findViewById(R.id.collect_user_estimates_checkbox);
        collectEstimatesCheckbox.setChecked(collectEstimates);
        collectEstimatesCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                collectEstimates = isChecked;
            }
        });

        playerNameEditText = (EditText) settingsDialogView.findViewById(R.id.player_name_edittext);
        playerNameEditText.setText(preferences.getString("player_name", ""));

        return builder.create();
    }

    private void updateSettings() {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean("archive_drawings", archiveDrawings);
        editor.putBoolean("collect_user_estimates", collectEstimates);
        editor.putString("player_name", playerNameEditText.getText().toString());

        editor.apply();
    }
}
