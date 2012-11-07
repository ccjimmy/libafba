// Module for input using SDL
#include <SDL.h>

#include "burner.h"
#include "inp_sdl_keys.h"

#ifdef ANDROID
extern "C" {
	int android_pad_up;
	int android_pad_down;
	int android_pad_left;
	int android_pad_right;
	int android_pad_1;
	int android_pad_2;
	int android_pad_3;
	int android_pad_4;
	int android_pad_5;
	int android_pad_6;
	int android_pad_start;
	int android_pad_coins;
	int android_pad_test;
	int android_pad_service;
	int android_pad_reset;
}
#endif

#define MAX_JOYSTICKS (8)

static int FBKtoSDL[512];

static int nInitedSubsytems = 0;
static SDL_Joystick* JoyList[MAX_JOYSTICKS];
static int* JoyPrevAxes = NULL;
static int nJoystickCount = 0;						// Number of joysticks connected to this machine

// Sets up one Joystick (for example the range of the joystick's axes)
static int SDLinpJoystickInit(int i)
{
#ifndef ANDROID
	JoyList[i] = SDL_JoystickOpen(i);
#endif
	return 0;
}

// Set up the keyboard
static int SDLinpKeyboardInit()
{
#ifndef ANDROID
	for (int i = 0; i < 512; i++) {
		FBKtoSDL[SDLtoFBK[i]] = i;
	}
#endif
	return 0;
}

// Get an interface to the mouse
static int SDLinpMouseInit()
{
	return 0;
}

int SDLinpSetCooperativeLevel(bool bExclusive, bool /*bForeGround*/)
{
	SDL_WM_GrabInput((bDrvOkay && (bExclusive || nVidFullscreen)) ? SDL_GRAB_ON : SDL_GRAB_OFF);
	SDL_ShowCursor((bDrvOkay && (bExclusive || nVidFullscreen)) ? SDL_DISABLE : SDL_ENABLE);

	return 0;
}

int SDLinpExit()
{
#ifndef ANDROID
	// Close all joysticks
	for (int i = 0; i < MAX_JOYSTICKS; i++) {
		if (JoyList[i]) {
			SDL_JoystickClose(JoyList[i]);
			JoyList[i] = NULL;
		}
	}

	nJoystickCount = 0;

	free(JoyPrevAxes);
	JoyPrevAxes = NULL;

	if (!(nInitedSubsytems & SDL_INIT_JOYSTICK)) {
		SDL_QuitSubSystem(SDL_INIT_JOYSTICK);
	}
	if (!(nInitedSubsytems & SDL_INIT_VIDEO)) {
		SDL_QuitSubSystem(SDL_INIT_VIDEO);
	}
	nInitedSubsytems = 0;

//	SDL_Quit();
#endif
	return 0;
}

int SDLinpInit()
{
#ifndef ANDROID
	int nSize;

	SDLinpExit();

	memset(&JoyList, 0, sizeof(JoyList));

	nSize = MAX_JOYSTICKS * 8 * sizeof(int);
	if ((JoyPrevAxes = (int*)malloc(nSize)) == NULL) {
		SDLinpExit();
		return 1;
	}
	memset(JoyPrevAxes, 0, nSize);

//	SDL_Init(0);

	nInitedSubsytems = SDL_WasInit(SDL_INIT_EVERYTHING);

	if (!(nInitedSubsytems & SDL_INIT_VIDEO)) {
		SDL_InitSubSystem(SDL_INIT_VIDEO);
	}
	if (!(nInitedSubsytems & SDL_INIT_JOYSTICK)) {
		SDL_InitSubSystem(SDL_INIT_JOYSTICK);
	}

//	SDL_SetVideoMode(320, 224, 0, SDL_RESIZABLE | SDL_HWSURFACE);

	// Set up the joysticks
	nJoystickCount = SDL_NumJoysticks();
	for (int i = 0; i < nJoystickCount; i++) {
		SDLinpJoystickInit(i);
	}
	SDL_JoystickEventState(SDL_IGNORE);

	// Set up the keyboard
	SDLinpKeyboardInit();

	// Set up the mouse
	SDLinpMouseInit();
#endif
	return 0;
}

