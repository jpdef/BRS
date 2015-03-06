package brs.com.brs;

import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.widget.TextView;

import android.widget.LinearLayout;
import android.widget.Toast;


public class MainActivity extends Activity {
    boolean isConnected=false;

    int proximitySetting;
    int alertSetting;
    int themeSetting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DeviceDetect.intializeSerial(this);
        try{
            DeviceDetect.connectToDevice();
        }catch(Exception e){
            createToast("Not Connected");
        }


        LinearLayout layout1 = (LinearLayout) findViewById(R.id.main_layout);
        //Preferences for saved data
        @SuppressWarnings("deprecation")
        final SharedPreferences myPrefs = this.getSharedPreferences(
                "myPrefs", MODE_WORLD_READABLE);


        proximitySetting = myPrefs.getInt("proximity", 0);



        alertSetting = myPrefs.getInt("alert", 0);


        themeSetting = myPrefs.getInt("theme", 0);

        //set current theme
        if(themeSetting==1){
            layout1.setBackgroundResource(R.drawable.background_main_light);
        }
    }

    @Override
    protected void onDestroy(){
        try {
            DeviceDetect.disconnectDevice();
        }catch (Exception no_connect){
            createToast("Internal error");
        }
    }


    @Override
    protected void onResume(){
        super.onResume();
        //Preferences for saved data
        @SuppressWarnings("deprecation")
        final SharedPreferences myPrefs = this.getSharedPreferences(
                "myPrefs", MODE_WORLD_READABLE);
        LinearLayout layout1 = (LinearLayout) findViewById(R.id.main_layout);
        themeSetting = myPrefs.getInt("theme", 0);
        if(themeSetting==1){
            layout1.setBackgroundResource(R.drawable.background_main_light);
        }
        else{
            layout1.setBackgroundResource(R.drawable.background_main_dark);
        }
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
    public void connectDevice(View view){

        try{
            DeviceDetect.connectToDevice();
        }catch(Exception e){
            createToast("Device is not connected");

        }

    }
    public void settings(View view){
         Intent intent = new Intent(this, Settings.class);
         startActivity(intent);

    }

    public void start(View view){
        if(DeviceDetect.isConnected()){
            Intent intent = new Intent(this,StartDetect.class);
            startActivity(intent);

        }else{
            createToast("Not Connected");
        }

    }
    //method to create a toast message
    public void createToast(String message){
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
