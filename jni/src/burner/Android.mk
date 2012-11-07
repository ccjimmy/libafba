BURNER_PATH := $(call my-dir)

BURNER := 	$(subst jni/src/,, \
			$(wildcard $(BURNER_PATH)/*.cpp) \
			$(wildcard $(BURNER_PATH)/sdl/*.cpp))





