package pma.vinko.legendtracker.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import pma.vinko.legendtracker.helpers.StreamHelper;

/**
 * Created by Vinko on 4.9.2016..
 */
public class GetData extends AsyncTask<String, Void, String> {

    final HttpClient client = new DefaultHttpClient();
    private Context context;
    ProgressDialog progressDialog;
    String content;

    public GetData(){

    }

    public GetData(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
       /* progressDialog = new ProgressDialog(context);
        progressDialog.show();*/
    }


    @Override
    protected String doInBackground(String... params) {
        InputStream inputStream = null;
        String result = "";

        try{
            HttpResponse httpResponse = client.execute(new HttpGet(params[0]));
            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null){
                content = StreamHelper.readStream(inputStream);
            }
            else{
                content = "Oops, something went wrong";
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    @Override
    protected void onPostExecute(String result) {

       // progressDialog.dismiss();
    }

    @NonNull
    private String readStream(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line = "";
        StringBuilder result = new StringBuilder();

        while((line = reader.readLine()) != null){
            result.append(line);
        }

        stream.close();
        return  result.toString();
    }

}
