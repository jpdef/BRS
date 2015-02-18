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

    static public void intializeSerial(UsbManager usbManager){
         mUsbManager = usbManager;
    }

    public static boolean isConnected(){
        if (mUsbManager.getDeviceList().isEmpty() || mPort == null) {
            return false;
        }
        return true;
    }

    public static UsbSerialPort getPort(){
        return mPort;
    }


    /*
      getPorts()
            -gets manager for driver
            -driver gets list of ports
    */
    public static boolean setPort(){
        //must be passed in
        //mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> mDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);
        List<UsbSerialPort> ports = null;

        if(mDrivers.isEmpty()){
            return false;
        }else{
            ports = mDrivers.get(0).getPorts();
            mPort = ports.get(0);
            return true;
        }

    }


    /*
    *  connectToDevice()
    *        -opens connection to port
    *        -throws no-connect
    */
    public static void connectToDevice() throws Exception{
        UsbDeviceConnection mConnection = mUsbManager.openDevice(mPort.getDriver().getDevice());
        try{
            mPort.open(mConnection);
            mPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        }catch (IOException e){
            throw no_connect;
        }



    }












}
