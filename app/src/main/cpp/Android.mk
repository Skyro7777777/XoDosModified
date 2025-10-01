LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := libxodos-bootstrap
LOCAL_SRC_FILES := xodos-bootstrap-zip.S xodos-bootstrap.c
include $(BUILD_SHARED_LIBRARY)
