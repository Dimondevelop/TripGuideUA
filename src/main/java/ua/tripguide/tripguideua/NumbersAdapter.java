package ua.tripguide.tripguideua;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ua.tripguide.tripguideua.Models.ObjectList;

public class NumbersAdapter extends RecyclerView.Adapter<NumbersAdapter.NumberViewHolder> {

    private int numberItems;
    private Context mContextObj;
    private List<ObjectList> mDataObjectList;
    private ArrayList<ObjectList> CheckedObjects = new ArrayList<>();
    private TextView tvNumberList;
    private TextView tvTypeList;
    LinearLayout ll_create_excursion_with_objects;

    NumbersAdapter(int numberItems, Context mContextObj, List<ObjectList> mDataObjectList) {
        this.numberItems = numberItems;

        this.mContextObj = mContextObj;
        this.mDataObjectList = mDataObjectList;
    }

    @NonNull
    @Override
    public NumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.recyclerview_list_objects;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new NumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NumberViewHolder numberViewHolder, int position) {

        numberViewHolder.bind(position);

    }

    @Override
    public int getItemCount() {
        return numberItems;
    }

    class NumberViewHolder extends RecyclerView.ViewHolder {

        NumberViewHolder(@NonNull final View itemView) {
            super(itemView);

            tvNumberList = itemView.findViewById(R.id.tv_number_list);
            tvTypeList = itemView.findViewById(R.id.tv_type_list);
            LinearLayout ll_more = itemView.findViewById(R.id.ll_more);
            final CheckBox chb_create = itemView.findViewById(R.id.chb_create);

            ll_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int positionIndex = getAdapterPosition();

                    Intent intent = new Intent(mContextObj,MoreActivity.class);
                    intent.putExtra("name_object",mDataObjectList.get(positionIndex).getName_object());
                    intent.putExtra("coordinate_x",mDataObjectList.get(positionIndex).getCoordinate_x());
                    intent.putExtra("coordinate_y",mDataObjectList.get(positionIndex).getCoordinate_y());
                    intent.putExtra("thumbnail_object",mDataObjectList.get(positionIndex).getThumbnail_object());
                    intent.putExtra("object_description",mDataObjectList.get(positionIndex).getObject_description());
                    intent.putExtra("working_hours",mDataObjectList.get(positionIndex).getWorking_hours());
                    intent.putExtra("type_object",mDataObjectList.get(positionIndex).getType_object());

                    //start the activity
                    mContextObj.startActivity(intent);
                }
            });

            View.OnClickListener CheckBoxListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int positionIndex = getAdapterPosition();


                    if (!chb_create.isChecked()){
                        CheckedObjects.add(mDataObjectList.get(positionIndex));

                        if (CheckedObjects.size() == 2){
                            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 85.0f);
                            LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 15.0f);

                        }
                    } else if (chb_create.isChecked()) {
                        CheckedObjects.remove(mDataObjectList.get(positionIndex));
                        if (CheckedObjects.size() == 1){

                        }
                    }

                    chb_create.toggle();
                }
            };
            itemView.setOnClickListener(CheckBoxListener);

            itemView.setOnHoverListener(new View.OnHoverListener() {
                @Override
                public boolean onHover(View view, MotionEvent motionEvent) {
                    return false;
                }
            });

        }

        void bind(int position){
            tvNumberList.setText(mDataObjectList.get(position).getName_object());
            tvTypeList.setText(String.format(" Тип: %s", mDataObjectList.get(position).getType_object()));
        }
    }
}
