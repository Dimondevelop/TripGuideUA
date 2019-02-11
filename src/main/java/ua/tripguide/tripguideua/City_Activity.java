package ua.tripguide.tripguideua;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import java.util.Objects;

public class City_Activity extends AppCompatActivity {

  private String cityName;
  private int cityId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_city_);

    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);

    Intent intent = getIntent();
    cityName = Objects.requireNonNull(intent.getExtras()).getString("cityName");
    cityId = Objects.requireNonNull(intent.getExtras()).getInt("cityId");

    Objects.requireNonNull(getSupportActionBar()).setTitle("Обране місто - " + cityName);

    LinearLayout chooseIndividual = findViewById(R.id.ll_choose_individual_id);
    LinearLayout chooseFromList = findViewById(R.id.ll_choose_from_list_id);
    LinearLayout createExcursion_ = findViewById(R.id.ll_create_excursion_id);

    OnClickListener typeOfExcursionClickListener = new OnClickListener() {
      @Override
      public void onClick(View view) {
        Context vContext = view.getContext();
        switch (view.getId()){
          case R.id.ll_choose_individual_id:
            Intent newExIntent = new Intent(vContext, ChooseIndividualActivity.class);
            newExIntent.putExtra("cityName", cityName);
            newExIntent.putExtra("cityId", cityId);
            vContext.startActivity(newExIntent);
            break;
          case R.id.ll_choose_from_list_id:
            Intent fromListIntent = new Intent(vContext, FromListActivity.class);
            fromListIntent.putExtra("cityName", cityName);
            fromListIntent.putExtra("cityId", cityId);
            vContext.startActivity(fromListIntent);
            break;
          case R.id.ll_create_excursion_id:
            Intent createExIntent = new Intent(vContext, CreateExcursionActivity.class);
            createExIntent.putExtra("cityName", cityName);
            createExIntent.putExtra("cityId", cityId);
            vContext.startActivity(createExIntent);
            break;
        }
      }
    };

    chooseIndividual.setOnClickListener(typeOfExcursionClickListener);
    chooseFromList.setOnClickListener(typeOfExcursionClickListener);
    createExcursion_.setOnClickListener(typeOfExcursionClickListener);
  }

  //Стрілка назад
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
