package studies.drawingapp;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
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

import static com.android.volley.Request.Method;

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

    public void run(DrawingBitmap bitmap, final Listener<ImageComparisonResult> resultListener,
                    final ErrorListener errorListener) {

        DrawingBitmap downscaledBitmap = bitmap.resizeImage(DrawingBitmap.PIXEL_COUNT_FOR_COMP, false);
        double scaleMultiplier = 1 / bitmap.getResizeScale(DrawingBitmap.PIXEL_COUNT_FOR_COMP, true);
        ArrayList<int[]> pixels = downscaledBitmap.getBlackPixelPositions(scaleMultiplier);
        run(pixels, resultListener, errorListener);
    }

    public void run(ArrayList<int[]> pixels, final Listener<ImageComparisonResult> resultListener,
                                      final ErrorListener errorListener) {
        Map<String,String> params = new HashMap<String, String>();

        StringBuilder pointsBuilder = new StringBuilder();
        for( int [] pixel : pixels) {
            pointsBuilder.append(pixel[0]).append(' ').append(pixel[1]).append(' ');
        }

        params.put("image", modelSlug);
        params.put("points", pointsBuilder.toString());

        APIRequest request = new APIRequest(Method.POST, "compare", params, new Listener<String>() {
            @Override
            public void onResponse(String json) {
                    resultListener.onResponse(gson.fromJson(json, ImageComparisonResult.class));
            }
        }, errorListener);

        queue.add(request);
    }


    public void addUserEstimate(Double fraction, Double squareError, final Listener<String> successListener,
                                final ErrorListener errorListener) {
        Map<String,String> params = new HashMap<String, String>();
        params.put("image", modelSlug);
        params.put("user_estimate", fraction.toString());
        params.put("square_error", squareError.toString());

        APIRequest request = new APIRequest(Method.POST, "user-estimates/add", params, successListener, errorListener);

        queue.add(request);
    }
}
