package ua.tripguide.tripguideua;

import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

 class DBHelper extends SQLiteOpenHelper {

    private static String DB_PATH; // Повний шлях до бази даних
    private static String DB_NAME = "MediaDB";
    private static final int VERSION = 1; // версія бази даних
    static final String TABLE_CITIES = "cities"; // назва таблиці з містами в бд
    static final String TABLE_OBJECTS = "objects"; // назва таблиці з містами в бд



    private Context myContext;

     DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.myContext=context;
        DB_PATH =context.getFilesDir().getPath() + DB_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    void create_db(){
        InputStream myInput = null;
        OutputStream myOutput = null;
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
        }
        catch(IOException ex){
            Log.d("DatabaseHelper", ex.getMessage());
        }
    }
    SQLiteDatabase open()throws SQLException {
        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }
}
