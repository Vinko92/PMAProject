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

public class ItemDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        String url =  "https://global.api.pvp.net/api/lol/static-data/eune/v1.2/item/"+ getId() + "?itemData=all&api_key=RGAPI-CBA5D9B2-7ED7-42FB-A7F0-D312B30965EA ";
        try {
            JSONObject jsonData = new JSONObject(new GetData().execute(url).get());

            ImageView image = (ImageView) findViewById(R.id.itemImage);
            new GetImage(image).execute("http://ddragon.leagueoflegends.com/cdn/6.18.1/img/item/" + jsonData.getJSONObject("image").getString("full"));

            TextView title = (TextView) findViewById(R.id.itemTitle);
            title.setText(jsonData.getString("name"));

            TextView coast = (TextView) findViewById(R.id.itemCoast);
            coast.setText("Total cost: " + jsonData.getJSONObject("gold").getString("total") + " gold.");

            TextView plainText = (TextView) findViewById(R.id.itemText);
            plainText.setText(jsonData.getString("plaintext"));

            TextView description = (TextView) findViewById(R.id.itemDescription);
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
        Intent setIntent = new Intent(ItemDetailsActivity.this,StaticDataActivity.class);
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
