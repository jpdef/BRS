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

    public Sensor(UsbSerialPort port){
        this.port =port;
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
*              -Reads for 0.2 secs
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


    protected String decode(byte[] buffer_in ){
        String output ="";
        Integer i =0;
        for( ;i<buffer_in.length ; ++i){
            if(buffer_in[i] == DeviceDetect.s){
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


}
