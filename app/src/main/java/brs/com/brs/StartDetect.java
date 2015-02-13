package brs.com.brs;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by lesterpi on 2/1/15.
 */
public class StartDetect extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_detect);


    }

    public void GoBack(View view){

        finish();

    }

}