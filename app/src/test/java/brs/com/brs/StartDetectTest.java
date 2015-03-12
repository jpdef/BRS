package brs.com.brs;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.FloatMath;

import junit.framework.TestCase;

public class StartDetectTest extends TestCase {
    Canvas canvas;
    float[] radii;
    float prox;

    int init = 0;
    int alertFlag=0;
    @Override
    protected void setUp(){
        canvas = new Canvas();
        radii = new float[6];
        prox = 0.5f;
    }


    public void testMakePerimeter() throws Exception {
        float thirty= (float)Math.PI/6;
        float init_angle = init*thirty;
        int numPts=240;
        float numPtsf = 240;
        float two = 2;
        float[] perimeter = new float[4*numPts];
        for(int i=0; i< numPts; ++i){
            float angle = thirty*(i/numPtsf) + init_angle;
            perimeter[i]   = canvas.getWidth()/two;
            perimeter[++i] = 0;
            perimeter[++i]   = ((canvas.getWidth()/two))*
                    (FloatMath.cos(angle))*radii[init] + canvas.getWidth()/two;
            assertTrue(perimeter[i] > canvas.getWidth());
            perimeter[++i] = ((canvas.getHeight())/two)* FloatMath.sin(angle)*radii[init];
            assertTrue(perimeter[i] > canvas.getHeight());

        }
    }


}