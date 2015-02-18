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
    int readtime =200;
    int writetime=200;
    IOException no_write;
    IOException no_read ;

    /*Byte signals*/
    public final byte sig_start = (byte) 0xFF;
    public final byte sig_error = (byte) 0xEF;
    public final byte sig_kill  = (byte) 0xEE;

    public Sensor(UsbSerialPort port){
       this.port =port;
    }

    /* getData()
    *
    * */
    public float[] getData() throws Exception{
        try {
            writePort(sig_start);
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
        float output[] = new float[6];
        Integer i =0;
        for( ;i<buffer_in.length ; ++i){
            if(buffer_in[i] == sig_start){
                break; // find start signal
            }
        }
        ++i;
        int j = 0;
        while( j < 6 && i < buffer_in.length && buffer_in[i] != sig_kill) {
            output[j] = (float)(buffer_in[i]);
            output[j] /=(float)255;           //scale
            i+=2;
            ++j;
        }

        return output;

    }


}
