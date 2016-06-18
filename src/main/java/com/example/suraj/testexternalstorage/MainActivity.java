package com.example.suraj.testexternalstorage;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;

import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int WRITE_REQUEST_CODE = 43;
    private static final int EDIT_REQUEST_CODE = 44;
    private static final int READ_REQUEST_CODE = 42;
    private static final String TAG = "MainActivity" ;

    Uri pathExternal;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                triggerStorageAccessFramework();
            }
        });


        String secondaryStore = System.getenv("SECONDARY_STORAGE");

        Log.v("sac", secondaryStore.toString());


    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void triggerStorageAccessFramework() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public final void onActivityResult(final int requestCode, final int resultCode, final Intent resultData) {
        if (requestCode == WRITE_REQUEST_CODE) {
            Uri treeUri = resultData.getData();
            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);

            // List all existing files inside picked directory
            for (DocumentFile file : pickedDir.listFiles()) {
                Log.d(TAG, "Found file " + file.getName() + " with size " + file.length());
            }
            // Create a new file and write into it
            DocumentFile dir = pickedDir.createDirectory("My Novel");
            for(int i =0; i<5; i++ ) {
                DocumentFile newFile = dir.createFile("text/plain", "My Novel" + i);
                OutputStream out = null;
                try {
                    out = getContentResolver().openOutputStream(newFile.getUri());
                    out.write("A long time ago...".getBytes());
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



        }
    }



}
