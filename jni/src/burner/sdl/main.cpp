/*----------------
Stuff to finish:

It wouldn't be a stretch of the imagination to think the whole of the sdl 'port' needs a redo but here are the main things wrong with this version:


There is OSD of any kind which makes it hard to display info to the users.
There are lots of problems with the audio output code.
There are lots of problems with the opengl renderer
probably many other things.
------------------*/
#include "burner.h"

#ifdef ANDROID 
//#include "android.h"
extern "C" char android_img_state_path[2048];
extern "C" int android_pause;
extern "C" int android_quit;
extern "C" const char *getCachePath();
extern "C" const char *getRomsPath();
extern "C" const char *getDataPath();

TCHAR* GetIsoPath() { return NULL; }
INT32 CDEmuInit() { return 0; }
INT32 CDEmuExit() { return 0; }
INT32 CDEmuStop() { return 0; }
INT32 CDEmuPlay(UINT8 M, UINT8 S, UINT8 F) { return 0; }
INT32 CDEmuLoadSector(INT32 LBA, char* pBuffer) { return 0; }
UINT8* CDEmuReadTOC(INT32 track) { return 0; }
UINT8* CDEmuReadQChannel() { return 0; }
INT32 CDEmuGetSoundBuffer(INT16* buffer, INT32 samples) { return 0; }
CDEmuStatusValue CDEmuStatus;
bool bDoIpsPatch;
void IpsApplyPatches(UINT8 *, char *) {}
TCHAR szAppHiscorePath[MAX_PATH] = "highscores";
TCHAR szAppSamplesPath[MAX_PATH] = "samples";
TCHAR szAppCheatsPath[MAX_PATH] = "cheats";
void Reinitialise(void) {}
void NeoCDInfo_Exit() {}
bool bCDEmuOkay = false;
void wav_pause(bool bResume){}
#endif

int nAppVirtualFps = 6000;			// App fps * 100
bool bRunPause=0;
bool bAlwaysProcessKeyboardInput=0;


void init_emu(int gamenum)
{
	bBurnUseASMCPUEmulation=0;
 	bCheatsAllowed=false;
	ConfigAppLoad();
	ConfigAppSave();
	DrvInit(gamenum,0);
}

void CheckFirstTime()
{

}

void ProcessCommandLine(int argc, char *argv[])
{

}

void write_gamelist_sdcard()
{
	printf( "START: write_gamelist_sdcard()" );
	char buffer[1024];

	FILE* file = fopen( "/sdcard/aFBA-gamelist.txt", "w+" );
	if( file == NULL )
	{
		printf( "ERROR: could not create \"/sdcard/aFBA-gamelist.txt\"" );
		exit( 0 );
	}

	fputs( "package fr.mydedibox.afba;\n", file );
	fputs( "public class Compatibility\n", file );
	fputs( "{\n", file );
	fputs( "\tpublic ArrayList<RomInfo> list = new ArrayList<RomInfo>();\n\n", file );


	int i, listCount = 0;

	sprintf( buffer, "\tprivate void AddList%i()\n\t{\n", listCount );
	fputs( buffer, file );

	for (i = 0; i < nBurnDrvCount; i++)
	{
		nBurnDrvSelect[0] = i;
		nBurnDrvActive = i;

		int vertical = 0;
		int w, h;
		BurnDrvGetVisibleSize( &w, &h );
		if( BurnDrvGetFlags() & BDF_ORIENTATION_VERTICAL )
		{
			vertical = 1;
			int n = w;
			w = h;
			h = n;
		}

		int buttons = 0;
		for ( UINT32 j = 0; j < 0x1000; j++ )
		{
			struct BurnInputInfo bii;
			INT32 nRet = BurnDrvGetInputInfo( &bii, j );
			if ( nRet )
				break;
			else
			{
				if( strstr( bii.szInfo, "p1 fire" ) != NULL )
					buttons++;
			}
		}

		if( i!=0 && !(i % 512) ) // multiple of 512, create a new function to prevent java function overload
		{
			listCount++;
			sprintf( buffer, "\t}\n\tprivate void AddList%i()\n{\t\n", listCount );
			fputs( buffer, file );
		}

		sprintf( buffer, "\t\tlist.add( new RomInfo( \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", %i, %i, %i, %i, %i ) );\n",
				BurnDrvGetTextA( DRV_FULLNAME ),
				BurnDrvGetTextA( DRV_NAME ),
				BurnDrvGetTextA( DRV_PARENT ),
				BurnDrvGetTextA( DRV_COMMENT ),
				BurnDrvGetTextA( DRV_MANUFACTURER ),
				BurnDrvGetTextA( DRV_SYSTEM ),
				BurnDrvGetTextA( DRV_DATE ),
				BurnDrvIsWorking(),
				buttons,
				w, h, vertical );
		fputs( buffer, file );
	}

	sprintf( buffer, "\t}\n" );
	fputs( buffer, file );

	fputs( "\n\tpublic Compatibility()\n", file );
	fputs( "\t{", file );
	for( i=0; i<listCount+1; i++)
	{
		sprintf( buffer, "\t\tAddList%i();\n", i );
		fputs( buffer, file );
	}
	fputs( "\t}", file );

	fflush(file);
	fclose(file);
	
	printf( "END: write_gamelist_sdcard()" );

	exit(0);
}

