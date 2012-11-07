LOCAL_PATH := $(call my-dir)
MY_PATH := $(LOCAL_PATH)
include $(CLEAR_VARS)

include $(MY_PATH)/libpng/Android.mk
include $(CLEAR_VARS)
include $(MY_PATH)/zlib/Android.mk
#include $(CLEAR_VARS)
#include $(MY_PATH)/lib7z/Android.mk
include $(CLEAR_VARS)
LOCAL_PATH := $(MY_PATH)
include $(LOCAL_PATH)/Android.include
include $(LOCAL_PATH)/burn/Android.mk
include $(LOCAL_PATH)/burner/Android.mk
include $(LOCAL_PATH)/cpu/Android.mk

LOCAL_MODULE := afba
LOCAL_ARM_MODE   := arm

ANDROID_OBJS := android.c android_snd.cpp android_stated.cpp
#fastarm_memfunc.S

INTF_DIR := $(LOCAL_PATH)/intf
INTF_OBJS := $(wildcard $(INTF_DIR)/*.cpp)

INTF_AUDIO_DIR := $(LOCAL_PATH)/intf/audio
INTF_AUDIO_OBJS := $(wildcard $(INTF_AUDIO_DIR)/*.cpp)

INTF_AUDIO_SDL_DIR := $(LOCAL_PATH)/intf/audio/sdl
INTF_AUDIO_SDL_OBJS := $(wildcard $(INTF_AUDIO_SDL_DIR)/*.cpp)

INTF_INPUT_DIR := $(LOCAL_PATH)/intf/input
INTF_INPUT_OBJS := $(wildcard $(INTF_INPUT_DIR)/*.cpp)

INTF_INPUT_SDL_DIR := $(LOCAL_PATH)/intf/input/sdl
INTF_INPUT_SDL_OBJS := $(wildcard $(INTF_INPUT_SDL_DIR)/*.cpp)

INTF_VIDEO_DIR := $(LOCAL_PATH)/intf/video
INTF_VIDEO_OBJS := $(wildcard $(INTF_VIDEO_DIR)/*.cpp)

INTF_VIDEO_SDL_DIR := $(LOCAL_PATH)/intf/video/sdl
INTF_VIDEO_SDL_OBJS := $(wildcard $(INTF_VIDEO_SDL_DIR)/*.cpp)

#INTF_VIDEO_SCALERS_DIR := $(LOCAL_PATH)/intf/video/scalers
#INTF_VIDEO_SCALERS_OBJS := $(wildcard $(INTF_VIDEO_SCALERS_DIR)/*.cpp)

LOCAL_SRC_FILES += $(ANDROID_OBJS) \
		$(BURN) $(BURNER) $(CPU) \
		$(INTF_OBJS:$(LOCAL_PATH)%=%) $(INTF_AUDIO_OBJS:$(LOCAL_PATH)%=%) $(INTF_AUDIO_SDL_OBJS:$(LOCAL_PATH)%=%) \
		$(INTF_INPUT_OBJS:$(LOCAL_PATH)%=%) $(INTF_INPUT_SDL_OBJS:$(LOCAL_PATH)%=%) $(INTF_VIDEO_OBJS:$(LOCAL_PATH)%=%) \
		$(INTF_VIDEO_SDL_OBJS:$(LOCAL_PATH)%=%) 
#		$(INTF_VIDEO_SCALERS_OBJS:$(LOCAL_PATH)%=%) 

LOCAL_STATIC_LIBRARIES := png minizip
#7z
LOCAL_SHARED_LIBRARIES := SDL
LOCAL_LDLIBS := -lGLESv1_CM -llog -lz

include $(BUILD_SHARED_LIBRARY)


