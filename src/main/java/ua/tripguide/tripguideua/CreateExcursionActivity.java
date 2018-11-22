package ua.tripguide.tripguideua;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import ua.tripguide.tripguideua.Models.ObjectList;
import ua.tripguide.tripguideua.Utils.DBHelper;

public class CreateExcursionActivity extends AppCompatActivity implements NumbersAdapter.OnObjectClickListener {


    LinearLayout linearLayout;
    NumbersAdapter numbersAdapter;
    RecyclerView rv_numbers;

    float[] coordinates_x;
    float[] coordinates_y;
    String[] titles;
    String[] working_hours;
    String[] place_ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_excursion);

        Intent intent = getIntent();
//        String cityName = Objects.requireNonNull(intent.getExtras()).getString("cityName");
        int cityId = Objects.requireNonNull(intent.getExtras()).getInt("cityId");

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //об'єкт класу для роботи з бд
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        ArrayList<ObjectList> lstObjectList = dbHelper.getObjectsFromDB(cityId);

        linearLayout = findViewById(R.id.ll_create_excursion_with_objects);

        rv_numbers = findViewById(R.id.rv_numbers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_numbers.setLayoutManager(layoutManager);
        rv_numbers.setHasFixedSize(true);
        numbersAdapter = new NumbersAdapter(lstObjectList.size(), this, lstObjectList);
        numbersAdapter.setOnObjectClickListener(this);
        rv_numbers.setAdapter(numbersAdapter);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int countCheckedObjects = numbersAdapter.getCheckedObjects().size();
                coordinates_x = new float[countCheckedObjects];
                coordinates_y = new float[countCheckedObjects];
                titles = new String[countCheckedObjects];
                working_hours = new String[countCheckedObjects];
                place_ids = new String[countCheckedObjects];

                for (int i = 0; i < countCheckedObjects; i++) {
                    coordinates_x[i] = numbersAdapter.getCheckedObjects().get(i).getCoordinate_x();
                    coordinates_y[i] = numbersAdapter.getCheckedObjects().get(i).getCoordinate_y();
                    titles[i] = numbersAdapter.getCheckedObjects().get(i).getName_object();
                    working_hours[i] = numbersAdapter.getCheckedObjects().get(i).getWorking_hours();
                    place_ids[i] = numbersAdapter.getCheckedObjects().get(i).getPlace_id();
                }

                Context vContext = v.getContext();
                Intent intent = new Intent(vContext, RoutesActivity.class);
                intent.putExtra("coordinates_x", coordinates_x);
                intent.putExtra("coordinates_y", coordinates_y);
                intent.putExtra("titles", titles);
                intent.putExtra("working_hours", working_hours);
                intent.putExtra("place_ids", place_ids);
                vContext.startActivity(intent);
            }
        });
    }

    @Override
    public void onObjectClick(View view, int position) {

//                DEBUG
//        int countCheckedObjects = numbersAdapter.getCheckedObjects().size();
//        place_ids = new String[countCheckedObjects];
//
//        for (int i = 0; i < countCheckedObjects; i++) {
//            place_ids[i] = numbersAdapter.getCheckedObjects().get(i).getPlace_id();
//        }
//
//        StringBuilder string = new StringBuilder();
//        for (int i = 0; i < place_ids.length; i++){
//            string.append("\n LA: ").append(place_ids[i]);
//        }
//        Toast toast = Toast.makeText(getApplicationContext(),
//                string.toString(), Toast.LENGTH_SHORT);
//        toast.show();

        if (numbersAdapter.isFlag()) {
            rv_numbers.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 92.0f));
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 8.0f));
        } else {
            rv_numbers.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 100.0f));
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 0.0f));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}