package com.alanrs.photoeditor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Random;

public class EditActivity extends Activity {


    ImageView imageView;
    Button edit,save;
    CircularProgressIndicator progressIndicator;
    final int PIC_CROP = 1;
    private String ImagePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_preview);
        progressIndicator = findViewById(R.id.progress_circular);
        progressIndicator.setVisibility(View.GONE);
         imageView = findViewById(R.id.image_preview);
         save = findViewById(R.id.save);
         edit = findViewById(R.id.edit);




        Intent intent = getIntent();
        ImagePath = intent.getStringExtra("bitmap");
        imageView.setImageBitmap(BitmapFactory.decodeFile(ImagePath));
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    CropImage.activity(Uri.fromFile(new File(ImagePath))).start(EditActivity.this);
                }catch (Exception e) {
                    Toast.makeText(EditActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progressIndicator.setVisibility(View.VISIBLE);
                String filename = getName();
                File file = new File(GetPath(EditActivity.this)+"/"+filename+".png");
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    Bitmap bitmap = BitmapFactory.decodeFile(ImagePath);
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
                    progressIndicator.setVisibility(View.INVISIBLE);
                    Toast.makeText(EditActivity.this, "Successfully Saved", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    String GetPath(Context context){
        if ((android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)){
            return context.getExternalMediaDirs()[0].toString();
        }else {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
    }
    private String getName() {

        return String.valueOf(System.currentTimeMillis());
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                ImagePath = resultUri.getPath();
                imageView.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}