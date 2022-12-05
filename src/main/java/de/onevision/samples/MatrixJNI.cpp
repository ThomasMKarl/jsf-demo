#include "MatrixJNI.h"

#include "hello.hpp"


JNIEXPORT jstring JNICALL Java_MatrixJNI_cat(JNIEnv * env, jobject thisObject, jobjectArray toCat, jstring delim)
{
  const char *delim_c = (*env)->GetStringUTFChars(env, delim, NULL);
   
  jsize number = (*env)->GetArrayLength(env, toCat);
  for (int i = 0; i < length; i++)
  {
    jobject objString = (*env)->GetObjectArrayElement(env, toCat, i);
  }

  (*env)->ReleaseStringUTFChars(env, delim, delim_c);
}

JNIEXPORT void JNICALL Java_MatrixJNI_sayHello(JNIEnv *, jobject)
{
  hello();
}
