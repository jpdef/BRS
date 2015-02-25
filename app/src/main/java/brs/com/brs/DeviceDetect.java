package brs.com.brs;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.HexDump;
import java.util.Queue;

/**
 * Created by jake on 1/17/15.
 */
public class DeviceDetect{
    private static final String TAG = "DeviceDetect";

    //USB VARS
    private static UsbManager mUsbManager;
    private static UsbSerialPort mPort;
    private static UsbDeviceConnection mConnection;

    public static Exception no_connect;


    /*-----------MAIN  FN'S--------------*/

    static public void intializeSerial(Context context)
    {
         mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    public static boolean isConnected(){
        if (mPort == null || mUsbManager.getDeviceList().isEmpty()) {
            return false;
        }
        return true;
    }

    public static UsbSerialPort getPort(){
        return mPort;
    }



    /*
    *  connectToDevice()
    *        -opens connection to port
    *        -throws no-connect
    */
    public static void connectToDevice() throws Exception{
        List<UsbSerialDriver> mDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);
        List<UsbSerialPort> ports = null;
        if(mDrivers.isEmpty()){throw no_connect;}

        ports = mDrivers.get(0).getPorts();
        mPort = ports.get(0);

        mConnection = mUsbManager.openDevice(mPort.getDriver().getDevice());
        try {
            mPort.open(mConnection);
            mPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        } catch (IOException e) {
           throw no_connect;
        }
<<<<<<< HEAD
         return buffer_in;
    }




/*decode()
 *      Parameters: Byte Buffer
 *      Returns: List of longs
 *      Protocol:
 *           -Read through buffer find 0xFF
 *           -Parse, convert, and insert data 
 */
     protected String decode(byte[] buffer_in ){
               String output ="";
               Integer i =0;
               for( ;i<buffer_in.length ; ++i){
                   if(buffer_in[i] == sig_start){
                      break; // find start signal
                   }
                }   
                ++i;
                int j = 0;
                while( j < 2 && i < buffer_in.length && buffer_in[i] != sig_kill) {
                       long lo = (long)(buffer_in[i]);
                       long hi = (long) (buffer_in[i+1] );
                            hi = hi << 4;
                       long temp = lo +hi;
                       output = HexDump.toHexString(buffer_in[i]) + HexDump.toHexString(buffer_in[i+1]) + " = ";

                       output =  output + " " + temp + "  ";
                      i+=2;
                      ++j;
                }

                return output + "\n";

     }



/*  
*   Runnable and Handler for communication with arduino
*/
    final Handler readHandle = new Handler(){
        @Override
        public void handleMessage(Message msg){
            // on a message of sig kill send to arduino
            // and stop running read write
             try {
                 if(mPort != null) {
                     writePort((byte) msg.arg1);
                 }
             }catch(IOException no_write){
                    //do nothing
             }
            readHandle.removeCallbacks(readRun);

            }

    };

    final Runnable readRun = new Runnable() {
        @Override
        public void run() {
            try {
                writePort(sig_start);
                infoView.append(decode(readPort()));
                //Should auto scroll
               /*
                int scrollAmount = infoView.getLayout().getLineTop(infoView.getLineCount()) - infoView.getHeight();
                if (scrollAmount > 0) {
                    infoView.scrollTo(0, scrollAmount);
                } else {
                    infoView.scrollTo(0, 0);
                }
*/
                readHandle.postDelayed(this, 0);                     // calls same thread in .2 secs
            }catch(IOException e){
                failure_message(e.getMessage());
            }
        }

    };






/*--------- TEST INTERFACE ---------*/



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_detect);
        infoView =(TextView)findViewById(R.id.infoMessage);
        infoView.setText("Information:\n S1:\tS2:\tS3:\tS4:\tS5:\tS6:\t \n");
        infoView.setTextSize(20);
        infoView.setMovementMethod(new ScrollingMovementMethod()); //enables scrolling for interface


        /*Setup Connection
         *   -Find Drivers
         *   -Connect with driver
         *   -Connect to a port
         */

        if(getPorts() != null){
            mPort = getPorts().get(0);
        }else{
            failure_message("Couldn't get a port");
            return;
        }
        connectToDevice(mPort);




    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.

=======
>>>>>>> graphics



    }












}
