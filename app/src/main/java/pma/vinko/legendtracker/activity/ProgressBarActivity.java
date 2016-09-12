package pma.vinko.legendtracker.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import pma.vinko.legendtracker.R;
import pma.vinko.legendtracker.asynctasks.GetBitmap;
import pma.vinko.legendtracker.helpers.CacheHelper;

public class ProgressBarActivity extends AppCompatActivity {

    private static final int PROGRESS = 0x1;

    private ProgressBar mProgress;
    private int mProgressStatus = 0;
    private TextView text;

    private Handler mHandler = new Handler();

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_progress_bar);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        text = (TextView) findViewById(R.id.progressText);
        text.setText("Initalizing...");
        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                try {
                    while (mProgressStatus < 100) {
                        mProgressStatus = cache(mProgressStatus);

                        // Update the progress bar
                        mHandler.post(new Runnable() {
                            public void run() {
                                mProgress.setProgress(mProgressStatus);
                            }
                        });
                    }
                }  finally {
                    onContinue();
                }
            }
        }).start();
    }

    private void onContinue(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    private int cache(int progressStatus){
        switch (progressStatus){
            case 0:
                text.post(new Runnable() {
                    public void run() {
                        text.setText("Caching champion images...");
                    }
                });
                progressStatus += cache(CacheHelper.CacheType.CHAMPION,
                                        "http://ddragon.leagueoflegends.com/cdn/6.17.1/data/en_US/champion.json",
                                        "champion","key",progressStatus);

                break;
            case 25:
                text.post(new Runnable() {
                    public void run() {
                        text.setText("Caching item images...");
                    }
                });
                progressStatus += cache(CacheHelper.CacheType.ITEM,
                        "https://global.api.pvp.net/api/lol/static-data/eune/v1.2/item?itemListData=image&api_key=RGAPI-CBA5D9B2-7ED7-42FB-A7F0-D312B30965EA",
                        "item","id",progressStatus);

                break;
            case 50:
                text.post(new Runnable() {
                    public void run() {
                        text.setText("Caching rune images...");
                    }
                });
                progressStatus += cache(CacheHelper.CacheType.RUNE,
                        "https://global.api.pvp.net/api/lol/static-data/eune/v1.2/rune?runeListData=all&api_key=RGAPI-CBA5D9B2-7ED7-42FB-A7F0-D312B30965EA",
                        "rune","id",progressStatus);

                break;
            case 75:
                text.post(new Runnable() {
                    public void run() {
                        text.setText("Caching spell images...");
                    }
                });
                progressStatus += cache(CacheHelper.CacheType.SPELL,
                        "https://global.api.pvp.net/api/lol/static-data/eune/v1.2/summoner-spell?spellData=all&api_key=RGAPI-CBA5D9B2-7ED7-42FB-A7F0-D312B30965EA",
                        "spell","id",progressStatus);

                break;
            default:
                return 101;
        }

        return progressStatus;

        //return 101;
    }


    private String populateJson( String url) {
        InputStream inputStream = null;
        String result = "";

        try{
            final HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(new HttpGet(url));

            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null){
                result = readStream(inputStream);
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    private int cache(CacheHelper.CacheType type, String url, String urlType,String column,int pogressStatus){

        CacheHelper.populate(type, getImages(url, urlType, column));
        return pogressStatus += 25;
    }

    private HashMap<String, Bitmap> getImages(String url, String type, String column){

        HashMap<String,Bitmap> images = new HashMap<>();

        try {
            InputStream firstStream = new java.net.URL(url).openStream();
            JSONObject data = new JSONObject(readStream(firstStream)).getJSONObject("data");
            Iterator<String> keys = data.keys();

            while (keys.hasNext()) {
                //get champ details
                JSONObject champion = data.getJSONObject(keys.next());
                //add champ details
                if (champion.has("image")) {
                    images.put(champion.getString(column), new GetBitmap().execute("http://ddragon.leagueoflegends.com/cdn/6.17.1/img/"+type+"/" + champion.getJSONObject("image").getString("full")).get());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return images;
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
