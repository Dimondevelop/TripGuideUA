package ua.tripguide.tripguideua;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ua.tripguide.tripguideua.Models.ObjectList;
import ua.tripguide.tripguideua.Utils.DBHelper;

public class CreateExcursionActivity extends AppCompatActivity implements NumbersAdapter.OnObjectClickListener {

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

    float[] coordinates_x;
    float[] coordinates_y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_excursion);

        Intent intent = getIntent();
        String cityName = Objects.requireNonNull(intent.getExtras()).getString("cityName");
        int cityId = Objects.requireNonNull(intent.getExtras()).getInt("cityId");

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Оберіть об'єкти");

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

                    for (int i = 0; i < countCheckedObjects; i++) {
                        coordinates_x[i] = numbersAdapter.getCheckedObjects().get(i).getCoordinate_x();
                        coordinates_y[i] = numbersAdapter.getCheckedObjects().get(i).getCoordinate_y();
                    }

                    Context vContext = v.getContext();
                    Intent intent = new Intent(vContext, RoutesActivity.class);
                    intent.putExtra("coordinates_x", coordinates_x);
                    intent.putExtra("coordinates_y", coordinates_y);
                    vContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onObjectClick(View view, int position) {

        //DEBUG
//        int countCheckedObjects = numbersAdapter.getCheckedObjects().size();
//        coordinates_x = new float[countCheckedObjects];
//        coordinates_y = new float[countCheckedObjects];
//
//        for (int i = 0; i < countCheckedObjects; i++) {
//            coordinates_x[i] = numbersAdapter.getCheckedObjects().get(i).getCoordinate_x();
//            coordinates_y[i] = numbersAdapter.getCheckedObjects().get(i).getCoordinate_y();
//        }
//
//        StringBuilder string = new StringBuilder();
//        for (int i = 0; i < coordinates_x.length; i++){
//            string.append("\n LA: ").append(coordinates_x[i]);
//            string.append(" RO: ").append(coordinates_y[i]);
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

