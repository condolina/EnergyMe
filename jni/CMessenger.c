//
// Created by UcheUdeh on 7/22/17.
//

#include "CMessenger.h"
#include <string.h>
#include <jni.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <fcntl.h>
#include <stdlib.h>


/* fixed size buffer for c reads*/
/*#define MAXBUF	8192*/



JNIEXPORT jint JNICALL Java_com_energymeasures_ucheudeh_energyme_SimpleReader_nativeRead(JNIEnv* env, jobject This,jint fdo, jobject buf, jint len,jint offset, jint bufOffset) {

    int _fd;
    int MAXBUF;

    int total_read = 0;
    int byte_read;
    int i = 0;
    off_t file_size;
    struct stat stbuf;
    jbyte *buff_in;


    _fd = (int) fdo;

    int _offset = (int) offset;
    buff_in = (*env)->GetDirectBufferAddress(env, buf);
    int _len = (int) len;
    int _bufOffset = (int) bufOffset;


    int retAdv =0;

    //int retAdv = posix_fadvise(_fd, _offset, 0, POSIX_FADV_SEQUENTIAL);//Kernel Hint for seq read.




    if (_offset==0){
        int count = read(_fd, buff_in+_bufOffset, _len);
        //retAdv = posix_fadvise(_fd, _offset, 0, POSIX_FADV_SEQUENTIAL);//Lazy Hint for seq read.

        //retAdv = posix_fadvise(_fd, _offset, -len, POSIX_FADV_NOREUSE);
        return count;
    }else {
        int count =pread(_fd, buff_in + _bufOffset, _len, _offset);
        //retAdv = posix_fadvise(_fd, _offset, -len, POSIX_FADV_NOREUSE);
        return count;
    }
}







