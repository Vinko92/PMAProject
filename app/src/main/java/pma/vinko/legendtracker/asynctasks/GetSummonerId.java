package pma.vinko.legendtracker.asynctasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import pma.vinko.legendtracker.helpers.StreamHelper;

/**
 * Created by Vinko on 4.9.2016..
 */
public class GetSummonerId extends AsyncTask<String, Void,String> {

    private final HttpClient client = new DefaultHttpClient();
    String data, content;

    protected String doInBackground(String... params) {
        InputStream inputStream = null;
        String result = "";

        try {
            HttpResponse httpResponse = client.execute(new HttpGet(params[0]));
            inputStream = httpResponse.getEntity().getContent();

            if (inputStream != null) {
                content = StreamHelper.readStream(inputStream);
            } else {
                content = "Oops, something went wrong";
            }

            JSONObject json = new JSONObject(content);
            data = json.getJSONObject(json.keys().next()).getString("id");

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }


}

