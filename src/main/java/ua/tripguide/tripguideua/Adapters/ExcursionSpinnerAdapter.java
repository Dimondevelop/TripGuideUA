package ua.tripguide.tripguideua.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import ua.tripguide.tripguideua.Models.SpinnerList;
import ua.tripguide.tripguideua.R;
import ua.tripguide.tripguideua.Utils.UniversalImageLoader;

public class ExcursionSpinnerAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater layoutInflater;
    private ArrayList<SpinnerList> spinnerList;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    private TextView tvSpinnerName;
    private TextView tvSpinnerDescription;

    public ExcursionSpinnerAdapter(Context context, ArrayList<SpinnerList> spinnerList) {
        this.mContext = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.spinnerList = spinnerList;
        initImageLoader();
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }



    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        View view = layoutInflater.inflate(R.layout.simple_spinner_dropdown, parent, false);

        SpinnerList spinnerList = (SpinnerList) getItem(position);

        TextView tvSpinnerName = view.findViewById(R.id.tv_spinner_name);
        tvSpinnerName.setText(spinnerList.getSpinnerName());

        TextView tvSpinnerDescription = view.findViewById(R.id.tv_spinner_description);
        tvSpinnerDescription.setText(spinnerList.getSpinnerDescription());

        ImageView ivBrandLogo = view.findViewById(R.id.iv_spinner_logo);
        imageLoader.displayImage("drawable://"+spinnerList.getSpinnerLogo() ,ivBrandLogo);

        return view;
    }

    @Override
    public int getCount() {
        return spinnerList.size();
    }

    @Override
    public Object getItem(int position) {
        return spinnerList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.spinner_item, parent, false);

        SpinnerList spinnerList = (SpinnerList) getItem(position);

        tvSpinnerName = view.findViewById(R.id.tv_spinner_name);
        tvSpinnerName.setText(spinnerList.getSpinnerName());

        tvSpinnerDescription = view.findViewById(R.id.tv_spinner_description);
        tvSpinnerDescription.setText(spinnerList.getSpinnerDescription());

        ImageView ivBrandLogo = view.findViewById(R.id.iv_spinner_logo);
        imageLoader.displayImage("drawable://"+spinnerList.getSpinnerLogo(), ivBrandLogo);

        return view;
    }


    public void setName(String name){
        this.tvSpinnerName.setText(name);
    }

    public void setDescription(String description){
        this.tvSpinnerDescription.setText(description);
    }
}
