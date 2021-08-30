package com.example.recipe_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.LongSummaryStatistics;

public class DetailedActivity extends AppCompatActivity {

    TextView  foodDescription,RecipeName,RecipePrice;
    ImageView foodImage;
    String imgUrl="";
    String key="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        RecipeName=(TextView)findViewById(R.id.txtRecipeName);
        RecipePrice=(TextView)findViewById(R.id.txtRecipePrice);
       foodDescription=(TextView)findViewById(R.id.txtDescription);
       foodImage = (ImageView)findViewById(R.id.ivImage2);

        Bundle mBundle = getIntent().getExtras();

        if(mBundle !=null){

            RecipeName.setText(mBundle.getString("Name"));
            RecipePrice.setText(mBundle.getString("Price"));
            foodDescription.setText(mBundle.getString("Description"));

//            key from database will be stored in key
            key = mBundle.getString("keyValue");
            imgUrl = mBundle.getString("Image");


//            Glide library for image
            Glide.with(this).load(mBundle.getString("Image")).into(foodImage);
        }



    }

    public void btnDeleteRecipe(View view) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Recipe");
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference= storage.getReferenceFromUrl(imgUrl);

        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
               reference.child(key).removeValue();
                Toast.makeText(DetailedActivity.this,"Recipe Deleted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });

    }

//    Move to update activity through intent
    public void btnUpdateRecipe(View view) {
        startActivity(new Intent(getApplicationContext(),UpdateRecipe.class)
                .putExtra("recipeNameKey",RecipeName.getText()
                        .toString()).putExtra("descriptionKey",foodDescription.getText().toString())
                .putExtra("priceKey",RecipePrice.getText().toString()).putExtra("oldImage",imgUrl)
                .putExtra("key",key)
        );
    }
}