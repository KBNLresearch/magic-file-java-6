/*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

#include "nl_kb_magicfile_MagicFile.h"
#include <iostream>
#include <string.h>
#include <stdlib.h>
#include <magic.h>

using namespace std;

magic_t initMagic(int flags) {
	magic_t checker = magic_open(flags);
	magic_load(checker, NULL);
	return checker;
}

char* checkFile(int flags, const char *path) {
	magic_t checker = initMagic(flags);
	static char ret[1024];
	strcpy(ret, magic_file(checker, path));
	magic_close(checker);
	return ret;
}

char* checkStream(int flags, void *bytes, size_t len) {
	magic_t checker = initMagic(flags);
	static char ret[1024];
	strcpy(ret, magic_buffer(checker, bytes, len));
	magic_close(checker);
	free(bytes);
	return ret;
}

int castBytes(JNIEnv *env, jbyteArray bytes, void* &pointer) {
	if(bytes == NULL)
		return -1;
	jint len = env->GetArrayLength(bytes);
	pointer = (void*)malloc(len+1);
	env->GetByteArrayRegion(bytes, 0, len, (jbyte*)pointer);
	return (int)len;
}

/*
 * Class:     MagicFile
 * Method:    checkText
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_nl_kb_magicfile_MagicFile_checkText(JNIEnv *env, jclass cl, jstring path) {
	return env->NewStringUTF(checkFile(MAGIC_NONE, env->GetStringUTFChars(path, 0)));
}

/*
 * Class:     MagicFile
 * Method:    checkMime
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_nl_kb_magicfile_MagicFile_checkMime(JNIEnv *env, jclass cl, jstring path) { 
	return env->NewStringUTF(checkFile(MAGIC_MIME_TYPE, env->GetStringUTFChars(path, 0)));
}

/*
 * Class:     MagicFile
 * Method:    checkEncoding
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_nl_kb_magicfile_MagicFile_checkEncoding(JNIEnv *env, jclass cl, jstring path) { 
	return env->NewStringUTF(checkFile(MAGIC_MIME_ENCODING, env->GetStringUTFChars(path, 0)));
}

/*
 * Class:     MagicFile
 * Method:    checkTextStream
 * Signature: ([B)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_nl_kb_magicfile_MagicFile_checkTextStream(JNIEnv *env, jclass cl, jbyteArray bytes) {
	void* pointer = NULL;
	int len = castBytes(env, bytes, pointer);
	if(len < 0)
		return env->NewStringUTF("fail");
	return env->NewStringUTF(checkStream(MAGIC_NONE, pointer, (size_t)len));
}

/*
 * Class:     MagicFile
 * Method:    checkMimeStream
 * Signature: ([B)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_nl_kb_magicfile_MagicFile_checkMimeStream(JNIEnv *env, jclass cl, jbyteArray bytes) {
	void* pointer = NULL;
	int len = castBytes(env, bytes, pointer);
	if(len < 0)
		return env->NewStringUTF("fail");
	return env->NewStringUTF(checkStream(MAGIC_MIME_TYPE, pointer, (size_t)len));
}

/*
 * Class:     MagicFile
 * Method:    checkEncodingStream
 * Signature: ([B)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_nl_kb_magicfile_MagicFile_checkEncodingStream(JNIEnv *env, jclass cl, jbyteArray bytes) {
	void* pointer = NULL;
	int len = castBytes(env, bytes, pointer);
	if(len < 0)
		return env->NewStringUTF("fail");
	return env->NewStringUTF(checkStream(MAGIC_MIME_ENCODING, pointer, (size_t)len));
}


int main() { }
