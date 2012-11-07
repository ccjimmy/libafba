#include <jni.h>
#include <android/log.h>
#include "android.h"
#include <SDL.h>

extern void SDL_Android_Init(JNIEnv* env, jclass cls);
static jclass pActivityClass;
static JNIEnv* pEnv = NULL;
jmethodID showBar;
jmethodID hideBar;
jmethodID setBar;
jmethodID setError;

jmethodID JNIgetRomsPath;
jmethodID JNIgetCachePath;
jmethodID JNIgetDataPath;
const char* rom_path;
const char* data_path;
const char* cache_path;

extern int StatedLoad(int nSlot);
extern int StatedSave(int nSlot);

void Java_fr_mydedibox_libafba_sdl_SDLJni_nativeInitWithArgs(JNIEnv* env, jclass cls, jobjectArray strArray)
{
	int status, i;
	
	SDL_Android_Init(env, cls);

	pEnv = env;
	pActivityClass = (jclass)(*env)->NewGlobalRef(env,cls);

	JNIgetRomsPath = (*env)->GetStaticMethodID( env, pActivityClass, "getRomsPath","()Ljava/lang/String;" );
	jstring rompath = (jstring)(*env)->CallStaticObjectMethod( env, pActivityClass, JNIgetRomsPath );
        rom_path = (*env)->GetStringUTFChars( env, rompath, 0 );

	//JNIgetCachePath = (*env)->GetStaticMethodID( env, pActivityClass, "getCachePath","()Ljava/lang/String;" );
	//jstring cachepath = (jstring)(*env)->CallStaticObjectMethod( env, pActivityClass, JNIgetCachePath );
        //cache_path = (*env)->GetStringUTFChars( env, cachepath, 0 );

	JNIgetDataPath = (*env)->GetStaticMethodID( env, pActivityClass, "getDataPath","()Ljava/lang/String;" );
	jstring datapath = (jstring)(*env)->CallStaticObjectMethod( env, pActivityClass, JNIgetDataPath );
        data_path = (*env)->GetStringUTFChars( env, datapath, 0 );

	showBar = (*env)->GetStaticMethodID( env, pActivityClass, "showProgressBar","(Ljava/lang/String;I)V" );
	hideBar = (*env)->GetStaticMethodID( env, pActivityClass, "hideProgressBar","()V" );
	setBar = (*env)->GetStaticMethodID( env, pActivityClass,"setProgressBar","(Ljava/lang/String;I)V" );
	setError = (*env)->GetStaticMethodID( env, pActivityClass, "setErrorMessage","(Ljava/lang/String;)V" );

	jsize len = (*env)->GetArrayLength( env, strArray );
	const char *argv[len];
	argv[0] = strdup( "aFBA" );

	for( i=0; i<len; i++ )
	{
		jstring str = (jstring)(*env)->GetObjectArrayElement(env,strArray,i);
		argv[i+1] = (*env)->GetStringUTFChars( env, str, 0 );
	}

	android_pause = 0;
	android_quit = 0;
	android_fskip = 5;
	//android_fast = 0;

	status = SDL_main(i+1, (char **)argv);
}

const char *getRomsPath()
{
	return rom_path;
}

const char *getDataPath()
{
	return data_path;
}

void Java_fr_mydedibox_libafba_sdl_SDLJni_setfskip( JNIEnv *env, jobject thiz, jint n )
{
	android_fskip = n;
}


void Java_fr_mydedibox_libafba_sdl_SDLJni_emustop( JNIEnv *env, jobject thiz )
{
	android_quit = 1;
}

jint Java_fr_mydedibox_libafba_sdl_SDLJni_ispaused( JNIEnv *env, jobject thiz )
{
	return android_pause;
}

void Java_fr_mydedibox_libafba_sdl_SDLJni_pauseemu( JNIEnv *env, jobject thiz )
{
	android_pause = 1;
}

void Java_fr_mydedibox_libafba_sdl_SDLJni_resumeemu( JNIEnv *env, jobject thiz )
{
	android_pause = 0;
}

jint Java_fr_mydedibox_libafba_sdl_SDLJni_getslotnum( JNIEnv *env, jobject thiz )
{
	return 0;
}

void Java_fr_mydedibox_libafba_sdl_SDLJni_statesave( JNIEnv *env, jobject thiz, jint statenum )
{
	StatedSave( statenum );
}

void Java_fr_mydedibox_libafba_sdl_SDLJni_stateload( JNIEnv *env, jobject thiz, jint statenum )
{
	StatedLoad( statenum );
}

void Java_fr_mydedibox_libafba_sdl_SDLJni_setPadData( JNIEnv *env, jobject thiz, jint i, jlong jl )
{
	unsigned long l = (unsigned long)jl;

	android_pad_test = (l & ANDROID_TEST);
	if( android_pad_test > 0 )
		printf( "SDLJni_setPadData: test dip event" );
	android_pad_service = (l & ANDROID_SERVICE);
	if( android_pad_service > 0 )
			printf( "SDLJni_setPadData: service dip event" );
	android_pad_reset = (l & ANDROID_RESET);
	if( android_pad_reset > 0 )
			printf( "SDLJni_setPadData: reset dip event" );

	android_pad_coins = (l & ANDROID_COINS);
	android_pad_start = (l & ANDROID_START);

	android_pad_up = (l & ANDROID_UP);
	android_pad_down = (l & ANDROID_DOWN);
	android_pad_left = (l & ANDROID_LEFT);
	android_pad_right = (l & ANDROID_RIGHT);
	android_pad_1 = (l & ANDROID_1);
	android_pad_2 = (l & ANDROID_2);
	android_pad_3 = (l & ANDROID_3);
	android_pad_4 = (l & ANDROID_4);
	android_pad_5 = (l & ANDROID_5);
	android_pad_6 = (l & ANDROID_6);

}

void setErrorMsg( char *msg )
{
	if( setError )
	{
		(*pEnv)->CallStaticVoidMethod(pEnv,pActivityClass, setError, (*pEnv)->NewStringUTF(pEnv,msg) );
	}
}

void progressBarShow(char *name, int size)
{
	if(showBar)
	{
		(*pEnv)->CallStaticVoidMethod(pEnv,pActivityClass, showBar, (*pEnv)->NewStringUTF(pEnv,name), size );
	}
}

void progressBarUpdate(char *msg, int pos)
{
	if (setBar) 
	{
		(*pEnv)->CallStaticVoidMethod(pEnv, pActivityClass, setBar, (*pEnv)->NewStringUTF(pEnv,msg), pos );
	}
}

void progressBarHide(void)
{
	if(hideBar)
	{
		(*pEnv)->CallStaticVoidMethod(pEnv,pActivityClass, hideBar);
	}
}

