package io.scryp.scryp;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by Matt on 8/7/2017.
 */

public class HttpRequestHandler {
    public static final String TAG = "Scryp";
    HttpRequestHandler(Context c) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(c);
//        String url = "http://api.blockcypher.com/v1/beth/test";
        String url = "http://api.blockcypher.com/v1/beth/test/";

        // Request a JSON response from the provided URL.
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v(TAG, response.toString());
                new EthereumService().handleSuccessfulJsonResponse(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new EthereumService().handleBadJsonResponse(error.getMessage());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);
    }
}
