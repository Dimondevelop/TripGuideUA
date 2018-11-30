package ua.tripguide.tripguideua;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import ua.tripguide.tripguideua.Adapters.RecyclerViewAdapter;
import ua.tripguide.tripguideua.Models.City;
import ua.tripguide.tripguideua.Utils.DBHelper;

public class MainActivity extends AppCompatActivity {

    //список з містами
    List<City> lstCity;

//    //меню
//    MenuItem action_mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Оберіть місто");

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        lstCity = dbHelper.getCitiesFromDB();

        RecyclerView myrv = findViewById(R.id.recyclerview_id);
        myrv.setHasFixedSize(true);
        RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(lstCity.size(),this, lstCity);
        myrv.setLayoutManager(new GridLayoutManager(this, 2));
        myrv.setAdapter(myAdapter);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

//        action_mail = menu.findItem(R.id.action_mail);

        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Toast.makeText(MainActivity.this, getString(R.string.action_settings), Toast.LENGTH_SHORT).show();
                break;
//            case R.id.action_mail:
//                Toast.makeText(MainActivity.this, getString(R.string.action_email), Toast.LENGTH_SHORT).show();
//                break;
            case R.id.action_menu_SuggestAnObject:
                Toast.makeText(MainActivity.this, getString(R.string.action_menu_SuggestAnObject), Toast.LENGTH_SHORT).show();
                break;
//            case R.id.action_menu_2:
//                Toast.makeText(MainActivity.this, getString(R.string.action_menu_2), Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.action_menu_3:
//                Toast.makeText(MainActivity.this, getString(R.string.action_menu_3), Toast.LENGTH_SHORT).show();
//                break;
        }
        return super.onOptionsItemSelected(item);
    }
}


// do {
//         try {
//         lstCity.add(new City(userCursor.getInt(idIndex),userCursor.getString(nameIndex),
//         R.drawable.class.getField(userCursor.getString(thumbnailIndex)).getInt(getResources())));
//        } catch (IllegalAccessException | NoSuchFieldException e) {
//        e.printStackTrace();
//        }
//        } while (userCursor.moveToNext());
