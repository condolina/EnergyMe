package com.energymeasures.ucheudeh.energyme;

import java.io.IOException;

/**
 * Created by ucheudeh on 5/27/17.
 */

abstract class SerializeMe {
    abstract void doRead();
    abstract String doWrite(SnapshotsBasket jkl) throws IOException;

}
