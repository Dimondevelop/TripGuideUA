package ua.tripguide.tripguideua.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import ua.tripguide.tripguideua.City_Activity;
import ua.tripguide.tripguideua.Models.City;
import ua.tripguide.tripguideua.R;
import ua.tripguide.tripguideua.Utils.UniversalImageLoader;

public class RecyclerViewAdapter extends RecyclerView.Adapter <RecyclerViewAdapter.MyViewHolder>{

    private Context mContext;
    private List<City> mData;
    private int itemCount;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public RecyclerViewAdapter(int itemCount, Context mContext, List<City> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.itemCount = itemCount;
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        initImageLoader();

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item_city, parent, false);
        return new MyViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.tv_city_title.setText(mData.get(position).getName());
        imageLoader.displayImage(mData.get(position).getThumbnail(), holder.img_city_thumbnail);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //passing data the city activity
                Intent intent = new Intent(mContext, City_Activity.class);
                intent.putExtra("cityId",mData.get(position).getId());
                intent.putExtra("cityName",mData.get(position).getName());
                //start the activity
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_city_title;
        ImageView img_city_thumbnail;
        CardView cardView;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_city_title = itemView.findViewById(R.id.city_title_id);
            img_city_thumbnail = itemView.findViewById(R.id.city_img_id);
            cardView = itemView.findViewById(R.id.cardview_id);
        }
    }
}
