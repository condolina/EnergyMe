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

#define MAXBUF	8192



JNIEXPORT jbyteArray JNICALL Java_com_energymeasures_ucheudeh_energyme_SimpleReader_nativeRead(JNIEnv* env, jobject This,jstring path){

        int fd;
        jbyte buf[MAXBUF];
        int total_read = 0;
        int byte_read;
        int i =0;
        off_t file_size;
        struct stat stbuf;



        const char *path_ = (*env)->GetStringUTFChars(env,path, 0);
       if ((fd = open(path_,O_RDONLY)) < 0) {
       		printf("%s: cannot open %s\n", "Cmessenger",path);// send exeption to java
       		exit(2);
       	}
        if ((fstat(fd, &stbuf) != 0) || (!S_ISREG(stbuf.st_mode))) {
        printf("%s: cannot determine file _size %s\n", "Cmessenger",path);// send exeption to java
        exit(4);
        }

        file_size = stbuf.st_size;

        jbyteArray ret = (*env)->NewByteArray(env,file_size);


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



