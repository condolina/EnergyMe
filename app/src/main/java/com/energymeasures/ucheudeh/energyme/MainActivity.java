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
import java.util.LinkedHashMap;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = this.getApplicationContext();
        setContentView(R.layout.activity_main);
        String message = "Testing in progress. Results are being generated ";

        TextView textView = new TextView(this);
        textView.setText(message);
        setContentView(textView);


        Log.i("EnergyMe", "Commencing Data creation");

        //matrices and vectors are constructed 2exp(x)*2exp(x) entries. matrix and be manually fixed
        // On some devices making this CORE(8), BOARDER, EXTREME, INSANE may not be possible due to
        // JVM environment limitations. Minimum, Xmx1024M, XX:maxPermSize=512.
        //seed array need to generate data groups


//for read only block off from this point

        LinkedHashMap<String,int[]> dbanke = new LinkedHashMap<>();



        dbanke.put("dPreamble",new int[]{1,2,3,4});//largest matrix/vector has 256 doubles : PRE_AMBLE
        dbanke.put("dCore",new int[]{5,6,7,8});//largest matrix/vector has 65536 doubles: CORE
        //The following groups are padded with 1s, to give a uniform structure in the "big" read
        dbanke.put("dBoarder",new int[]{1,1,9,10});//largest matrix/vector has 1048576 doubles : BOARDER
        dbanke.put("dExtreme",new int[]{1,1,11,12});//largest matrix/vector has 16777216 doubles : EXTREME
        dbanke.put("dInsane",new int[]{1,1,1,13});//largest matrix/vector has 671108864 doubles : INSANE





        for (int i = 0; i<dbanke.size(); i++) {
            SnapshotsBasket numData = new SnapshotsBasket(dbanke.get(dbanke.keySet().toArray()[i]), (String)dbanke.keySet().toArray()[i]);
            try {
                experimentWrite(context,numData);

            } catch (IOException e) {
                Log.e("ExperimentWriter","IOerror at File creation" + e.toString());
            }


        }


