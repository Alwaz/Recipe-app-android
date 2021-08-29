package com.example.recipe_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class MyAdapter extends  RecyclerView.Adapter<FoodViewHolder>{



    private Context mContext;
    private List<FoodData> myFoodList;
    private int lastPosition = -1;

    public MyAdapter(Context mContext, List<FoodData> myFoodList) {
        this.mContext = mContext;
        this.myFoodList = myFoodList;
    }




    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_item,parent,false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int i) {

//        dependency to get images
        Glide.with(mContext).load(myFoodList.get(i).getItemImage()).into(holder.imageView);


        holder.mTitle.setText(myFoodList.get(i).getItemName());
        holder.mDescription.setText(myFoodList.get(i).getItemDescription());
        holder.mPrice.setText(myFoodList.get(i).getItemPrice());


//        to move to next activity
       holder.mCardView.setOnClickListener(v -> {

           Intent intent = new Intent(mContext,DetailedActivity.class);
           intent.putExtra("Image", myFoodList.get(holder.getBindingAdapterPosition()).getItemImage());
           intent.putExtra("Description", myFoodList.get(holder.getBindingAdapterPosition()).getItemDescription());

           intent.putExtra("keyValue",
                   myFoodList.get(holder.getBindingAdapterPosition()).getKey());
           mContext.startActivity(intent);
       });

       setAnimation(holder.itemView,i);

    }

    public void setAnimation(View viewToAnimate, int position){
        if(position > lastPosition){

            ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f,0.0f,1.0f,
                    Animation.RELATIVE_TO_SELF,0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(1500);
            viewToAnimate.startAnimation(animation);
            lastPosition= position;
        }

    }

    @Override
    public int getItemCount() {
        return myFoodList.size();
    }

    public void filteredList(ArrayList<FoodData> filteredList) {
      myFoodList = filteredList;
      notifyDataSetChanged();
    }
}

class FoodViewHolder extends RecyclerView.ViewHolder{

    ImageView imageView;
    TextView mTitle, mDescription , mPrice;
    CardView mCardView;

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);

//  connected all the views
        imageView=itemView.findViewById(R.id.ivImage);
        mTitle=itemView.findViewById(R.id.tvTitle);
        mDescription=itemView.findViewById(R.id.tvDescription);
        mPrice=itemView.findViewById(R.id.tvPrice);

        mCardView=itemView.findViewById(R.id.myCarView);

    }
}
