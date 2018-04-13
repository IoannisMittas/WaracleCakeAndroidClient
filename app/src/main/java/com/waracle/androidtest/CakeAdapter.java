package com.waracle.androidtest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CakeAdapter extends RecyclerView.Adapter<CakeAdapter.ViewHolder>{

    private Context context;
    private List<Cake> cakeList;

    public CakeAdapter(List<Cake> my_data) {
        this.cakeList = my_data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout,parent,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Cake cake = cakeList.get(position);
        holder.titleTextView.setText(cake.getTitle());
        holder.descriptionTextView.setText(cake.getDescription());

        new ImageLoader().load(cake.getImageLink(), holder.imageView);

        // TODO add image
        //Glide.with(context).load(cakeList.get(position).getImage_link()).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return cakeList.size();
    }

    public void setCakes(List<Cake> cakeList) {
        this.cakeList = cakeList;
        notifyDataSetChanged();
    }

    static  class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView titleTextView;
        private TextView descriptionTextView;
        private ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.title);
            descriptionTextView =  itemView.findViewById(R.id.description);
            imageView = itemView.findViewById(R.id.image);
        }
    }





}