// Block off readonly end point









        try {
            int repeats = 1; // number of read repeats
            // Tag to help identify the files and name the CVS files. verify that files exist
            String[] experimentTag = {"dPreamble","dCore","dBoarder"};// ,control which group is read with tag
            for(String tag: experimentTag)
            experimentRead(context, repeats, tag);//
            cacheCleaner(context);
        } catch (IOException e) {
            Log.e("ExperimentWrite","IOerror at File creation" + e.toString());
        }

        message = "Test now completed. Results are in the respective CSV files";
        textView.setText(message);
        setContentView(textView);





    }


    private void experimentWrite(Context context, SnapshotsBasket numData) throws IOException {
        // Run Once, create all files from the same SnapshotsBasket (Same Matrices and Arrays)
        /*
        NOTE: Write durations are not absolute. System.nanotime has a poor granularity/resolution of
        25 -30nS at best, and up to 15118nS. There is also an associated latency of nearly the same
        ranges. Depending on the hardware and OS. So best to determine granularity and latency for
        this platform. Will be done separately. Needless to mention that logs also have associated
        latencies, hence logs are elminated for the final tests.
        (ref: https://shipilev.net/blog/2014/nanotrusting-nanotime/#_granularity).
         */


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
            file = new File (context.getFilesDir(),"cvs"+tag+ System.currentTimeMillis()+".csv");}
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





            file = new File (resultDir,"CSV_"+tag+System.currentTimeMillis()+".csv");


        }
        CSVWriter results = new CSVWriter(new FileWriter(file));
        ArrayList<String> headerCan = new ArrayList<>();



        /*

        Assumption here is that it is possible to create a Reader without actually reading anything
        Time log begins when the read method is called on the Reader (INTERNAL VALIDATION)

         */
        ArrayList<Long> timeStamps;
        for (int i=0; i<repeats;i++) {

            timeStamps = new ArrayList<>();// will be written out to CVS later

            // eneM Single File Simple
            timeStamps.add(callReadeneMBig(context, tag, headerCan));

            // jSer read Single File
            timeStamps.add(callReadjSerBig(context, tag, headerCan));

            // msgP Single File
            timeStamps.add(callReadmsgPBig(context, tag, headerCan));

            //eneM MMap Single File
            timeStamps.add(callReadeneMMap(context, tag, headerCan));


            // eneM Indi
            timeStamps.addAll(callReadeneMIndi(context,tag,headerCan));

            // jSer Indi
            timeStamps.addAll(callReadjSerIndi(context,tag,headerCan));

            // msgP Indi Files
            timeStamps.addAll(callReadmsgPIndi(context,tag,headerCan));

            //eneM Random Proactive
            timeStamps.add(callReadeneMRndEarly(context, tag, headerCan));

            // eneM Random Lazy
            timeStamps.add(callReadeneMRndLate(context, tag, headerCan));

            //eneM Random with MMap - Lazy Only
            timeStamps.add(callReadeneMRndLateMmap(context, tag, headerCan));

            if (i==0){// after first iteration write CSV file headers
                String [] headers = new String [headerCan.size()];
                headers = headerCan.toArray(headers);
                results.writeNext(headers);
            }



            results.writeNext(long2String(timeStamps));

        }
        results.flush();
        results.close();
        Log.i("Energy_Measures", "Experiment Complete: Check"+file.toString()+"for the CSV files");

    }




    private ArrayList<Long> callReadmsgPIndi(Context context, String tag, ArrayList<String> headerCan) throws IOException {
        cacheCleaner(context);
        //long starttime = System.nanoTime();
        File path = new File (context.getFilesDir(),"MsgPBasisBig"+tag+".dat");// Ignored in actual method

        MsgPReaderIndiFile msgPIndi = new MsgPReaderIndiFile(path);

        // we are reading 4 matrices and 4 vectors files, we add the headers for CVS. First m then v

        // timing for each file read happens in the actual reader (read() method)
        for (int i = 0; i<4; i++){
            headerCan.add("MsgPIndi"+tag+"m"+Integer.toString(i));//add matrix header
        }
        for (int i = 0; i<4; i++){
            headerCan.add("MsgPIndi"+tag+"v"+Integer.toString(i));//add vector header
        }

        // long endtime = System.nanoTime();
        //long duration = endtime-starttime;
        //Log.i("msgPIndiFile         - ",Long.toString(duration));
        return msgPIndi.read(context,"MsgPIndi"+tag);

    }

    private long callReadmsgPBig(Context context, String tag, ArrayList<String> headerCan) throws IOException {
        headerCan.add("msgPBigRead");
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

    private ArrayList<Long> callReadjSerIndi(Context context, String tag, ArrayList<String> headerCan) throws IOException {
        cacheCleaner(context);
        //long starttime = System.nanoTime();
        File path = new File (context.getFilesDir(),"jSerBig"+tag+".dat");// Ignored in actual method

        JReaderIndiFile jSerRIndi = new JReaderIndiFile(path);

        // we are reading 4 matrices and 4 vectors files, we add the headers for CVS. First m then v

        // timing for each file read happens in the actual reader (read() method)
        for (int i = 0; i<4; i++){
            headerCan.add("jSerIndi"+tag+"m"+Integer.toString(i));//add matrix header
        }
        for (int i = 0; i<4; i++){
            headerCan.add("jSerIndi"+tag+"v"+Integer.toString(i));//add vector header
        }

        //long endtime = System.nanoTime();
        //long duration = endtime-starttime;
        //Log.i("jSerIndiFile         - ", Long.toString(duration));
        return jSerRIndi.read(context,"jSerIndi"+tag);

    }

    private long callReadjSerBig(Context context, String tag, ArrayList<String> headerCan) throws IOException {
        cacheCleaner(context);
        headerCan.add("jSerBigFile");
        long starttime = System.nanoTime();
        File path = new File (context.getFilesDir(),"jSerBig"+tag+".dat");

        JReaderSingle jSerR = new JReaderSingle(path);

        jSerR.read();

        long endtime = System.nanoTime();
        long duration = endtime-starttime;
        Log.i("jSerBigFile          - ", Long.toString(duration));
        return duration;


    }

    private long callReadeneMMap(Context context, String tag, ArrayList<String> headerCan) throws IOException {
        headerCan.add("EnerMeRMmapBigFile");
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

    private long callReadeneMRndLate(Context context, String tag, ArrayList<String> headerCan) throws IOException {
        /*
        The Random access read will fetch 3 matrix from the file to the application heap.
        Read can be called several times on the reader object for this RNDaccesReader. I think this
        is a better use case than asking for all records in the file.
         */
        headerCan.add("eneM_Rnd_Lazy");
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

    private long callReadeneMRndEarly(Context context, String tag, ArrayList<String> headerCan) throws IOException {
        headerCan.add("eneM_Rnd_Proactv");
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

    private long callReadeneMRndLateMmap(Context context, String tag, ArrayList<String> headerCan) throws IOException {
        /*
        The Random access read will fetch 3 matrix from the file to the application heap.
        Read can be called several times on the reader object for this RNDaccesReader. I think this
        is a better use case than asking for all records in the file.
         */
        headerCan.add("eneM_Mmap_Rnd_Lazy");
        cacheCleaner(context);
        int [] bringList = new int[]{3,0,2};
        long eMeRSRndMmaptime = System.nanoTime();
        File pathRnd = new File (context.getFilesDir(),"eneMRnd"+tag+".dat");
        MMapRandomAccessReader eneRndLateMmap = new MMapRandomAccessReader(pathRnd,context);
        eneRndLateMmap.read(bringList);

        //Log.i("Element Val -",Double.toString(eneRnd.vectorTable.get(1).getEntry(20)));
        long eMeRERndMmaptime = System.nanoTime();
        long duration = eMeRERndMmaptime-eMeRSRndMmaptime;
        Log.i("EnerMeREADTImeRndMMAP- ", Long.toString(duration));
        return duration;
    }

    private ArrayList<Long> callReadeneMIndi(Context context, String tag, ArrayList<String> headerCan) throws IOException {
        cacheCleaner(context);
        //long eMeRSInditime = System.nanoTime();
        File path = new File (context.getFilesDir(),"regMappedBig"+tag);

        SimpleReader regMapRIndi = new SimpleReader(path);// same path as above but not needed
        // we are reading 4 matrices and 4 vectors files, we add the headers for CVS. First m then v

        // timing for each file read happens in the actual reader (read() method)
        for (int i = 0; i<4; i++){
            headerCan.add("regMappedIndi"+tag+"m"+Integer.toString(i));//add matrix header
        }
        for (int i = 0; i<4; i++){
            headerCan.add("regMappedIndi"+tag+"v"+Integer.toString(i));//add vector header
        }

        //long eMeREInditime = System.nanoTime();
       // long duration = eMeREInditime-eMeRSInditime;
        //Log.i("EnerMeREADTImeIndi   - ", Long.toString(duration));
        return regMapRIndi.read(context,"regMappedIndi"+tag);
    }

    private long callReadeneMBig(Context context, String tag, ArrayList<String> headerCan) throws IOException {
        cacheCleaner(context);
        headerCan.add("EnerMeREADTBig");
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
        int i=0;
        for(File file: cachedFiles) {
            if (file.delete())
                i++;
        }
        Log.d("AppCache:deleted files ", Integer.toString(i));
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
        return Environment.MEDIA_MOUNTED.equals(state);
    }
   
   


}
