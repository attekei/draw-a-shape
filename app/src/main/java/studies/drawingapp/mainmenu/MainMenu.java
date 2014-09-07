package studies.drawingapp.mainmenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

import studies.drawingapp.DrawingCanvas;
import studies.drawingapp.R;


public class MainMenu extends Activity {
    public MainMenuItem[] menuItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        final ListView listview = (ListView) findViewById(R.id.list);

        menuItems = constructMenuItems();

        final ArrayList<MainMenuItem> list = new ArrayList<MainMenuItem>();
        Collections.addAll(list, menuItems);

        final MainMenuArrayAdapter adapter = new MainMenuArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final MainMenuItem item = (MainMenuItem) parent.getItemAtPosition(position);
                item.action();
            }

        });

    }

    private MainMenuItem[] constructMenuItems() {
        MainMenuItem continueDrawingItem = new MainMenuItem("Continue current drawing session") {
            @Override
            public void action() {

            }
        };

        MainMenuItem startNewDrawingItem = new MainMenuItem("Start new drawing session") {
            @Override
            public void action() {
                promptName("Enter name", "Please enter the name of the person", new OnNameEnteredListener() {
                    @Override
                    public void onNameEntered(String name) {
                        Intent intent = new Intent(MainMenu.this, DrawingCanvas.class);
                        intent.putExtra("username", name);
                        startActivity(intent);
                    }
                });
            }
        };

        MainMenuItem openDirectoryItem = new MainMenuItem("Open drawing file directory") {
            @Override
            public void action() {

            }
        };

        MainMenuItem quitItem = new MainMenuItem("Quit") {
            @Override
            public void action() {

            }
        };

        return new MainMenuItem[]{
                continueDrawingItem,
                startNewDrawingItem,
                openDirectoryItem,
                quitItem
        };
    }

    private void promptName(String title, String message, final OnNameEnteredListener listener) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(title);
        alert.setMessage(message);

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                listener.onNameEntered(input.getText().toString());
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private interface OnNameEnteredListener {
        public void onNameEntered(String name);
    }
}
