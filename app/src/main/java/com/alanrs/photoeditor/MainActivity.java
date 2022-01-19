package com.alanrs.photoeditor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.icu.util.GregorianCalendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    public static final int PICK_IMAGE = 1;
    String currentPath;
    File directory;
    GridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE
            },100);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },100);
        }

        String fileName = getName();
        directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        gridView = findViewById(R.id.grid_view);



        File files = new File(GetPath(MainActivity.this));
        File images[] = files.listFiles(File::isFile);
        if (images != null){
            ArrayList imageUri = new ArrayList<Uri>();
            for(int i=0;i<images.length;i++){
               // imageUri.set(i, Uri.fromFile(images[i].getAbsoluteFile()));

                imageUri.add(Uri.fromFile(images[i].getAbsoluteFile()));
            }
            GridAdapter customAdapter = new GridAdapter(getApplicationContext(), imageUri);
            gridView.setAdapter(customAdapter);


        }
        FloatingActionButton camera_ = findViewById(R.id.floatingActionButton);
        FloatingActionButton gallery_ = findViewById(R.id.floatingActionButton2);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent2 = new Intent(MainActivity.this,EditActivity.class);
                intent2.putExtra("bitmap",images[position].getAbsolutePath());
                startActivity(intent2);
            }
        });


        camera_.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (checkCameraHardware(getApplicationContext())){
//


                    try{
                        File imageFile = File.createTempFile(fileName,".jpg",directory);
                        currentPath = imageFile.getAbsolutePath();
                        Uri imageUri = FileProvider.getUriForFile(MainActivity.this,
                                "com.alanrs.photoeditor.fileprovider",imageFile);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                        startActivityForResult(intent, 100);
                    }catch (IOException e){
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }


                }else{
                    Toast.makeText(MainActivity.this, "Has no Camera", Toast.LENGTH_SHORT).show();
                }
            }
        });

        gallery_.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private String getName() {

        return String.valueOf(System.currentTimeMillis());
    }
    String GetPath(Context context){
        if ((android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)){
            return context.getExternalMediaDirs()[0].toString();
        }else {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE){
            String filename = getName();
            File imageFile;
            try {
                imageFile = File.createTempFile(filename,".png",directory);
                InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

               FileOutputStream fos = new FileOutputStream(imageFile);
               bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // bmp is your Bitmap instance
                currentPath = imageFile.getAbsolutePath();
                Intent intent = new Intent(MainActivity.this,EditActivity.class);
                intent.putExtra("bitmap",currentPath);
                startActivity(intent);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if(requestCode == 100){
           // Bitmap image_captured = BitmapFactory.decodeFile(currentPath);
            Intent intent2 = new Intent(MainActivity.this,EditActivity.class);
            intent2.putExtra("bitmap",currentPath);
            startActivity(intent2);
        }
    }
}