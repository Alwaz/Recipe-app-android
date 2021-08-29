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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;

public class UpdateRecipe extends AppCompatActivity {

    ImageView recipyImage;
    EditText res_name, res_description, res_price;
    Uri uri;
    String imageUrl;
    String key, oldImageUrl;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_recipe);

        recipyImage=(ImageView)findViewById(R.id.iv_foodImage);
        res_name = (EditText) findViewById(R.id.txt_recipy_name);
        res_description = (EditText) findViewById(R.id.txt_recipy_description);
        res_price=(EditText) findViewById(R.id.txt_recipy_price);

//      To move data from one activity to another
Bundle bundle = getIntent().getExtras();
 if(bundle!=null) {
     Glide.with(UpdateRecipe.this).load(bundle.getString("oldImage")).into(recipyImage);
     res_name.setText(bundle.getString("recipeNameKey"));
     res_description.setText(bundle.getString("descriptionKey"));
     res_price.setText(bundle.getString("priceKey"));

     key = bundle.getString("key");
     oldImageUrl = bundle.getString("oldImage");
 }

 databaseReference = FirebaseDatabase.getInstance().getReference("Recipe").child(key);

    }

    public void btnSelectimage(View view) {

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



    public void btnUpdateRecipe(View view) {
        String recipename = res_name.getText().toString().trim();
        String recipeDescription = res_description.getText().toString().trim();
        String recipePrice = res_price.getText().toString();


        ProgressDialog progressDialogue = new ProgressDialog(this);
        progressDialogue.setMessage("Recipe Updating...");
        progressDialogue.show();


        storageReference = FirebaseStorage.getInstance()
                .getReference().child("RecipeImage").child(uri.getLastPathSegment());
        storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {

            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while(!uriTask.isComplete());
            Uri urlImage =uriTask.getResult();
            imageUrl = urlImage.toString();
            uploadRecipe();
            progressDialogue.dismiss();

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialogue.dismiss();

            }});

    }

    public void uploadRecipe(){
        FoodData foodData = new FoodData (
                res_name,
                res_description,
                res_price,
                imageUrl

        );

      databaseReference.setValue(foodData).addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {

              StorageReference storageReferenceeNew = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);
              storageReferenceeNew.delete();
              Toast.makeText(UpdateRecipe.this, "Data Updated", Toast.LENGTH_SHORT).show();

          }
      });

    }
}