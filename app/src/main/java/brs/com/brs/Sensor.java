package brs.com.brs;

import java.io.IOException;
import java.util.List;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.HexDump;

/**
 * Created by jake on 2/16/15.
 */
public class Sensor {
    UsbSerialPort port;
    int readtime =500;
    int writetime=500;
    IOException no_write;
    IOException no_read ;

    /*Byte signals*/
    public final byte sig_start = (byte) 0xFF;
    public final byte sig_error = (byte) 0xEF;
    public final byte sig_kill  =  (byte) 0xEE;
    public final byte s1        =  (byte) 0xFE;

    public Sensor(UsbSerialPort port){
       this.port =port;
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
        byte buffer_in[] = new byte[32];
        try {
            int numBytesRead = port.read(buffer_in, readtime);
            if(numBytesRead >0){
                return buffer_in;
            }
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
            output[j] /=(float) 156;           //scale
            output[j+6] = (0xFF & (buffer_in[i]))/(float)(156);
            i+=2;
            ++j;
        }
        //Log.v("Sensor output:", output[0] + " : " + output[2] );
        return output;

    }


}
