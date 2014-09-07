package studies.drawingapp.mainmenu;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;

/**
* Created by atte on 7.9.2014.
*/
class MainMenuArrayAdapter extends ArrayAdapter<MainMenuItem> {

    HashMap<MainMenuItem, Integer> mIdMap = new HashMap<MainMenuItem, Integer>();

    public MainMenuArrayAdapter(Context context, int textViewResourceId, List<MainMenuItem> objects) {
        super(context, textViewResourceId, objects);
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public long getItemId(int position) {
        MainMenuItem item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
