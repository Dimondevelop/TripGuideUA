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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import ua.tripguide.tripguideua.Adapters.ExcursionSpinnerAdapter;
import ua.tripguide.tripguideua.Models.ObjectList;
import ua.tripguide.tripguideua.Models.RouteObjectsInfo;
import ua.tripguide.tripguideua.Models.SpinnerList;
import ua.tripguide.tripguideua.Utils.DBHelper;
import ua.tripguide.tripguideua.Utils.SelectAgainSpinner;
import ua.tripguide.tripguideua.Utils.UtilMethods;

public class ChooseIndividualActivity extends AppCompatActivity {

    private static final String TAG = ChooseIndividualActivity.class.getSimpleName();
    Context mContext;
    LinearLayout llCreateExcurion;

    ArrayList<SpinnerList> typeOfExcursionList;
    CheckBox checkBoxActual, checkBoxSightseeing, checkBoxAttractions, checkBoxArchitecture, checkBoxNature, checkBoxMuseum;
    LinearLayout llActual, llSightseeing, llAttractions, llArchitecture, llNature, llMuseum;

    ExcursionSpinnerAdapter spinnerOneAdapter;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    ArrayList<String> types;
    ArrayList<String> typesNames;

    ArrayList<ObjectList> lstObjectList;
    ArrayList<RouteObjectsInfo> lstROI;
    float[] coordinates_x;
    float[] coordinates_y;
    String[] titles;
    String[] working_hours;
    String[] place_ids;
    int[] average_duration;

    ArrayList<SpinnerList> priceOfExcursionList;
    SelectAgainSpinner spTypeSpinner;
    Spinner spPriceSpinner;
    Spinner spDurationSpinner;
    CheckBox chb_visible;
    LinearLayout ll_visible;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_individual);

        mContext = ChooseIndividualActivity.this;

        Intent intent = getIntent();
        String cityName = Objects.requireNonNull(intent.getExtras()).getString("cityName");
        int cityId = Objects.requireNonNull(intent.getExtras()).getInt("cityId");

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Підбір екскурсії в місті " + cityName);

        //об'єкт класу для роботи з бд
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        lstObjectList = dbHelper.getObjectsFromDB(cityId);


        typeOfExcursionList = new ArrayList<>();

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
        spinnerOneAdapter = new ExcursionSpinnerAdapter(this, typeOfExcursionList);
        spTypeSpinner.setPrompt("Оберіть бажаний тип екскурсії");
