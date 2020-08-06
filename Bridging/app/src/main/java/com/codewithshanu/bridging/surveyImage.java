package com.codewithshanu.bridging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class surveyImage extends AppCompatActivity {

    Datas datas;
    ProgressBar progressBar;
    EditText bridgeName, location, height, length, locCoord;
    Spinner designType, bridgeType;
    Button ch, up;
    ImageView img;
    StorageReference mStorageRef;
    DatabaseReference dbreff;
    private StorageTask uploadTask;
    public Uri imguri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_image);

        mStorageRef = FirebaseStorage.getInstance().getReference("Images");

        ch =(Button) findViewById(R.id.btnChoose);
        up =(Button) findViewById(R.id.btnUpload);
        img =(ImageView) findViewById(R.id.imageView);
        bridgeName = (EditText) findViewById(R.id.bridgeName);
        location = (EditText) findViewById(R.id.location);
        height = (EditText) findViewById(R.id.height);
        length = findViewById(R.id.length);
        locCoord = findViewById(R.id.locCoord);
        designType = findViewById(R.id.designType);
        bridgeType = findViewById(R.id.bridgeType);
        progressBar = findViewById(R.id.progressBar);
        dbreff = FirebaseDatabase.getInstance().getReference().child("Datas");

        //setting dropdown entry
        String[] items = new String[]{"Truss", "Beam", "Cantilever", "Suspension", "Arch", "Beam"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        designType.setAdapter(adapter);

        String[] items1 = new String[]{"Roadway", "Railway", "Pedestrian"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items1);
        bridgeType.setAdapter(adapter1);

        datas = new Datas();

        ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileChooser();
            }
        });

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(uploadTask != null && uploadTask.isInProgress())
                {
                    Toast.makeText(surveyImage.this, "Please wait Upload is in progress", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    FileUploader();
                }
            }
        });
    }

    private String getExtension(Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }


    //function for uploading the file
    private  void FileUploader()
    {
        //image input
        String imageid;
        imageid = System.currentTimeMillis()+"."+getExtension(imguri);
        //normal text input
        datas.setName(bridgeName.getText().toString().trim());
        datas.setLocation(location.getText().toString().trim());
        datas.setLength(length.getText().toString().trim());
        datas.setLocCoord(locCoord.getText().toString().trim());
        datas.setDesignType(designType.getSelectedItem().toString().trim());
        datas.setBridgeType(bridgeType.getSelectedItem().toString().trim());
        datas.setHeight(height.getText().toString().trim());
        datas.setImageid(imageid);

        dbreff.push().setValue(datas);



        //getting storage reference
        StorageReference Ref = mStorageRef.child(imageid);

        uploadTask = Ref.putFile(imguri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(surveyImage.this,"Data Saved Successfully",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });

        progressBar.setVisibility(View.VISIBLE);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    //function for choosing the file
    private void FileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            //image url
            imguri = data.getData();
            img.setImageURI(imguri);
        }
    }
}
