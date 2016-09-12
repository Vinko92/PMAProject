package pma.vinko.legendtracker.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.concurrent.ExecutionException;

import pma.vinko.legendtracker.R;
import pma.vinko.legendtracker.adapters.StaticDataAdapter;
import pma.vinko.legendtracker.asynctasks.GetData;
import pma.vinko.legendtracker.asynctasks.GetImage;

public class SpellDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spell_details);
        String url =  "https://global.api.pvp.net/api/lol/static-data/eune/v1.2/summoner-spell/"+getId()+"?spellData=all&api_key=RGAPI-CBA5D9B2-7ED7-42FB-A7F0-D312B30965EA";
        try {
            JSONObject jsonData = new JSONObject(new GetData().execute(url).get());

            ImageView image = (ImageView) findViewById(R.id.spellImage);
            new GetImage(image).execute("http://ddragon.leagueoflegends.com/cdn/6.18.1/img/spell/" + jsonData.getJSONObject("image").getString("full"));

            TextView title = (TextView) findViewById(R.id.spellTitle);
            title.setText(jsonData.getString("name"));

            TextView description = (TextView) findViewById(R.id.spellDescription);
            description.setText(Html.fromHtml(jsonData.getString("description")));

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
        Intent setIntent = new Intent(SpellDetailsActivity.this,StaticDataActivity.class);
        startActivity(setIntent);
        finish();
        return;

    }

    private String getId(){
        String championId = "";
        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            championId = bundle.get("id").toString();
        }

        return championId;
    }
}
