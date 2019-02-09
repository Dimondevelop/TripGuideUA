package ua.tripguide.tripguideua.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import ua.tripguide.tripguideua.Models.City;
import ua.tripguide.tripguideua.Models.Excursion;
import ua.tripguide.tripguideua.Models.ObjectList;
import ua.tripguide.tripguideua.Models.RouteObjectsInfo;

public class DBHelper extends SQLiteOpenHelper {

    private static String DB_PATH; // Повний шлях до бази даних
    private static String DB_NAME = "MediaDB";
    private static final int VERSION = 1; // версія бази даних
    private static final String TABLE_CITIES = "cities"; // назва таблиці з містами в бд
    private static final String TABLE_OBJECTS = "objects"; // назва таблиці з об'єктами в бд
    private static final String TABLE_EXCURSION = "excursions"; // назва таблиці з екскурсіями в бд
    // назви стовбців таблиці з містами
    private static final String COLUMN_CITY_ID = "_id";
    private static final String COLUMN_CITY_NAME = "name";
    private static final String COLUMN_CITY_THUMBNAIL = "thumbmail";
    private static final String URL_THUMBNAIL_CITIES_SERVER = "http://tripguideua.kl.com.ua/images/cities/";
    // назви стовбців таблиці з об'єктами
    private static final String COLUMN_OBJECT_ID = "_id_object";
    private static final String COLUMN_OBJECT_NAME = "name_object";
    private static final String COLUMN_OBJECT_THUMBNAIL = "thumbnail_object";
    private static final String URL_THUMBNAIL_OBJECT_SERVER = "http://tripguideua.kl.com.ua/images/objects/";
    private static final String COLUMN_OBJECT_PLACE_ID = "place_id";
    private static final String COLUMN_OBJECT_COORDINATE_X = "coordinate_x";
    private static final String COLUMN_OBJECT_COORDINATE_Y = "coordinate_y";
    private static final String COLUMN_CITY_ID_OBJECT = "_id_city_object";
    private static final String COLUMN_OBJECT_DESCRIPTION = "description_object";
    private static final String COLUMN_OBJECT_TYPE = "type_object";
    private static final String COLUMN_OBJECT_WORK_TIME = "working_hours";
    private static final String COLUMN_OBJECT_AVERAGE_DURATION = "average_duration";
    private static final String COLUMN_OBJECT_PRICE = "price";
    private static final String COLUMN_OBJECT_VISIBLE = "visible";
    // назви стовбців таблиці з екскурсіями
    private static final String COLUMN_EXCURSION_ID = "_id_excursion";
    private static final String COLUMN_EXCURSION_NAME = "name_excursion";
    private static final String COLUMN_EXCURSION_LIST_OF_OBJECTS = "objects_list";
    private static final String COLUMN_EXCURSION_ID_CITY = "_id_city_excursion";
    private static final String COLUMN_EXCURSION_TYPE = "type_excutsion";

    private Context myContext;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.myContext = context;
        DB_PATH = context.getFilesDir().getPath() + DB_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private void create_db() {
        InputStream myInput;
        OutputStream myOutput;
        try {
            File file = new File(DB_PATH);
            if (!file.exists()) {
                this.getReadableDatabase();
                //отримуємо локальну бд як поток
                myInput = myContext.getAssets().open(DB_NAME);
                // шлях до нової бд
                String outFileName = DB_PATH;

                // відкриваємо пусту бд
                myOutput = new FileOutputStream(outFileName);

                // побайтово копіюємо дані
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
                myOutput.close();
                myInput.close();
            }
        } catch (IOException ex) {
            Log.d("DatabaseHelper", ex.getMessage());
        }
    }

