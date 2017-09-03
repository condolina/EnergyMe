package com.energymeasures.ucheudeh.energyme;


import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;


import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;

import java.io.*;
import java.util.ArrayList;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    String [] exArr = new String[]{"A","B","C","D","E","F","G","H"}; // modifiy experiment name here to give propername csv file
    String ex = "L";
    File resultDir;
    File experimentDir;
    File processedDataDir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = this.getApplicationContext();
        setContentView(R.layout.activity_main);
        String message = "Testing in progress. Results are being generated ";
        TextView textView = new TextView(this);
        textView.setText(message);
        setContentView(textView);
        Log.i("EnergyMe", message);

        try {
            //cleanSampleFolder(context); qucik and dirty maintenance method
            prepareStorageRights();
            setupExperiment(context); //READ experiments starts Here!!
        } catch (IOException e) {
            Log.e("Experiment Setup","unable to read SetupFile");
        }
        processResultFiles();

        cleanUpFolders();
        cleanUpExpFolders();

       // Run some unitilities to find page size and block size


       // find page Size and block size

/*
        File path = Environment.getDataDirectory();
        android.os.StatFs stat = new android.os.StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();det
        message = Long.toString(blockSize);

*/


        //matrices and vectors are constructed 2exp(x)*2exp(x) entries. matrix and be manually fixed
        // On some devices making this CORE(8), BOARDER, EXTREME, INSANE may not be possible due to
        // JVM environment limitations. Minimum, Xmx1024M, XX:maxPermSize=512.
        //seed array need to generate data groups


//for read only block off from this point

        LinkedHashMap<String,int[]> dbanke = new LinkedHashMap<>();

