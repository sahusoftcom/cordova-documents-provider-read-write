package com.zyber.nativeDirectory;

import android.content.Context;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;

/**
 * Created by suraj on 15/06/16.
 */
public interface Downloader {

    public void chooseFile(NativeDirectory context, CordovaInterface cordova, CallbackContext callbackContext, int requestCode);
    public void startDownload(CordovaInterface cordova, CallbackContext callbackContext, JSONArray args);
    public void createDirectory(CordovaInterface cordova, CallbackContext callbackContext, JSONArray args);
    public void abortDownload(CordovaInterface cordova, CallbackContext callbackContext, JSONArray args);
    public void getFreeSpace(CordovaInterface cordova, CallbackContext callbackContext, JSONArray args);

}