//        spTypeSpinner.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_ATOP);
        spTypeSpinner.setAdapter(spinnerOneAdapter);
        spTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (id != 6) {
                    if (types != null && typesNames != null) {
                        types.clear();
                        types = null;
                        typesNames.clear();
                        typesNames = null;
                    }
                }
                if (id == 6) {
                    View checkBoxView = View.inflate(mContext, R.layout.custom_types, null);

                    builder = new AlertDialog.Builder(mContext);
                    checkBoxActual = checkBoxView.findViewById(R.id.checkbox_actual);
                    checkBoxSightseeing = checkBoxView.findViewById(R.id.checkbox_sightseeing);
                    checkBoxAttractions = checkBoxView.findViewById(R.id.checkbox_attractions);
                    checkBoxArchitecture = checkBoxView.findViewById(R.id.checkbox_architecture);
                    checkBoxNature = checkBoxView.findViewById(R.id.checkbox_nature);
                    checkBoxMuseum = checkBoxView.findViewById(R.id.checkbox_museum);

                    llActual = checkBoxView.findViewById(R.id.ll_checkbox_actual);
                    llSightseeing = checkBoxView.findViewById(R.id.ll_checkbox_sightseeing);
                    llAttractions = checkBoxView.findViewById(R.id.ll_checkbox_attractions);
                    llArchitecture = checkBoxView.findViewById(R.id.ll_checkbox_architecture);
                    llNature = checkBoxView.findViewById(R.id.ll_checkbox_nature);
                    llMuseum = checkBoxView.findViewById(R.id.ll_checkbox_museum);

                    if (types != null && typesNames != null) {
                        types.clear();
                        types = null;
                        typesNames.clear();
                        typesNames = null;
                    }
                    types = new ArrayList<>();
                    typesNames = new ArrayList<>();

                    View.OnClickListener checkBoxes = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (v.getId()) {
                                case R.id.ll_checkbox_actual:
                                    checkBoxActual.toggle();
                                    if (checkBoxActual.isChecked()) {
                                        types.add(typeOfExcursionList.get(0).getSpinnerValue());
                                        typesNames.add(typeOfExcursionList.get(0).getSpinnerName());
                                    } else if (!checkBoxActual.isChecked()) {
                                        types.remove(typeOfExcursionList.get(0).getSpinnerValue());
                                        typesNames.remove(typeOfExcursionList.get(0).getSpinnerName());
                                    }
                                    break;
                                case R.id.ll_checkbox_sightseeing:
                                    checkBoxSightseeing.toggle();
                                    if (checkBoxSightseeing.isChecked()) {
                                        types.add(typeOfExcursionList.get(1).getSpinnerValue());
                                        typesNames.add(typeOfExcursionList.get(1).getSpinnerName());
                                    } else if (!checkBoxSightseeing.isChecked()) {
                                        types.remove(typeOfExcursionList.get(1).getSpinnerValue());
                                        typesNames.remove(typeOfExcursionList.get(1).getSpinnerName());
                                    }
                                    break;
                                case R.id.ll_checkbox_attractions:
                                    checkBoxAttractions.toggle();
                                    if (checkBoxAttractions.isChecked()) {
                                        types.add(typeOfExcursionList.get(2).getSpinnerValue());
                                        typesNames.add(typeOfExcursionList.get(2).getSpinnerName());
                                    } else if (!checkBoxAttractions.isChecked()) {
                                        types.remove(typeOfExcursionList.get(2).getSpinnerValue());
                                        typesNames.remove(typeOfExcursionList.get(2).getSpinnerName());
                                    }
                                    break;
                                case R.id.ll_checkbox_architecture:
                                    checkBoxArchitecture.toggle();
                                    if (checkBoxArchitecture.isChecked()) {
                                        types.add(typeOfExcursionList.get(3).getSpinnerValue());
                                        typesNames.add(typeOfExcursionList.get(3).getSpinnerName());
                                    } else if (!checkBoxArchitecture.isChecked()) {
                                        types.remove(typeOfExcursionList.get(3).getSpinnerValue());
                                        typesNames.remove(typeOfExcursionList.get(3).getSpinnerName());
                                    }
                                    break;
                                case R.id.ll_checkbox_nature:
                                    checkBoxNature.toggle();
                                    if (checkBoxNature.isChecked()) {
                                        types.add(typeOfExcursionList.get(4).getSpinnerValue());
                                        typesNames.add(typeOfExcursionList.get(4).getSpinnerName());
                                    } else if (!checkBoxNature.isChecked()) {
                                        types.remove(typeOfExcursionList.get(4).getSpinnerValue());
                                        typesNames.remove(typeOfExcursionList.get(4).getSpinnerName());
                                    }

                                    break;
                                case R.id.ll_checkbox_museum:
                                    checkBoxMuseum.toggle();
                                    if (checkBoxMuseum.isChecked()) {
                                        types.add(typeOfExcursionList.get(5).getSpinnerValue());
                                        typesNames.add(typeOfExcursionList.get(5).getSpinnerName());
                                    } else if (!checkBoxMuseum.isChecked()) {
                                        types.remove(typeOfExcursionList.get(5).getSpinnerValue());
                                        typesNames.remove(typeOfExcursionList.get(5).getSpinnerName());
                                    }
                                    break;
                            }
                        }
                    };

                    llActual.setOnClickListener(checkBoxes);
                    llSightseeing.setOnClickListener(checkBoxes);
                    llAttractions.setOnClickListener(checkBoxes);
                    llArchitecture.setOnClickListener(checkBoxes);
                    llNature.setOnClickListener(checkBoxes);
                    llMuseum.setOnClickListener(checkBoxes);


                    builder.setOnCancelListener(
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    dialog.cancel();
                                    if (types != null && typesNames != null) {
                                        types.clear();
                                        typesNames.clear();
                                    }
                                    spTypeSpinner.setSelection(0);
                                }
                            }
                    );

                    builder.setNegativeButton("Cкасувати", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton("Обрати", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            spinnerOneAdapter.setDescription("");
                            if (types == null || types.isEmpty()) {
                                dialog.cancel();
                            } else {
                                spinnerOneAdapter.setName("Обрано типи: ");

                                TextView tvSpinnerDescription = spTypeSpinner.findViewById(R.id.tv_spinner_description);
                                tvSpinnerDescription.setText(UtilMethods.listToFormatString(typesNames));
                            }
                        }
                    });

                    builder.setView(checkBoxView);

                    alertDialog = builder.create();
                    alertDialog.show();

                    Button buttonPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    if (buttonPositive != null) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(10, 5, 10, 10);

                        buttonPositive.setTextColor(Color.rgb(255, 255, 255));
                        buttonPositive.setTextSize(18);
                        buttonPositive.setGravity(Gravity.CENTER);
                        buttonPositive.setLayoutParams(params);
                        buttonPositive.setBackground(getResources().getDrawable(R.drawable.buttonshape_positive));
                    }

                    Button buttonNegative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    if (buttonNegative != null) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(10, 5, 10, 10);

                        buttonNegative.setTextColor(Color.rgb(255, 255, 255));
                        buttonNegative.setTextSize(18);
                        buttonNegative.setGravity(Gravity.CENTER);
                        buttonNegative.setLayoutParams(params);
                        buttonNegative.setBackground(getResources().getDrawable(R.drawable.buttonshape_negative));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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
                    if (lstObjectList.get(i).getType_object().contains(spTypeSpinner.getSelectedItem().toString()) || (types != null && UtilMethods.containsArray(lstObjectList.get(i).getType_object(), types))) {
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

                if (lstROI != null && lstROI.isEmpty() && types != null && types.isEmpty()) {

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
                    if (types != null)
                        Log.i(TAG, "ТИПИ(debug2): " + Arrays.toString(types.toArray()));

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


    @Override
    protected void onDestroy() {
        if (alertDialog != null)
            alertDialog.dismiss();
        super.onDestroy();
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