/*


        dbanke.put("dPreamble",new int[]{10,10,10,10});//{1,2,3,4}largest matrix/vector has 256 doubles : PRE_AMBLE
       // dbanke.put("dCore",new int[]{5,6,7,8});//largest matrix/vector has 65,536 doubles: CORE
        //The following groups are padded with 1s, to give a uniform structure in the "big" read
       // dbanke.put("dBoarder",new int[]{1,1,9,10});//largest matrix/vector has 1,048,576 doubles : BOARDER
      //  dbanke.put("dExtreme",new int[]{1,1,11,12});//largest matrix/vector has 16,777,216 doubles : EXTREME
       // dbanke.put("dInsane",new int[]{1,1,1,13});//largest matrix/vector has 671,108,864 doubles : INSANE





        for (int i = 0; i<dbanke.size(); i++) {
            SnapshotsBasket numData = new SnapshotsBasket(dbanke.get(dbanke.keySet().toArray()[i]), (String)dbanke.keySet().toArray()[i]);
            try {
                experimentWrite(context,numData);

            } catch (IOException e) {
                Log.e("ExperimentWriter","IOerror at File creation" + e.toString());
            }


        }


// Block off readonly end point


*/


    message = "Test now completed. Results are in the respective CSV files";
      textView.setText(message);
      setContentView(textView);







    }

    private void cleanSampleFolder(Context context) {
        File[] cachedFiles = context.getFilesDir().listFiles();
        int i=0;
        for(File file: cachedFiles) {

            if (file.delete())
                i++;
        }
        Log.d("AppFileDir:deleted", Integer.toString(i));
    }

    private void cleanUpExpFolders() {
        File[] rawFiles = experimentDir.listFiles();

        int i=0;
        for(File file: rawFiles) {
            if (file.delete())
                i++;

        }
    }

    private void cleanUpFolders() {
        File[] rawFiles = resultDir.listFiles();

        int i=0;
        for(File file: rawFiles) {
            if (file.delete())
                i++;

    }

    }

    private void processResultFiles()  {
        String resultName = null;
        File[] resultsFiles = resultDir.listFiles();
        List <String> header = new ArrayList<>();
        List<String []> latencies = new ArrayList<>();
        //List <String> Q = new ArrayList<>();


        for (File file : resultsFiles) { // will read all result files

            CSVReader readerRes = null;// csv pls. will handle err later
            try {
                readerRes = new CSVReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                Log.e("File Error", "Cannot find File CVS ExpResult");
            }

            List<String[]> results = null;// First row header next matching latency
            try {
                assert readerRes != null;
                results = readerRes.readAll();
            } catch (IOException e) {
                Log.e("File Read fail : ","cvs ExpResults");
            }
            assert results != null;
            String [] localHead = results.get(0);
            String [] localLantency = results.get(1);


            for(String s : localHead){
                if(!header.contains(s)){
                    header.add(s); // grows result header
                    //Q.add("0"); // growing Q to march header
                }
            }
            String[] Q = new String[header.size()];
            int headMatch = 0;
            for(int i=0;i<localLantency.length;i++) {




                    int k = 0;
                    boolean b = true;
                    while (b & k < header.size()) {
                        if (localHead[i].equals(header.get(k))) {
                            Q[k] = localLantency[i];
                            b = false;
                            headMatch++;
                        }
                        k++;
                    }


                if (headMatch == Q.length) {
                    latencies.add(Q.clone());
                    headMatch=0;

                    for (int p = 0; p < Q.length; p++) { //resets the  elements in Q to "0"
                        Q[p] = "0";
                    }
                }
            }



        }
        List<String []> fullResults = new ArrayList<>();
        resultName= header.get(0).substring(1,3);
        String [] eH = new String[header.size()];
        fullResults.add(header.toArray(eH));
        fullResults.addAll(latencies);
        File file = new File (processedDataDir,resultName+"Processed_"+System.currentTimeMillis()+".csv");
        writeCvs2file(file, fullResults);

    }

    private void prepareStorageRights() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        // prepare to save file in SDCard
        File nestedFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        nestedFolder.mkdirs();
        if (!nestedFolder.isDirectory()) {
            Log.e("Document Directory__:", "Directory not created");
        }
        resultDir= new File(nestedFolder, "EnergymeasuresCSVs");
        resultDir.mkdirs();
        if (!resultDir.isDirectory()) {
            Log.e("Document Directory__:", "Directory not created");
        }

        experimentDir= new File(nestedFolder, "ExperimentSetups");
        experimentDir.mkdirs();
        if (!experimentDir.isDirectory()) {
            Log.e("Document Directory__:", "Directory not created_ExperimentSetup");
        }
        processedDataDir= new File(nestedFolder, "ProcessedData");
        processedDataDir.mkdirs();
        if (!processedDataDir.isDirectory()) {
            Log.e("Document Directory__:", "Directory not created_Processeddata");
        }


    }


    private void setupExperiment(Context context) throws IOException {
        File[] exPerimentFiles = experimentDir.listFiles();//will handle later


        for(File file: exPerimentFiles) { // for ease of use put just one file in  "ExperimentSetups"

            CSVReader expReader = new CSVReader(new FileReader(file));// csv pls. to handle err later
            String [] nextExp;
            List<String[]> expRuns = expReader.readAll();// list to hold this experiment runs
                expReader.close(); // close reader and underlaying file
            callExperiments(file,context,expRuns);







            }
        }

    private void callExperiments(File file, Context context, List<String[]> expRuns) {

// SET 2: Set runsize
        int runSize = getRunSize(expRuns); // number of experiment before restart (App or Phone)



        for (int i=0;i<expRuns.size();i+=runSize) { // |indicator|Filelabelheader|DirectBuf|Buffsize|Native or Mode|
            if ((expRuns.get(i)[0]).equals("1")) {// first column tells if this experiment had been run before
                // first hit means we continue runnign experiment from hear after resTART
                // we take the next runSize experiments
                int start = i;
                ex=Integer.toString(i/runSize); //sets file labelHeader as batch of runSize from 0,1,2...
                List<String[]> replicate = new ArrayList<String[]>();
                for(int k =0;k<runSize;k++){
                    replicate.add(expRuns.get(i+k)); // add the next runSize runs to replicate list
                }

                replicate = experiment(context,replicate); // new returned replicate
                for(int k =0;k<runSize;k++){
                    expRuns.get(start+k)[0] = replicate.get(k)[0]; // update masterlist from new replicate
                }

                writeCvs2file(file, expRuns);
                // Force application Restart Here
                //System.exit(0);
                //restartApplication(context);
                // When application restarts it perform remaining replicate one by one with restarts
            }


        }

    }

    private int getRunSize(List<String[]> expRuns) {
        String s = expRuns.get(0)[4];
        int count = 0;
        while (count<expRuns.size()){

            if (expRuns.get(count)[4].equals(s)&count!=0){
                break;
            }
            count++;
        }
        return count;

    }

    private void restartApplication(Context context) {
        Intent mStartActivity = new Intent(context, MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    private void writeCvs2file(File file, List<String[]> expRuns) {
        try {
            CSVWriter updateExp = new CSVWriter(new FileWriter(file));
            updateExp.writeAll(expRuns);
            updateExp.flush();
            updateExp.close(); // file updated and ready to be used after restart
        } catch (IOException e) {
            Log.e("Error ","Setup CSV file not found");
        }
    }

    private List<String[]>  experiment(Context context, List<String[]> replicate) {
        try {
            int repeats = 1; // number of read repeats
            // Tag to help identify the files and name the CVS files. verify that files exist

            String[] experimentTag = {" test"};//,"dCore","dBoarder","dBoarder" ,control which group is read with tag
            for(String tag: experimentTag)

                replicate = experimentRead(context, repeats, tag, replicate);//
            //cacheCleaner(context);
            //System.gc();
            // try {
            //Thread.sleep(100);


        } catch (IOException e) {
            Log.e("ExperimentWrite","IOerror at File creation" + e.toString());

        }

        return replicate;


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
/*

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


*/


    }

    private List<String[]>  experimentRead(Context context, int repeats, String tag, List<String[]> replicate) throws IOException {
        /*
        Result file handle, will be a CSV with corresponding name. An attempt will be made save the
        files in the SDCard in a folder named "EnergymeasuresCSVs", otherwise is is saved in the
        application files diretory on the internal Flash drive. Files will have a timestamp appended
        to the name.


         */


        File file;







        if(!isExternalStorageWritable()){
            file = new File (context.getFilesDir(),"cvs"+tag+ System.currentTimeMillis()+".csv");}
        else{






            file = new File (resultDir,ex+"CSV_"+tag+System.currentTimeMillis()+".csv");


        }
        CSVWriter results = new CSVWriter(new FileWriter(file));
        ArrayList<String> headerCan = new ArrayList<>();



        /*

        Assumption here is that it is possible to create a Reader without actually reading anything
        Time log begins when the read method is called on the Reader (INTERNAL VALIDATION)

         */
        //ArrayList<Long> timeStamps;
        ExpBox exBox = null;
        for (int i=0; i<repeats;i++) {
            ArrayList<Long> timeStamps = new ArrayList<>();// will be written out to CVS later


            //timeStamps = new ArrayList<>();// will be written out to CVS later
/*
            //eneM Single File (Contains matrices and vectors)
             exBox = callReadeneMBig(context,tag,headerCan,replicate);
            timeStamps.addAll(exBox.timeStamps);
/*
            // jSer read Single File
            timeStamps.add(callReadjSerBig(context, tag, headerCan));

            // msgP Single File
            timeStamps.add(callReadmsgPBig(context, tag, headerCan));

            //eneM MMap Single File
            timeStamps.add(callReadeneMMap(context, tag, headerCan));
*/
            // msgP Indi Files
           // timeStamps.addAll(callReadmsgPIndi(context,tag,headerCan));

            // eneM Indi
           //
            exBox = callReadeneMIndi(context,tag,headerCan,replicate);
            timeStamps.addAll(exBox.timeStamps);

            // jSer Indi
            //timeStamps.addAll(callReadjSerIndi(context,tag,headerCan));

            //mmapIndi
          //  exBox = callReadMMapIndi(context,tag,headerCan,replicate);
           // timeStamps.addAll(exBox.timeStamps);



            //eneM Random Proactive
            //timeStamps.add(callReadeneMRndEarly(context, tag, headerCan));
          //  exBox = callReadeneMRndEarly(context,tag,headerCan,replicate);
           // timeStamps.addAll(exBox.timeStamps);

   /*         // eneM Random Lazy
            timeStamps.add(callReadeneMRndLate(context, tag, headerCan));

            //eneM Random with MMap - Lazy Only
            timeStamps.add(callReadeneMRndLateMmap(context, tag, headerCan));
*/
            if (i==0){// after first iteration write CSV file headers
                String [] headers = new String [headerCan.size()];
                headers = headerCan.toArray(headers);
                results.writeNext(headers);
            }

/*
            for(String s: long2String(timeStamps)){
                results.writeNext(new String[]{s});
            }
*/


            results.writeNext(long2String(timeStamps));

        }
        //results.writeNext(long2String(timeStamps));
        results.flush();
        results.close();
        Log.i("Energy_Measures", "Experiment Complete: Check"+file.toString()+"for the CSV files");

        return exBox.replicate;

    }




    private ArrayList<Long> callReadmsgPIndi(Context context, String tag, ArrayList<String> headerCan) throws IOException {
        //cacheCleaner(context);
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
        //cacheCleaner(context);
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
        //cacheCleaner(context);
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
        //cacheCleaner(context);
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
        headerCan.add(ex+"EnerMeRMmapBigFile");
        //cacheCleaner(context);
        long eMeRStime = System.nanoTime();
        File path = new File (context.getFilesDir(),"regMappedBig"+tag);

        MappedReader regMapR = new MappedReader(path);

        regMapR.read(context, "2kExpD_10_");

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
        headerCan.add(ex+"eneM_Rnd_Lazy");
        //cacheCleaner(context);
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

    private ExpBox callReadeneMRndEarly(Context context, String tag, ArrayList<String> headerCan, List<String[]> replicate) throws IOException {
        headerCan.add("apache addtime ");


        //cacheCleaner(context);

        ArrayList<Long> durations = new ArrayList<>();
        ArrayList<String> headers = new ArrayList<>();




        //timeStamps.add(System.nanoTime());

                int [] bringList = new int[]{3,1,2};



        long eMeRSRndtime = System.nanoTime();
        File pathRnd = new File (context.getFilesDir(),"eneMRnd"+tag+".dat");
        RandomAccessReader eneRnd = new RandomAccessReader(pathRnd, context);
        eneRnd.read(bringList, RandomAccessReader.ReadMode.EARLY_FETCH);
        long eMeRERndtime = System.nanoTime();
        Array2DRowRealMatrix adder = eneRnd.getFirstMatrix();
        Array2DRowRealMatrix sing = eneRnd.getFirstMatrix();
        Array2DRowRealMatrix res = adder.add(sing);


        long duration = eMeRERndtime-eMeRSRndtime;
        Log.i("Addition time Apache- ", Long.toString(duration));
        ExpBox exBox =null; // remove this line
        return exBox;
    }

    private long callReadeneMRndLateMmap(Context context, String tag, ArrayList<String> headerCan) throws IOException {
        /*
        The Random access read will fetch 3 matrix from the file to the application heap.
        Read can be called several times on the reader object for this RNDaccesReader. I think this
        is a better use case than asking for all records in the file.
         */
        headerCan.add(ex+"eneM_Mmap_Rnd_Lazy");
        //cacheCleaner(context);
        int [] bringList = new int[]{6,3,2};
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

    private ExpBox callReadeneMIndi(Context context, String tag, ArrayList<String> headerCan, List<String[]> replicate) throws IOException {
        //cacheCleaner(context);
        //long eMeRSInditime = System.nanoTime();
        boolean clustering = false;
        File path = new File (context.getFilesDir(),"regMappedIndidCorem3.dat");

        SimpleReader regMapRIndi = new SimpleReader(path);// same path as above but not needed
        // we are reading 4 matrices and 4 vectors files, we add the headers for CVS. First m then v

        // timing for each file read happens in the actual reader (read() method)
        /*
        for (int i = 0; i<4; i++){
            headerCan.add(ex+"_"+tag+"m"+Integer.toString(i));//add matrix header
        }
        for (int i = 0; i<4; i++){
            headerCan.add(ex+"regMappedIndi"+tag+"v"+Integer.toString(i));//add vector header
        }
*/

       // long duration = eMeREInditime-eMeRSInditime;
        ExpBox exBox = null;
       // return regMapRIndi.read(context,"regMappedIndi"+tag);
        //ExpBox exBox = regMapRIndi.read(context,"2kExpD",replicate,clustering);

        long eMeRSInditime = System.nanoTime();
        regMapRIndi.read(context,"regMappedIndidCorem3");
        long eMeREInditime = System.nanoTime();
        long duration = eMeREInditime-eMeRSInditime;
        Log.i("Addition time Apache- ", Long.toString(duration));

       Array2DRowRealMatrix  mat = regMapRIndi.matriceTable.get(0);
        Array2DRowRealMatrix me = new Array2DRowRealMatrix(mat.getData().clone());


        Array2DRowRealMatrix uli = mat.add(me);

        headerCan.addAll(exBox.headers);
        return exBox;
    }

    private ExpBox callReadMMapIndi(Context context, String tag, ArrayList<String> headerCan, List<String[]> replicate) throws IOException {
        //cacheCleaner(context);
        //long eMeRSInditime = System.nanoTime();
        File path = new File (context.getFilesDir(),"regMappedBig"+tag);

        MappedReader mmapRIndi = new MappedReader(path);// same path as above but not needed
        // we are reading 4 matrices and 4 vectors files, we add the headers for CVS. First m then v
/*
        // timing for each file read happens in the actual reader (read() method)
        for (int i = 0; i<4; i++){
            headerCan.add(ex+"MMappedIndi"+tag+"m"+Integer.toString(i));//add matrix header
        }
        for (int i = 0; i<4; i++){
            headerCan.add(ex+"MMappedIndi"+tag+"v"+Integer.toString(i));//add vector header
        }
*/
        //long eMeREInditime = System.nanoTime();
        // long duration = eMeREInditime-eMeRSInditime;
        //Log.i("EnerMeREADTImeIndi   - ", Long.toString(duration));
        ExpBox exBox = mmapRIndi.read(context,"regMappedIndidCore",replicate); //ReadMode for mmap ="0"
        headerCan.addAll(exBox.headers);
        return exBox;
    }

    private ExpBox callReadeneMBig(Context context, String tag, ArrayList<String> headerCan, List<String[]> replicate) throws IOException {
        //cacheCleaner(context);
        boolean clustering = true;
        headerCan.add(ex+"EnerMeREADTBig");
        long eMeRStime = System.nanoTime();
       // File path = new File (context.getFilesDir(),"regMappedBig"+tag);
        File path = new File (context.getFilesDir(),"BulkBig"+tag);

        SimpleReader regMapR = new SimpleReader(path);
        ExpBox exBox = regMapR.read(context,"BulkBig",replicate, clustering);
        headerCan.addAll(exBox.headers);
        return exBox;

        //regMapR.read(context, tag);

        //long eMeREtime = System.nanoTime();
        //long duration = eMeREtime-eMeRStime;
        //Log.i("EnerMeREADTBig       - ", Long.toString(duration));

        //return duration;
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

    public void exit()
    {

        android.os.Process.killProcess(android.os.Process.myPid());
        //System.exit(0);
    }


}