static unsigned char bKeyboardRead = 0;
static unsigned char* SDLinpKeyboardState;

static unsigned char bJoystickRead = 0;

static unsigned char bMouseRead = 0;
static struct { unsigned char buttons; int xdelta; int ydelta; } SDLinpMouseState;

#define SDL_KEY_DOWN(key) (FBKtoSDL[key] > 0 ? SDLinpKeyboardState[FBKtoSDL[key]] : 0)

// Call before checking for Input in a frame
int SDLinpStart()
{
#ifndef ANDROID
	// Update SDL event queue
	SDL_PumpEvents();

	// Keyboard not read this frame
	bKeyboardRead = 0;

	// No joysticks have been read for this frame
	bJoystickRead = 0;

	// Mouse not read this frame
	bMouseRead = 0;
#endif
	return 0;
}

// Read one of the joysticks
static int ReadJoystick()
{
#ifndef ANDROID
	if (bJoystickRead) {
		return 0;
	}

	SDL_JoystickUpdate();

	// All joysticks have been Read this frame
	bJoystickRead = 1;
#endif
	return 0;
}

// Read one joystick axis
int SDLinpJoyAxis(int i, int nAxis)
{
#ifndef ANDROID
	if (i < 0 || i >= nJoystickCount) {				// This joystick number isn't connected
		return 0;
	}

	if (ReadJoystick() != 0) {						// There was an error polling the joystick
		return 0;
	}

	if (nAxis >= SDL_JoystickNumAxes(JoyList[i])) {
		return 0;
	}

	return SDL_JoystickGetAxis(JoyList[i], nAxis) << 1;
#else
	return 0;
#endif
}

// Read the keyboard
static int ReadKeyboard()
{
#ifndef ANDROID
	int numkeys;

	if (bKeyboardRead) {							// already read this frame - ready to go
		return 0;
	}

	SDLinpKeyboardState = SDL_GetKeyState(&numkeys);
	if (SDLinpKeyboardState == NULL) {
		return 1;
	}

	// The keyboard has been successfully Read this frame
	bKeyboardRead = 1;
#endif
	return 0;
}

static int ReadMouse()
{
#ifndef ANDROID
	if (bMouseRead) {
		return 0;
	}

	SDLinpMouseState.buttons = SDL_GetRelativeMouseState(&(SDLinpMouseState.xdelta), &(SDLinpMouseState.ydelta));

	bMouseRead = 1;
#endif
	return 0;
}

// Read one mouse axis
int SDLinpMouseAxis(int i, int nAxis)
{
#ifndef ANDROID
	if (i < 0 || i >= 1) {									// Only the system mouse is supported by SDL
		return 0;
	}

	switch (nAxis) {
		case 0:
			return SDLinpMouseState.xdelta;
		case 1:
			return SDLinpMouseState.ydelta;
	}
#endif
	return 0;
}

