package com.zyber.nativeDirectory;
//


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.provider.DocumentFile;
import android.telecom.Call;
import android.util.Log;
import android.webkit.URLUtil;

import com.zyber.nativeDirectory.Constants;

import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

public class NativeDirectory extends CordovaPlugin {

    private static final String TAG = "NativeDirectory";
    private static final String ACTION_OPEN = "openDocumentTree";
    private static final String DOWNLOAD = "startDownload";
    private static final String ABORT_DOWNLOAD = "abortDownload";
    private static final String CREATE_DIRECTORY = "createDirectory";
    private static final String GET_FREE_SPACE = "getFreeSpace";
    private static final int PICK_FILE_REQUEST = 1;
    private static final int PICK_FOLDER_REQUEST = 2;
    CallbackContext callback;
    private static Boolean isCancelled;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            StorageAccessFrameworkDownloader storageDownloader = new StorageAccessFrameworkDownloader();
            if (action.equals(ACTION_OPEN)) {

                storageDownloader.chooseFile(this, cordova, callbackContext, PICK_FILE_REQUEST);

                PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
                pluginResult.setKeepCallback(true);

                callback = callbackContext;
                callbackContext.sendPluginResult(pluginResult);
                return true;

            } else if (action.equals(DOWNLOAD)) {
                storageDownloader.startDownload( cordova, callbackContext, args);
                return true;
            } else if (action.equals(CREATE_DIRECTORY)) {
                storageDownloader.createDirectory(cordova, callbackContext, args);
                return true;
            } else if (action.equals(ABORT_DOWNLOAD)) {
               storageDownloader.abortDownload(cordova, callbackContext, args);
                return true;
            } else if (action.equals(GET_FREE_SPACE)) {
                storageDownloader.getFreeSpace(cordova, callbackContext, args);
                return true;
            }
        } else{
            // do something for phones running an SDK before lollipop
           FileDownloader fileDownloader = new FileDownloader();

            if (action.equals(ACTION_OPEN)) {
                //chooseFile(callbackContext);
                fileDownloader.chooseFile(this, cordova, callbackContext, PICK_FILE_REQUEST);

                PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
                pluginResult.setKeepCallback(true);

                callback = callbackContext;
                callbackContext.sendPluginResult(pluginResult);
                return true;
            } else if (action.equals(DOWNLOAD)) {
                fileDownloader.startDownload( cordova, callbackContext, args);
                return true;
            } else if (action.equals(CREATE_DIRECTORY)) {
                fileDownloader.createDirectory(cordova, callbackContext, args);
                return true;
            } else if (action.equals(ABORT_DOWNLOAD)) {
                fileDownloader.abortDownload(cordova, callbackContext, args);
                return true;
            } else if (action.equals(GET_FREE_SPACE)) {
                fileDownloader.getFreeSpace(cordova, callbackContext, args);
                return true;
            }
        }


        return true;

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FILE_REQUEST && callback != null) {

            if (resultCode == Activity.RESULT_OK) {

                Uri uri = data.getData();

                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    try {
                        final int takeFlags = data.getFlags()
                                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                        this.cordova.getActivity().grantUriPermission(this.cordova.getActivity().getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        this.cordova.getActivity().getContentResolver().takePersistableUriPermission(uri, takeFlags);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                if (uri != null) {

                    // Log.w(TAG, uri.toString());
                    callback.success(uri.toString());

                } else {

                    callback.error("File uri was null");

                }

            }  
            else {
                callback.error(resultCode);
            }
        }
    }

}

