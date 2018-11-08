package ua.tripguide.tripguideua;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import ua.tripguide.tripguideua.Utils.DBHelper;

public class ChooseIndividualActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnAdd, btnRead, btnClear;
    EditText etName, etEmail;
    TextView tvRead;
    ImageView iv_test;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_individual);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Підбір екскурсії");

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnRead = findViewById(R.id.btnRead);
        btnRead.setOnClickListener(this);

        btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);

        tvRead = findViewById(R.id.tvRead);
        iv_test = findViewById(R.id.iv_test);

        try {
            iv_test.setImageResource(R.drawable.class.getField("chernigiv").getInt(getResources()));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

//        dbHelper = new DBHelper(this,MainActivity.DB_NAME, MainActivity.versionDB);
    }

    @Override
    public void onClick(View view) {
//        String name = etName.getText().toString();
//        String email = etEmail.getText().toString();
//
//
//        SQLiteDatabase database = dbHelper.getWritableDatabase();
//
//        ContentValues contentValues = new ContentValues();
//
//        switch (view.getId()) {
//            case R.id.btnAdd:
//                contentValues.put(DBHelper.KEY_NAME, name);
//                contentValues.put(DBHelper.KEY_MAIL, email);
//
//                database.insert(DBHelper.TABLE_CITIES, null, contentValues);
//                break;
//            case R.id.btnRead:
//                Cursor cursor = database.query(DBHelper.TABLE_CITIES, null, null, null, null, null, null);
//
//                if (cursor.moveToFirst()) {
//                    int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
//                    int keyExCountIndex = cursor.getColumnIndex(DBHelper.KEY_EXCURSION_COUNT);
//                    int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
//                    int thumbnailIndex = cursor.getColumnIndex(DBHelper.KEY_THUMBNAIL);
//                    tvRead.setText("");
//                    do {
//                        tvRead.append("ID = " + cursor.getInt(idIndex) +
//                                ", [" + cursor.getColumnName(keyExCountIndex) + "] = " + cursor.getString(keyExCountIndex) +
//                                ", [" + cursor.getColumnName(nameIndex) + "] = " + cursor.getString(nameIndex) +
//                                ", [" + cursor.getColumnName(thumbnailIndex) + "] = " + cursor.getString(thumbnailIndex) + "\n");
//
//
//                    } while (cursor.moveToNext());
//                } else {
//                    tvRead.setText("0 rows");
//                }
//                break;
//            case R.id.btnClear:
//                database.delete(DBHelper.TABLE_CITIES, null, null);
////                dbHelper.onUpgrade(database,0,1);
//                break;
//        }
//        dbHelper.close();
    }

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