// Check a subcode (the 40xx bit in 4001, 4102 etc) for a joystick input code
static int JoystickState(int i, int nSubCode)
{
#ifndef ANDROID
	if (i < 0 || i >= nJoystickCount) {							// This joystick isn't connected
		return 0;
	}

	if (ReadJoystick() != 0) {									// Error polling the joystick
		return 0;
	}

	if (nSubCode < 0x10) {										// Joystick directions
		const int DEADZONE = 0x4000;

		if (SDL_JoystickNumAxes(JoyList[i]) <= nSubCode) {
			return 0;
		}

		switch (nSubCode) {
			case 0x00: return SDL_JoystickGetAxis(JoyList[i], 0) < -DEADZONE;		// Left
			case 0x01: return SDL_JoystickGetAxis(JoyList[i], 0) > DEADZONE;		// Right
			case 0x02: return SDL_JoystickGetAxis(JoyList[i], 1) < -DEADZONE;		// Up
			case 0x03: return SDL_JoystickGetAxis(JoyList[i], 1) > DEADZONE;		// Down
			case 0x04: return SDL_JoystickGetAxis(JoyList[i], 2) < -DEADZONE;
			case 0x05: return SDL_JoystickGetAxis(JoyList[i], 2) > DEADZONE;
			case 0x06: return SDL_JoystickGetAxis(JoyList[i], 3) < -DEADZONE;
			case 0x07: return SDL_JoystickGetAxis(JoyList[i], 3) > DEADZONE;
			case 0x08: return SDL_JoystickGetAxis(JoyList[i], 4) < -DEADZONE;
			case 0x09: return SDL_JoystickGetAxis(JoyList[i], 4) > DEADZONE;
			case 0x0A: return SDL_JoystickGetAxis(JoyList[i], 5) < -DEADZONE;
			case 0x0B: return SDL_JoystickGetAxis(JoyList[i], 5) > DEADZONE;
			case 0x0C: return SDL_JoystickGetAxis(JoyList[i], 6) < -DEADZONE;
			case 0x0D: return SDL_JoystickGetAxis(JoyList[i], 6) > DEADZONE;
			case 0x0E: return SDL_JoystickGetAxis(JoyList[i], 7) < -DEADZONE;
			case 0x0F: return SDL_JoystickGetAxis(JoyList[i], 7) > DEADZONE;
		}
	}
	if (nSubCode < 0x20) {										// POV hat controls
		if (SDL_JoystickNumHats(JoyList[i]) <= ((nSubCode & 0x0F) >> 2)) {
			return 0;
		}

		switch (nSubCode & 3) {
			case 0:												// Left
				return SDL_JoystickGetHat(JoyList[i], (nSubCode & 0x0F) >> 2) & SDL_HAT_LEFT;
			case 1:												// Right
				return SDL_JoystickGetHat(JoyList[i], (nSubCode & 0x0F) >> 2) & SDL_HAT_RIGHT;
			case 2:												// Up
				return SDL_JoystickGetHat(JoyList[i], (nSubCode & 0x0F) >> 2) & SDL_HAT_UP;
			case 3:												// Down
				return SDL_JoystickGetHat(JoyList[i], (nSubCode & 0x0F) >> 2) & SDL_HAT_DOWN;
		}

		return 0;
	}
	if (nSubCode < 0x80) {										// Undefined
		return 0;
	}
	if (nSubCode < 0x80 + SDL_JoystickNumButtons(JoyList[i])) {	// Joystick buttons
		return SDL_JoystickGetButton(JoyList[i], nSubCode & 0x7F);
	}
#endif
	return 0;
}

// Check a subcode (the 80xx bit in 8001, 8102 etc) for a mouse input code
static int CheckMouseState(unsigned int nSubCode)
{
#ifndef ANDROID
	switch (nSubCode & 0x7F) {
		case 0:
			return (SDLinpMouseState.buttons & SDL_BUTTON(SDL_BUTTON_LEFT)) != 0;
		case 1:
			return (SDLinpMouseState.buttons & SDL_BUTTON(SDL_BUTTON_RIGHT)) != 0;
		case 2:
			return (SDLinpMouseState.buttons & SDL_BUTTON(SDL_BUTTON_MIDDLE)) != 0;
	}
#endif
	return 0;
}

