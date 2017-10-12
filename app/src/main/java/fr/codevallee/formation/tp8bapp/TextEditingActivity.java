package fr.codevallee.formation.tp8bapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;


public class TextEditingActivity extends Activity {
    private Uri uri;
    private EditText textEditArea;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editing);

        textEditArea =(EditText) findViewById(R.id.text_edit_area);
        Intent intent = getIntent();
        String action = intent.getAction();
        if (!action.equals(Intent.ACTION_VIEW)) {
            throw new RuntimeException("Wtf ?");
        }
        String name = intent.getData().getPath();
        uri =intent.getData();
        Log.d("EDITOR",uri.toString());

        boolean r = verifyStoragePermissions(this);

        if (r) {
            readFile(uri);
        }
    }

    private void readFile(Uri uri) {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(new File(uri.getPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Unknown file");
        }

        Scanner flux = new Scanner(inputStream).useDelimiter("\\A");

        textEditArea.setText(flux.hasNext() ? flux.next() : "");
    }

    public static boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else return true;

        return false;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readFile(uri);
                } else {

                }
                return;
            }
        }
    }
}
