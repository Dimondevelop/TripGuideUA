package ua.tripguide.tripguideua;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;


import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Objects;

import ua.tripguide.tripguideua.Adapters.ExcursionAdapter;
import ua.tripguide.tripguideua.Models.Excursion;
import ua.tripguide.tripguideua.Models.RouteObjectsInfo;
import ua.tripguide.tripguideua.Utils.DBHelper;

public class FromListActivity extends AppCompatActivity {

    ExcursionAdapter excursionAdapter;
    RecyclerView rv_excursion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_list);

        Intent intent = getIntent();
        int cityId = Objects.requireNonNull(intent.getExtras()).getInt("cityId");
        String cityName = Objects.requireNonNull(intent.getExtras()).getString("cityName");

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Екскурсії в місті " + cityName);

        //об'єкт класу для роботи з бд
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        //метод отримує дані з таблиці з екскурсіями
        ArrayList<Excursion> lstExcursion = dbHelper.getExcurionsFromDB(cityId);

        rv_excursion = findViewById(R.id.rv_excursions);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_excursion.setLayoutManager(layoutManager);
        rv_excursion.setHasFixedSize(true);
        excursionAdapter = new ExcursionAdapter( this, lstExcursion);
        rv_excursion.setAdapter(excursionAdapter);

    }

    // Кнопка назад
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            return true;
        } else if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

//    ArrayList<Integer[]> getExcursionsObjects (ArrayList<Excursion> lstExcursion){
//        ArrayList<Integer[]> ids = new ArrayList<>();
//        for (int i = 0; i < lstExcursion.size(); i++){
//            String[] subStr;
//            String delimeter = ","; // Розділювач
//            subStr = lstExcursion.get(i).getObjects_list().split(delimeter);
//            int count = subStr.length;
//            Integer[] objectsIds = new Integer[count];
//            for (int j = 0; j < count; j++){
//                objectsIds[j] = Integer.valueOf(subStr[j]);
//                ids.add(objectsIds);
//            }
//        }
//        return ids;
//    }
