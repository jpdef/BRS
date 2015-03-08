package brs.com.brs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.HexDump;

/**
 * Created by jake on 2/16/15.
 */
public class Sensor {
    UsbSerialPort port;
    sensorThread sensorthread;
    int readtime =100;
    int writetime=100;
    IOException no_write;
    IOException no_read ;

    /*Byte signals*/
    public final byte sig_start = (byte) 0xFF;
    public final byte sig_error = (byte) 0xEF;
    public final byte sig_kill  =  (byte) 0xEE;
    public final byte s1        =  (byte) 0xFE;

    /* FIFO */
    List<float[]> fifo;

    public Sensor(UsbSerialPort port){
       this.port =port;
        fifo = new ArrayList<float[]>();
        sensorthread = new sensorThread(this);

    }


    /* getData()
    *
    * */
    public float[] getData() throws Exception{
        try {
          // writePort(sig_start);
            try {
                 return decode(readPort());
            }catch (Exception nr){
                throw no_read;
            }
        }catch (Exception nw){
            throw no_write;
        }

    }

    public void stopArdiuno(){
        try{
            writePort(sig_kill);
            port.close();
        }catch (Exception e){
            // do somethign
        }
    }


    /* writePort()
*       Parameters: byte signal
*       Returns: Void
*       Protocol:
*            -Converts Sig to Byte array
*            -Writes for 0.5 sec
*       Errors
*             -IO exception no_write
 */
    protected void writePort(byte sig) throws IOException {
        byte[] sig_out = new byte[1];
        sig_out[0] = sig;
        try{
            port.write(sig_out,writetime);
        }catch (IOException e4){
            throw no_write;
        }

    }

    /* readPort()
*      Parameters: N/A
*      Returns: String (for now)
*      Protocol:
*              -Buffers 32 bytes
*              -Reads for readtime secs
*              -Return HEX string
*      Errors:
*              -IO exception no_read
*/
    protected byte[] readPort() throws IOException{
        byte buffer_in[] = new byte[16];
        try {
            int numBytesRead = port.read(buffer_in, readtime);
            if(numBytesRead >0){
                return buffer_in;
            }
            port.purgeHwBuffers(true,false);
        } catch (IOException e6) {
            //failure_message("Couldn't Read/Write");
            try {
                port.close();
            } catch (IOException e7) {
                //ignore
            }
            throw no_read;

        }
        return buffer_in;
    }


    protected float[] decode(byte[] buffer_in ){
        float output[] = new float[12];
        Integer i =0;
        for( ;i<buffer_in.length ; ++i){
            if(buffer_in[i] == sig_start){
                break; // find start signal
            }
        }
        ++i;
        int j = 0;
        while( j < 6 && i < buffer_in.length && buffer_in[i] != sig_kill) {
            output[j] = 0xFF & (buffer_in[i]);
            output[j] /=(float) 175;           //scale
            output[j+6] = (0xFF & (buffer_in[i]))/(float)(175);
            i+=2;
            ++j;
        }
        //Log.v("Sensor output:", output[0] + " : " + output[2] );
        return output;

    }


    class sensorThread extends Thread {
        volatile boolean run;
        Sensor sensor;


        //constructor
        public sensorThread(Sensor sensor){
           this.sensor=sensor;
        }

        //on/off
        public void setRunning(boolean run){
            this.run = run;
        }

        @Override
        public void run(){
            while(run){
                //catch interupts
                try{
                    Thread.sleep(100);
                }catch (InterruptedException e){

                    try {
                        port.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

                try {
                    float[] tmp = {0,0,0,0,0,0};
                    if (DeviceDetect.isConnected()) {
                        tmp = sensor.getData();
                        sensor.fifo.add(tmp);
                    }


                }catch (Exception e){
                    this.setRunning(false);
                }


            }
        }
    }

}
