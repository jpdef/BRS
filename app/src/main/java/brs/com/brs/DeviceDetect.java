package brs.com.brs;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Environment;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.HexDump;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
/**
 * Created by jake on 1/17/15.
 */
public class DeviceDetect{

    //USB VARS
    private static UsbManager mUsbManager;
    private static UsbSerialPort mPort;
    private static UsbDeviceConnection mConnection;

    public static Exception no_connect;

    //Debug VARs
    public static File debugFile;
    public static FileOutputStream debugger;
    public static boolean debugmode = false;



    /*-----------MAIN  FN'S--------------*/

    static public void intializeSerial(Context context)
    {
         mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    public static boolean isConnected(){
        if (mPort == null ) {
            return false;
        }else if(mUsbManager.openDevice(mPort.getDriver().getDevice()) == null){
            return false;
        }else {
            return true;
        }
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
    }
    public static void disconnectDevice() throws Exception {
        try{
            mPort.close();
        }catch(IOException e1){
            throw no_connect;
        }

    }

    public static void intializeDebug(){
        String filename = "BRSLOG.txt";
        if(debugFile == null)
        debugFile = new File(Environment.getExternalStorageDirectory(),filename);


    }


    public static void debug(String message) {

        if(debugmode) {
            message += '\n';
            byte data[] = message.getBytes();
            try {
                debugger = new FileOutputStream(debugFile, true);
                debugger.write(data);
                debugger.flush();
                debugger.close();
            } catch (IOException e) {
                //do nothing
            }
        }

    }


    }













