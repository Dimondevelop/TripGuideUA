package ua.tripguide.tripguideua;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Objects;

import ua.tripguide.tripguideua.Adapters.ExcursionSpinnerAdapter;
import ua.tripguide.tripguideua.Models.ObjectList;
import ua.tripguide.tripguideua.Models.RouteObjectsInfo;
import ua.tripguide.tripguideua.Models.SpinnerList;
import ua.tripguide.tripguideua.Utils.DBHelper;

public class ChooseIndividualActivity extends AppCompatActivity {

    private static final String TAG = ChooseIndividualActivity.class.getSimpleName();
    LinearLayout llCreateExcurion;

    ArrayList<ObjectList> lstObjectList;
    ArrayList<RouteObjectsInfo> lstROI;
    float[] coordinates_x;
    float[] coordinates_y;
    String[] titles;
    String[] working_hours;
    String[] place_ids;
    int[] average_duration;

    ArrayList<SpinnerList> priceOfExcursionList;
    Spinner spTypeSpinner;
    Spinner spPriceSpinner;
    Spinner spDurationSpinner;
    CheckBox chb_visible;
    LinearLayout ll_visible;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_individual);

        Intent intent = getIntent();
        String cityName = Objects.requireNonNull(intent.getExtras()).getString("cityName");
        int cityId = Objects.requireNonNull(intent.getExtras()).getInt("cityId");

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Підбір екскурсії в місті " + cityName);

        //об'єкт класу для роботи з бд
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        lstObjectList = dbHelper.getObjectsFromDB(cityId);


//        try {
//            iv_test.setImageResource(R.drawable.class.getField("chernigiv").getInt(getResources()));
//        } catch (IllegalAccessException | NoSuchFieldException e) {
//            e.printStackTrace();
//        }

        ArrayList<SpinnerList> typeOfExcursionList = new ArrayList<>();

        typeOfExcursionList.add(new SpinnerList("актуальний", "Актуальна",
                "Прокласти маршрут екскурсії з найбільш актуальними в даний період місцями", R.drawable.actual_blue));
        typeOfExcursionList.add(new SpinnerList("оглядова", "Оглядова",
                "Багатотемна екскурсія історичного та сучасного направлення. Демонстрування пам'яток історії та культури, будівель та споруд," +
                        "природних об'єктів, місць знаменитих подій, елементів благоустрою міста, промислових та сільськогосподарських виробництв тощо", R.drawable.sightseeing));
        typeOfExcursionList.add(new SpinnerList("пам'ятка", "Пам'ятки",
                "Пам'ятні місця, пов'язані з історичними подіями в житті нашого народу, розвитком суспільства і держави", R.drawable.attractions));
        typeOfExcursionList.add(new SpinnerList("архітектура", "Архітектура",
                "Будівлі та споруди, меморіальні пам'ятники, пов'язані з життям і діяльністю видатних особистостей, " +
                        "твори архітектури і містобудування, житлові і громадські будівлі, будівлі промислових підприємств, інженерні споруди " +
                        "(фортеці, мости, башти), мавзолеї, будівлі культурного призначення та інші споруди", R.drawable.architecture));
        typeOfExcursionList.add(new SpinnerList("природа", "Природа",
                "Природні об'єкти - ліси, гаї, парки, річки, озера, ставки, заповідники, заказники, окремі дерева, реліктові рослини\n" +
                        "\n", R.drawable.nature));
        typeOfExcursionList.add(new SpinnerList("музей", "Музеї",
                "Експозиції державних і народних музеїв, картинних галерей, постійних і тимчасових виставок", R.drawable.museum));
        typeOfExcursionList.add(new SpinnerList("обрати", "Обрати декілька",
                "Прокласти маршрут з додаванням до нього місць різних типів, обраних користувачем", R.drawable.custom));

        spTypeSpinner = findViewById(R.id.sp_type_spinner);
        ExcursionSpinnerAdapter spinnerOneAdapter = new ExcursionSpinnerAdapter(this, typeOfExcursionList);
        spTypeSpinner.setPrompt("Оберіть бажаний тип екскурсії");
