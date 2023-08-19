package com.example.tareagooglemarcador;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;

public class HttpUtil {

    private final RequestQueue requestQueue;

    public HttpUtil(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void sendJsonObjectRequest(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
        requestQueue.add(request);
    }
}