#ifdef ANDROID
/*
CPS2: SF3A - 2 players - 6 buttons
=====================
CinpState: 2 (name:P1 Start || info:p1 start)
CinpState: 200 (name:P1 Up || info:p1 up)
CinpState: 208 (name:P1 Down || info:p1 down)
CinpState: 203 (name:P1 Left || info:p1 left)
CinpState: 205 (name:P1 Right || info:p1 right)
CinpState: 30 (name:P1 Weak Punch || info:p1 fire 1) -> && 47 ?!
CinpState: 31 (name:P1 Medium Punch || info:p1 fire 2)
CinpState: 32 (name:P1 Strong Punch || info:p1 fire 3)
CinpState: 44 (name:P1 Weak Kick || info:p1 fire 4)
CinpState: 45 (name:P1 Medium Kick || info:p1 fire 5)
CinpState: 46 (name:P1 Strong Kick || info:p1 fire 6)
CinpState: 7 (name:P2 Coin || info:p2 coin)
CinpState: 3 (name:P2 Start || info:p2 start)
CinpState: 16386 (name:P2 Up || info:p2 up)
CinpState: 16387 (name:P2 Down || info:p2 down)
CinpState: 16384 (name:P2 Left || info:p2 left)
CinpState: 16385 (name:P2 Right || info:p2 right)
CinpState: 16512 (name:P2 Weak Punch || info:p2 fire 1)
CinpState: 16513 (name:P2 Medium Punch || info:p2 fire 2)
CinpState: 16514 (name:P2 Strong Punch || info:p2 fire 3)
CinpState: 16515 (name:P2 Weak Kick || info:p2 fire 4)
CinpState: 16516 (name:P2 Medium Kick || info:p2 fire 5)
CinpState: 16517 (name:P2 Strong Kick || info:p2 fire 6)
CinpState: 61 (name:Reset || info:reset)
CinpState: 60 (name:Diagnostic || info:diag)
CinpState: 10 (name:Service || info:service)
CinpState: 6 (name:P1 Coin || info:p1 coin)

NEOGEO: MSLUG - 2 players - 4 buttons
=====================
CinpState: 2 (name:P1 Start || info:p1 start)
CinpState: 4 (name:P1 Select || info:p1 select)
CinpState: 200 (name:P1 Up || info:p1 up)
CinpState: 208 (name:P1 Down || info:p1 down)
CinpState: 203 (name:P1 Left || info:p1 left)
CinpState: 205 (name:P1 Right || info:p1 right)
CinpState: 44 (name:P1 Button A || info:p1 fire 1)
CinpState: 45 (name:P1 Button B || info:p1 fire 2)
CinpState: 46 (name:P1 Button C || info:p1 fire 3)
CinpState: 47 (name:P1 Button D || info:p1 fire 4)
CinpState: 7 (name:P2 Coin || info:p2 coin)
CinpState: 3 (name:P2 Start || info:p2 start)
CinpState: 5 (name:P2 Select || info:p2 select)
CinpState: 16386 (name:P2 Up || info:p2 up)
CinpState: 16387 (name:P2 Down || info:p2 down)
CinpState: 16384 (name:P2 Left || info:p2 left)
CinpState: 16385 (name:P2 Right || info:p2 right)
CinpState: 16512 (name:P2 Button A || info:p2 fire 1)
CinpState: 16513 (name:P2 Button B || info:p2 fire 2)
CinpState: 16514 (name:P2 Button C || info:p2 fire 3)
CinpState: 16515 (name:P2 Button D || info:p2 fire 4)
CinpState: 61 (name:Reset || info:reset)
CinpState: 60 (name:Test || info:diag)
CinpState: 10 (name:Service || info:service)
CinpState: 6 (name:P1 Coin || info:p1 coin)
*/
#endif

// Get the state (pressed = 1, not pressed = 0) of a particular input code
int SDLinpState( int nCode )
{
#ifdef ANDROID
	int pressed = 0;

	switch( nCode )
	{
		case 2:
			return android_pad_start;

		case 200:
			return android_pad_up;

		case 208:
			return android_pad_down;

		case 203:
			return android_pad_left;

		case 205:
			return android_pad_right;

		case 30:
		case 47:
			return android_pad_4;
		break;

		case 31:
			return android_pad_5;
		break;

		case 32:
			return android_pad_6;
		break;

		case 44:
			return android_pad_1;
		break;

		case 45:
			return android_pad_2;
		break;

		case 46:
			return android_pad_3;
		break;

		case 61: //FBK_F3
			pressed = android_pad_reset;
			android_pad_reset = 0;
		return pressed;

		case 60: //FBK_F2
			pressed = android_pad_test;
			android_pad_test = 0;
		return pressed;

		case 10: //FBK_9
			pressed = android_pad_service;
			android_pad_service = 0;
		return pressed;

		case 6:
		case 4: //neogeo select
			return android_pad_coins;
	}
#else
	if (nCode < 0) {
		return 0;
	}

	if (nCode < 0x100) {
		if (ReadKeyboard() != 0) {							// Check keyboard has been read - return not pressed on error
			return 0;
		}
		return SDL_KEY_DOWN(nCode);							// Return key state
	}

	if (nCode < 0x4000) {
		return 0;
	}

	if (nCode < 0x8000) {
		// Codes 4000-8000 = Joysticks
		int nJoyNumber = (nCode - 0x4000) >> 8;

		// Find the joystick state in our array
		return JoystickState(nJoyNumber, nCode & 0xFF);
	}

	if (nCode < 0xC000) {
		// Codes 8000-C000 = Mouse
		if ((nCode - 0x8000) >> 8) {						// Only the system mouse is supported by SDL
			return 0;
		}
		if (ReadMouse() != 0) {								// Error polling the mouse
			return 0;
		}
		return CheckMouseState(nCode & 0xFF);
	}
#endif
	return 0;
}

