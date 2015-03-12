package brs.com.brs;

import junit.framework.TestCase;


public class SensorTest extends TestCase {
    int numBytesRead;
    byte[] buffer_in;
    byte sig_start = (byte)0xFF;
    byte sig_kill =  (byte)0xEE;
    float maxDistance =255;

    protected void setUp(){
        // Check crazy inputs
        buffer_in = new byte[12];
        buffer_in[0] = (byte)0xFF;
        buffer_in[1] = (byte) 10;
        buffer_in[2] = (byte) -1;
        buffer_in[3] = (byte) 512 ;
        buffer_in[4] = (byte) 0;
        buffer_in[5] = (byte)0xFF;
        buffer_in[6] = (byte)0xEE;
        buffer_in[7] = (byte)0xFF;



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
        assertTrue(output[0] > 0.0);
        assertTrue(output[1] >= 0.0);
        assertTrue(output[2] >= 0.0);
        assertEquals(1.0 , output[3], .0001);
        assertEquals(0.0 , output[4], .0001);
        assertEquals(0.0 , output[5], .0001);
        assertEquals(output[0],output[6]);


    }

    }
