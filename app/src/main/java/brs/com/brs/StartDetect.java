        package brs.com.brs;

        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.graphics.Point;
        import android.graphics.Rect;
        import android.graphics.RectF;
        import android.os.Bundle;
        import android.util.DisplayMetrics;
        import android.util.FloatMath;
        import android.util.Log;
        import android.view.Display;
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
        import android.view.WindowManager;
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
    int proximitySetting;
    int alertSetting;
    float prox;
    DisplayMetrics display = this.getResources().getDisplayMetrics();
    int screenWidth = display.widthPixels;
    int screenHeight = display.heightPixels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(DeviceDetect.isConnected()) {
            Sensor sensor = new Sensor(DeviceDetect.getPort());
            Radial radial = new Radial(this, sensor);
            setContentView(radial);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        }else{
            Intent intent = new Intent(StartDetect.this,MainActivity.class);
            startActivity(intent);
        }
        //Preferences for saved data
        @SuppressWarnings("deprecation")
        final SharedPreferences myPrefs = this.getSharedPreferences(
                "myPrefs", MODE_WORLD_READABLE);
        final SharedPreferences.Editor editor= myPrefs.edit();
        proximitySetting = myPrefs.getInt("proximity", 0);
        prox = (float)proximitySetting/10;
        alertSetting = myPrefs.getInt("alert", 1);



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
            float endX = (float) canvas.getWidth();
            float endY = (float) canvas.getHeight();
            canvas.drawBitmap(bgr,maxW,maxH,paint);

            canvas.drawColor(Color.BLACK); // clears screen

            //float six = 6;
            paint.setStrokeWidth(12);
            for(int i =0;i<6; ++i){
                //canvas.drawCircle(i*endX/6,(1+thread.radii[i])*endY/(float)2,maxW/15,paint);
                int intensity =  Math.round(255*(1-thread.radii[i]));
                paint.setARGB(intensity, intensity, 100, 0);
                canvas.drawLines(makePerimeter(canvas,thread.radii,i),paint);
                canvas.drawText(Float.toString(thread.radii[i+6]),i*endX/6,endY*(1-1/(float)8),paint);
            }

            paint.setStrokeWidth(3);
            paint.setColor(Color.parseColor("#ffffff"));
            canvas.drawText(Long.toString(thread.drop_count),endX/2, endY*(1-2/(float)8),paint);

            canvas.drawLines(makeArc(canvas,2),paint);
            canvas.drawLines(makeArc(canvas,3), paint);
            canvas.drawLines(makeArc(canvas,4),paint);
//IF auto alert is set to on, then execute autoAlert() function to test when there is danger
            if(alertSetting==1) {
                autoAlert(thread.radii, canvas);
            }

            makeSectors(canvas,paint);


        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                if(DeviceDetect.isConnected()) sensor.writePort(sensor.sig_start);
                thread = new radialThread(getHolder(),this);
                thread.setRunning(true);
                thread.start();
            }catch(IOException e){
                Intent intent = new Intent(StartDetect.this,MainActivity.class);
                startActivity(intent);
            }

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.v(gtag, "DESTROY");
            //sensor.stopArdiuno();
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
            float[] radii = new float[12];
            long drop_count =0;


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
                        paint.setColor(Color.parseColor("#00ff00"));
                        float[] tmp = {0,0,0,0,0,0};
                        int k = 0;
                        if(DeviceDetect.isConnected()) tmp = sensor.getData();
                        for(int i = 0;i < 6; ++i){
                            if(tmp[i]!=0) {
                                radii[i] = tmp[i];
                                ++k;
                            }
                            radii[i+6] = tmp[i+6];
                        }
                        drop_count += (4-k);
                    }catch (Exception e1){
                        setRunning(false);
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

    public float[] makePerimeter(Canvas canvas, float[] radii,int init){
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
                  perimeter[++i] = ((canvas.getHeight())/two)* FloatMath.sin(angle)*radii[init];
            }

           return perimeter;

    }

    public float[] makeArc(Canvas canvas, float rad_div){
        int numPts =200;
        float[] arcArray = new float[2*numPts];
        for(int i=0;i<numPts; ++i){
            float angle;
            float numPtsf = (float) numPts;
            angle = (float)Math.PI*(i/numPtsf);
            arcArray[i]   = (canvas.getWidth()/rad_div)*
                   (FloatMath.cos(angle)) + canvas.getWidth()/2;
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

//takes in radii and canvas, when it detects something within proximity,
//it will turn the screen red.
//for now, it SHOULD leave the screen red as long as there is something within
//the proximity.
    public void autoAlert(float[] rad, Canvas can){
        int alertFlag = 0;
        for(int i = 0; i < rad.length; i++){
            float dist = rad[i];
            if(dist <= prox) alertFlag = 1;
        }

        if(alertFlag == 1) {
            Paint paint = new Paint();
            paint.setStrokeWidth(3);
            paint.setColor(Color.parseColor("#FF0000"));
            Rect rectangle = new Rect(0,0,screenWidth, screenHeight);
            can.drawRect(rectangle,paint);

        }


    }

}
