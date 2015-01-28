package studies.drawingapp;

import android.content.Context;
import com.android.volley.RequestQueue;

import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;

import com.android.volley.toolbox.Volley;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ImageComparison {
    private static final String TAG = "ImageComparison";
    private String modelSlug;
    private RequestQueue queue;
    private final Gson gson;

    public ImageComparison(Context context, String modelSlug) {
        this.modelSlug = modelSlug;
        queue = Volley.newRequestQueue(context);
        gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    public void run(ArrayList<int[]> pixels, final Listener<ImageComparisonResult> responseListener,
                                      final ErrorListener errorListener) {
        Map<String,String> params = new HashMap<String, String>();

        StringBuilder pointsBuilder = new StringBuilder();
        for( int [] pixel : pixels) {
            pointsBuilder.append(pixel[0]).append(' ').append(pixel[1]).append(' ');
        }

        params.put("image", modelSlug);
        params.put("points", pointsBuilder.toString());

        APIRequest request = new APIRequest("compare", params, new Listener<String>() {
            @Override
            public void onResponse(String json) {
                    responseListener.onResponse(gson.fromJson(json, ImageComparisonResult.class));
            }
        }, errorListener);

        queue.add(request);
    }


}
