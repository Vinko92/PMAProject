package pma.vinko.legendtracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ChampionDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champion_detalis);

        String championId = getChampionId();
        String url = "http://ddragon.leagueoflegends.com/cdn/6.17.1/data/en_US/champion/"+ championId +".json"; //"http://ddragon.leagueoflegends.com/cdn/6.17.1/data/en_US/champion/" + championId  +" .json";


        showStaticViews(R.id.txtSkill,R.id.txtAttack,R.id.txtDefense,R.id.txtDifficulty, R.id.txtLore, R.id.txtMagic,R.id.txtStats);
        new DownloadImageTask((ImageView) findViewById(R.id.championSplashArt))
                .execute("http://ddragon.leagueoflegends.com/cdn/img/champion/splash/"+ championId +"_0.jpg");


        new ChampionOperation().execute(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) < 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {

            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }


    public void onBackPressed() {


        Intent setIntent = new Intent(ChampionDetails.this,StaticDataActivity.class);
        startActivity(setIntent);
        finish();
        return;

    }

    private String getChampionId(){
        String championId = "";
        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            championId = bundle.get("championId").toString();
        }

        return championId;
    }

    private void showStaticViews(Integer... ids){

        View view;

        for(int id: ids){
            view = findViewById(id);
            view.setVisibility(View.VISIBLE);
        }
    }

    private class ChampionOperation extends AsyncTask<String, Void, Void> {

        final HttpClient client = new DefaultHttpClient();
        ProgressDialog progressDialog = new ProgressDialog(ChampionDetails.this);
        String data, content;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            progressDialog.setTitle("Please wait...");
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(String... params) {
            InputStream inputStream = null;
            String result = "";

            try{
                HttpResponse httpResponse = client.execute(new HttpGet(params[0]));

                inputStream = httpResponse.getEntity().getContent();

                if(inputStream != null){
                    content = readStream(inputStream);
                }
                else{
                    content = "Oops, something went wrong";
                }

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

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
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            try {

                JSONObject json = new JSONObject(content);
                populateText(json);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        private void populateText(JSONObject json) throws JSONException {

            json = json.getJSONObject("data");
            JSONObject champDetails = json.getJSONObject(json.keys().next());


            TextView championName = (TextView) findViewById(R.id.championName);
            TextView championLore = (TextView) findViewById(R.id.championLore);
            TextView championTitle = (TextView) findViewById(R.id.championTitle);

            championName.setText(champDetails.getString("name"));
            championLore.setText(Html.fromHtml(champDetails.getString("lore")));
            championTitle.setText(champDetails.getString("title"));

            setStats(champDetails);
            setSpells(champDetails);

        }

        private void setStats(JSONObject champDetails) throws JSONException {
            JSONObject stats = champDetails.getJSONObject("info");

            changeWidth((TextView) findViewById(R.id.attack),Integer.parseInt(stats.getString("attack")));
            changeWidth((TextView) findViewById(R.id.defense),Integer.parseInt(stats.getString("defense")));
            changeWidth((TextView) findViewById(R.id.difficulty),Integer.parseInt(stats.getString("difficulty")));
            changeWidth((TextView) findViewById(R.id.magic),Integer.parseInt(stats.getString("magic")));
        }

        private void setSpells(JSONObject championDetails) throws JSONException {

            JSONArray spells = championDetails.getJSONArray("spells");

            for(int i = 0; i < spells.length(); i++){
                String spellImage = spells.getJSONObject(i).getJSONObject("image").getString("full");

                switch(i){
                    case 0:
                        new DownloadImageTask((ImageView) findViewById(R.id.spellQ))
                                .execute("http://ddragon.leagueoflegends.com/cdn/6.17.1/img/spell/" + spellImage);
                        break;
                    case 1:
                        new DownloadImageTask((ImageView) findViewById(R.id.spellW))
                                .execute("http://ddragon.leagueoflegends.com/cdn/6.17.1/img/spell/" + spellImage);
                        break;
                    case 2:
                        new DownloadImageTask((ImageView) findViewById(R.id.spellE))
                                .execute("http://ddragon.leagueoflegends.com/cdn/6.17.1/img/spell/" + spellImage);
                        break;
                    case 3:
                    new DownloadImageTask((ImageView) findViewById(R.id.spellR))
                            .execute("http://ddragon.leagueoflegends.com/cdn/6.17.1/img/spell/" + spellImage);
                    break;

                }


            }


        }


        private void changeWidth(TextView textView, int pixels){

            ViewGroup.LayoutParams params = textView.getLayoutParams();
            params.width = pixels * 27;
            textView.setLayoutParams(params);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
