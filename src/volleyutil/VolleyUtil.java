package volleyutil;

import java.lang.reflect.Method;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class VolleyUtil{

    private static VolleyUtil volleyUtil;
    private RequestQueue requestQueue;
    private  Context context;
    private JSONObject rqResponse;
    
    private VolleyUtil(Context context){
        this.context = context;
        requestQueue = Volley.newRequestQueue(this.context);
    }
    
    public static void init(Context context){
        volleyUtil = new VolleyUtil(context); 
    }
    
    public static VolleyUtil get(){
        return volleyUtil;
    }
    
    public void createRequest(String url, final RequestListener requestListener){
     JsonObjectRequest rq = new JsonObjectRequest(Method.PUBLIC, url, null, new Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            rqResponse = response;
            requestListener.onRequestDone();
        }
    }, new ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("lem", "error");
        }
    });
     
     requestQueue.add(rq);
    }

    public JSONObject getResponse() {
        return rqResponse;
    }
}
