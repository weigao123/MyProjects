package com.lesports.bike.settings.http;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lesports.bike.settings.application.SettingApplication;
import com.lesports.bike.settings.utils.UrlBuilder;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by gaowei3 on 2016/7/4.
 */
public class VolleyHelper {
    protected RequestQueue mQueue;
    protected UrlBuilder urlBuilder;
    private final static String TAG = VolleyHelper.class.getSimpleName();
    public VolleyHelper() {
        mQueue = Volley.newRequestQueue(SettingApplication.getContext());
        urlBuilder = UrlBuilder.newBuilder(SettingApplication.getBaseUrl());
    }
    public VolleyHelper setParameters(Map<String, String> parameters) {
        urlBuilder.addQueryParameters(parameters);
        return this;
    }
    public VolleyHelper setPath(String path) {
        urlBuilder.appendPath(path);
        return this;
    }

    public void startStringRequest(final RequestResult requestResult) {
        if (attachToken()) {
            urlBuilder.addQueryParameter("access_token", SettingApplication.getAccesToken());
        }
        String url = urlBuilder.build();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject> (){
            @Override
            public void onResponse(JSONObject jsonObject) {
                requestResult.onSuccess(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                requestResult.onError();
            }
        });
        request.setTag(TAG);
        request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
        mQueue.start();
    }

    protected boolean attachToken() {
        return true;
    }

    public void cancel() {
        mQueue.cancelAll(TAG);
    }

    public interface RequestResult {
        void onSuccess(JSONObject jsonObject);
        void onError();
    }
}
