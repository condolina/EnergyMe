package com.energymeasures.ucheudeh.energyme;

import android.content.Context;

import java.io.File;
import java.io.IOException;

/**
 * Created by ucheudeh on 6/22/17.
 */

public class MMapRandomAccessReader extends RandomAccessReader {
    public MMapRandomAccessReader(File path, Context context) throws IOException {
        super(path, context);
    }
}
