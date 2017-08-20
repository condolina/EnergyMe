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
    /*  const char *path_ = (*env)->GetStringUTFChars(env,path, 0);
     if ((fd = open(path_,O_RDONLY)) < 0) {
             printf("%s: cannot open %s\n", "Cmessenger",path);// send exeption to java
             exit(2);
         }
      if ((fstat(fd, &stbuf) != 0) || (!S_ISREG(stbuf.st_mode))) {
      printf("%s: cannot determine file _size %s\n", "Cmessenger",path);// send exeption to java
      exit(4);
      }


*/

/*
    int retAdv = posix_fadvise(_fd, _offset, -len, POSIX_FADV_SEQUENTIAL);
    if (retAdv == -1) {
        printf("posix_fadvise");
    }
*/
    if (_offset==0){
        int count = read(_fd, buff_in+_bufOffset, _len);
        return count;
    }else {
        int count =pread(_fd, buff_in + _bufOffset, _len, _offset);
        return count;
    }
}
       /* file_size = stbuf.st_size;

        jbyteArray ret = (*env)->NewByteArray(env,file_size);

         /*set read buffer size here

        MAXBUF = 2048;

        /*MAXBUF = file_size;

        jbyte buf[MAXBUF];

        while ( (byte_read = read(fd,buf,MAXBUF)) > 0 ){


       		    (*env)->SetByteArrayRegion(env,ret, (i*MAXBUF), byte_read, buf);//seting byte array region
                i++;
                total_read+=byte_read;

       		}
        if (total_read != file_size){
        printf("%s: Read/Filesize mismatch %s\n", "Cmessenger",path);// send exeption to java
                exit(4);
        }

       	if (close(fd) < 0) {
       		printf("%s: cannot close %s\n", "Cmessenger",path);//send exception to Java
       		exit(3);
       	}
       	return ret;
       }

*/






