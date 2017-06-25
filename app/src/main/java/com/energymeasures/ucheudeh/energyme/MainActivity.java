package com.energymeasures.ucheudeh.energyme;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    SnapshotsBasket numData;
    CSVWriter results;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };





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

        //matrices and vectors are constructed 2exp(x)*2exp(x) entries. matrix and be manually fixed
        // On some devices making this CORE(8), BOARDER, EXTREME, INSANE may not be possible due to
        // JVM environment limitations. Minimum, Xmx1024M, XX:maxPermSize=512

//for read only block off from this point


        ArrayList<int[]> dbank = new ArrayList<int []>(); //holds the
        dbank.add(new int[]{1,2,3,4});//largest matrix/vector has 256 doubles : PRE_AMBLE
        dbank.add(new int[]{5,6,7,8});//largest matrix/vector has 65536 doubles: CORE
        //The following groups are padded with 1s, to give a uniform structure in the read

        //dbank.add(new int[]{1,1,9,10});//largest matrix/vector has 1048576 doubles : BOARDER
        //dbank.add(new int[]{1,1,11,12});//largest matrix/vector has 16777216 doubles : EXTREME
       // dbank.add(new int[]{1,1,1,13});//largest matrix/vector has 671108864 doubles : INSANE





        for (int i = 0; i<dbank.size(); i++) {
            SnapshotsBasket numData = new SnapshotsBasket(dbank.get(i), getGroupType(i));
            try {
                experimentWrite(context,numData);

            } catch (IOException e) {
                Log.e("ExperimentWrite","IOerror at File creation" + e.toString());
            }


        }


