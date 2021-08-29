package com.example.recipe_app;

import static com.example.recipe_app.R.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    List<FoodData> myFoodlist;
    FoodData mFoodData;
    EditText searchRecipe;

    MyAdapter mAdapter;

//    Creating Database refrence
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);


        RecyclerView mRecyclerView = this.findViewById(id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this,1);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        searchRecipe=(EditText)findViewById(id.searchText);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Items...");

        myFoodlist=new ArrayList<>();

        mAdapter = new MyAdapter(MainActivity.this,myFoodlist);
        mRecyclerView.setAdapter(mAdapter);


        progressDialog.show();
        databaseReference = FirebaseDatabase.getInstance().getReference("Recipe");
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Array list
                myFoodlist.clear();

                for(DataSnapshot itemSnap: snapshot.getChildren()){
                    FoodData foodData = itemSnap.getValue(FoodData.class);

//                    retrieving key
                    foodData.setKey(itemSnap.getKey());
                    myFoodlist.add(foodData);

                }


                mAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
              progressDialog.dismiss();
            }
        });

        searchRecipe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                 filter(s.toString());
            }
        });


    }

    private void filter(String text) {
        ArrayList<FoodData> filteredList = new ArrayList<>();

        for(FoodData item: myFoodlist){
            if(item.getItemName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        mAdapter.filteredList(filteredList);

    }



    public void btn_uploadActivity(View view) {
        startActivity(new Intent(this, UploadRecipy.class));
    }
}