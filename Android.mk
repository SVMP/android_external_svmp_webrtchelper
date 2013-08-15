# Copyright 2008 The Android Open Source Project
#
LOCAL_PATH:= $(call my-dir)

################################################################
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-subdir-java-files)
LOCAL_STATIC_JAVA_LIBRARIES := SVMPProtocol
LOCAL_STATIC_JAVA_LIBRARIES += netty-codec
LOCAL_STATIC_JAVA_LIBRARIES += netty-common
LOCAL_STATIC_JAVA_LIBRARIES += netty-transport
LOCAL_STATIC_JAVA_LIBRARIES += netty-buffer
LOCAL_STATIC_JAVA_LIBRARIES += netty-codec-http
LOCAL_STATIC_JAVA_LIBRARIES += netty-handler
LOCAL_MODULE := webrtc_helper
include $(BUILD_JAVA_LIBRARY)

################################################################
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := netty-codec:lib/netty/netty-codec-4.0.6.Final.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += netty-common:lib/netty/netty-common-4.0.6.Final.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += netty-transport:lib/netty/netty-transport-4.0.6.Final.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += netty-buffer:lib/netty/netty-buffer-4.0.6.Final.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += netty-codec-http:lib/netty/netty-codec-http-4.0.6.Final.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += netty-handler:lib/netty/netty-handler-4.0.6.Final.jar
include $(BUILD_MULTI_PREBUILT)

################################################################
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE := webrtc_helper
LOCAL_MODULE_CLASS := BIN
LOCAL_MODULE_PATH := $(TARGET_OUT)/bin
LOCAL_SRC_FILES := $(LOCAL_MODULE)
include $(BUILD_PREBUILT)
