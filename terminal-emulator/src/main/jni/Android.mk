LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE:= libxodos
LOCAL_SRC_FILES:= xodos.c
include $(BUILD_SHARED_LIBRARY)