int main(int argc, char *argv[]) 
{
	UINT32 i=0;
	
#ifdef ANDROID
	snprintf( szAppRomPaths[0], MAX_PATH, "%s/", getRomsPath() );
	printf( "szAppRomPaths[0]: %s", szAppRomPaths[0] );
#endif
	ConfigAppLoad(); 
	
	CheckFirstTime(); // check for first time run
	
	SDL_Init(SDL_INIT_TIMER|SDL_INIT_VIDEO);

	BurnLibInit(); 

#ifdef ANDROID
	//write_gamelist_sdcard();
#endif

	SDL_WM_SetCaption( "FBA, SDL port.", "FBA, SDL port.");
	SDL_ShowCursor(SDL_DISABLE);

	if (argc == 2)
	{
		for (i = 0; i < nBurnDrvCount; i++) {
			nBurnDrvSelect[0] = i;
#ifdef ANDROID
			nBurnDrvActive = i;
#endif
			if (strcmp(BurnDrvGetTextA(0), argv[1]) == 0) {
				break;
			}
		}

		if (i == nBurnDrvCount) {
			printf("%s is not supported by FB Alpha.",argv[1]);
			return 1;
		}
	}

	InputInit();
	init_emu(i);
	
	RunMessageLoop();
	InputExit();

	DrvExit();
	ConfigAppSave();
	BurnLibExit();
	SDL_Quit();

	return 0;
}


/* const */ TCHAR* ANSIToTCHAR(const char* pszInString, TCHAR* pszOutString, int nOutSize)
{
#if defined (UNICODE)
	static TCHAR szStringBuffer[1024];

	TCHAR* pszBuffer = pszOutString ? pszOutString : szStringBuffer;
	int nBufferSize  = pszOutString ? nOutSize * 2 : sizeof(szStringBuffer);

	if (MultiByteToWideChar(CP_ACP, 0, pszInString, -1, pszBuffer, nBufferSize)) {
		return pszBuffer;
	}

	return NULL;
#else
	if (pszOutString) {
		_tcscpy(pszOutString, pszInString);
		return pszOutString;
	}

	return (TCHAR*)pszInString;
#endif
}


/* const */ char* TCHARToANSI(const TCHAR* pszInString, char* pszOutString, int nOutSize)
{
#if defined (UNICODE)
	static char szStringBuffer[1024];
	memset(szStringBuffer, 0, sizeof(szStringBuffer));

	char* pszBuffer = pszOutString ? pszOutString : szStringBuffer;
	int nBufferSize = pszOutString ? nOutSize * 2 : sizeof(szStringBuffer);

	if (WideCharToMultiByte(CP_ACP, 0, pszInString, -1, pszBuffer, nBufferSize, NULL, NULL)) {
		return pszBuffer;
	}

	return NULL;
#else
	if (pszOutString) {
		strcpy(pszOutString, pszInString);
		return pszOutString;
	}

	return (char*)pszInString;
#endif
}


bool AppProcessKeyboardInput()
{
	return true;
}
