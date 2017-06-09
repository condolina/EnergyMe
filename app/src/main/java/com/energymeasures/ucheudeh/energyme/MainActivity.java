package com.energymeasures.ucheudeh.energyme;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

//import static android.provider.AlarmClock.EXTRA_MESSAGE;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = this.getApplicationContext();
        setContentView(R.layout.activity_main);
        String message = "Java Serialization - with Normal Read. ";

        TextView textView = new TextView(this);
        textView.setText(message);
        setContentView(textView);


        Log.i("EnergyMe", "Commencing Data creation");
        SnapshotsBasket numData = new SnapshotsBasket();

        JSerializer jSt= new JSerializer(context);
        jSt.doWrite(numData);
        jSt.doRead();


        MsgPSerializer msgPSt = new MsgPSerializer(context);

        try {
            msgPSt.doWrite(numData);
        } catch (IOException e) {
            Log.e("Error MsgPackwrite",e.toString());
        }
        msgPSt.doRead();

        MsgPackRandomAccess msgRandom = new MsgPackRandomAccess(context);


        try{
            msgRandom.doWrite(numData);
        }catch(IOException e){
            Log.e("Error MsgPackrandowrite",e.toString());
        }

        msgRandom.doRead();

        textView.setText("OK");
        setContentView(textView);

    }

}
