package studies.drawingapp;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import org.json.JSONException;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public class APIRequest extends Request<String> {

    private Listener<String> listener;
    private Map<String, String> params;

    public APIRequest(int method, String path, Map<String, String> params,
                      Listener<String> responseListener, ErrorListener errorListener) {
        super(method, "http://drawing-app-algorithm.herokuapp.com/" + path, errorListener);

        this.listener = responseListener;
        this.params = params;

        this.setRetryPolicy(new DefaultRetryPolicy(
                25000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return params;
    };

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));

            return Response.success(jsonString,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        // TODO Auto-generated method stub
        if (listener != null) {
            listener.onResponse(response);
        }

    }
}