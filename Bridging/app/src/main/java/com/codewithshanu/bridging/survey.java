package com.codewithshanu.bridging;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class survey extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText mName, mLocation, mDesign, mLength, mCoord, mDate;
    Button mSaveBtn;
    ProgressBar progressBar;
    ImageView mImageView;
    FirebaseFirestore fStore;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        mName = findViewById(R.id.name);
        mLocation = findViewById(R.id.location);
        mDesign = findViewById(R.id.design);
        mLength = findViewById(R.id.length);
        mCoord = findViewById(R.id.height);
        mDate = findViewById(R.id.date);
        progressBar = findViewById(R.id.progressBar);
        mSaveBtn = findViewById(R.id.saveBtn);
        mImageView = findViewById(R.id.imageView);

        fStore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = mName.getText().toString().trim();
                final String location = mLocation.getText().toString();
                final String design = mDesign.getText().toString().trim();
                final String length = mLength.getText().toString().trim();
                final String coord = mCoord.getText().toString().trim();
                final String date = mDate.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    mName.setError("Name of Bridge is Required");
                    return;
                }
                if (TextUtils.isEmpty(location)) {
                    mLocation.setError("Location is Required");
                    return;
                }

                DocumentReference documentReference = fStore.collection("survey").document();
                Map<String, Object> user = new HashMap<>();
                user.put("Bridge Name", name);
                user.put("Location", location);
                user.put("Design", design);
                user.put("Length", length);
                user.put("Coordinates", coord);
                user.put("Survey Date", date);

                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(survey.this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.VISIBLE);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                });
            }

        });
    }}