    private SQLiteDatabase open() throws SQLException {
        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public ArrayList<City> getCitiesFromDB() {
        // створюємо екземпляр бази данних
        create_db();
        // відкриваємо підключення
        SQLiteDatabase db = open();
        //отримуємо дані з бд у вигляді курсора
        Cursor userCursor = db.rawQuery("select * from " + DBHelper.TABLE_CITIES, null);
        ArrayList<City> lstCity = new ArrayList<>();
        if (userCursor.moveToFirst()) {
            int idIndex = userCursor.getColumnIndex(COLUMN_CITY_ID);
            int nameIndex = userCursor.getColumnIndex(COLUMN_CITY_NAME);
            int thumbnailIndex = userCursor.getColumnIndex(COLUMN_CITY_THUMBNAIL);
            do {
                lstCity.add(new City(userCursor.getInt(idIndex),
                        userCursor.getString(nameIndex),
                        URL_THUMBNAIL_CITIES_SERVER + userCursor.getString(thumbnailIndex)));
            } while (userCursor.moveToNext());
        }
        // Закриваємо підключення і курсор
        close();
        db.close();
        userCursor.close();

        return lstCity;
    }

    public ArrayList<ObjectList> getObjectsFromDB(int cityId) {
        // створюємо екземпляр бази данних
        create_db();
        // відкриваємо підключення
        SQLiteDatabase db = open();
        //отримуємо дані з бд у вигляді курсора
        Cursor userCursor = db.rawQuery("select * from " + TABLE_OBJECTS + " where " + COLUMN_CITY_ID_OBJECT + " = " + cityId +
                " and " + COLUMN_OBJECT_VISIBLE + " = 1  order by " + COLUMN_OBJECT_NAME + " asc ", null);

        ArrayList<ObjectList> lstObjectList = new ArrayList<>();

        if (userCursor.moveToFirst()) {
            int idObjectIndex = userCursor.getColumnIndex(COLUMN_OBJECT_ID);
            int nameObjectIndex = userCursor.getColumnIndex(COLUMN_OBJECT_NAME);
            int thumbnailObjectIndex = userCursor.getColumnIndex(COLUMN_OBJECT_THUMBNAIL);
            int placeIdIndex = userCursor.getColumnIndex(COLUMN_OBJECT_PLACE_ID);
            int coordinateXIndex = userCursor.getColumnIndex(COLUMN_OBJECT_COORDINATE_X);
            int coordinateYIndex = userCursor.getColumnIndex(COLUMN_OBJECT_COORDINATE_Y);
            int idCityObjectIndex = userCursor.getColumnIndex(COLUMN_CITY_ID_OBJECT);
            int descriptionObjectIndex = userCursor.getColumnIndex(COLUMN_OBJECT_DESCRIPTION);
            int typeObjectIndex = userCursor.getColumnIndex(COLUMN_OBJECT_TYPE);
            int workTimeObjectIndex = userCursor.getColumnIndex(COLUMN_OBJECT_WORK_TIME);
            int averageDurationObjectIndex = userCursor.getColumnIndex(COLUMN_OBJECT_AVERAGE_DURATION);
            int priceObjectIndex = userCursor.getColumnIndex(COLUMN_OBJECT_PRICE);

            do {
                lstObjectList.add(new ObjectList(userCursor.getInt(idObjectIndex),
                        userCursor.getString(nameObjectIndex),
                        URL_THUMBNAIL_OBJECT_SERVER + cityId + "/" + userCursor.getString(thumbnailObjectIndex),
                        userCursor.getString(placeIdIndex),
                        userCursor.getFloat(coordinateXIndex),
                        userCursor.getFloat(coordinateYIndex),
                        userCursor.getInt(idCityObjectIndex),
                        userCursor.getString(descriptionObjectIndex),
                        userCursor.getString(typeObjectIndex),
                        userCursor.getString(workTimeObjectIndex),
                        userCursor.getInt(averageDurationObjectIndex),
                        userCursor.getInt(priceObjectIndex)
                ));
            } while (userCursor.moveToNext());
            // Закриваємо підключення і курсор
            close();
            db.close();
            userCursor.close();
        }
        return lstObjectList;
    }

    public ArrayList<Excursion> getExcurionsFromDB(int cityId) {
        // створюємо екземпляр бази данних
        create_db();
        // відкриваємо підключення
        SQLiteDatabase db = open();
        //отримуємо дані з бд у вигляді курсора
        Cursor userCursor = db.rawQuery("select * from " + TABLE_EXCURSION + " where " + COLUMN_EXCURSION_ID_CITY + " = " + cityId, null);

        ArrayList<Excursion> lstExcursion = new ArrayList<>();

        if (userCursor.moveToFirst()) {
            int idExcursionIndex = userCursor.getColumnIndex(COLUMN_EXCURSION_ID);
            int nameExcursionIndex = userCursor.getColumnIndex(COLUMN_EXCURSION_NAME);
            int objectsListIndex = userCursor.getColumnIndex(COLUMN_EXCURSION_LIST_OF_OBJECTS);
            int idCityExcursionIndex = userCursor.getColumnIndex(COLUMN_EXCURSION_ID_CITY);
            int typeExcutsionIndex = userCursor.getColumnIndex(COLUMN_EXCURSION_LIST_OF_OBJECTS);

            do {
                Cursor userCursorObj = db.rawQuery("select " + COLUMN_OBJECT_NAME + "," + COLUMN_OBJECT_PLACE_ID + "," + COLUMN_OBJECT_WORK_TIME + "," +
                        COLUMN_OBJECT_AVERAGE_DURATION + "," + COLUMN_OBJECT_COORDINATE_X + "," + COLUMN_OBJECT_COORDINATE_Y +
                        " from " + TABLE_OBJECTS + " where " + COLUMN_OBJECT_ID + " IN (" + userCursor.getString(typeExcutsionIndex) + ") and " + COLUMN_CITY_ID_OBJECT + " = " + cityId, null);
                if (userCursorObj.moveToFirst()) {
                    lstExcursion.add(new Excursion(
                            userCursor.getInt(idExcursionIndex),
                            userCursor.getString(nameExcursionIndex),
                            userCursor.getString(objectsListIndex),
                            userCursor.getInt(idCityExcursionIndex),
                            userCursor.getString(typeExcutsionIndex),
                            getROIFromCursor(userCursorObj)
                    ));
                }

            } while (userCursor.moveToNext());

            // Закриваємо підключення і курсор
            close();
            db.close();
            userCursor.close();
        }
        return lstExcursion;
    }

//    private Integer[] getExcursionsObjectsIds(String s) {
//        String[] subStr = s.split(",");
//        int count = subStr.length;
//        Integer[] objectsIds = new Integer[count];
//        for (int j = 0; j < count; j++) {
//            objectsIds[j] = Integer.valueOf(subStr[j]);
//        }
//        return objectsIds;
//    }

                //ROI (RouteObjectsInfo) - загальна модель екскурсійного об'єкту з обмеженим набором
                // атрибутів, потрібних для прокладання маршруту
    private ArrayList<RouteObjectsInfo> getROIFromCursor(Cursor userCursorObj) {
        ArrayList<RouteObjectsInfo> lstRouteObjectsInfos = new ArrayList<>();

        int placeIdIndex = userCursorObj.getColumnIndex(COLUMN_OBJECT_PLACE_ID);
        int nameObjectIndex = userCursorObj.getColumnIndex(COLUMN_OBJECT_NAME);
        int workTimeObjectIndex = userCursorObj.getColumnIndex(COLUMN_OBJECT_WORK_TIME);
        int averageDurationObjectIndex = userCursorObj.getColumnIndex(COLUMN_OBJECT_AVERAGE_DURATION);
        int coordinateXIndex = userCursorObj.getColumnIndex(COLUMN_OBJECT_COORDINATE_X);
        int coordinateYIndex = userCursorObj.getColumnIndex(COLUMN_OBJECT_COORDINATE_Y);
        do {
            lstRouteObjectsInfos.add(new RouteObjectsInfo(
                    userCursorObj.getString(placeIdIndex),
                    userCursorObj.getString(nameObjectIndex),
                    userCursorObj.getString(workTimeObjectIndex),
                    userCursorObj.getInt(averageDurationObjectIndex),
                    new LatLng(userCursorObj.getFloat(coordinateXIndex),
                            userCursorObj.getFloat(coordinateYIndex))));

        } while (userCursorObj.moveToNext());

        return lstRouteObjectsInfos;
    }
}

//select * from objects where _id_object in (1,3,5,7)