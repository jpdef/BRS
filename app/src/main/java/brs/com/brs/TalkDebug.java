package brs.com.brs;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;;
import android.text.format.Time;
import android.text.method.ScrollingMovementMethod;
import android.widget.CheckBox;
import android.widget.TextView;
import com.hoho.android.usbserial.util.HexDump;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import android.os.Message;
import android.os.Handler;

/**
 * Created by Jacob Defilippis on 2/1/15.
 */

public class TalkDebug extends Activity {
    Sensor sensor;
    float[] sensor_data;
    int pauseFlag = 0;

    TextView failure;
    TextView sucess;
    TextView infoView;

    CheckBox cb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.talk_debug);
        failure = (TextView)findViewById(R.id.failureMessage);
        sucess =  (TextView)findViewById(R.id.sucessMessage);
        infoView = (TextView) findViewById(R.id.infoMessage);
        infoView.setText("Information:\n S1:\tS2:\tS3:\tS4:\tS5:\tS6:\t \n");
        infoView.setTextSize(15);
        infoView.setMovementMethod(new ScrollingMovementMethod()); //enables scrolling for interface

          cb = (CheckBox) findViewById(R.id.logbox);

        if(DeviceDetect.isConnected()) {
            sensor = new Sensor(DeviceDetect.getPort());
            sucess.setText("Connected to Port");
            try{
                sensor.writePort(sensor.sig_start);
            }catch (Exception e){
                    DeviceDetect.debug("TalkDebug: No write");
            }
            readHandle.postDelayed(readRun,0); // start thread
        }else{
              sensor =null;
              failure.setText("Failure to Connect");

        }



    }
    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onResume(){
        super.onResume();
        try{
            sensor.writePort(sensor.sig_start);
        }catch (Exception e){
            DeviceDetect.debug("TalkDebug: No write");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
    /*
    *   Runnable and Handler for communication with arduino
    */
    final Handler readHandle = new Handler(){
        @Override
        public void handleMessage(Message msg){
            sensor.stopArdiuno();
            readHandle.removeCallbacks(readRun);

        }

    };

    final Runnable readRun = new Runnable() {
        @Override
        public void run() {
            try {
                String data = HexDump.toHexString(sensor.readPort());
                Time now = new Time(Time.getCurrentTimezone());
                now.setToNow();
                String strtime = "Recieved at"
                +now.monthDay + "/" +          // Day of the month (1-31)
                now.month + "/" +                // Month (0-11)
                now.year + "::" +              // Year
                now.format("%k:%M:%S") + "\n" ;
                infoView.append(strtime);
                infoView.append(data+"\n");
                // auto scrolling
                if(cb.isChecked())  DeviceDetect.debug(strtime + data + "\n");

                int scrollAmount = infoView.getLayout().getLineTop(infoView.getLineCount()) - infoView.getHeight();
                if (scrollAmount > 0) {
                    infoView.scrollTo(0, scrollAmount);
                } else {
                    infoView.scrollTo(0, 0);
                }

                readHandle.postDelayed(this, 200);                     // calls same thread in .2 secs
            }catch(Exception e){
                 DeviceDetect.debug("'Failed to read");
            }
        }

    };



}
