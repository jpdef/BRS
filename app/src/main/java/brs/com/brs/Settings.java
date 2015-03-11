package brs.com.brs;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;


/**
 * Created by lesterpi on 2/1/15.
 */
public class Settings extends Activity {

    int proximitySetting;
    int alertSetting;
    int themeSetting;
    int debugSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        DeviceDetect.debug("Settings: onCreate");
        final SharedPreferences myPrefs = this.getSharedPreferences(
                "myPrefs", MODE_WORLD_READABLE);
        final SharedPreferences.Editor editor= myPrefs.edit();
        proximitySetting = myPrefs.getInt("proximity", 0);

        alertSetting = myPrefs.getInt("alert", 1);

        themeSetting = myPrefs.getInt("theme", 0);

        debugSetting = myPrefs.getInt("debug",0);

        //set current theme
        LinearLayout layout2 = (LinearLayout) findViewById(R.id.settings_layout);
        if(themeSetting==1){
            layout2.setBackgroundResource(R.drawable.background_light);
        }


        //Seek Bar code
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(proximitySetting);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progress = proximitySetting;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                //gets progress of the seek bar
                progress = progressValue;
                editor.putInt("proximity", progress);
                editor.apply();

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        ToggleButton alert_button = (ToggleButton) findViewById(R.id.button_alert);
        //autoAlert button state code
        if (alertSetting==1) {
            alert_button.setChecked(true);
        }
        else{
            alert_button.setChecked(false);
        }

        //theme button state code
        ToggleButton theme_button = (ToggleButton) findViewById(R.id.button_theme);
        if(themeSetting==1){
            theme_button.setChecked(true);
        }
        else{
            theme_button.setChecked(false);
        }

        ToggleButton debug_button = (ToggleButton) findViewById(R.id.button_debug);
        if(debugSetting==1){
            debug_button.setChecked(true);
        }
        else{
            debug_button.setChecked(false);
        }
    }

    public void autoAlert(View view){
        //Preferences for saved data
        @SuppressWarnings("deprecation")
        final SharedPreferences myPrefs = this.getSharedPreferences(
                "myPrefs", MODE_WORLD_READABLE);
        final SharedPreferences.Editor editor= myPrefs.edit();
        boolean on = ((ToggleButton) view).isChecked();
        //switched on
        if(on){
            //auto alert turns on
            editor.putInt("alert",1);
            editor.apply();
            alertSetting=1;
        }
        //off
        else{
            //auto alert turns off
            editor.putInt("alert",0);
            editor.apply();
            alertSetting=0;
        }
    }
    public void themeChange(View view){
        //Preferences for saved data
        @SuppressWarnings("deprecation")
        final SharedPreferences myPrefs = this.getSharedPreferences(
                "myPrefs", MODE_WORLD_READABLE);
        final SharedPreferences.Editor editor= myPrefs.edit();

        LinearLayout layout2 = (LinearLayout) findViewById(R.id.settings_layout);

        boolean on = ((ToggleButton) view).isChecked();
        //switched on
        if(on){
            //Theme changes to light
            editor.putInt("theme",1);
            editor.apply();
            themeSetting=1;
            layout2.setBackgroundResource(R.drawable.background_light);
        }
        //off
        else{
            //Theme changes to dark
            editor.putInt("theme",0);
            editor.apply();
            themeSetting=0;
            layout2.setBackgroundResource(R.drawable.background_dark);
        }
    }

    public void showDebug(View view){
        //Preferences for saved data
        @SuppressWarnings("deprecation")
        final SharedPreferences myPrefs = this.getSharedPreferences(
                "myPrefs", MODE_WORLD_READABLE);
        final SharedPreferences.Editor editor= myPrefs.edit();
        boolean on = ((ToggleButton) view).isChecked();
        //switched on
        if(on){
            //auto alert turns on
            editor.putInt("debug",1);
            editor.apply();
            debugSetting=1;
        }
        //off
        else{
            //auto alert turns off
            editor.putInt("debug",0);
            editor.apply();
            debugSetting=0;
        }
    }
    @Override
    public void onBackPressed() {
           finish();
    }
}