// Block off readonly end point









        try {
            int repeats = 30; // number of read repeats
            // Tag to help identify the files and name the CVS files. verify that files exist
            String[] experimentTag = {"dPreamble","dCore"};// ,controll which group is read with tag
            for(String tag: experimentTag)
            experimentRead(context, repeats, tag);//
            cacheCleaner(context);
        } catch (IOException e) {
            Log.e("ExperimentWrite","IOerror at File creation" + e.toString());
        }





    }

    private String getGroupType(int i) {
        String type;


        switch (i){
            case 0:
               type = "dPreamble";
                break;

            case 1:
                type = "dCore";
                break;

            case 2:
                type =  "dBoarder";
                break;

            case 3:
                type =  "dExtreme";
                break;

            default:
                type =  "dCore";
                break;


        }
        return type;

    }

    private void experimentWrite(Context context, SnapshotsBasket numData) throws IOException {
        // Run Once, create all files from the same SnapshotsBasket (Same Matrices and Arrays)


        //eneM: Simple and MMAP Test Files Big and Indi Files
        NumericalDataWriter regMapW = new NumericalDataWriter(context);
        long eMeWStime = System.nanoTime();

        regMapW.write(numData);

        long eMeWEtime = System.nanoTime();
        Log.i("EnerMeWriteTIme-", Long.toString(eMeWEtime-eMeWStime));


        // eneM: Random Access Test Files
        RandomAccessNumericalDataWriter regMapWRnd = new RandomAccessNumericalDataWriter(context);
        long eMeWSRndtime = System.nanoTime();

        regMapWRnd.write(numData);

        long eMeWERndtime = System.nanoTime();
        Log.i("EnerMeRndWriteTIme-", Long.toString(eMeWERndtime-eMeWSRndtime));




        // jSer: writes Files (individual and combined file)

        JSerializer jSt= new JSerializer(context);
        long jWStime = System.nanoTime();
        jSt.doWrite(numData);
        long jWEtime = System.nanoTime();
        Log.i("Java SerWriteTIme-", Long.toString(jWEtime-jWStime));



        // msgPWriter (Big and Indi files)


        MsgPSerializer msgPSt = new MsgPSerializer(context);

        try {
            long msgWStime = System.nanoTime();
            msgPSt.write(numData);
            long msgWEtime = System.nanoTime();
            Log.i("msgPWriteTIme-", Long.toString(msgWEtime-msgWStime));
        } catch (IOException e) {
            Log.e("Error MsgPackwrite",e.toString());
        }





    }

    private void experimentRead(Context context, int repeats, String tag) throws IOException {
        /*
        Result file handle, will be a CSV with corresponding name. An attempt will be made save the
        files in the SDCard in a folder named "EnergymeasuresCSVs", otherwise is is saved in the
        application files diretory on the internal Flash drive. Files will have a timestamp appended
        to the name.


         */

        File file;

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }





        if(!isExternalStorageWritable()){
            file = new File (context.getFilesDir(),"cvs"+tag+ System.currentTimeMillis());}
        else{

            // prepare to save file in SDCard
            File nestedFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            nestedFolder.mkdirs();
            if (!nestedFolder.isDirectory()) {
                Log.e("Document Directory__:", "Directory not created");
            }
           File resultDir= new File(nestedFolder, "EnergymeasuresCSVs");
            resultDir.mkdirs();
            if (!resultDir.isDirectory()) {
                Log.e("Document Directory__:", "Directory not created");
            }




            file = new File (resultDir,"CSV_"+tag+System.currentTimeMillis());


        }
        results = new CSVWriter(new FileWriter(file));
        String [] headers = ("EnerMeREADTBig#jSerBigFile#msgPBigRead#" +
                "EnerMeRMmapBigFile#EnerMeREADTImeIndi#jSerIndiFile#msgPIndiFile#" +
                "eneM_Rnd_Proactv#eneM_Rnd_Lazy#eneM_Mmap_Rnd_Early#eneM_Mmap_Rnd_Lazy").split("#");
        results.writeNext(headers);

        /*

        Assumption here is that it is possible to create a Reader without actually reading anything
        Time log begins when the read method is called on the Reader (INTERNAL VALIDATION)

        Do some house keeping here. Force cache dump to storage and clear page cache
        Force GC :-O will an unrooted phone even allow an application to do this!!!!
        I doubt it :-/. Well some research need here.

         */
        ArrayList<Long> timeStamps = null;
        for (int i=0; i<repeats;i++) {

             timeStamps = new ArrayList<Long>();// will be written out to CVS later

            // eneM Single File Simple
            timeStamps.add(Long.valueOf(callReadeneMBig(context,tag)));

            // jSer read Single File
            timeStamps.add(Long.valueOf(callReadjSerBig(context,tag)));

            // msgP Single File
            timeStamps.add(Long.valueOf(callReadmsgPBig(context,tag)));

            //eneM MMap Single File
            timeStamps.add(Long.valueOf(callReadeneMMap(context,tag)));


            // eneM Indi
            timeStamps.add(Long.valueOf(callReadeneMIndi(context,tag)));

            // jSer Indi
            timeStamps.add(Long.valueOf(callReadjSerIndi(context,tag)));

            // msgP Indi Files
            timeStamps.add(Long.valueOf(callReadmsgPIndi(context,tag)));

            //eneM Random Proactive
            timeStamps.add(Long.valueOf(callReadeneMRndEarly(context,tag)));

            // eneM Random Lazy
            timeStamps.add(Long.valueOf(callReadeneMRndLate(context,tag)));













            results.writeNext(long2String(timeStamps));

        }
        results.flush();
        results.close();
        Log.i("Energy_Measures", "Experiment Complete: Check"+file.toString()+"for the CSV files");

    }




    private long callReadmsgPIndi(Context context, String tag) throws IOException {
        cacheCleaner(context);
        long starttime = System.nanoTime();
        File path = new File (context.getFilesDir(),"MsgPBasisBig"+tag+".dat");// Ignored in actual method

        MsgPReaderIndiFile msgPIndi = new MsgPReaderIndiFile(path);

        msgPIndi.read(context,"MsgPIndi"+tag);

        long endtime = System.nanoTime();
        long duration = endtime-starttime;
        Log.i("msgPIndiFile         - ",Long.toString(duration));
        return duration;

    }

    private long callReadmsgPBig(Context context, String tag) throws IOException {

        cacheCleaner(context);
        long starttime = System.nanoTime();
        File path = new File (context.getFilesDir(),"MsgPBasisBig"+tag+".dat");

        MsgPReader msgPread = new MsgPReader(path);

        msgPread.read();

        long endtime = System.nanoTime();
        long duration = endtime-starttime;
        Log.i("msgPBigRead          - ", Long.toString(duration));
        return duration;
    }

    private long callReadjSerIndi(Context context, String tag) throws IOException {
        cacheCleaner(context);
        long starttime = System.nanoTime();
        File path = new File (context.getFilesDir(),"jSerBig"+tag+".dat");// Ignored in actual method

        JReaderIndiFile jSerRIndi = new JReaderIndiFile(path);

        jSerRIndi.read(context,"jSerIndi"+tag);

        long endtime = System.nanoTime();
        long duration = endtime-starttime;
        Log.i("jSerIndiFile         - ", Long.toString(duration));
        return duration;

    }

    private long callReadjSerBig(Context context, String tag) throws IOException {
        cacheCleaner(context);
        long starttime = System.nanoTime();
        File path = new File (context.getFilesDir(),"jSerBig"+tag+".dat");

        JReaderSingle jSerR = new JReaderSingle(path);

        jSerR.read();

        long endtime = System.nanoTime();
        long duration = endtime-starttime;
        Log.i("jSerBigFile          - ", Long.toString(duration));
        return duration;


    }

    private long callReadeneMMap(Context context, String tag) throws IOException {
        cacheCleaner(context);
        long eMeRStime = System.nanoTime();
        File path = new File (context.getFilesDir(),"regMappedBig"+tag);

        MappedReader regMapR = new MappedReader(path);

        regMapR.read();

        long eMeREtime = System.nanoTime();
        long duration = eMeREtime-eMeRStime;
        Log.i("EnerMeRMmapBigFile   - ", Long.toString(duration));
        return duration;

    }

    private long callReadeneMRndLate(Context context, String tag) throws IOException {
        /*
        The Random access read will fetch 3 matrix from the file to the application heap.
        Read can be called several times on the reader object for this RNDaccesReader. I think this
        is a better use case than asking for all records in the file.
         */
        cacheCleaner(context);
        int [] bringList = new int[]{3,0,2};
        long eMeRSRndtime = System.nanoTime();
        File pathRnd = new File (context.getFilesDir(),"eneMRnd"+tag+".dat");
        RandomAccessReader eneRndLate = new RandomAccessReader(pathRnd,context);
        eneRndLate.read(bringList, RandomAccessReader.ReadMode.LATE_FETCH);

        //Log.i("Element Val -",Double.toString(eneRnd.vectorTable.get(1).getEntry(20)));
        long eMeRERndtime = System.nanoTime();
        long duration = eMeRERndtime-eMeRSRndtime;
        Log.i("EnerMeREADTImeRndLazy- ", Long.toString(duration));
        return duration;
    }

    private long callReadeneMRndEarly(Context context, String tag) throws IOException {
        cacheCleaner(context);
        int [] bringList = new int[]{3,0,2};
        long eMeRSRndtime = System.nanoTime();
        File pathRnd = new File (context.getFilesDir(),"eneMRnd"+tag+".dat");
        RandomAccessReader eneRnd = new RandomAccessReader(pathRnd, context);
        eneRnd.read(bringList, RandomAccessReader.ReadMode.EARLY_FETCH);

        long eMeRERndtime = System.nanoTime();
        long duration = eMeRERndtime-eMeRSRndtime;
        Log.i("EnerMeREADTImeRndErly- ", Long.toString(duration));
        return duration;
    }

    private long callReadeneMIndi(Context context, String tag) throws IOException {
        cacheCleaner(context);
        long eMeRSInditime = System.nanoTime();
        File path = new File (context.getFilesDir(),"regMappedBig"+tag);

        SimpleReader regMapRIndi = new SimpleReader(path);// same path as above but not needed
        regMapRIndi.read(context,"regMappedIndi"+tag);

        long eMeREInditime = System.nanoTime();
        long duration = eMeREInditime-eMeRSInditime;
        Log.i("EnerMeREADTImeIndi   - ", Long.toString(duration));
        return duration;
    }

    private long callReadeneMBig(Context context, String tag) throws IOException {
        cacheCleaner(context);
        long eMeRStime = System.nanoTime();
        File path = new File (context.getFilesDir(),"regMappedBig"+tag);

        SimpleReader regMapR = new SimpleReader(path);

        regMapR.read();

        long eMeREtime = System.nanoTime();
        long duration = eMeREtime-eMeRStime;
        Log.i("EnerMeREADTBig       - ", Long.toString(duration));
        return duration;
    }

    String [] long2String(ArrayList timeStamps) throws IOException {



        String [] csvString = new String[timeStamps.size()];
        for(int i = 0; i<csvString.length;i++){
            csvString[i]=timeStamps.get(i).toString();
        }
        return csvString;
    }



    private void gcForcer() {
        /*
        Where needed, this method prompts GC. It is just a flag for the system to GC asap.
        may not be necessary
         */
        System.gc();
    }

    private void cacheCleaner(Context context) {


        File[] cachedFiles = context.getCacheDir().listFiles();
        for(File file: cachedFiles)file.delete();
        try {
            //gcForcer();// forces GC then goes to sleep
            Thread.sleep(0);
        } catch (InterruptedException e) {
            Log.e("Sleep attempt",e.toString());
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
   
   


}
