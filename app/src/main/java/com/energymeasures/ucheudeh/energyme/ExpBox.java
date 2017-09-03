package com.energymeasures.ucheudeh.energyme;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ucheudeh on 8/20/17.
 */

class ExpBox {
    ArrayList<Long> timeStamps;
    List<String[]> replicate;
    List<String> headers;

    public ExpBox(ArrayList<Long> timeStamps, List<String[]> replicate,List<String> headers) {
        this.timeStamps = timeStamps;
        this.replicate = replicate;
        this.headers = headers;
    }
}
