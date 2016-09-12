package pma.vinko.legendtracker.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import pma.vinko.legendtracker.R;
import pma.vinko.legendtracker.dal.UserReader;

public class Settings extends AppCompatActivity {

    Switch showImages;
    Switch onWIfiOnly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        UserReader reader = new UserReader(Settings.this);
        String isImageVisible = reader.getIsImageVisible();
        ((TextView) findViewById(R.id.textSettings)).setText(isImageVisible);
        showImages = (Switch) findViewById(R.id.swImages);
        showImages.setChecked(Boolean.parseBoolean(isImageVisible));

        showImages.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    UserReader reader = new UserReader(Settings.this);
                    reader.update(String.valueOf(isChecked),"showImages");
                }
            }
        });

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
        Intent setIntent = new Intent(Settings.this,MainActivity.class);
        startActivity(setIntent);
        finish();
        return;

    }

}
