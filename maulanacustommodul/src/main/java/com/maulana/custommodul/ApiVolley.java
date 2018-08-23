package com.maulana.custommodul;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shin on 2/24/2017.
 */

public class ApiVolley {

    public static RequestQueue requestQueue;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    String token0 = "", token1 = "", token2 = "", token3 = "",token4 = "";

    public ApiVolley(final Context context, JSONObject jsonBody, String requestMethod, String REST_URL, final VolleyCallback callback){

        /*
        NOTE: you have to customize this class before you use it (haeder, etc)
        context : Application context
        jsonBody : jsonBody (usually be used for POST and PUT)
        requestMethod : GET, POST, PUT, DELETE
        REST_URL : Rest API URL
        successDialog : custom Dialog when success call API
        failDialog : custom Dialog when failed call API
        showDialogFlag : 1 = show successDialog / failDialog with filter
        callback : return of the response
        */

        session = new SessionManager(context);
        token0  = iv.encodeMD5(iv.encodeBase64(iv.getCurrentDate("SSSHHMMddmmyyyyss")));
        token1  = session.getKdcus();
        token2  = iv.getCurrentDate("SSSHHyyyyssMMddmm");
        token3  = iv.sha256(token1+"&"+token2,token1+"die");
        token4  = iv.sha256(token0 + token1 + "&"+token2,token1+"live");
        //token3  = iv.encodeMD5(token3);
        //token3  = iv.encodeBase64(token3);

        final String requestBody = jsonBody.toString();

        int method = 0;

        switch(requestMethod.toUpperCase()){

            case "GET" :
                method = Request.Method.GET;
                break;
            case "POST" :
                method = Request.Method.POST;
                break;
            case "PUT" :
                method = Request.Method.PUT;
                break;
            case "DELETE" :
                method = Request.Method.DELETE;
                break;
            default: method = Request.Method.GET;
                break;
        }

        //region initial of stringRequest
        StringRequest stringRequest = new StringRequest(method, REST_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                // Important Note : need to use try catch when parsing JSONObject, no need when parsing string

                if(response == null || response.equals("null")) {

                    Toast.makeText(context, context.getResources().getString(R.string.api_unauthorized), Toast.LENGTH_LONG).show();
                    callback.onError(context.getResources().getString(R.string.api_unauthorized));
                }

                try {

                    callback.onSuccess(response);

                } catch (Exception e) {

                    e.printStackTrace();
//                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    Toast.makeText(context, context.getResources().getString(R.string.api_error), Toast.LENGTH_LONG).show();
                    callback.onError(e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override

            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());
                callback.onError(error.toString());
                return;
            }
        }) {

            // Request Header
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Client-Service", "frontend-client");
                params.put("Auth-Key", "gmedia_psp_reseller");
                params.put("token0", token0);
                params.put("token1", token1);
                params.put("token2", token2);
                params.put("token3", token3);
                params.put("nomor", session.getUsername());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    return null;
                }
            }
        };
        //endregion

        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }

        /*// retry when timeout
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30*60*1000, *//*DefaultRetryPolicy.DEFAULT_MAX_RETRIES*//*0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));*/

        // retry when timeout
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20*1000, -1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);
        requestQueue.getCache().clear();

    }

    // interface for call callback from response API
    public interface VolleyCallback{
        void onSuccess(String result);
        void onError(String result);
    }
}
