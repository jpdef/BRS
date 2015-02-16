package brs.com.brs;

import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.Surface;
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

/**
 * Created by lesterpi on 2/1/15.
 */

public class StartDetect extends Activity {

    String gtag = new String("graphics:");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_detect);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        paint.setStrokeWidth(3);
        Bitmap bg = Bitmap.createBitmap(480,800,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bg);
        int maxH = canvas.getHeight();
        int maxW = canvas.getWidth();
        canvas.drawLines(makeArc(canvas,2),paint);
        canvas.drawLines(makeArc(canvas, 3), paint);
        canvas.drawLines(makeArc(canvas,4),paint);
        makeSectors(canvas,paint);
        //float st = 0; float swp = 180;
        //RectF base = new RectF(0,0,canvas.getWidth(),canvas.getHeight());
        //canvas.drawArc(base, st,swp,true,paint);
        SurfaceView sv = (SurfaceView) findViewById(R.id.mainSurfView);
        sv.setBackgroundDrawable(new BitmapDrawable(bg));

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
            Log.v(gtag, arcArray[i-1] + " " + arcArray[i]);
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
            Log.v(gtag, endX + " " + endY);
            angle += thirty;
            canvas.drawLine(maxW/2,0,endX, endY,paint);
        }

    }

    public void GoBack(View view){

        finish();

    }

}