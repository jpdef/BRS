package brs.com.brs;

import junit.framework.TestCase;

import java.io.IOException;

public class SensorTest extends TestCase {
    int numBytesRead;
    byte[] buffer_in;
    byte sig_start = (byte)0xFF;
    byte sig_kill =  (byte)0xEE;
    float maxDistance =176;

    protected void setUp(){
        numBytesRead = 0;
        buffer_in = new byte[12];
        for (int i = 0; i<12;++i){
            buffer_in[i] = (byte)0xFF;
        }
    }


    public void testDecode() throws Exception {
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
                output[j] /=maxDistance;           //scale
                output[j+6] = (0xFF & (buffer_in[i]))/maxDistance;
                i+=2;
                ++j;
            }
            assertNotNull(output);
        for(int p = 0;p<12;++p){
            assertEquals(1,output[p]);
        }

        }

    }
