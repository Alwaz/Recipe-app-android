package com.example.recipe_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;

public class UploadRecipy extends AppCompatActivity {

    ImageView recipyImage;
    EditText res_name, res_description;
    Uri uri;
    String imageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_recipy);

        recipyImage=(ImageView)findViewById(R.id.iv_foodImage);
        res_name = (EditText) findViewById(R.id.txt_recipy_name);
        res_description = (EditText) findViewById(R.id.txt_recipy_description);

    }

    public void btnSelectimage(View view) {
//        to pick images from gallery
        Intent photoPicker = new Intent(Intent.ACTION_PICK);
        photoPicker.setType("image/*");
        startActivityForResult(photoPicker, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            uri = data.getData();
            recipyImage.setImageURI(uri);
        } else {
            Toast.makeText(this,"You Havent pick any picture", Toast.LENGTH_SHORT).show();
        }
    }

//    upload image to firebase
    public void uploadImage(){

//        Creating storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("RecipyImage").child(uri.getLastPathSegment());

        ProgressDialog progressDialogue = new ProgressDialog(this);
        progressDialogue.setMessage("Uploading recipe...");
        progressDialogue.show();



        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {


            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isComplete());
                Uri urlImage =uriTask.getResult();
                imageUrl = urlImage.toString();
                uploadRecipe();
                progressDialogue.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
             progressDialogue.dismiss();

            }
        });
    }

    public void btnUploadRecipe(View view) {
        uploadImage();
    }

//    To store more info
public void uploadRecipe(){

        FoodData foodData = new FoodData (
                res_name.getText().toString(),
                res_description.getText().toString(),
                imageUrl
                );

        String myCurrentDateTime = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

    FirebaseDatabase.getInstance().getReference("Recipe").child(myCurrentDateTime)
            .setValue(foodData).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){
                Toast.makeText(UploadRecipy.this,"Recipy Uploaded",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(UploadRecipy.this,"Failed to Upload",Toast.LENGTH_SHORT).show();
        }
    });
}


}