package brs.com.brs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hoho.android.usbserial.util.HexDump;

import java.io.IOError;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by lesterpi on 2/1/15.
 */

public class StartDetect extends Activity {
    Radial radial;
    String gtag = new String("graphics:");
    Sensor sensor;
    float[] sensor_data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Sensor sensor = new Sensor(DeviceDetect.getPort());
        Radial radial = new Radial(this,sensor);
        //test write
        /*try {
            sensor.writePort(sensor.sig_start);
        }catch(Exception something){
            Intent goback = new Intent(this, MainActivity.class);
            startActivity(goback);

        }*/
        setContentView(radial);



    }
    @Override
    protected void onPause() {
        super.onStop();
        //sensor.stopArdiuno();

    }


    @Override
    protected void onStop() {
        super.onStop();
        //sensor.stopArdiuno();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //radial.surfaceDestroyed(radial.getHolder());



    }

    public class Radial extends SurfaceView implements SurfaceHolder.Callback{
    int numPts = 200; // resolution
    radialThread thread;
    Bitmap bgr;
    Paint paint;
    int maxH; int maxW;
    Sensor sensor;



    public Radial(Context context,Sensor sensor){
        super(context);
        //Initalize bitmap parameters
        bgr = Bitmap.createBitmap(480,800,Bitmap.Config.ARGB_8888);
        maxH = bgr.getWidth();
        maxW = bgr.getHeight();
        paint = new Paint();
        paint.setStrokeWidth(3);
        paint.setColor(Color.parseColor("#00ff00"));

        this.sensor = sensor;


        //Set canvas thread
        getHolder().addCallback(this);

    }

    @Override
    public void onDraw(Canvas canvas){
        float t1 = (float) canvas.getWidth();
        float t2 = (float) canvas.getHeight();
        canvas.drawBitmap(bgr,maxW,maxH,paint);
        //Log.v(gtag,"ondraw accessed");
        /*Get normalized data
          from sensor
          getData()*/
        canvas.drawColor(Color.BLACK); // clears screen
        float endX =  t1/2;
        float endY =  (thread.test_radius+1)*(t2/2);

        canvas.drawCircle(endX,endY,maxW/10,paint);

        canvas.drawLines(makeArc(canvas,2),paint);
        canvas.drawLines(makeArc(canvas,3), paint);
        canvas.drawLines(makeArc(canvas,4),paint);
        makeSectors(canvas,paint);

    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            sensor.writePort(sensor.sig_start);
        }catch(IOException e){
            //
        }
        thread = new radialThread(getHolder(),this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(gtag, "DESTROY");
        sensor.stopArdiuno();
        thread.setRunning(false);
        boolean retry = true;
        while(retry){
            try{
                thread.join();
                retry =false;

            }catch(InterruptedException e){

            }
        }

    }
    /*
    *   Thread updates frames for canvas
    */
    class radialThread extends Thread{
        private SurfaceHolder surfaceHolder; //underlying canvas for next frame
        private Radial mainView;
        private volatile boolean run = false;
        float test_radius = 0;


        //constructor
        public radialThread(SurfaceHolder surfaceHolder, Radial mainView){
            this.surfaceHolder = surfaceHolder;
            this.mainView      = mainView;
        }

        //on/off
        public void setRunning(boolean run){
            this.run = run;
        }

        public SurfaceHolder getSurfaceHolder() {
            return surfaceHolder;
        }

        @Override
        public void run(){
            Canvas c;
            while(run){
                c=null;
                try{
                    Thread.sleep(100);
                }catch (InterruptedException e){


                }

                try {
                    //error = HexDump.dumpHexString(sensor.readPort());
                    paint.setColor(Color.parseColor("#00ff00"));
                    test_radius  = sensor.getData()[0];
                }catch (Exception e1){
                    //test_radius = (float)Math.random();
                    paint.setColor(Color.parseColor("#ff0000"));
                    Log.v(gtag,"Couldn't read");
                }

                try {
                    c = surfaceHolder.lockCanvas(null);
                    if(c!=null){
                      synchronized (surfaceHolder) {
                          mainView.onDraw(c);
                      }
                    }
                }finally {
                    if (c != null) {
                        surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }


    }

}



    public float[] makeArc(Canvas canvas, float rad_div){
        int numPts =200;
        float[] arcArray = new float[numPts];
        for(int i=0;i<numPts; ++i){
            float angle;
            float numPtsf = (float) numPts;
            angle = (float)Math.PI*(i/numPtsf);
            arcArray[i]   = (canvas.getWidth()/rad_div)*(FloatMath.cos(angle)) + canvas.getWidth()/2;
            arcArray[++i] = (canvas.getHeight()/rad_div)* FloatMath.sin(angle);
            //Log.v(gtag, arcArray[i-1] + " " + arcArray[i]);
        }

        return arcArray;
    }

    public void makeSectors(Canvas canvas,Paint paint){
        paint.setTextSize(20);
        paint.setTextAlign(Paint.Align.CENTER);
        int maxW = canvas.getWidth();
        int maxH = canvas.getHeight();
        float thirty = (float)Math.PI/6;
        float angle = 0;
        for(int i = 0; i <7 ; ++i){
            float endX =  (maxW/2)*(FloatMath.cos(angle)+1);
            float endY =  (maxH/2)* FloatMath.sin(angle);
            canvas.drawText(String.format("%d",(int)(180*(angle/Math.PI))),
                    endX, (float)(1.2*endY),paint);
            //Log.v(gtag, endX + " " + endY);
            angle += thirty;
            canvas.drawLine(maxW/2,0,endX, endY,paint);
        }

    }

    public void GoBack(View view){

        finish();

    }

}
