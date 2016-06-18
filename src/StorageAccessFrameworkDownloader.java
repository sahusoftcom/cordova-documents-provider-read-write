package com.sahusoft.nativeDirectory;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StatFs;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

/**
 * Created by suraj on 15/06/16.
 */
public class StorageAccessFrameworkDownloader implements Downloader{
    private static boolean isCancelled = false;

    @Override
    public void chooseFile(NativeDirectory context, CordovaInterface cordova, CallbackContext callbackContext, int requestCode) {
        // type and title should be configurable

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        Intent chooser = Intent.createChooser(intent, "Select File");
        cordova.startActivityForResult(context, intent, requestCode);

    }

    @Override
    public void startDownload(CordovaInterface cordova, CallbackContext callbackContext, JSONArray args) {
        isCancelled = false;

        String token = new String();
        String url = new String();
        String contentUrl = new String();
        String name = new String();
        String contentType = new String();
        try {
            url = args.getJSONArray(0).getString(0);
            token = args.getJSONArray(0).getString(1);
            contentUrl = args.getJSONArray(0).getString(2);
            name = args.getJSONArray(0).getString(3);
            contentType = args.getJSONArray(0).getString(4);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String CurrentString = name;

         Log.d("filename", CurrentString);
        String[] separated = CurrentString.split("/");

         Log.d("separeted", separated.toString());


        Uri treeUri = Uri.parse(contentUrl);

        DocumentFile pickedDir = DocumentFile.fromTreeUri(cordova.getActivity(), treeUri);

        if(separated.length > 1) {

            for(int i = 0; i < separated.length -1; i++) {
                 Log.d("treeUri", treeUri.toString());
                 Log.d("clear", separated[i].toString());
                 Log.d("findfile", String.valueOf(pickedDir.findFile(separated[i])));
                if(pickedDir.findFile(separated[i]) == null) {
                    pickedDir = pickedDir.createDirectory(separated[i].toString());
                } else {
                    pickedDir = pickedDir.findFile(separated[i]);
                }
            }

            Log.v("contentUri", contentUrl);
        }

        String fileName = separated[separated.length - 1];
        new DownloadFileAsync(cordova.getActivity().getApplicationContext(), callbackContext, token, pickedDir, fileName, contentType).execute(url);

    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {

        private Context context;
        private String token;
        private DocumentFile pickedDir;
        private CallbackContext callbackContext;
        private String name;
        private String contentType;

        public DownloadFileAsync(Context con, CallbackContext callback, String t, DocumentFile df, String n, String ct) {
            context = con;
            token = t;
            pickedDir = df;
            callbackContext = callback;
            name = n;
            contentType = ct;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;

            try {

                URL url = new URL(aurl[0]);

                URLConnection conexion = url.openConnection();
                conexion.setRequestProperty("Authorization", token);
                conexion.connect();

                 Log.v("tokenhere", token);
                 Log.v("hello123", conexion.getContentType());
                int lenghtOfFile = conexion.getContentLength();
                 Log.v("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

//                Uri treeUri = Uri.parse(contentUrl);
//                Log.v("contentDisposition", conexion.getHeaderField("Content-Disposition").toString());
                Log.v("hello", "bye");
//                DocumentFile pickedDir = DocumentFile.fromTreeUri(context, treeUri);

                 Log.v("pickerdirectory", pickedDir.toString());

                // Create a new file and write into it
                String filename = name;//new String();


                InputStream input = new BufferedInputStream(conexion.getInputStream());

                DocumentFile newFile = pickedDir.createFile(conexion.getContentType().toString(), filename);
                OutputStream output = context.getContentResolver().openOutputStream(newFile.getUri());

                byte data[] = new byte[1024];

                long total = 0;

                long change = 0;

                while ((count = input.read(data)) != -1) {
                     Log.v("here", String.valueOf(isCancelled));
                    if (isCancelled) break;

                    total += count;
                    change += count;
                    if (change > (8 * 1024 * 10) ) {
                        publishProgress(""+(int)((total)));
                        change = 0;
                    }
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {

                callbackContext.error(e.getMessage());
            }
            return null;

        }


        protected void onProgressUpdate(String... progress) {
             Log.d("this is my array", "arr: " + Arrays.toString(progress));

            PluginResult resulta = new PluginResult(PluginResult.Status.OK, progress[0]);
            resulta.setKeepCallback(true);
            callbackContext.sendPluginResult(resulta);

//            mProgressDialogalog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
//            dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
             Log.v("completed", "ehehe");
            PluginResult resulta = new PluginResult(PluginResult.Status.OK, "completed");
            resulta.setKeepCallback(false);
            callbackContext.sendPluginResult(resulta);
        }
    }

    @Override
    public void createDirectory(CordovaInterface cordovaInterface, CallbackContext callbackContext, JSONArray args) {
        String name = new String();
        String contentUrl = new String();
        try {
            name = args.getJSONArray(0).getString(0);
            contentUrl = args.getJSONArray(0).getString(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String CurrentString = name;

        String[] separated = CurrentString.split("/");

        Uri treeUri = Uri.parse(contentUrl);

        DocumentFile pickedDir = DocumentFile.fromTreeUri(cordovaInterface.getActivity(), treeUri);

        if(separated.length > 0) {

            for(int i = 0; i < separated.length; i++) {
                if(pickedDir.findFile(separated[i]) == null) {
                    pickedDir = pickedDir.createDirectory(separated[i].toString());
                } else {
                    pickedDir = pickedDir.findFile(separated[i]);
                }
            }

            Uri uri = pickedDir.getUri();

            if (uri != null) {

                // Log.w(TAG, uri.toString());
                callbackContext.success(uri.toString());

            } else {

                callbackContext.error("File uri was null");

            }
        } else {
            callbackContext.error("Empty Name!");
        }


    }

    @Override
    public void abortDownload(CordovaInterface cordova, CallbackContext callbackContext, JSONArray args) {
        try {
            isCancelled = true;
            callbackContext.success("cancelled");
        } catch (Exception e) {
            callbackContext.error("unable to cancel");
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void getFreeSpace(CordovaInterface cordova, CallbackContext callbackContext, JSONArray args) {

        String contentUrl = new String();
        try {
            contentUrl = args.getJSONArray(0).getString(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            Uri treeUri = Uri.parse(contentUrl);
            String path = FileUtil.getFullPathFromTreeUri(treeUri, cordova.getActivity());

            StatFs stat = new StatFs(path.toString());
            long bytesAvailable = (long)stat.getBlockSizeLong() *(long)stat.getAvailableBlocksLong();

            callbackContext.success(String.valueOf(bytesAvailable));

        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }


}
