package ua.tripguide.tripguideua;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ua.tripguide.tripguideua.Models.ObjectList;

public class CreateExcursionActivity extends AppCompatActivity implements NumbersAdapter.OnCardClickListener{

    List<ObjectList> lstObjectList;

    // назви стовбців
    static final String COLUMN_OBJECT_ID = "_id_object";
    static final String COLUMN_OBJECT_NAME = "name_object";
    static final String COLUMN_OBJECT_THUMBNAIL = "thumbnail_object";
    static final String COLUMN_OBJECT_COORDINATE_X = "coordinate_x";
    static final String COLUMN_OBJECT_COORDINATE_Y = "coordinate_y";
    static final String COLUMN_CITY_ID_OBJECT = "_id_city_object";
    static final String COLUMN_OBJECT_DESCRIPTION = "description_object";
    static final String COLUMN_OBJECT_TYPE = "type_object";
    static final String COLUMN_OBJECT_WORK_TIME = "working_hours";

    //об'єкти класу для роботи з бд
    DBHelper dbHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    LinearLayout linearLayout;
    NumbersAdapter numbersAdapter;
    RecyclerView rv_numbers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_excursion);

        Intent intent = getIntent();
        String cityName = Objects.requireNonNull(intent.getExtras()).getString("cityName");
        int cityId = Objects.requireNonNull(intent.getExtras()).getInt("cityId");

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Оберіть об'єкти для створення екскурсії в місті " + cityName);


        dbHelper = new DBHelper(getApplicationContext());
        // создаем базу данных
        dbHelper.create_db();
        // открываем подключение
        db = dbHelper.open();
        //получаем данные из бд в виде курсора
        userCursor = db.rawQuery("select * from " + dbHelper.TABLE_OBJECTS + " where " + COLUMN_CITY_ID_OBJECT + " = " + cityId, null);

        lstObjectList = new ArrayList<>();

        if (userCursor.moveToFirst()) {
            int idObjectIndex = userCursor.getColumnIndex(COLUMN_OBJECT_ID);
            int nameObjectIndex = userCursor.getColumnIndex(COLUMN_OBJECT_NAME);
            int thumbnailObjectIndex = userCursor.getColumnIndex(COLUMN_OBJECT_THUMBNAIL);
            int coordinateXIndex = userCursor.getColumnIndex(COLUMN_OBJECT_COORDINATE_X);
            int coordinateYIndex = userCursor.getColumnIndex(COLUMN_OBJECT_COORDINATE_Y);
            int idCityObjectIndex = userCursor.getColumnIndex(COLUMN_CITY_ID_OBJECT);
            int descriptionObjectIndex = userCursor.getColumnIndex(COLUMN_OBJECT_DESCRIPTION);
            int typeObjectIndex = userCursor.getColumnIndex(COLUMN_OBJECT_TYPE);
            int workTimeObjectIndex = userCursor.getColumnIndex(COLUMN_OBJECT_WORK_TIME);

            do {
                lstObjectList.add(new ObjectList(userCursor.getInt(idObjectIndex),
                        userCursor.getString(nameObjectIndex),
                        userCursor.getString(thumbnailObjectIndex),
                        userCursor.getFloat(coordinateXIndex),
                        userCursor.getFloat(coordinateYIndex),
                        userCursor.getInt(idCityObjectIndex),
                        userCursor.getString(descriptionObjectIndex),
                        userCursor.getString(typeObjectIndex),
                        userCursor.getString(workTimeObjectIndex)));
            } while (userCursor.moveToNext());

            rv_numbers = findViewById(R.id.rv_numbers);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            rv_numbers.setLayoutManager(layoutManager);
            rv_numbers.setHasFixedSize(true);
            numbersAdapter = new NumbersAdapter(lstObjectList.size(), this, lstObjectList);
            numbersAdapter.setOnCardClickListener(this);
            rv_numbers.setAdapter(numbersAdapter);
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
    @Override
    public void onCardClick(View view, int position) {
        if (numbersAdapter.isFlag()){
            rv_numbers.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 92.0f));
            linearLayout = findViewById(R.id.ll_create_excursion_with_objects);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 8.0f));
        } else {
            rv_numbers.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 100.0f));
            linearLayout = findViewById(R.id.ll_create_excursion_with_objects);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 0.0f));
        }
    }
}




//
//            do {
//                    try {
//                    lstObjectList.add(new ObjectList(userCursor.getInt(idObjectIndex),
//                    userCursor.getString(nameObjectIndex),
//                    R.drawable.class.getField(userCursor.getString(thumbnailObjectIndex)).getInt(getResources()),
//        userCursor.getFloat(coordinateXIndex),
//        userCursor.getFloat(coordinateYIndex),
//        userCursor.getInt(idCityObjectIndex),
//        userCursor.getString(descriptionObjectIndex),
//        userCursor.getString(typeObjectIndex),
//        userCursor.getString(workTimeObjectIndex)));
//        } catch (IllegalAccessException | NoSuchFieldException e) {
//        e.printStackTrace();
//        }
//        } while (userCursor.moveToNext());