// This function finds which key is pressed, and returns its code
int SDLinpFind(bool CreateBaseline)
{
	int nRetVal = -1;										// assume nothing pressed
#ifndef ANDROID
	// check if any keyboard keys are pressed
	if (ReadKeyboard() == 0) {
		for (int i = 0; i < 0x100; i++) {
			if (SDL_KEY_DOWN(i) > 0) {
				nRetVal = i;
				goto End;
			}
		}
	}

	// Now check all the connected joysticks
	for (int i = 0; i < nJoystickCount; i++) {
		int j;
		if (ReadJoystick() != 0) {							// There was an error polling the joystick
			continue;
		}

		for (j = 0; j < 0x10; j++) {						// Axes
			int nDelta = JoyPrevAxes[(i << 3) + (j >> 1)] - SDLinpJoyAxis(i, (j >> 1));
			if (nDelta < -0x4000 || nDelta > 0x4000) {
				if (JoystickState(i, j)) {
					nRetVal = 0x4000 | (i << 8) | j;
					goto End;
				}
			}
		}

		for (j = 0x10; j < 0x20; j++) {						// POV hats
			if (JoystickState(i, j)) {
				nRetVal = 0x4000 | (i << 8) | j;
				goto End;
			}
		}

		for (j = 0x80; j < 0x80 + SDL_JoystickNumButtons(JoyList[i]); j++) {
			if (JoystickState(i, j)) {
				nRetVal = 0x4000 | (i << 8) | j;
				goto End;
			}
		}
	}

	// Now the mouse
	if (ReadMouse() == 0) {
		int nDeltaX, nDeltaY;

		for (unsigned int j = 0x80; j < 0x80 + 0x80; j++) {
			if (CheckMouseState(j)) {
				nRetVal = 0x8000 | j;
				goto End;
			}
		}

		nDeltaX = SDLinpMouseAxis(0, 0);
		nDeltaY = SDLinpMouseAxis(0, 1);
		if (abs(nDeltaX) < abs(nDeltaY)) {
			if (nDeltaY != 0) {
				return 0x8000 | 1;
			}
		} else {
			if (nDeltaX != 0) {
				return 0x8000 | 0;
			}
		}
	}

End:

	if (CreateBaseline) {
		for (int i = 0; i < nJoystickCount; i++) {
			for (int j = 0; j < 8; j++) {
				JoyPrevAxes[(i << 3) + j] = SDLinpJoyAxis(i, j);
			}
		}
	}
#endif
	return nRetVal;
}

int SDLinpGetControlName(int nCode, TCHAR* pszDeviceName, TCHAR* pszControlName)
{
#ifndef ANDROID
	if (pszDeviceName) {
		pszDeviceName[0] = _T('\0');
	}
	if (pszControlName) {
		pszControlName[0] = _T('\0');
	}

	switch (nCode & 0xC000) {
		case 0x0000: {
			_tcscpy(pszDeviceName, _T("System keyboard"));

			break;
		}
		case 0x4000: {
			int i = (nCode >> 8) & 0x3F;

			if (i >= nJoystickCount) {				// This joystick isn't connected
				return 0;
			}
			_tsprintf(pszDeviceName, "%hs", SDL_JoystickName(i));

			break;
		}
		case 0x8000: {
			int i = (nCode >> 8) & 0x3F;

			if (i >= 1) {
				return 0;
			}
			_tcscpy(pszDeviceName, _T("System mouse"));

			break;
		}
	}
#endif
	return 0;
}

struct InputInOut InputInOutSDL = { SDLinpInit, SDLinpExit, SDLinpSetCooperativeLevel, SDLinpStart, SDLinpState, SDLinpJoyAxis, SDLinpMouseAxis, SDLinpFind, SDLinpGetControlName, NULL, _T("SDL input") };