//        spTypeSpinner.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_ATOP);
        spTypeSpinner.setAdapter(spinnerOneAdapter);


        priceOfExcursionList = new ArrayList<>();
        priceOfExcursionList.add(new SpinnerList("122", "Будь-яка",
                "Додавати до маршруту всі підходящі по обраних критеріяк туристичні об'єкти, незалежно від ціни", R.drawable.question));
        priceOfExcursionList.add(new SpinnerList("0", "Безкоштовно",
                "Прокласти маршрут з безкоштовними туристичними об'єктами", R.drawable.free_price));
        priceOfExcursionList.add(new SpinnerList("60", "Економний варіант",
                "Надавати перевагу туристичним об'єктам з нижчою ціною", R.drawable.cheap_price));
        priceOfExcursionList.add(new SpinnerList("120", "Щось середнє",
                "Додати до маршруту декілька платних туристичних об'єктів, якщо вони більше підходять по обраних критеріях", R.drawable.normal_price));


        spPriceSpinner = findViewById(R.id.sp_price_spinner);
        ExcursionSpinnerAdapter spinnerTwoAdapter = new ExcursionSpinnerAdapter(this, priceOfExcursionList);
        spPriceSpinner.setPrompt("Оберіть бажану ціну екскурсії");
        spPriceSpinner.setAdapter(spinnerTwoAdapter);

        ArrayList<SpinnerList> durationOfExursionList = new ArrayList<>();

        durationOfExursionList.add(new SpinnerList("0", "Будь-яка",
                "Не обмежувати маршрут за тривалістю", R.drawable.question));
        durationOfExursionList.add(new SpinnerList("2", "До 2-х годин",
                "Внести до маршруту тільки найближчі за відстанню та найпопулярніші екскурсійні об'єкти, " +
                        "відвідування яких не займе багато часу, щоб вкластись в 2 години", R.drawable.clock_up_to_2));
        durationOfExursionList.add(new SpinnerList("4", "До 4-ти годин",
                "Обмежити маршрут, виключивши з нього віддалені або довготривалі екскурсії, щоб вкластись в 4 години", R.drawable.clock_up_to_4));
        durationOfExursionList.add(new SpinnerList("5", "До 5-ти годин",
                "Обмежити маршрут, виключивши з нього віддалені або довготривалі екскурсії, щоб вкластись в 5 годин", R.drawable.clock_up_to_5));
        durationOfExursionList.add(new SpinnerList("6", "До 6-ти годин",
                "Обмежити маршрут, виключивши з нього віддалені або довготривалі екскурсії, щоб вкластись в 6 годин", R.drawable.clock_up_to_6));
        durationOfExursionList.add(new SpinnerList("8", "До 8-ми годин",
                "Обмежити маршрут, виключивши з нього віддалені або довготривалі екскурсії, щоб вкластись в 8 годин", R.drawable.clock_up_to_8));

        spDurationSpinner = findViewById(R.id.sp_duration_spinner);
        ExcursionSpinnerAdapter spinnerThreeAdapter = new ExcursionSpinnerAdapter(this, durationOfExursionList);
        spDurationSpinner.setPrompt("Оберіть бажану тривалість екскурсії");
        spDurationSpinner.setAdapter(spinnerThreeAdapter);


        chb_visible = findViewById(R.id.chb_visible);
        ll_visible = findViewById(R.id.ll_visible);
        ll_visible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!chb_visible.isChecked()) {

                } else if (chb_visible.isChecked()) {

                }

                chb_visible.toggle();
            }
        });


        llCreateExcurion = findViewById(R.id.ll_create_individual_excursion);

        llCreateExcurion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lstROI = new ArrayList<>();
                int selectedPrice = Integer.valueOf(spPriceSpinner.getSelectedItem().toString());


                for (int i = 0; i < lstObjectList.size(); i++) {
                    if (lstObjectList.get(i).getType_object().contains(spTypeSpinner.getSelectedItem().toString())) {
                        if (lstObjectList.get(i).getPrice() <= selectedPrice) {

                            lstROI.add(new RouteObjectsInfo(lstObjectList.get(i).getPlace_id(), lstObjectList.get(i).getName_object(),
                                    lstObjectList.get(i).getWorking_hours(), lstObjectList.get(i).getAverage_duration(),
                                    (new LatLng(lstObjectList.get(i).getCoordinate_x(), lstObjectList.get(i).getCoordinate_y()))));

                            Log.i(TAG, "ObjectList(debug): " + lstObjectList.get(i).getName_object()
                                    + " \nТип: " + lstObjectList.get(i).getType_object()
                                    + "\nЦіна: " + lstObjectList.get(i).getPrice()
                                    + "\nТривалість: " + lstObjectList.get(i).getAverage_duration() + "хв");
                        }
                    }
                }
                int countAvg = 0;

                if (lstROI.isEmpty()) {

                    Context mContext = ChooseIndividualActivity.this;

                    TextView title = new TextView(mContext);
                    title.setText("Увага!");
                    title.setPadding(10, 15, 15, 10);
                    title.setGravity(Gravity.CENTER);
                    title.setTextColor(getResources().getColor(R.color.colorBlackLight));
                    title.setTextSize(19);

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setCustomTitle(title)
                            .setMessage("В обраному місті не знайдено результатів з такими параметрами! Спробуйте інші параметри.")
                            .setCancelable(false)
                            .setNegativeButton("ОК",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    builder.create();
                    AlertDialog alert = builder.show();

                    TextView messageView = alert
                            .findViewById(android.R.id.message);
                    if (messageView != null) {
                        messageView.setGravity(Gravity.CENTER);
                    }


                } else {
                    for (int i = 0; i < lstROI.size(); i++) {
                        countAvg += lstROI.get(i).getAverage_duration();
                        Log.i(TAG, "LST(debug): " + lstROI.get(i).getTitle() + " - " + lstROI.get(i).getAverage_duration() + "хв");
                    }
                    int selectedMinutes = Integer.valueOf(spDurationSpinner.getSelectedItem().toString()) * 60;
                    Log.i(TAG, "Всього хвилин(debug): " + countAvg + "хв id: " + spTypeSpinner.getSelectedItemId());
                    lstROI = new ArrayList<>(cutDownRoute(countAvg, selectedMinutes, lstROI));
                    countAvg = 0;
                    for (int i = 0; i < lstROI.size(); i++) {
                        countAvg += lstROI.get(i).getAverage_duration();
                        Log.i(TAG, "LST(debug2): " + lstROI.get(i).getTitle() + " - " + lstROI.get(i).getAverage_duration() + "хв");
                    }
                    Log.i(TAG, "Всього хвилин(debug2): " + countAvg + "хв id: " + spTypeSpinner.getSelectedItemId());

                    int countObjects = lstROI.size();
                    coordinates_x = new float[countObjects];
                    coordinates_y = new float[countObjects];
                    titles = new String[countObjects];
                    working_hours = new String[countObjects];
                    place_ids = new String[countObjects];
                    average_duration = new int[countObjects];

                    for (int i = 0; i < countObjects; i++) {
                        coordinates_x[i] = (float) lstROI.get(i).getLatLng().latitude;
                        coordinates_y[i] = (float) lstROI.get(i).getLatLng().longitude;
                        titles[i] = lstROI.get(i).getTitle();
                        working_hours[i] = lstROI.get(i).getWorking_hour();
                        place_ids[i] = lstROI.get(i).getPlace_id();
                        average_duration[i] = lstROI.get(i).getAverage_duration();
                    }

                    Context vContext = v.getContext();
                    Intent intent = new Intent(vContext, RoutesActivity.class);
                    intent.putExtra("coordinates_x", coordinates_x);
                    intent.putExtra("coordinates_y", coordinates_y);
                    intent.putExtra("titles", titles);
                    intent.putExtra("working_hours", working_hours);
                    intent.putExtra("place_ids", place_ids);
                    intent.putExtra("average_duration", average_duration);
                    vContext.startActivity(intent);
                }
            }
        });
    }

    private ArrayList<RouteObjectsInfo> cutDownRoute(int countMinutes, int selectedMinutes, ArrayList<RouteObjectsInfo> routeObjectsInfos) {
        if (selectedMinutes == 0) {
            return routeObjectsInfos;
        } else if (countMinutes > selectedMinutes) {
            routeObjectsInfos = new ArrayList<>(RoutesActivity.sortLatLng(routeObjectsInfos));
            routeObjectsInfos.remove(routeObjectsInfos.size() - 1);


            countMinutes = 0;
            for (int i = 0; i < routeObjectsInfos.size(); i++) {
                countMinutes += routeObjectsInfos.get(i).getAverage_duration();
            }

            return cutDownRoute(countMinutes, selectedMinutes, routeObjectsInfos);
        } else
            return routeObjectsInfos;
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
