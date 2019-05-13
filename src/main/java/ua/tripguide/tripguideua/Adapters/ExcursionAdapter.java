package ua.tripguide.tripguideua.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ua.tripguide.tripguideua.Models.Excursion;
import ua.tripguide.tripguideua.R;
import ua.tripguide.tripguideua.RoutesActivity;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionsViewHolder> {
    private Context mContextEx;
    private List<Excursion> lstExcursions;

    private float[] coordinates_x;
    private float[] coordinates_y;
    private String[] titles;
    private String[] working_hours;
    private int[] average_duration;
    private int[] price;
    private String[] place_ids;


    public ExcursionAdapter(Context mContextEx, List<Excursion> mDataExcursionList) {
        this.mContextEx = mContextEx;
        this.lstExcursions = mDataExcursionList;
    }


    @NonNull
    @Override
    public ExcursionAdapter.ExcursionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        int layoutIdForListItem = R.layout.recyclerview_list_excursions;

        LayoutInflater inflater = LayoutInflater.from(mContextEx);
        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new ExcursionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionAdapter.ExcursionsViewHolder holder, int position) {
        holder.tvExcursionList.setText(lstExcursions.get(position).getName_excursion());
        holder.tvTypeOfExcursionList.setText(String.format(" Тип: %s", lstExcursions.get(position).getType_excutsion()));
    }

    @Override
    public int getItemCount() {
        return lstExcursions.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    class ExcursionsViewHolder extends RecyclerView.ViewHolder {

        private TextView tvExcursionList;
        private TextView tvTypeOfExcursionList;

        ExcursionsViewHolder(@NonNull View itemView) {
            super(itemView);

            tvExcursionList = itemView.findViewById(R.id.tv_excursion_list);
            tvTypeOfExcursionList = itemView.findViewById(R.id.tv_type_of_excursion_list);
            LinearLayout ll_excursion_list = itemView.findViewById(R.id.ll_excursion_list);

            ll_excursion_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    int countlstExcursions = lstExcursions.get(position).getRouteObjectsInfos().size();

                    coordinates_x = new float[countlstExcursions];
                    coordinates_y = new float[countlstExcursions];
                    titles = new String[countlstExcursions];
                    working_hours = new String[countlstExcursions];
                    average_duration = new int[countlstExcursions];
                    price = new int[countlstExcursions];
                    place_ids = new String[countlstExcursions];

                    for (int i = 0; i < countlstExcursions; i++) {
                        coordinates_x[i] = (float)lstExcursions.get(position).getRouteObjectsInfos().get(i).getLatLng().latitude;
                        coordinates_y[i] = (float)lstExcursions.get(position).getRouteObjectsInfos().get(i).getLatLng().longitude;
                        titles[i] =lstExcursions.get(position).getRouteObjectsInfos().get(i).getTitle();
                        working_hours[i] = lstExcursions.get(position).getRouteObjectsInfos().get(i).getWorking_hour();
                        average_duration[i] = lstExcursions.get(position).getRouteObjectsInfos().get(i).getAverage_duration();
                        price[i] = lstExcursions.get(position).getRouteObjectsInfos().get(i).getPrice();
                        place_ids[i] = lstExcursions.get(position).getRouteObjectsInfos().get(i).getPlace_id();
                    }
                              //DEBUG
//                    Toast.makeText(view.getContext(),
//                            lstExcursions.get(position).getName_excursion() + " \n" +
//                            lstExcursions.get(position).getRouteObjectsInfos().get(0).getTitle()+" ", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(mContextEx, RoutesActivity.class);

                    intent.putExtra("coordinates_x", coordinates_x);
                    intent.putExtra("coordinates_y", coordinates_y);
                    intent.putExtra("titles", titles);
                    intent.putExtra("working_hours", working_hours);
                    intent.putExtra("average_duration", average_duration);
                    intent.putExtra("price", price);
                    intent.putExtra("place_ids", place_ids);
                    mContextEx.startActivity(intent);

                }
            });


        }
    }
}
