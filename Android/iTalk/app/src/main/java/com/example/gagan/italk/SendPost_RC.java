package com.example.gagan.italk;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by gagan on 21/5/15.
 */
public class SendPost_RC extends JsonObjectRequest{
    Map<String,String> _params;
    private boolean isOutputJSON=false;

    public SendPost_RC(int method, String url, HashMap<String,String> params, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);
        if(params!=null)
            _params=params;
        else
        _params=new HashMap<String,String>();



    }
    public SendPost_RC( String url, HashMap<String,String> params, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, null, listener, errorListener);
        if(params!=null)
            _params=params;
        else
            _params=new HashMap<String,String>();
    }
    public SendPost_RC(boolean OutputJson, String url, HashMap<String,String> params, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, null, listener, errorListener);
        if(params!=null)
            _params=params;
        else
            _params=new HashMap<String,String>();
        isOutputJSON=OutputJson;
    }
    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

        if(isOutputJSON)
        {
            return super.parseNetworkResponse(response);
        }
            HashMap hm=new HashMap<String,Object>();
            hm.put("sc",response.statusCode);
            hm.put("cookie",getCookieStringFromHeader(response.headers));
            return Response.success(new JSONObject(hm),
                    HttpHeaderParser.parseCacheHeaders(response));


    }

    @Override
    protected Map<String,String> getParams()
    {
        return _params;
    }



    public static final String SET_COOKIE="Set-Cookie";
    public static final String COOKIE="Cookie";

    private String getCookieStringFromHeader(Map<String,String> header)
    {
        if(header.containsKey(SET_COOKIE))
        {
            return header.get(SET_COOKIE);
        }
        return "";

    }


}
