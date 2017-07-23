LOCAL_PATH:=$(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE:= CMessenger
LOCAL_SRC_FILES := CMessenger.c

include $(BUILD_SHARED_LIBRARY)

