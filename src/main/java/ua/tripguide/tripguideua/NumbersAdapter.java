package ua.tripguide.tripguideua;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

    private Context mContextObj;
    private List<ObjectList> mDataObjectList;
    private ArrayList<ObjectList> CheckedObjects = new ArrayList<>();
    private TextView tvNumberList;
    private TextView tvTypeList;
    private boolean flag = false;

    boolean isFlag() {
        return flag;
    }

    ArrayList<ObjectList> getCheckedObjects() {
        return CheckedObjects;
    }

    interface OnObjectClickListener {
        void onObjectClick(View view, int position);
    }

    private OnObjectClickListener mListener;

    NumbersAdapter(Context mContextObj, List<ObjectList> mDataObjectList) {
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
        tvNumberList.setText(mDataObjectList.get(position).getName_object());
        tvTypeList.setText(String.format(" Тип: %s", mDataObjectList.get(position).getType_object()));
    }

    @Override
    public int getItemCount() {
        return mDataObjectList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // метод-сеттер для прив'язки колбека до отримувача подій
    void setOnObjectClickListener(OnObjectClickListener listener) {
        mListener = listener;
    }


    class NumberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        NumberViewHolder(@NonNull final View itemView) {
            super(itemView);

            tvNumberList = itemView.findViewById(R.id.tv_number_list);
            tvTypeList = itemView.findViewById(R.id.tv_type_list);
            LinearLayout ll_more = itemView.findViewById(R.id.ll_more);

            ll_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int positionIndex = getAdapterPosition();

                    Intent intent = new Intent(mContextObj, MoreActivity.class);
                    intent.putExtra("name_object", mDataObjectList.get(positionIndex).getName_object());
                    intent.putExtra("coordinate_x", mDataObjectList.get(positionIndex).getCoordinate_x());
                    intent.putExtra("coordinate_y", mDataObjectList.get(positionIndex).getCoordinate_y());
                    intent.putExtra("thumbnail_object", mDataObjectList.get(positionIndex).getThumbnail_object());
                    intent.putExtra("object_description", mDataObjectList.get(positionIndex).getObject_description());
                    intent.putExtra("working_hours", mDataObjectList.get(positionIndex).getWorking_hours());
                    intent.putExtra("type_object", mDataObjectList.get(positionIndex).getType_object());

                    //start the activity
                    mContextObj.startActivity(intent);
                }
            });
            itemView.setOnClickListener(this);
        }

        CheckBox chb_create = itemView.findViewById(R.id.chb_create);

        @Override
        public void onClick(View v) {
            int positionIndex = getAdapterPosition();
            if (!chb_create.isChecked()) {
                CheckedObjects.add(mDataObjectList.get(positionIndex));

                if (CheckedObjects.size() == 2) {
                    flag = true;
                }
            } else if (chb_create.isChecked()) {
                CheckedObjects.remove(mDataObjectList.get(positionIndex));
                if (CheckedObjects.size() == 1) {
                    flag = false;
                }
            }

            chb_create.toggle();

            int position = getAdapterPosition();
            mListener.onObjectClick(v, position);
        }

    }
}
