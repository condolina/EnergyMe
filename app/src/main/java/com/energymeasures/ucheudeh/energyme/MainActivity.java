package com.energymeasures.ucheudeh.energyme;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

//import static android.provider.AlarmClock.EXTRA_MESSAGE;


public class MainActivity extends AppCompatActivity {

    SnapshotsBasket numData;

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
        numData = new SnapshotsBasket();



        JSerializer jSt= new JSerializer(context);
        long jWStime = System.nanoTime();
        jSt.doWrite(numData);
        long jWEtime = System.nanoTime();
        Log.i("Java SerWriteTIme-", Long.toString(jWEtime-jWStime));

        // Test read time here for Bigfile
        jSt.doRead();


        MsgPSerializer msgPSt = new MsgPSerializer(context);

        try {
            long msgWStime = System.nanoTime();
            msgPSt.doWrite(numData);
            long msgWEtime = System.nanoTime();
            Log.i("msgPWriteTIme-", Long.toString(msgWEtime-msgWStime));
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





        try {
            experiment(context);
        } catch (IOException e) {
            Log.e("RegMapped","IOerror at File creation" + e.toString());
        }



    }

    private void experiment(Context context) throws IOException {


            NumericalDataWriter regMap = new NumericalDataWriter(context);
            long eMeWStime = System.nanoTime();

            regMap.write(numData);

        long eMeWEtime = System.nanoTime();
        Log.i("EnerMeWriteTIme-", Long.toString(eMeWEtime-eMeWStime));



    }

}
